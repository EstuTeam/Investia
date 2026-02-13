"""
Database Base Configuration
SQLAlchemy setup and session management with Neon PostgreSQL support
"""
from sqlalchemy import create_engine, event, text
from sqlalchemy.ext.declarative import declarative_base
from sqlalchemy.orm import sessionmaker
from sqlalchemy.pool import StaticPool, QueuePool
from app.config import settings
from app.utils.logger import logger
import os
import ssl
from urllib.parse import urlparse, parse_qs, urlencode, urlunparse

# Database URL handling
DATABASE_URL = settings.database_url

# Fix Neon PostgreSQL URL format (replace postgres:// with postgresql://)
if DATABASE_URL.startswith("postgres://"):
    DATABASE_URL = DATABASE_URL.replace("postgres://", "postgresql://", 1)

# Create engine based on database type
engine = None
SessionLocal = None
Base = declarative_base()


def _strip_sslmode_from_url(url: str) -> str:
    """Remove sslmode and channel_binding from URL query params (pg8000 handles SSL via ssl_context)"""
    parsed = urlparse(url)
    params = parse_qs(parsed.query)
    params.pop("sslmode", None)
    params.pop("channel_binding", None)
    new_query = urlencode({k: v[0] for k, v in params.items()}) if params else ""
    return urlunparse(parsed._replace(query=new_query))


def _init_database():
    """Lazy database initialization to avoid startup crashes"""
    global engine, SessionLocal, DATABASE_URL
    if engine is not None:
        return True
    
    try:
        if DATABASE_URL.startswith("sqlite"):
            # SQLite - for local development
            engine = create_engine(
                DATABASE_URL,
                connect_args={"check_same_thread": False},
                poolclass=StaticPool,
                echo=settings.log_level.upper() == "DEBUG"
            )
            logger.info("Using SQLite database for development")
        else:
            # PostgreSQL - for production (Neon)
            pg_url = DATABASE_URL
            connect_args = {}
            use_pg8000 = False
            
            try:
                import pg8000  # noqa: F401
                use_pg8000 = True
                # pg8000 doesn't support sslmode as URL param — use ssl_context
                pg_url = _strip_sslmode_from_url(pg_url)
                if "postgresql://" in pg_url and "+pg8000" not in pg_url:
                    pg_url = pg_url.replace("postgresql://", "postgresql+pg8000://", 1)
                # Create SSL context for Neon (required)
                ssl_context = ssl.create_default_context()
                ssl_context.check_hostname = False
                ssl_context.verify_mode = ssl.CERT_NONE
                connect_args["ssl_context"] = ssl_context
                logger.info("Using pg8000 driver for PostgreSQL (SSL enabled)")
            except ImportError:
                logger.info("pg8000 not available, trying psycopg2")
                # psycopg2 supports sslmode in URL natively — ensure it's present
                if "neon" in pg_url.lower() and "sslmode" not in pg_url:
                    separator = "&" if "?" in pg_url else "?"
                    pg_url = f"{pg_url}{separator}sslmode=require"
            
            engine = create_engine(
                pg_url,
                poolclass=QueuePool,
                pool_size=settings.database_pool_size,
                max_overflow=settings.database_max_overflow,
                pool_timeout=settings.database_pool_timeout,
                pool_recycle=settings.database_pool_recycle,
                pool_pre_ping=True,
                connect_args=connect_args,
                echo=settings.log_level.upper() == "DEBUG"
            )
            logger.info("Using PostgreSQL database for production")

        SessionLocal = sessionmaker(autocommit=False, autoflush=False, bind=engine)
        return True
    except Exception as e:
        logger.error(f"Database initialization failed: {e}")
        engine = None
        SessionLocal = None
        return False

# Try to initialize on import, but don't crash if it fails
try:
    _init_database()
except Exception as e:
    logger.warning(f"Database not available at startup: {e}")


def get_db():
    """
    Dependency for getting database session
    Usage: db: Session = Depends(get_db)
    """
    if SessionLocal is None:
        # Try lazy initialization
        if not _init_database():
            raise RuntimeError("Database not available")
    
    db = SessionLocal()
    try:
        yield db
    finally:
        db.close()


def init_db():
    """
    Initialize database tables
    Call this on application startup
    """
    # Import all models to register them with Base
    from app.models import user, portfolio, alert, chat
    from app.services import email_service  # VerificationToken model
    
    try:
        Base.metadata.create_all(bind=engine)
        logger.info("Database tables created/verified successfully")
    except Exception as e:
        logger.error(f"Failed to create database tables: {e}")
        raise


def check_db_connection() -> bool:
    """
    Check database connection health
    
    Returns:
        bool: True if connection is healthy, False otherwise
    """
    try:
        with engine.connect() as conn:
            conn.execute(text("SELECT 1"))
        return True
    except Exception as e:
        logger.error(f"Database connection check failed: {e}")
        return False
