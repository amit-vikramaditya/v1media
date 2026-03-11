#include <jni.h>
#include <string>
#include <vector>
#include "whisper.h"

extern "C" JNIEXPORT jlong JNICALL
Java_com_v1media_engine_WhisperEngine_initContext(JNIEnv* env, jobject thiz, jstring model_path) {
    const char* path = env->GetStringUTFChars(model_path, nullptr);
    struct whisper_context_params params = whisper_context_default_params();
    
    struct whisper_context* ctx = whisper_init_from_file_with_params(path, params);
    
    env->ReleaseStringUTFChars(model_path, path);
    return reinterpret_cast<jlong>(ctx);
}

extern "C" JNIEXPORT void JNICALL
Java_com_v1media_engine_WhisperEngine_freeContext(JNIEnv* env, jobject thiz, jlong ctx_ptr) {
    struct whisper_context* ctx = reinterpret_cast<struct whisper_context*>(ctx_ptr);
    if (ctx) {
        whisper_free(ctx);
    }
}

extern "C" JNIEXPORT jstring JNICALL
Java_com_v1media_engine_WhisperEngine_transcribe(JNIEnv* env, jobject thiz, jlong ctx_ptr, jfloatArray audio_data) {
    struct whisper_context* ctx = reinterpret_cast<struct whisper_context*>(ctx_ptr);
    if (!ctx) return env->NewStringUTF("Error: Context is null");

    // Convert jfloatArray to std::vector<float>
    jsize len = env->GetArrayLength(audio_data);
    jfloat* body = env->GetFloatArrayElements(audio_data, nullptr);
    std::vector<float> pcmf(body, body + len);
    env->ReleaseFloatArrayElements(audio_data, body, JNI_ABORT);

    struct whisper_full_params params = whisper_full_default_params(WHISPER_SAMPLING_GREEDY);
    params.n_threads = 4;
    params.language = "auto";
    params.print_timestamps = false;

    if (whisper_full(ctx, params, pcmf.data(), pcmf.size()) != 0) {
        return env->NewStringUTF("Error: Transcription failed");
    }

    std::string result = "";
    int n_segments = whisper_full_n_segments(ctx);
    for (int i = 0; i < n_segments; ++i) {
        result += whisper_full_get_segment_text(ctx, i);
    }

    return env->NewStringUTF(result.c_str());
}
