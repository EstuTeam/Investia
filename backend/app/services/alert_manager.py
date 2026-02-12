"""
Alert Manager Service
Trading alert sistemi - DB-backed persistent alerts
"""
from typing import Dict, List, Any, Optional
from datetime import datetime
import uuid
from sqlalchemy.orm import Session
from app.models.alert import Alert, Notification
from app.models.base import SessionLocal
from app.utils.logger import logger


class AlertManager:
    """Trading alert yonetimi — DB-backed"""
    
    _instance = None
    
    def __new__(cls):
        if cls._instance is None:
            cls._instance = super().__new__(cls)
            cls._instance._initialized = False
        return cls._instance
    
    def __init__(self):
        if self._initialized:
            return
        self._initialized = True
        logger.info("AlertManager initialized (DB-backed)")
    
    def _get_db(self) -> Session:
        """Get a new database session"""
        return SessionLocal()
    
    def create_alert(
        self,
        alert_type: str,
        ticker: str,
        condition: Dict[str, Any],
        notification: Optional[Dict[str, bool]] = None,
        priority: str = 'medium',
        user_id: Optional[int] = None
    ) -> str:
        """
        Yeni alert olustur
        
        Args:
            alert_type: 'price', 'score', 'signal', 'position'
            ticker: Hisse sembolu
            condition: Alert kosulu (orn: {'price_above': 450.0})
            notification: Bildirim ayarlari
            priority: low, medium, high, critical
            user_id: Kullanici ID (opsiyonel)
        
        Returns:
            Alert ID
        """
        alert_id = str(uuid.uuid4())
        
        if notification is None:
            notification = {'browser': True, 'sound': True}
        
        message = self._generate_message(alert_type, ticker, condition)
        
        db = self._get_db()
        try:
            alert = Alert(
                id=alert_id,
                user_id=user_id,
                alert_type=alert_type,
                ticker=ticker,
                condition=condition,
                priority=priority,
                message=message,
                notification_settings=notification,
                active=True,
                triggered=False
            )
            db.add(alert)
            db.commit()
            logger.info(f"Created alert {alert_id}: {alert_type} for {ticker}")
            return alert_id
        except Exception as e:
            db.rollback()
            logger.error(f"Error creating alert: {e}")
            raise
        finally:
            db.close()
    
    def _generate_message(self, alert_type: str, ticker: str, condition: Dict) -> str:
        """Alert mesaji olustur"""
        if condition.get('type') == 'new_ipo':
            return f"Yeni Halka Arz: {ticker}"
        
        if alert_type == 'price':
            if 'price_above' in condition:
                return f"{ticker} {condition['price_above']} TL seviyesine ulasti"
            elif 'price_below' in condition:
                return f"{ticker} {condition['price_below']} TL seviyesine dustu"
        elif alert_type == 'score':
            if 'score_above' in condition:
                return f"{ticker} momentum skoru {condition['score_above']} ustunde"
        elif alert_type == 'signal':
            return f"{ticker} icin BUY sinyali geldi"
        elif alert_type == 'position':
            if 'stop_loss' in condition:
                return f"{ticker} stop-loss tetiklendi"
            elif 'take_profit' in condition:
                return f"{ticker} take-profit hedefine ulasti"
        
        return f"{ticker} alert tetiklendi"
    
    def check_alert(
        self,
        alert_id: str,
        current_data: Dict[str, Any]
    ) -> bool:
        """Bir alert'in tetiklenip tetiklenmedigini kontrol et"""
        db = self._get_db()
        try:
            alert = db.query(Alert).filter(
                Alert.id == alert_id,
                Alert.active == True,
                Alert.triggered == False
            ).first()
            
            if not alert:
                return False
            
            triggered = self._evaluate_condition(alert, current_data)
            
            if triggered:
                return self._trigger_alert(db, alert)
            
            return False
        finally:
            db.close()
    
    def _evaluate_condition(self, alert: Alert, current_data: Dict[str, Any]) -> bool:
        """Alert kosulunu degerlendir"""
        condition = alert.condition
        alert_type = alert.alert_type
        
        if alert_type == 'price':
            current_price = current_data.get('price', 0)
            if 'price_above' in condition and current_price >= condition['price_above']:
                return True
            if 'price_below' in condition and current_price <= condition['price_below']:
                return True
        
        elif alert_type == 'score':
            current_score = current_data.get('score', 0)
            if 'score_above' in condition and current_score >= condition['score_above']:
                return True
            if 'score_below' in condition and current_score <= condition['score_below']:
                return True
        
        elif alert_type == 'signal':
            recommendation = current_data.get('recommendation', '')
            if recommendation == 'BUY':
                return True
        
        return False
    
    def _trigger_alert(self, db: Session, alert: Alert) -> bool:
        """Alert'i tetikle ve bildirim olustur"""
        try:
            alert.triggered = True
            alert.triggered_at = datetime.utcnow()
            
            notif = Notification(
                user_id=alert.user_id,
                alert_id=alert.id,
                title=f"{alert.alert_type.upper()} Alert",
                message=alert.message or f"{alert.ticker} alert tetiklendi",
                notification_type='alert',
                priority=alert.priority or 'medium',
                ticker=alert.ticker,
                data={'condition': alert.condition}
            )
            db.add(notif)
            db.commit()
            
            logger.info(f"Alert triggered: {alert.id} - {alert.message}")
            return True
        except Exception as e:
            db.rollback()
            logger.error(f"Error triggering alert: {e}")
            return False
    
    def check_all_alerts(
        self,
        market_data: Dict[str, Dict[str, Any]]
    ) -> List[Dict[str, Any]]:
        """Tum aktif alertleri kontrol et"""
        db = self._get_db()
        try:
            active_alerts = db.query(Alert).filter(
                Alert.active == True,
                Alert.triggered == False
            ).all()
            
            newly_triggered = []
            
            for alert in active_alerts:
                ticker = alert.ticker
                if ticker not in market_data:
                    continue
                
                if self._evaluate_condition(alert, market_data[ticker]):
                    if self._trigger_alert(db, alert):
                        newly_triggered.append(alert.to_dict())
            
            return newly_triggered
        finally:
            db.close()
    
    def get_active_alerts(self, user_id: Optional[int] = None) -> List[Dict[str, Any]]:
        """Aktif alertleri getir"""
        db = self._get_db()
        try:
            query = db.query(Alert).filter(
                Alert.active == True,
                Alert.triggered == False
            )
            if user_id:
                query = query.filter(Alert.user_id == user_id)
            
            alerts = query.order_by(Alert.created_at.desc()).all()
            return [a.to_dict() for a in alerts]
        finally:
            db.close()
    
    def get_triggered_alerts(self, clear: bool = False, user_id: Optional[int] = None) -> List[Dict[str, Any]]:
        """Tetiklenmis alertleri getir"""
        db = self._get_db()
        try:
            query = db.query(Alert).filter(Alert.triggered == True)
            if user_id:
                query = query.filter(Alert.user_id == user_id)
            
            alerts = query.order_by(Alert.triggered_at.desc()).all()
            result = [a.to_dict() for a in alerts]
            
            if clear:
                for alert in alerts:
                    alert.active = False
                db.commit()
            
            return result
        finally:
            db.close()
    
    def delete_alert(self, alert_id: str) -> bool:
        """Alert sil"""
        db = self._get_db()
        try:
            alert = db.query(Alert).filter(Alert.id == alert_id).first()
            if alert:
                db.delete(alert)
                db.commit()
                logger.info(f"Deleted alert {alert_id}")
                return True
            return False
        finally:
            db.close()
    
    def toggle_alert(self, alert_id: str, active: bool) -> bool:
        """Alert'i aktif/pasif yap"""
        db = self._get_db()
        try:
            alert = db.query(Alert).filter(Alert.id == alert_id).first()
            if alert:
                alert.active = active
                db.commit()
                logger.info(f"Alert {alert_id} set to {'active' if active else 'inactive'}")
                return True
            return False
        finally:
            db.close()
    
    def get_notification_history(
        self,
        limit: int = 50,
        unread_only: bool = False,
        user_id: Optional[int] = None
    ) -> List[Dict[str, Any]]:
        """Bildirim gecmisini getir"""
        db = self._get_db()
        try:
            query = db.query(Notification)
            if user_id:
                query = query.filter(Notification.user_id == user_id)
            if unread_only:
                query = query.filter(Notification.read == False)
            
            notifications = query.order_by(
                Notification.created_at.desc()
            ).limit(limit).all()
            
            return [n.to_dict() for n in notifications]
        finally:
            db.close()
    
    def mark_notification_read(self, alert_id: str) -> bool:
        """Bildirimi okundu olarak isaretle"""
        db = self._get_db()
        try:
            notif = db.query(Notification).filter(
                Notification.alert_id == alert_id
            ).first()
            if notif:
                notif.read = True
                notif.read_at = datetime.utcnow()
                db.commit()
                return True
            return False
        finally:
            db.close()
    
    def mark_all_read(self, user_id: Optional[int] = None) -> int:
        """Tum bildirimleri okundu isaretle"""
        db = self._get_db()
        try:
            query = db.query(Notification).filter(Notification.read == False)
            if user_id:
                query = query.filter(Notification.user_id == user_id)
            
            count = query.update({
                Notification.read: True,
                Notification.read_at: datetime.utcnow()
            })
            db.commit()
            return count
        finally:
            db.close()
    
    def clear_history(self, days: Optional[int] = None, user_id: Optional[int] = None) -> int:
        """Bildirim gecmisini temizle"""
        db = self._get_db()
        try:
            query = db.query(Notification)
            if user_id:
                query = query.filter(Notification.user_id == user_id)
            
            if days is not None:
                from datetime import timedelta
                cutoff = datetime.utcnow() - timedelta(days=days)
                query = query.filter(Notification.created_at < cutoff)
            
            count = query.delete()
            db.commit()
            return count
        finally:
            db.close()
    
    def get_statistics(self, user_id: Optional[int] = None) -> Dict[str, Any]:
        """Bildirim istatistiklerini getir"""
        db = self._get_db()
        try:
            alert_query = db.query(Alert)
            notif_query = db.query(Notification)
            
            if user_id:
                alert_query = alert_query.filter(Alert.user_id == user_id)
                notif_query = notif_query.filter(Notification.user_id == user_id)
            
            total_alerts = alert_query.count()
            active_alerts = alert_query.filter(
                Alert.active == True, Alert.triggered == False
            ).count()
            
            today = datetime.utcnow().date()
            from sqlalchemy import func as sqlfunc
            triggered_today = notif_query.filter(
                sqlfunc.date(Notification.created_at) == today
            ).count()
            
            unread_count = notif_query.filter(Notification.read == False).count()
            total_history = notif_query.count()
            
            return {
                'total_alerts': total_alerts,
                'active_alerts': active_alerts,
                'triggered_today': triggered_today,
                'unread_count': unread_count,
                'total_history': total_history
            }
        finally:
            db.close()
