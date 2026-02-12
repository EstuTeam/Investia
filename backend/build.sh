#!/usr/bin/env bash
# Render.com build script
set -e

echo "🐍 Python version: $(python3 --version)"

# Upgrade pip
pip install --upgrade pip

# Install Python dependencies
# Use --only-binary for packages that fail to compile from source
pip install --only-binary=:all: pandas numpy
pip install -r requirements.txt

echo "✅ Build completed successfully"
