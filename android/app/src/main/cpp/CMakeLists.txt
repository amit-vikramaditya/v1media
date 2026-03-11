cmake_minimum_required(VERSION 3.22.1)

project("v1media")

set(WHISPER_DIR "${CMAKE_CURRENT_SOURCE_DIR}/../../../../../../core/whisper.cpp")

add_library(whisper_jni SHARED
        whisper_bridge.cpp
        "${WHISPER_DIR}/whisper.cpp"
        "${WHISPER_DIR}/ggml/src/ggml.c"
        "${WHISPER_DIR}/ggml/src/ggml-alloc.c"
        "${WHISPER_DIR}/ggml/src/ggml-backend.cpp"
        "${WHISPER_DIR}/ggml/src/ggml-quants.c"
)

target_include_directories(whisper_jni PRIVATE
        "${WHISPER_DIR}"
        "${WHISPER_DIR}/ggml/include"
        "${WHISPER_DIR}/ggml/src"
)

target_link_libraries(whisper_jni
        android
        log
)

# Enable ARM NEON for Android performance
if(ANDROID_ABI STREQUAL "arm64-v8a")
    add_definitions(-DGGML_USE_NEON)
endif()
