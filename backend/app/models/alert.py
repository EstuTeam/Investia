"""
Alert & Notification Models
SQLAlchemy models for persistent alert and notification storage
"""
from sqlalchemy import (
    Column, Integer, String, Float, Boolean, DateTime,
    Text, ForeignKey, JSON, Index
)
from sqlalchemy.orm import relationship
from sqlalchemy.sql import func
from app.models.base import Base


class Alert(Base):
    """
    Trading alert model
    Persistent storage for price/score/signal alerts
    """
    __tablename__ = "alerts"
    __table_args__ = (
        Index('ix_alerts_user_active', 'user_id', 'active'),
        Index('ix_alerts_ticker_active', 'ticker', 'active'),
    )

    id = Column(String(36), primary_key=True)  # UUID
    user_id = Column(Integer, ForeignKey("users.id", ondelete="CASCADE"), index=True, nullable=True)

    # Alert configuration
    alert_type = Column(String(20), nullable=False)  # price, score, signal, position
    ticker = Column(String(20), nullable=False, index=True)
    condition = Column(JSON, nullable=False)  # {'price_above': 450.0}
    priority = Column(String(10), default='medium')  # low, medium, high, critical
    message = Column(Text, nullable=True)

    # State
    active = Column(Boolean, default=True, index=True)
    triggered = Column(Boolean, default=False)
    triggered_at = Column(DateTime(timezone=True), nullable=True)

    # Notification settings
    notification_settings = Column(JSON, nullable=True)  # {'browser': True, 'sound': True}

    # Timestamps
    created_at = Column(DateTime(timezone=True), server_default=func.now())
    updated_at = Column(DateTime(timezone=True), onupdate=func.now())

    def __repr__(self):
        return f"<Alert {self.id[:8]} {self.alert_type} {self.ticker}>"

    def to_dict(self):
        return {
            'id': self.id,
            'type': self.alert_type,
            'ticker': self.ticker,
            'condition': self.condition,
            'priority': self.priority,
            'message': self.message,
            'active': self.active,
            'triggered': self.triggered,
            'triggered_at': self.triggered_at.isoformat() if self.triggered_at else None,
            'notification': self.notification_settings,
            'created_at': self.created_at.isoformat() if self.created_at else None,
        }


class Notification(Base):
    """
    Notification history model
    Stores triggered alert notifications for user review
    """
    __tablename__ = "notifications"
    __table_args__ = (
        Index('ix_notifications_user_read', 'user_id', 'read'),
    )

    id = Column(Integer, primary_key=True, index=True)
    user_id = Column(Integer, ForeignKey("users.id", ondelete="CASCADE"), index=True, nullable=True)
    alert_id = Column(String(36), ForeignKey("alerts.id", ondelete="SET NULL"), nullable=True)

    # Notification content
    title = Column(String(200), nullable=False)
    message = Column(Text, nullable=False)
    notification_type = Column(String(20), default='alert')  # alert, system, signal
    priority = Column(String(10), default='medium')

    # State
    read = Column(Boolean, default=False, index=True)
    archived = Column(Boolean, default=False)

    # Metadata
    ticker = Column(String(20), nullable=True, index=True)
    data = Column(JSON, nullable=True)  # Extra data (price, score, etc.)

    # Timestamps
    created_at = Column(DateTime(timezone=True), server_default=func.now())
    read_at = Column(DateTime(timezone=True), nullable=True)

    def __repr__(self):
        return f"<Notification {self.id} {self.notification_type}>"

    def to_dict(self):
        return {
            'id': str(self.id),
            'alert_id': self.alert_id,
            'title': self.title,
            'message': self.message,
            'type': self.notification_type,
            'priority': self.priority,
            'read': self.read,
            'ticker': self.ticker,
            'data': self.data,
            'created_at': self.created_at.isoformat() if self.created_at else None,
        }
