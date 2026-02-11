#!/usr/bin/env python3
"""
GÜNLÜK TRADE (DAY TRADING) STRATEJİSİ
=====================================
Aynı gün içinde al-sat stratejisi.

Swing Trading vs Day Trading:
- Swing: 2-10 gün pozisyon, geniş stop, büyük hedefler
- Day Trading: Dakika-saat pozisyon, dar stop, küçük hedefler

Günlük Trade Parametreleri:
- Timeframe: 5dk / 15dk / 1 saat
- Stop Loss: %0.5 - %1.0 (çok sıkı)
- Take Profit: %1.0 - %2.0 (küçük ama sık)
- R:R: 1:1.5 - 1:2.0
- Win Rate hedef: %60+
"""

import yfinance as yf
import pandas as pd
import numpy as np
from datetime import datetime, timedelta
import warnings
warnings.filterwarnings('ignore')


def calculate_rsi(prices: pd.Series, period: int = 14) -> pd.Series:
    delta = prices.diff()
    gain = (delta.where(delta > 0, 0)).rolling(window=period).mean()
    loss = (-delta.where(delta < 0, 0)).rolling(window=period).mean()
    rs = gain / (loss + 1e-10)
    return 100 - (100 / (1 + rs))


def calculate_ema(prices: pd.Series, period: int) -> pd.Series:
    return prices.ewm(span=period, adjust=False).mean()


def calculate_vwap(df: pd.DataFrame) -> pd.Series:
    """VWAP - Günlük trade'de önemli"""
    typical_price = (df['High'] + df['Low'] + df['Close']) / 3
    vwap = (typical_price * df['Volume']).cumsum() / df['Volume'].cumsum()
    return vwap


def calculate_bollinger(prices: pd.Series, period: int = 20, std_mult: float = 2.0):
    """Bollinger Bands"""
    sma = prices.rolling(window=period).mean()
    std = prices.rolling(window=period).std()
    upper = sma + (std * std_mult)
    lower = sma - (std * std_mult)
    return upper, sma, lower


def day_trading_signal(df: pd.DataFrame, ticker: str) -> dict:
    """
    GÜNLÜK TRADE SİNYALİ
    
    Kriterler:
    1. Fiyat VWAP'ın üzerinde (alış için)
    2. RSI 40-60 arası (ne aşırı alım ne aşırı satım)
    3. EMA9 > EMA21 (kısa vadeli momentum)
    4. Bollinger ortasının üzerinde
    5. Hacim ortalama üzerinde
    """
    if len(df) < 50:
        return None
    
    close = df['Close']
    high = df['High']
    low = df['Low']
    volume = df['Volume']
    
    current_price = close.iloc[-1]
    
    # İndikatörler
    rsi = calculate_rsi(close, 14)
    ema_9 = calculate_ema(close, 9)
    ema_21 = calculate_ema(close, 21)
    vwap = calculate_vwap(df)
    bb_upper, bb_middle, bb_lower = calculate_bollinger(close, 20)
    
    rsi_val = rsi.iloc[-1]
    ema_9_val = ema_9.iloc[-1]
    ema_21_val = ema_21.iloc[-1]
    vwap_val = vwap.iloc[-1]
    bb_mid = bb_middle.iloc[-1]
    bb_low = bb_lower.iloc[-1]
    
    vol_avg = volume.iloc[-20:].mean()
    vol_current = volume.iloc[-1]
    
    # === SKOR HESAPLA ===
    score = 0
    reasons = []
    signal_type = None
    
    # ====== ALIŞ (LONG) SİNYALİ ======
    # 1. VWAP üzerinde (+20)
    if current_price > vwap_val:
        score += 20
        reasons.append(f"VWAP üzeri ({vwap_val:.2f})")
    
    # 2. RSI 40-60 (+15)
    if 40 <= rsi_val <= 60:
        score += 15
        reasons.append(f"RSI nötr ({rsi_val:.0f})")
    elif 30 <= rsi_val < 40:
        score += 20  # Aşırı satım = fırsat
        reasons.append(f"RSI düşük - fırsat ({rsi_val:.0f})")
    
    # 3. EMA9 > EMA21 (+20)
    if ema_9_val > ema_21_val:
        score += 20
        reasons.append("Kısa momentum yukarı")
    
    # 4. Bollinger ortasının üzerinde (+15)
    if current_price > bb_mid:
        score += 15
        reasons.append("BB ortası üzeri")
    elif current_price > bb_low:
        score += 10
        reasons.append("BB alt bandı üzeri")
    
    # 5. Hacim yüksek (+15)
    if vol_current > vol_avg * 1.3:
        score += 15
        reasons.append("Hacim yüksek (+30%)")
    elif vol_current > vol_avg:
        score += 8
        reasons.append("Hacim normal")
    
    # 6. Son mumun yeşil olması (+10)
    if close.iloc[-1] > close.iloc[-2]:
        score += 10
        reasons.append("Son mum yeşil")
    
    # 7. Fiyat EMA9 üzerinde (+5)
    if current_price > ema_9_val:
        score += 5
        reasons.append("EMA9 üzeri")
    
    # Min skor: 60
    if score < 60:
        return None
    
    signal_type = "LONG"
    
    # === STOP LOSS & TAKE PROFIT (DAY TRADING) ===
    # Günlük trade için çok daha sıkı stoplar
    
    # Stop: Son 5 mumun en düşüğü veya %0.8
    swing_low_5 = low.iloc[-5:].min()
    pct_stop = current_price * 0.992  # %0.8 stop
    
    stop_loss = max(swing_low_5 * 0.998, pct_stop)
    risk = current_price - stop_loss
    
    # Min risk %0.5
    if risk / current_price < 0.005:
        stop_loss = current_price * 0.995
        risk = current_price - stop_loss
    
    # Max risk %1.5
    if risk / current_price > 0.015:
        stop_loss = current_price * 0.985
        risk = current_price - stop_loss
    
    # Take Profit: 1:1.5 R:R (day trading için yeterli)
    target_1 = current_price + (risk * 1.5)
    target_2 = current_price + (risk * 2.5)
    
    rr = (target_1 - current_price) / risk
    
    return {
        'ticker': ticker.replace('.IS', ''),
        'signal': signal_type,
        'score': score,
        'entry_price': round(current_price, 2),
        'stop_loss': round(stop_loss, 2),
        'take_profit_1': round(target_1, 2),
        'take_profit_2': round(target_2, 2),
        'risk_pct': round((risk / current_price) * 100, 2),
        'reward_pct': round(((target_1 - current_price) / current_price) * 100, 2),
        'risk_reward': round(rr, 2),
        'reasons': reasons,
        'strategy': 'Day Trading',
        'holding_time': '1-4 saat',
        'exit_rules': {
            'tp1': "TP1'de %70 kapat",
            'tp2': "TP2'de kalan %30 kapat",
            'time_stop': "Gün sonuna kadar açık kalırsa %0.3 kâr/zararda kapat",
            'trailing': "TP1 sonrası %0.5 trailing stop"
        }
    }


