"""
News API Routes
"""
from fastapi import APIRouter, HTTPException
from loguru import logger
from app.services.news_service import (
    get_economy_news, 
    get_general_news,
    DEMO_ECONOMY_NEWS,
    DEMO_GENERAL_NEWS
)

router = APIRouter(prefix="/news", tags=["news"])

# Demo finance news for fallback
DEMO_FINANCE_NEWS = {
    "type": "finance",
    "turkey": [
        {
            "title": "BIST 100 Endeksi Güne Yükselişle Başladı",
            "description": "Borsa İstanbul'da BIST 100 endeksi yeni haftaya yükselişle başladı.",
            "source": "Borsa İstanbul",
            "link": "https://www.borsaistanbul.com",
            "published": "2024-01-01T09:00:00",
            "category": "finance"
        },
        {
            "title": "Dolar/TL Kurunda Son Durum",
            "description": "Dolar/TL kuru güncel gelişmeler ışığında hareketini sürdürüyor.",
            "source": "TCMB",
            "link": "https://www.tcmb.gov.tr",
            "published": "2024-01-01T10:00:00",
            "category": "finance"
        }
    ],
    "world": [],
    "finance": [],
    "articles": [],
    "source": "demo"
}


@router.get("/economy")
async def economy_news():
    """Ekonomi haberlerini getir"""
    try:
        news = await get_economy_news()
        # Eğer haber çekemediyse demo data döndür
        if not news["turkey"] and not news["world"]:
            logger.warning("No economy news fetched, returning demo data")
            return DEMO_ECONOMY_NEWS
        return news
    except Exception as e:
        logger.error(f"Error fetching economy news: {e}")
        return DEMO_ECONOMY_NEWS


@router.get("/finance")
async def finance_news():
    """Borsa/finans haberlerini getir"""
    try:
        # Reuse economy news but mark as finance
        news = await get_economy_news()
        if not news.get("turkey") and not news.get("world"):
            logger.warning("No finance news fetched, returning demo data")
            return DEMO_FINANCE_NEWS
        news["type"] = "finance"
        return news
    except Exception as e:
        logger.error(f"Error fetching finance news: {e}")
        return DEMO_FINANCE_NEWS


@router.get("/general")
async def general_news():
    """Gündem haberlerini getir"""
    try:
        news = await get_general_news()
        # Eğer haber çekemediyse demo data döndür
        if not news["turkey"] and not news["world"]:
            logger.warning("No general news fetched, returning demo data")
            return DEMO_GENERAL_NEWS
        return news
    except Exception as e:
        logger.error(f"Error fetching general news: {e}")
        return DEMO_GENERAL_NEWS
