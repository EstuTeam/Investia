"""
Redis Cache Service
Provides caching with TTL support using Redis (with in-memory fallback)
"""
import json
import time
from typing import Optional, Any
from app.config import settings
from app.utils.logger import logger


class CacheService:
    """
    Cache service with Redis backend and in-memory fallback.
    Thread-safe, TTL-aware caching for API responses.
    """
    
    _instance = None
    
    def __new__(cls):
        if cls._instance is None:
            cls._instance = super().__new__(cls)
            cls._instance._initialized = False
        return cls._instance
    
    def __init__(self):
        if self._initialized:
            return
        
        self._redis = None
        self._memory_cache: dict = {}
        self._use_redis = False
        self._initialized = True
        
        # Try to connect to Redis
        if settings.redis_enabled:
            try:
                import redis
                self._redis = redis.Redis.from_url(
                    settings.redis_url,
                    decode_responses=True,
                    socket_timeout=2,
                    socket_connect_timeout=2,
                    retry_on_timeout=True
                )
                self._redis.ping()
                self._use_redis = True
                logger.info("✅ Redis cache connected")
            except Exception as e:
                logger.warning(f"⚠️ Redis not available, using memory cache: {e}")
                self._redis = None
                self._use_redis = False
        else:
            logger.info("📦 Using in-memory cache (Redis disabled)")
    
    def get(self, key: str) -> Optional[Any]:
        """Get a cached value by key"""
        try:
            if self._use_redis and self._redis:
                data = self._redis.get(f"investia:{key}")
                if data:
                    return json.loads(data)
                return None
            else:
                return self._memory_get(key)
        except Exception as e:
            logger.warning(f"Cache get error for {key}: {e}")
            return self._memory_get(key)
    
    def set(self, key: str, value: Any, ttl: int = 60) -> bool:
        """Set a cached value with TTL (seconds)"""
        try:
            serialized = json.dumps(value, default=str)
            
            if self._use_redis and self._redis:
                self._redis.setex(f"investia:{key}", ttl, serialized)
                return True
            else:
                return self._memory_set(key, value, ttl)
        except Exception as e:
            logger.warning(f"Cache set error for {key}: {e}")
            return self._memory_set(key, value, ttl)
    
    def delete(self, key: str) -> bool:
        """Delete a cached value"""
        try:
            if self._use_redis and self._redis:
                self._redis.delete(f"investia:{key}")
            # Also clear from memory
            self._memory_cache.pop(key, None)
            return True
        except Exception as e:
            logger.warning(f"Cache delete error for {key}: {e}")
            return False
    
    def invalidate_prefix(self, prefix: str) -> int:
        """Invalidate all keys matching a prefix"""
        count = 0
        try:
            if self._use_redis and self._redis:
                for key in self._redis.scan_iter(f"investia:{prefix}*"):
                    self._redis.delete(key)
                    count += 1
            
            # Also clear from memory
            keys_to_delete = [k for k in self._memory_cache if k.startswith(prefix)]
            for k in keys_to_delete:
                del self._memory_cache[k]
                count += 1
        except Exception as e:
            logger.warning(f"Cache invalidate error for prefix {prefix}: {e}")
        
        return count
    
    def clear(self) -> None:
        """Clear all cached data"""
        try:
            if self._use_redis and self._redis:
                for key in self._redis.scan_iter("investia:*"):
                    self._redis.delete(key)
            self._memory_cache.clear()
        except Exception as e:
            logger.warning(f"Cache clear error: {e}")
    
    def get_stats(self) -> dict:
        """Get cache statistics"""
        stats = {
            'backend': 'redis' if self._use_redis else 'memory',
            'memory_entries': len(self._memory_cache),
        }
        
        if self._use_redis and self._redis:
            try:
                info = self._redis.info('memory')
                stats['redis_memory'] = info.get('used_memory_human', 'N/A')
                stats['redis_keys'] = self._redis.dbsize()
            except Exception:
                pass
        
        return stats
    
    # === In-memory fallback ===
    
    def _memory_get(self, key: str) -> Optional[Any]:
        """Get from in-memory cache"""
        entry = self._memory_cache.get(key)
        if entry is None:
            return None
        
        value, expiry = entry
        if time.time() > expiry:
            del self._memory_cache[key]
            return None
        return value
    
    def _memory_set(self, key: str, value: Any, ttl: int) -> bool:
        """Set in in-memory cache"""
        self._memory_cache[key] = (value, time.time() + ttl)
        
        # Clean expired entries periodically (every 100 sets)
        if len(self._memory_cache) % 100 == 0:
            self._memory_cleanup()
        return True
    
    def _memory_cleanup(self) -> None:
        """Remove expired entries from memory cache"""
        now = time.time()
        expired = [k for k, (_, exp) in self._memory_cache.items() if now > exp]
        for k in expired:
            del self._memory_cache[k]


# Singleton instance
cache_service = CacheService()


def get_cache() -> CacheService:
    """Get the cache service instance"""
    return cache_service