def scan_day_trading_opportunities():
    """Günlük trade fırsatlarını tara"""
    
    # En likit BIST hisseleri (spread düşük, hacim yüksek)
    DAY_TRADE_STOCKS = [
        'THYAO.IS', 'GARAN.IS', 'AKBNK.IS', 'YKBNK.IS', 'SISE.IS',
        'EREGL.IS', 'ASELS.IS', 'FROTO.IS', 'KCHOL.IS', 'SAHOL.IS',
        'TUPRS.IS', 'TCELL.IS', 'BIMAS.IS', 'PGSUS.IS', 'TOASO.IS'
    ]
    
    print("📊 Günlük Trade Taraması Başlıyor...")
    print("=" * 70)
    
    # 1 saatlik veri indir (son 5 gün)
    print("Veri indiriliyor (1 saatlik)...")
    data = yf.download(DAY_TRADE_STOCKS, period='5d', interval='1h', 
                       progress=False, threads=True)
    
    opportunities = []
    
    for symbol in DAY_TRADE_STOCKS:
        try:
            df = data.xs(symbol, level='Ticker', axis=1) if 'Ticker' in data.columns.names else data[symbol]
            if len(df) < 20:
                continue
            
            signal = day_trading_signal(df, symbol)
            if signal:
                opportunities.append(signal)
                print(f"✓ {signal['ticker']}: Skor={signal['score']}, R:R={signal['risk_reward']}")
        except Exception as e:
            pass
    
    # Skora göre sırala
    opportunities.sort(key=lambda x: x['score'], reverse=True)
    
    return opportunities[:5]  # En iyi 5


def print_day_trading_results(picks):
    """Sonuçları yazdır"""
    print("\n" + "=" * 70)
    print("📈 GÜNLÜK TRADE ÖNERİLERİ")
    print("=" * 70)
    
    if not picks:
        print("❌ Şu an uygun günlük trade fırsatı bulunamadı.")
        print("\n💡 Öneriler:")
        print("   - Piyasa açılışından 30-60 dk sonra tekrar deneyin")
        print("   - Hacim düşükse sinyaller zayıf olabilir")
        return
    
    for i, p in enumerate(picks, 1):
        print(f"\n{i}. {p['ticker']} ({p['signal']})")
        print(f"   Skor: {p['score']}/100")
        print(f"   Giriş: {p['entry_price']} TL")
        print(f"   Stop Loss: {p['stop_loss']} TL ({p['risk_pct']}% risk)")
        print(f"   TP1: {p['take_profit_1']} TL ({p['reward_pct']}% hedef)")
        print(f"   TP2: {p['take_profit_2']} TL")
        print(f"   R:R = 1:{p['risk_reward']}")
        print(f"   Nedenler: {', '.join(p['reasons'])}")
        print(f"   ⏱️  Pozisyon süresi: {p['holding_time']}")
    
    print("\n" + "=" * 70)
    print("⚠️  GÜNLÜK TRADE KURALLARI:")
    print("=" * 70)
    print("1. Pozisyon boyutu: Portföyün max %5'i")
    print("2. Günlük max kayıp: Portföyün %2'si")
    print("3. Stop Loss'u MUTLAKA koy, hareket ettirme!")
    print("4. TP1'de %70 pozisyon kapat, trailing stop aç")
    print("5. Gün sonunda açık pozisyon bırakma")
    print("6. Piyasa açılışı ilk 15 dk ve kapanış son 15 dk işlem yapma")
    print("=" * 70)


if __name__ == "__main__":
    picks = scan_day_trading_opportunities()
    print_day_trading_results(picks)
