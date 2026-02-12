"""
Token Blacklist Service
Manages invalidated JWT tokens for secure logout.
Uses Redis if available, falls back to in-memory set with TTL cleanup.
"""
import time
from typing import Set, Dict
from app.config import settings
from app.utils.logger import logger


class TokenBlacklist:
    """
    JWT Token Blacklist for implementing real logout.
    Tokens are stored until their natural expiry time.
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
        self._memory_blacklist: Dict[str, float] = {}  # token -> expiry timestamp
        self._use_redis = False
        self._initialized = True
        
        if settings.redis_enabled:
            try:
                import redis
                self._redis = redis.Redis.from_url(
                    settings.redis_url,
                    decode_responses=True,
                    socket_timeout=2,
                )
                self._redis.ping()
                self._use_redis = True
                logger.info("Token blacklist using Redis")
            except Exception as e:
                logger.warning(f"Redis not available for blacklist: {e}")
    
    def blacklist_token(self, token: str, ttl_seconds: int = None) -> bool:
        """
        Add a token to the blacklist.
        
        Args:
            token: JWT token string
            ttl_seconds: Time until token naturally expires (auto-cleanup)
        """
        if ttl_seconds is None:
            ttl_seconds = settings.jwt_expiration_hours * 3600
        
        try:
            if self._use_redis and self._redis:
                self._redis.setex(f"blacklist:{token}", ttl_seconds, "1")
            else:
                self._memory_blacklist[token] = time.time() + ttl_seconds
                # Periodic cleanup
                if len(self._memory_blacklist) % 50 == 0:
                    self._cleanup()
            return True
        except Exception as e:
            logger.error(f"Failed to blacklist token: {e}")
            return False
    
    def is_blacklisted(self, token: str) -> bool:
        """Check if a token is blacklisted"""
        try:
            if self._use_redis and self._redis:
                return self._redis.exists(f"blacklist:{token}") > 0
            else:
                expiry = self._memory_blacklist.get(token)
                if expiry is None:
                    return False
                if time.time() > expiry:
                    del self._memory_blacklist[token]
                    return False
                return True
        except Exception as e:
            logger.error(f"Failed to check blacklist: {e}")
            return False
    
    def _cleanup(self):
        """Remove expired entries from memory blacklist"""
        now = time.time()
        expired = [t for t, exp in self._memory_blacklist.items() if now > exp]
        for t in expired:
            del self._memory_blacklist[t]


# Singleton
token_blacklist = TokenBlacklist()
