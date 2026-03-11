# v1media: The Local-First Media Pipeline

## Overview
A lean, open-source utility for high-speed media conversion and AI transcription. Everything runs locally on the device (SoC) using FFmpeg and Whisper.cpp.

## Phase 1 Goals
- [ ] **Video-to-Audio:** High-speed extraction (Stream Copy) from MP4/MKV/MOV.
- [ ] **Audio-to-Audio:** Transcoding between MP3, M4A, and WAV.
- [ ] **Transcription:** Local Whisper.cpp integration (Tiny/Base/Small models).
- [ ] **Verbatim Mode:** Forced language transcription (No auto-translation).

## Tech Stack
- **Engine A:** [FFmpeg-Kit (LTS)](https://github.com/tanersener/ffmpeg-kit)
- **Engine B:** [Whisper.cpp](https://github.com/ggerganov/whisper.cpp) (Metal/NNAPI optimized)
- **Platform:** Native Mobile (Android/iOS)

## Directory Structure
- `android/`: Native Android project (Kotlin/Compose).
- `ios/`: Native iOS project (Swift/SwiftUI).
- `core/`: Shared C++/JNI logic for engine bridges.
- `models/`: Placeholder for downloading/testing Whisper GGUF models.
- `docs/`: Technical specifications and benchmarking results.
- `scripts/`: Python/Bash utilities for model conversion and testing.

## License
GPL-3.0
