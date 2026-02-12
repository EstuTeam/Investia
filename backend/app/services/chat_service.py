"""
Chat Service - DB-backed grup sohbet yonetimi
"""
from datetime import datetime
from typing import Dict, List, Optional
import re
import uuid
from sqlalchemy.orm import Session
from app.models.chat import ChatRoom as ChatRoomModel, ChatMessage as ChatMessageModel
from app.models.base import SessionLocal
from app.utils.logger import logger


class ChatService:
    """Chat servisi - DB-backed persistent chat"""

    _instance = None

    def __new__(cls):
        if cls._instance is None:
            cls._instance = super().__new__(cls)
            cls._instance._initialized = False
        return cls._instance

    def __init__(self):
        if self._initialized:
            return
        self.online_users: Dict[str, str] = {}  # user_id -> room_id
        self._initialized = True
        self._rooms_ensured = False
        logger.info("ChatService initialized (DB-backed)")

    def _lazy_ensure_rooms(self):
        """Ensure default rooms exist (called lazily on first DB access)"""
        if self._rooms_ensured:
            return
        try:
            self._ensure_default_rooms()
            self._rooms_ensured = True
        except Exception as e:
            logger.warning(f"Could not ensure default rooms yet: {e}")

    def _get_db(self) -> Session:
        """Get a new database session"""
        return SessionLocal()

    def _ensure_default_rooms(self):
        """Varsayilan sohbet odalarini olustur (yoksa)"""
        default_rooms = [
            ("general", "Genel Sohbet", "Tum konular icin genel sohbet odasi", "💬", "general"),
            ("bist30", "BIST 30", "BIST 30 hisseleri hakkinda tartismalar", "📊", "market"),
            ("teknik-analiz", "Teknik Analiz", "Teknik analiz ve grafikler", "📈", "analysis"),
            ("haberler", "Haberler", "Piyasa haberleri ve duyurular", "📰", "news"),
            ("halka-arz", "Halka Arz", "Yeni halka arzlar hakkinda tartismalar", "🚀", "ipo"),
            ("yeni-baslayanlara", "Yeni Baslayanlara", "Borsaya yeni baslayanlar icin", "🎓", "education"),
        ]

        db = self._get_db()
        try:
            for room_id, name, desc, icon, category in default_rooms:
                existing = db.query(ChatRoomModel).filter(ChatRoomModel.id == room_id).first()
                if not existing:
                    room = ChatRoomModel(
                        id=room_id,
                        name=name,
                        description=desc,
                        icon=icon,
                        category=category,
                        is_active=True,
                        member_count=0
                    )
                    db.add(room)

                    # Hosgeldin mesaji
                    welcome = ChatMessageModel(
                        room_id=room_id,
                        user_id=None,
                        username="Sistem",
                        content=f"{name} odasina hos geldiniz! {desc}",
                        message_type="system"
                    )
                    db.add(welcome)

            db.commit()
        except Exception as e:
            db.rollback()
            logger.error(f"Error creating default rooms: {e}")
        finally:
            db.close()

    def get_rooms(self) -> List[Dict]:
        """Tum odalari getir"""
        self._lazy_ensure_rooms()
        db = self._get_db()
        try:
            rooms = db.query(ChatRoomModel).filter(
                ChatRoomModel.is_active == True
            ).all()
            return [r.to_dict() for r in rooms]
        finally:
            db.close()

    def get_room(self, room_id: str) -> Optional[Dict]:
        """Belirli bir odayi getir"""
        self._lazy_ensure_rooms()
        db = self._get_db()
        try:
            room = db.query(ChatRoomModel).filter(ChatRoomModel.id == room_id).first()
            if room:
                return room.to_dict()
            return None
        finally:
            db.close()

    def get_messages(self, room_id: str, limit: int = 50, before: Optional[str] = None) -> List[Dict]:
        """Oda mesajlarini getir"""
        self._lazy_ensure_rooms()
        db = self._get_db()
        try:
            query = db.query(ChatMessageModel).filter(
                ChatMessageModel.room_id == room_id
            )

            if before:
                # before ID'den onceki mesajlari getir
                try:
                    before_id = int(before)
                    query = query.filter(ChatMessageModel.id < before_id)
                except (ValueError, TypeError):
                    pass

            messages = query.order_by(
                ChatMessageModel.created_at.desc()
            ).limit(limit).all()

            # Kronolojik sirala (eski -> yeni)
            messages.reverse()
            return [m.to_dict() for m in messages]
        finally:
            db.close()

    def add_message(
        self,
        room_id: str,
        user_id: str,
        username: str,
        content: str,
        message_type: str = "text",
        reply_to: Optional[str] = None
    ) -> Optional[Dict]:
        """Yeni mesaj ekle"""
        self._lazy_ensure_rooms()
        db = self._get_db()
        try:
            # Oda var mi kontrol et
            room = db.query(ChatRoomModel).filter(ChatRoomModel.id == room_id).first()
            if not room:
                return None

            # Hisse sembollerini bul
            mentions = self._extract_stock_mentions(content)

            # user_id integer'a cevir (mumkunse)
            uid = None
            try:
                uid = int(user_id)
            except (ValueError, TypeError):
                pass

            message = ChatMessageModel(
                room_id=room_id,
                user_id=uid,
                username=username,
                content=content,
                message_type=message_type,
                mentions=mentions if mentions else None
            )
            db.add(message)
            db.commit()
            db.refresh(message)

            return message.to_dict()
        except Exception as e:
            db.rollback()
            logger.error(f"Error adding message: {e}")
            return None
        finally:
            db.close()

    def _extract_stock_mentions(self, content: str) -> List[str]:
        """Mesajdaki hisse sembollerini cikar (orn: $THYAO)"""
        pattern = r'\$([A-Z]{3,5})'
        matches = re.findall(pattern, content.upper())
        return list(set(matches))

    def add_reaction(self, room_id: str, message_id: str, user_id: str, emoji: str) -> bool:
        """Mesaja tepki ekle"""
        db = self._get_db()
        try:
            msg = db.query(ChatMessageModel).filter(
                ChatMessageModel.room_id == room_id,
                ChatMessageModel.id == int(message_id)
            ).first()

            if not msg:
                return False

            reactions = msg.reactions or {}
            if emoji not in reactions:
                reactions[emoji] = 0
            reactions[emoji] += 1
            msg.reactions = reactions

            db.commit()
            return True
        except Exception as e:
            db.rollback()
            logger.error(f"Error adding reaction: {e}")
            return False
        finally:
            db.close()

    def remove_reaction(self, room_id: str, message_id: str, user_id: str, emoji: str) -> bool:
        """Mesajdan tepki kaldir"""
        db = self._get_db()
        try:
            msg = db.query(ChatMessageModel).filter(
                ChatMessageModel.room_id == room_id,
                ChatMessageModel.id == int(message_id)
            ).first()

            if not msg:
                return False

            reactions = msg.reactions or {}
            if emoji in reactions:
                reactions[emoji] = max(0, reactions[emoji] - 1)
                if reactions[emoji] == 0:
                    del reactions[emoji]
                msg.reactions = reactions
                db.commit()
                return True

            return False
        except Exception as e:
            db.rollback()
            logger.error(f"Error removing reaction: {e}")
            return False
        finally:
            db.close()

    def get_online_users(self, room_id: Optional[str] = None) -> int:
        """Online kullanici sayisi"""
        if room_id:
            return sum(1 for r in self.online_users.values() if r == room_id)
        return len(self.online_users)

    def user_join(self, user_id: str, username: str, room_id: str = "general"):
        """Kullanici odaya katildi"""
        self.online_users[user_id] = room_id

    def user_leave(self, user_id: str):
        """Kullanici ayrildi"""
        if user_id in self.online_users:
            del self.online_users[user_id]

    def share_trade(
        self,
        room_id: str,
        user_id: str,
        username: str,
        trade_data: Dict
    ) -> Optional[Dict]:
        """Islem paylas"""
        symbol = trade_data.get("symbol", "")
        action = trade_data.get("action", "")
        price = trade_data.get("price", 0)
        quantity = trade_data.get("quantity", 0)
        pnl = trade_data.get("pnl")

        emoji = "BUY" if action == "BUY" else "SELL"
        pnl_str = ""
        if pnl is not None:
            pnl_str = f" | PnL: %{pnl:.2f}"

        content = f"{emoji} ${symbol} @ {price:,.2f} TL x {quantity}{pnl_str}"

        db = self._get_db()
        try:
            room = db.query(ChatRoomModel).filter(ChatRoomModel.id == room_id).first()
            if not room:
                return None

            uid = None
            try:
                uid = int(user_id)
            except (ValueError, TypeError):
                pass

            message = ChatMessageModel(
                room_id=room_id,
                user_id=uid,
                username=username,
                content=content,
                message_type="trade",
                mentions=[symbol] if symbol else None,
                trade_data=trade_data
            )
            db.add(message)
            db.commit()
            db.refresh(message)

            return message.to_dict()
        except Exception as e:
            db.rollback()
            logger.error(f"Error sharing trade: {e}")
            return None
        finally:
            db.close()


# Global singleton instance
chat_service = ChatService()
