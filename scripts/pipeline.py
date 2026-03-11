import subprocess
import os
import sys
import argparse

def run_pipeline(input_file, language='auto', model='base'):
    # Paths
    project_root = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
    whisper_cli = os.path.join(project_root, "core/whisper.cpp/build/bin/whisper-cli")
    model_path = os.path.join(project_root, f"models/ggml-{model}.bin")
    
    if not os.path.exists(input_file):
        print(f"Error: Input file {input_file} not found.")
        return

    if not os.path.exists(model_path):
        print(f"Error: Model {model_path} not found. Run setup_benchmark.sh first.")
        return

    # Create temporary WAV for Whisper
    temp_wav = "temp_whisper_input.wav"
    output_base = os.path.splitext(input_file)[0] + "_transcribed"

    print(f"--- Step 1: Extracting Audio (FFmpeg) ---")
    ffmpeg_cmd = [
        "ffmpeg", "-i", input_file,
        "-ar", "16000", "-ac", "1",
        "-c:a", "pcm_s16le", temp_wav, "-y"
    ]
    
    try:
        subprocess.run(ffmpeg_cmd, check=True, capture_output=True)
    except subprocess.CalledProcessError as e:
        print(f"FFmpeg Error: {e.stderr.decode()}")
        return

    print(f"--- Step 2: Transcribing (Whisper.cpp) ---")
    whisper_cmd = [
        whisper_cli,
        "-m", model_path,
        "-f", temp_wav,
        "-l", language,
        "-otxt", "-osrt",
        "-of", output_base,
        "-t", "4"
    ]

    try:
        subprocess.run(whisper_cmd, check=True)
        print(f"\n--- Success! ---")
        print(f"Text file: {output_base}.txt")
        print(f"Subtitle file: {output_base}.srt")
    except subprocess.CalledProcessError as e:
        print(f"Whisper Error: {e}")
    finally:
        if os.path.exists(temp_wav):
            os.remove(temp_wav)

if __name__ == "__main__":
    parser = argparse.ArgumentParser(description="v1media One-Click Pipeline")
    parser.add_argument("input", help="Path to video or audio file")
    parser.add_argument("--lang", default="auto", help="Language code (e.g., 'en', 'es', 'hi')")
    parser.add_argument("--model", default="base", help="Whisper model name (base, tiny, small)")
    
    args = parser.parse_args()
    run_pipeline(args.input, args.lang, args.model)
