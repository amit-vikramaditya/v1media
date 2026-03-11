import subprocess
import time
import os
import json

def run_benchmark():
    # Paths
    project_root = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
    whisper_cli = os.path.join(project_root, "core/whisper.cpp/build/bin/whisper-cli")
    model_path = os.path.join(project_root, "models/ggml-base.bin")
    sample_path = os.path.join(project_root, "tests/samples/benchmark_sample.wav")
    
    if not os.path.exists(whisper_cli):
        print(f"Error: whisper-cli not found at {whisper_cli}. Ensure it is compiled.")
        return

    print(f"--- Benchmark: Local Transcription ---")
    print(f"Model: {os.path.basename(model_path)}")
    print(f"Audio Sample: {os.path.basename(sample_path)} (30 seconds)")
    
    start_time = time.time()
    
    # Run whisper-cli
    cmd = [
        whisper_cli,
        "-m", model_path,
        "-f", sample_path,
        "-nt", # no timestamps for raw speed test
        "-t", "4" # use 4 threads (standard for most SoCs)
    ]
    
    try:
        result = subprocess.run(cmd, capture_output=True, text=True, check=True)
        end_time = time.time()
        
        duration = 30.08  # Audio duration in seconds
        processing_time = end_time - start_time
        rtf = processing_time / duration
        
        print(f"\nTranscription Output:\n{result.stdout.strip()[:200]}...")
        print(f"\n--- Results ---")
        print(f"Total Processing Time: {processing_time:.2f} seconds")
        print(f"Real-Time Factor (RTF): {rtf:.4f}x")
        
        if rtf < 1.0:
            print("Status: Faster than real-time (Excellent)")
        elif rtf < 2.0:
            print("Status: Near real-time (Good)")
        else:
            print("Status: Slower than real-time (Heavy)")
            
    except subprocess.CalledProcessError as e:
        print(f"Error running benchmark: {e.stderr}")

if __name__ == "__main__":
    run_benchmark()
