import subprocess
import time
import os
import psutil
import json

def get_peak_memory(pid):
    try:
        process = psutil.Process(pid)
        # Include all memory (RSS)
        return process.memory_info().rss / (1024 * 1024) # MB
    except:
        return 0

def run_bench(model_name, input_file):
    script_dir = os.path.dirname(os.path.abspath(__file__))
    project_root = os.path.dirname(script_dir)
    whisper_cli = os.path.join(project_root, "core/whisper.cpp/build/bin/whisper-cli")
    model_path = os.path.join(project_root, f"models/ggml-{model_name}.bin")
    
    if not os.path.exists(model_path):
        print(f"Model not found: {model_path}")
        return None

    print(f"\n--- Benchmarking Model: {model_name} ---")
    start_time = time.time()
    
    # Run whisper-cli
    cmd = [whisper_cli, "-m", model_path, "-f", input_file, "-nt", "-t", "4"]
    
    process = subprocess.Popen(cmd, stdout=subprocess.PIPE, stderr=subprocess.PIPE, text=True)
    
    peak_mem = 0
    # Higher frequency sampling for memory
    while process.poll() is None:
        mem = get_peak_memory(process.pid)
        if mem > peak_mem:
            peak_mem = mem
        time.sleep(0.01) # 100Hz sampling
    
    stdout, stderr = process.communicate()
    end_time = time.time()
    
    if process.returncode != 0:
        print(f"Error running whisper-cli for {model_name}: {stderr}")
        return None
        
    duration = 30.08 
    proc_time = end_time - start_time
    rtf = proc_time / duration
    
    return {
        "model": model_name,
        "time": round(proc_time, 2),
        "rtf": round(rtf, 4),
        "peak_mem_mb": round(peak_mem, 2),
        "snippet": stdout.strip()[:100]
    }

if __name__ == "__main__":
    script_dir = os.path.dirname(os.path.abspath(__file__))
    project_root = os.path.dirname(script_dir)
    sample = os.path.join(project_root, "tests/samples/benchmark_sample.wav")
    
    results = []
    for m in ["base", "small"]:
        res = run_bench(m, sample)
        if res:
            results.append(res)
            print(f"Time: {res['time']}s | RTF: {res['rtf']}x | Peak RAM: {res['peak_mem_mb']}MB")
    
    docs_dir = os.path.join(project_root, "docs")
    os.makedirs(docs_dir, exist_ok=True)
    with open(os.path.join(docs_dir, "benchmarks_2026.json"), "w") as f:
        json.dump(results, f, indent=4)
    print(f"\nBenchmarks saved to {os.path.join(docs_dir, 'benchmarks_2026.json')}")
