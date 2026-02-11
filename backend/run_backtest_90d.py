#!/usr/bin/env python3
"""90 Günlük Backtest - Standalone Script"""
import sys, os
sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))

import yfinance as yf
import pandas as pd
import numpy as np
from datetime import datetime, timedelta
import warnings
warnings.filterwarnings('ignore')

from backtest_hybrid import (
    generate_hybrid_signal, check_market_trend, apply_sector_diversification,
    STOCK_SECTORS, TEST_TICKERS
)

def run():
    days = 90
    max_picks = 5
    
    print('='*70)
    print('🚀 BACKTEST HYBRID - 90 GÜN')
    print('='*70)
    
    end_date = datetime.now()
    start_date = end_date - timedelta(days=400)  # 400 gün geriye (200 gün gösterge + ~63 iş günü test)
    
    print(f'📥 {len(TEST_TICKERS)} hisse için veri çekiliyor...')
    
    all_data = {}
    failed = []
    for ticker in TEST_TICKERS:
        for retry in range(3):
            try:
                df = yf.download(ticker, start=start_date, end=end_date, progress=False, timeout=15)
                if not df.empty and len(df) >= 50:
                    if isinstance(df.columns, pd.MultiIndex):
                        df.columns = df.columns.get_level_values(0)
                    all_data[ticker] = df
                    break
            except:
                if retry < 2:
                    import time
                    time.sleep(1)
        else:
            failed.append(ticker)
    
    print(f'✅ {len(all_data)} hisse yüklendi', end='')
    if failed:
        print(f' | ⚠️ Yüklenemedi: {", ".join(failed)}', end='')
    print()
    
    # BIST100
    try:
        xu100 = yf.download('XU100.IS', start=start_date, end=end_date, progress=False)
        if isinstance(xu100.columns, pd.MultiIndex):
            xu100.columns = xu100.columns.get_level_values(0)
        print(f'📥 BIST100: {len(xu100)} gün veri')
    except:
        xu100 = None
        print('⚠️ BIST100 yüklenemedi')
    
    if not all_data:
        print('❌ Hiç veri yok!')
        return
    
    ref = list(all_data.keys())[0]
    total_days = len(all_data[ref])
    start_idx = 200
    
    # Son 90 takvim günü başlangıcını bul
    test_start_date = end_date - timedelta(days=90)
    ref_dates = all_data[ref].index
    test_start_idx = start_idx
    for i, d in enumerate(ref_dates):
        dt = d.tz_localize(None) if d.tzinfo else d
        if dt >= test_start_date:
            test_start_idx = max(i, start_idx)
            break
    
    test_days = total_days - test_start_idx
    print(f'📊 Toplam veri: {total_days} gün | Test: {test_days} iş günü')
    print()
    
    if test_days <= 0:
        print('❌ Test için yeterli veri yok!')
        return
    
    trades = []
    active_positions = {}
    daily_balance = [0]
    market_blocked = 0
    no_signal = 0
    booster_used = 0
    
    for day_idx in range(test_start_idx, total_days):
        date = all_data[ref].index[day_idx]
        
        # Market trend check
        if xu100 is not None and len(xu100) > day_idx:
            try:
                xu100_idx = xu100.index.get_loc(date, method='ffill')
                if not check_market_trend(xu100, xu100_idx):
                    market_blocked += 1
                    continue
            except:
                pass
        
        # Position management
        to_remove = []
        for ticker, pos in active_positions.items():
            if ticker not in all_data:
                continue
            df = all_data[ticker]
            if day_idx >= len(df):
                continue
            
            cp = df['Close'].iloc[day_idx]
            hi = df['High'].iloc[day_idx]
            lo = df['Low'].iloc[day_idx]
            pos['days_held'] += 1
            
            if lo <= pos['sl']:
                pnl = ((pos['sl'] - pos['entry']) / pos['entry']) * 100
                trades.append({
                    'ticker': ticker, 'entry_date': pos['entry_date'], 'exit_date': date,
                    'entry': pos['entry'], 'exit': pos['sl'], 'pnl': pnl,
                    'reason': 'STOP_LOSS', 'days': pos['days_held'], 'score': pos['score']
                })
                to_remove.append(ticker)
                continue
            
            if not pos.get('tp1_hit') and hi >= pos['tp1']:
                pos['tp1_hit'] = True
                pos['pnl_accumulated'] = ((pos['tp1'] - pos['entry']) / pos['entry']) * 100 * 0.5
                pos['sl'] = pos['entry']
                continue
            
            if pos.get('tp1_hit') and hi >= pos['tp2']:
                pnl_rem = ((pos['tp2'] - pos['entry']) / pos['entry']) * 100 * 0.5
                total_pnl = pos['pnl_accumulated'] + pnl_rem
                trades.append({
                    'ticker': ticker, 'entry_date': pos['entry_date'], 'exit_date': date,
                    'entry': pos['entry'], 'exit': pos['tp2'], 'pnl': total_pnl,
                    'reason': 'TP1+TP2', 'days': pos['days_held'], 'score': pos['score']
                })
                to_remove.append(ticker)
                continue
            
            if pos['days_held'] >= 10:
                if pos.get('tp1_hit'):
                    pnl_rem = ((cp - pos['entry']) / pos['entry']) * 100 * 0.5
                    total_pnl = pos['pnl_accumulated'] + pnl_rem
                    reason = 'TP1+10D'
                else:
                    total_pnl = ((cp - pos['entry']) / pos['entry']) * 100
                    reason = '10D'
                trades.append({
                    'ticker': ticker, 'entry_date': pos['entry_date'], 'exit_date': date,
                    'entry': pos['entry'], 'exit': cp, 'pnl': total_pnl,
                    'reason': reason, 'days': pos['days_held'], 'score': pos['score']
                })
                to_remove.append(ticker)
        
        for t in to_remove:
            del active_positions[t]
        
        if len(active_positions) < max_picks:
            candidates = []
            for ticker in all_data:
                if ticker in active_positions:
                    continue
                df = all_data[ticker]
                if day_idx >= len(df):
                    continue
                signal = generate_hybrid_signal(df, ticker, day_idx)
                if signal:
                    candidates.append(signal)
                    if signal.get('booster_active'):
                        booster_used += 1
            
            if candidates:
                candidates.sort(key=lambda x: x['score'], reverse=True)
                picks = apply_sector_diversification(candidates, max_picks - len(active_positions))
                for s in picks:
                    active_positions[s['ticker']] = {
                        'entry_date': date, 'entry': s['entry'], 'sl': s['sl'],
                        'tp1': s['tp1'], 'tp2': s['tp2'], 'days_held': 0,
                        'score': s['score'], 'tp1_hit': False, 'pnl_accumulated': 0
                    }
            else:
                no_signal += 1
        
        daily_balance.append(sum(t['pnl'] for t in trades))
        
        # Progress
        progress = day_idx - test_start_idx + 1
        if progress % 5 == 0:
            print(f'\r  İşleniyor: {progress}/{test_days} gün...', end='', flush=True)
    
    print(f'\r  İşleniyor: {test_days}/{test_days} gün... ✅')
    
    # === SONUÇLAR ===
    print()
    print('='*70)
    print('📊 90 GÜNLÜK BACKTEST SONUÇLARI')
    print('='*70)
    
    if not trades:
        print('⚠️  Hiç işlem yapılmadı!')
        print(f'   Market filtre blokları: {market_blocked} gün')
        print(f'   Sinyal yok günleri: {no_signal} gün')
        print(f'   Test edilen gün: {test_days}')
        return
    
    total = len(trades)
    winners = [t for t in trades if t['pnl'] > 0]
    losers = [t for t in trades if t['pnl'] <= 0]
    wr = len(winners) / total * 100
    total_ret = sum(t['pnl'] for t in trades)
    avg_trade = total_ret / total
    gross_p = sum(t['pnl'] for t in winners) if winners else 0
    gross_l = abs(sum(t['pnl'] for t in losers)) if losers else 0.01
    pf = gross_p / gross_l if gross_l > 0 else 0
    
    peak = 0
    max_dd = 0
    for b in daily_balance:
        if b > peak:
            peak = b
        dd = peak - b
        if dd > max_dd:
            max_dd = dd
    
    print(f'\n📈 GENEL İSTATİSTİKLER:')
    print(f'   Toplam İşlem:    {total}')
    print(f'   Kazanan:         {len(winners)}')
    print(f'   Kaybeden:        {len(losers)}')
    print(f'   Win Rate:        %{wr:.1f}')
    
    print(f'\n💰 KAR/ZARAR:')
    print(f'   Toplam Getiri:   %{total_ret:.2f}')
    print(f'   Ort. İşlem:      %{avg_trade:.2f}')
    print(f'   Ort. Kazanç:     %{(sum(t["pnl"] for t in winners)/len(winners)) if winners else 0:.2f}')
    print(f'   Ort. Kayıp:      %{(sum(t["pnl"] for t in losers)/len(losers)) if losers else 0:.2f}')
    
    print(f'\n📈 PERFORMANS:')
    print(f'   Profit Factor:   {pf:.2f}')
    print(f'   Max Drawdown:    %{max_dd:.1f}')
    
    print(f'\n🛡️ FİLTRELER:')
    print(f'   Market Blocked:  {market_blocked} gün')
    print(f'   No Signal:       {no_signal} gün')
    if booster_used > 0:
        print(f'   Booster Aktif:   {booster_used} sinyal')
    
    # Çıkış nedeni analizi
    exit_reasons = {}
    for t in trades:
        r = t['reason']
        if r not in exit_reasons:
            exit_reasons[r] = {'c': 0, 'pnl': 0}
        exit_reasons[r]['c'] += 1
        exit_reasons[r]['pnl'] += t['pnl']
    
    print(f'\n📊 ÇIKIŞ NEDENLERİ:')
    for r, s in sorted(exit_reasons.items(), key=lambda x: x[1]['pnl'], reverse=True):
        print(f'   {r:12s}: {s["c"]:2d} işlem, %{s["pnl"]:+.2f}')
    
    # Sektör analizi
    sector_stats = {}
    for t in trades:
        sec = STOCK_SECTORS.get(t['ticker'], 'Diğer')
        if sec not in sector_stats:
            sector_stats[sec] = {'c': 0, 'pnl': 0}
        sector_stats[sec]['c'] += 1
        sector_stats[sec]['pnl'] += t['pnl']
    
    print(f'\n📊 SEKTÖR ANALİZİ:')
    for sec, s in sorted(sector_stats.items(), key=lambda x: x[1]['pnl'], reverse=True):
        print(f'   {sec:18s}: {s["c"]:2d} işlem, %{s["pnl"]:+.2f}')
    
    # İşlem detayları
    print(f'\n📝 TÜM İŞLEMLER:')
    print('-' * 90)
    for i, t in enumerate(trades, 1):
        e = '🟢' if t['pnl'] > 0 else '🔴'
        sec = STOCK_SECTORS.get(t['ticker'], 'Diğer')
        ed = t['entry_date'].strftime('%Y-%m-%d') if hasattr(t['entry_date'], 'strftime') else str(t['entry_date'])[:10]
        xd = t['exit_date'].strftime('%Y-%m-%d') if hasattr(t['exit_date'], 'strftime') else str(t['exit_date'])[:10]
        print(f'  {i:2d}. {e} {t["ticker"]:10s} | {ed}→{xd} ({t["days"]:2d}g) | ₺{t["entry"]:.2f}→₺{t["exit"]:.2f} | {t["pnl"]:+.2f}% | {t["reason"]:10s} | {sec}')
    
    # Değerlendirme
    print()
    print('='*70)
    print('🎯 STRATEJİ DEĞERLENDİRMESİ:')
    if wr >= 70 and pf >= 3.0 and max_dd < 8:
        print('   ✅ MÜKEMMEL - Hedeflere ulaştı! (%70+ WR, 3.0+ PF, <8% DD)')
    elif wr >= 65 and pf >= 2.5 and max_dd < 10:
        print('   ✅ GÜÇLÜ - Canlı kullanıma uygun')
    elif wr >= 60 and pf >= 2.0:
        print('   🟡 KABUL EDİLEBİLİR - Dikkatli kullanın')
    elif wr >= 55 and pf >= 1.5:
        print('   🟠 ZAYIF - Optimizasyon gerekli')
    else:
        print('   ⚠️ GELİŞTİRİLMELİ - Stratejide revizyon gerekli')
    print('='*70)


if __name__ == '__main__':
    run()
