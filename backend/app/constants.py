"""
Central configuration for BIST stock tickers and constants.
All modules should import from here to avoid duplication.
"""

# BIST 30 Index Constituents (updated periodically)
BIST30_TICKERS = [
    "AKBNK.IS", "AKSEN.IS", "ARCLK.IS", "ASELS.IS", "BIMAS.IS",
    "EKGYO.IS", "ENKAI.IS", "EREGL.IS", "FROTO.IS", "GARAN.IS",
    "GUBRF.IS", "HEKTS.IS", "ISCTR.IS", "KCHOL.IS", "KRDMD.IS",
    "ODAS.IS", "PETKM.IS", "PGSUS.IS", "SAHOL.IS", "SASA.IS",
    "SISE.IS", "TAVHL.IS", "TCELL.IS", "THYAO.IS", "TKFEN.IS",
    "TOASO.IS", "TUPRS.IS", "YKBNK.IS", "VAKBN.IS"
]

# Sector mapping for BIST stocks
SECTOR_MAP = {
    "AKBNK": "Bankacılık", "GARAN": "Bankacılık", "ISCTR": "Bankacılık",
    "YKBNK": "Bankacılık", "VAKBN": "Bankacılık",
    "THYAO": "Havacılık", "PGSUS": "Havacılık", "TAVHL": "Havacılık",
    "ASELS": "Savunma", "TCELL": "Telekomünikasyon",
    "EREGL": "Demir-Çelik", "KRDMD": "Demir-Çelik",
    "FROTO": "Otomotiv", "TOASO": "Otomotiv",
    "TUPRS": "Enerji", "AKSEN": "Enerji", "ODAS": "Enerji",
    "ARCLK": "Beyaz Eşya", "SISE": "Cam-Seramik",
    "BIMAS": "Perakende", "SAHOL": "Holding", "KCHOL": "Holding",
    "EKGYO": "GYO", "ENKAI": "İnşaat", "PETKM": "Petrokimya",
    "SASA": "Kimya", "GUBRF": "Gübre", "HEKTS": "Tarım",
    "TKFEN": "Holding",
}

# Cache TTL values (seconds)
CACHE_TTL = {
    'stock_quote': 30,        # Real-time price
    'market_overview': 60,    # Market summary
    'daily_picks': 300,       # 5 minutes
    'news': 600,              # 10 minutes
    'backtest': 600,          # 10 minutes
    'indicators': 120,        # 2 minutes
    'ipo': 300,               # 5 minutes
    'signals': 180,           # 3 minutes
}

# Market hours (Istanbul timezone, UTC+3)
MARKET_OPEN_HOUR = 10   # 10:00
MARKET_CLOSE_HOUR = 18  # 18:00

# Rate limit defaults per endpoint type
RATE_LIMITS = {
    'auth': 5,       # per minute
    'data': 60,      # per minute
    'ai': 10,        # per minute
    'admin': 30,     # per minute
    'default': 120,  # per minute
}
