#!/bin/bash
# setup_benchmark.sh: Downloads whisper.cpp and the base model

PROJECT_ROOT=$(pwd)
CORE_DIR="$PROJECT_ROOT/core"
MODELS_DIR="$PROJECT_ROOT/models"

# 1. Clone whisper.cpp into core/ if it doesn't exist
if [ ! -d "$CORE_DIR/whisper.cpp" ]; then
    echo "Cloning whisper.cpp..."
    git clone https://github.com/ggerganov/whisper.cpp "$CORE_DIR/whisper.cpp"
else
    echo "whisper.cpp already cloned."
fi

# 2. Compile whisper.cpp with default settings (Apple Silicon/Metal optimized on Darwin)
cd "$CORE_DIR/whisper.cpp"
echo "Compiling whisper.cpp..."
make -j4

# 3. Download the 'base' model if not present in models/
cd "$PROJECT_ROOT"
if [ ! -f "$MODELS_DIR/ggml-base.bin" ]; then
    echo "Downloading base model..."
    bash "$CORE_DIR/whisper.cpp/models/download-ggml-model.sh" base
    mv "$CORE_DIR/whisper.cpp/models/ggml-base.bin" "$MODELS_DIR/ggml-base.bin"
else
    echo "Base model already exists."
fi

echo "Setup complete. Run benchmark with run_benchmark.py"
