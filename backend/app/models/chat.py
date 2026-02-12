"""
Chat Models
SQLAlchemy models for persistent chat room and message storage
"""
from sqlalchemy import (
    Column, Integer, String, Boolean, DateTime,
    Text, ForeignKey, JSON, Index
)
from sqlalchemy.orm import relationship
from sqlalchemy.sql import func
from app.models.base import Base


class ChatRoom(Base):
    """
    Chat room model
    Community chat rooms for discussion
    """
    __tablename__ = "chat_rooms"

    id = Column(String(50), primary_key=True)  # e.g. 'genel', 'bist30'
    name = Column(String(100), nullable=False)
    description = Column(Text, nullable=True)
    icon = Column(String(10), nullable=True)  # Emoji
    category = Column(String(50), default='general')
    is_active = Column(Boolean, default=True)
    member_count = Column(Integer, default=0)

    # Timestamps
    created_at = Column(DateTime(timezone=True), server_default=func.now())

    # Relationships
    messages = relationship("ChatMessage", back_populates="room", cascade="all, delete-orphan")

    def __repr__(self):
        return f"<ChatRoom {self.id} {self.name}>"

    def to_dict(self):
        return {
            'id': self.id,
            'name': self.name,
            'description': self.description,
            'icon': self.icon,
            'category': self.category,
            'member_count': self.member_count,
            'is_active': self.is_active,
        }


class ChatMessage(Base):
    """
    Chat message model
    Messages within chat rooms
    """
    __tablename__ = "chat_messages"
    __table_args__ = (
        Index('ix_chat_messages_room_created', 'room_id', 'created_at'),
    )

    id = Column(Integer, primary_key=True, index=True)
    room_id = Column(String(50), ForeignKey("chat_rooms.id", ondelete="CASCADE"), index=True)
    user_id = Column(Integer, ForeignKey("users.id", ondelete="SET NULL"), nullable=True)

    # Message content
    username = Column(String(100), nullable=False)
    content = Column(Text, nullable=False)
    message_type = Column(String(20), default='text')  # text, trade, image

    # Metadata
    mentions = Column(JSON, nullable=True)  # Stock mentions extracted
    reactions = Column(JSON, nullable=True)  # {'👍': 2, '🚀': 1}
    trade_data = Column(JSON, nullable=True)  # Shared trade details

    # Timestamps
    created_at = Column(DateTime(timezone=True), server_default=func.now())

    # Relationships
    room = relationship("ChatRoom", back_populates="messages")

    def __repr__(self):
        return f"<ChatMessage {self.id} room={self.room_id}>"

    def to_dict(self):
        return {
            'id': str(self.id),
            'room_id': self.room_id,
            'user_id': self.user_id,
            'username': self.username,
            'content': self.content,
            'type': self.message_type,
            'mentions': self.mentions,
            'reactions': self.reactions,
            'trade_data': self.trade_data,
            'created_at': self.created_at.isoformat() if self.created_at else None,
        }
