#!/usr/bin/env bash
# Render.com build script
set -e

# Install system dependencies for curl_cffi
apt-get update && apt-get install -y --no-install-recommends \
    libcurl4-openssl-dev \
    libssl-dev \
    build-essential \
    && rm -rf /var/lib/apt/lists/*

# Install Python dependencies
pip install --upgrade pip
pip install -r requirements.txt

echo "✅ Build completed successfully"
