"""
Email Verification & Password Reset Service
Token-based email verification and password reset flows
"""
from datetime import datetime, timedelta, timezone
from typing import Optional
import secrets
import hashlib
from sqlalchemy import Column, Integer, String, DateTime, Boolean
from sqlalchemy.sql import func
from sqlalchemy.orm import Session
from app.models.base import Base, SessionLocal
from app.utils.logger import logger


class VerificationToken(Base):
    """Email verification and password reset tokens"""
    __tablename__ = "verification_tokens"

    id = Column(Integer, primary_key=True, index=True)
    email = Column(String(255), nullable=False, index=True)
    token_hash = Column(String(255), nullable=False, unique=True)
    token_type = Column(String(20), nullable=False)  # 'email_verify' or 'password_reset'
    is_used = Column(Boolean, default=False)
    expires_at = Column(DateTime(timezone=True), nullable=False)
    created_at = Column(DateTime(timezone=True), server_default=func.now())

    def __repr__(self):
        return f"<VerificationToken {self.token_type} {self.email}>"


def _hash_token(token: str) -> str:
    """Hash a token for secure storage"""
    return hashlib.sha256(token.encode()).hexdigest()


def generate_verification_token(
    email: str,
    token_type: str = "email_verify",
    expiry_hours: int = 24
) -> str:
    """
    Generate a verification token and store its hash in DB.
    
    Args:
        email: User email
        token_type: 'email_verify' or 'password_reset'
        expiry_hours: Hours until token expires
    
    Returns:
        The raw token string (to be sent via email/API)
    """
    raw_token = secrets.token_urlsafe(32)
    token_hash = _hash_token(raw_token)
    
    db = SessionLocal()
    try:
        # Invalidate previous tokens of same type for this email
        db.query(VerificationToken).filter(
            VerificationToken.email == email,
            VerificationToken.token_type == token_type,
            VerificationToken.is_used == False
        ).update({VerificationToken.is_used: True})
        
        # Create new token
        vt = VerificationToken(
            email=email,
            token_hash=token_hash,
            token_type=token_type,
            expires_at=datetime.now(timezone.utc) + timedelta(hours=expiry_hours)
        )
        db.add(vt)
        db.commit()
        
        logger.info(f"Generated {token_type} token for {email}")
        return raw_token
    except Exception as e:
        db.rollback()
        logger.error(f"Error generating verification token: {e}")
        raise
    finally:
        db.close()


def verify_token(raw_token: str, token_type: str = "email_verify") -> Optional[str]:
    """
    Verify a token and return the associated email.
    
    Args:
        raw_token: The raw token string
        token_type: Expected token type
    
    Returns:
        Email address if token is valid, None otherwise
    """
    token_hash = _hash_token(raw_token)
    
    db = SessionLocal()
    try:
        vt = db.query(VerificationToken).filter(
            VerificationToken.token_hash == token_hash,
            VerificationToken.token_type == token_type,
            VerificationToken.is_used == False
        ).first()
        
        if not vt:
            logger.warning(f"Invalid or used {token_type} token")
            return None
        
        if vt.expires_at < datetime.now(timezone.utc):
            logger.warning(f"Expired {token_type} token for {vt.email}")
            return None
        
        # Mark token as used
        vt.is_used = True
        db.commit()
        
        logger.info(f"Verified {token_type} token for {vt.email}")
        return vt.email
    except Exception as e:
        db.rollback()
        logger.error(f"Error verifying token: {e}")
        return None
    finally:
        db.close()


def mark_user_verified(email: str) -> bool:
    """Mark a user as email-verified"""
    from app.models.user import User
    
    db = SessionLocal()
    try:
        user = db.query(User).filter(User.email == email).first()
        if user:
            user.is_verified = True
            db.commit()
            logger.info(f"User {email} marked as verified")
            return True
        return False
    except Exception as e:
        db.rollback()
        logger.error(f"Error marking user verified: {e}")
        return False
    finally:
        db.close()


def reset_user_password(email: str, new_password: str) -> bool:
    """Reset a user's password"""
    from app.models.user import User
    from app.services.auth_service import get_password_hash
    
    db = SessionLocal()
    try:
        user = db.query(User).filter(User.email == email).first()
        if user:
            user.hashed_password = get_password_hash(new_password)
            db.commit()
            logger.info(f"Password reset for {email}")
            return True
        return False
    except Exception as e:
        db.rollback()
        logger.error(f"Error resetting password: {e}")
        return False
    finally:
        db.close()
