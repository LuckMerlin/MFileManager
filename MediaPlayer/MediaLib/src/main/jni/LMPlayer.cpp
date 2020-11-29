#include <jni.h>
#include <string>
#include "constant.h"
#include "Log.h"
#include <android/log.h>
#include <android/native_window_jni.h>"
#include <unistd.h>

extern "C" {
    #include <libavcodec/avcodec.h>
    #include <libavformat/avformat.h>
    #include <libswscale/swscale.h>
    #include <libavutil/imgutils.h>
}



extern "C"
JNIEXPORT jstring JNICALL
Java_com_luckmerlin_player_LMPlayer_getJniVersion(JNIEnv *env, jobject thiz) {
    std::string hello = avcodec_configuration();
    return env->NewStringUTF(hello.c_str());
}

extern "C"
JNIEXPORT jlong JNICALL
Java_com_luckmerlin_player_LMPlayer_jniPlayMedia(JNIEnv *env, jobject thiz, jstring playPath,jdouble seek,
                                                 jobject display,jstring playDebug) {
    const char* path = NULL!=playPath?env->GetStringUTFChars(playPath,0):NULL;
    const char* debug = NULL!=playDebug?env->GetStringUTFChars(playDebug,0):NULL;
    if(NULL==path||strlen(path) <= 0){//Path invalid
        LOGE("Media path invalid %c", path);
        return FAIL;
    }
    int result=avformat_network_init();
    if (result != AV_SUCCEED){
        LOGE("Error init player network component %d",result);
        return ERROR;
    }
    AVFormatContext* formatContext = avformat_alloc_context();
    AVDictionary* args=NULL;
    av_dict_set(&args, "timeout","3000000", 0);
    int openResult=avformat_open_input(&formatContext, path, NULL, &args);
    if(openResult!=AV_SUCCEED){
        LOGE("Fail open media file %d",openResult);
        return FAIL;
    }
    int streamFindResult=avformat_find_stream_info(formatContext, NULL);
    if (streamFindResult<0){
        LOGE("Fail find media file stream %d",streamFindResult);
        return FAIL;
    }
    unsigned int streamsCount=formatContext->nb_streams;
    AVCodecParameters* videoCodecParameters=NULL;
    AVCodec* videoCodec=NULL;
    for (int i = 0; i < streamsCount; ++i) {
        AVStream* stream=formatContext->streams[i];
        if(NULL!=stream){
            if(stream->codecpar->codec_type==AVMEDIA_TYPE_VIDEO){//Video stream
                videoCodecParameters=stream->codecpar;
                videoCodec=NULL!=videoCodecParameters?avcodec_find_decoder(videoCodecParameters->codec_id):NULL;
                LOGD("Find video stream in media file.");
            }else if(stream->codecpar->codec_type==AVMEDIA_TYPE_AUDIO) {//Audio stream
                LOGD("Find audio stream in media file.");
            }
        }
    }
    if (NULL!=videoCodec&&NULL!=videoCodecParameters){
        AVCodecContext* codecContext=avcodec_alloc_context3(videoCodec);
        int videoContextReturn=avcodec_parameters_to_context(codecContext,videoCodecParameters);
        int videoStreamCodexOpenResult=avcodec_open2(codecContext,videoCodec,NULL);
        if(videoStreamCodexOpenResult!=AV_SUCCEED){
            LOGE("Fail open file video stream codex.");
            return FAIL;
        }
        AVPacket* packet=av_packet_alloc();
        int videoFrameReadResult=AV_SUCCEED;
        int videoFrameScaleReturn=AV_SUCCEED;
        int frameDecodeReturn=AV_SUCCEED;
        SwsContext* swsContext=sws_getContext(codecContext->width,codecContext->height,codecContext->pix_fmt,
                                              codecContext->width,codecContext->height,AV_PIX_FMT_RGBA,SWS_BILINEAR,0,0,0);
        ANativeWindow* displayWindow=ANativeWindow_fromSurface(env,display);
        ANativeWindow_setBuffersGeometry(displayWindow,codecContext->width,codecContext->height,WINDOW_FORMAT_RGBA_8888);
        ANativeWindow_Buffer displayBuffer;
        while((videoFrameReadResult=av_read_frame(formatContext,packet))>=0){
            avcodec_send_packet(codecContext, packet);
            AVFrame * videoFrame=av_frame_alloc();
            frameDecodeReturn=avcodec_receive_frame(codecContext,videoFrame);
            if (frameDecodeReturn == AVERROR(EAGAIN)){
                continue;
            }else if (frameDecodeReturn<0){//Decode over
                break;
            }
            uint8_t* videoFrameImageData[4];
            int videoFrameImageLineSize[4];
            av_image_alloc(videoFrameImageData,videoFrameImageLineSize,codecContext->width,
                    codecContext->height,AV_PIX_FMT_RGBA,1);
            videoFrameScaleReturn=sws_scale(swsContext,videoFrame->data,videoFrame->linesize,0,
                    videoFrame->height,videoFrameImageData,videoFrameImageLineSize);
            ANativeWindow_lock(displayWindow,&displayBuffer,NULL);
            uint8_t *firstWindow= static_cast<uint8_t *>(displayBuffer.bits);
            uint8_t *srcData=videoFrameImageData[0];
            int destStride=displayBuffer.stride*4;
            int srcLineSize=videoFrameImageLineSize[0];
            for (int i = 0; i < displayBuffer.height; ++i) {
                memcpy(firstWindow+i*destStride,srcData+i*srcLineSize,destStride);
            }
            ANativeWindow_unlockAndPost(displayWindow);
//            usleep(1000*16);
            av_frame_free(&videoFrame);
        }

    }
//    av_frame_free(&pFrameYUV);
//    av_frame_free(&pFrame);
//    avcodec_close(pCodecCtx);
//    avformat_close_input(&pFormatCtx);
    env->ReleaseStringUTFChars(playPath, path);
    env->ReleaseStringUTFChars(playDebug, debug);
    return SUCCEED;
}

extern "C"
JNIEXPORT jboolean JNICALL
Java_com_luckmerlin_player_LMPlayer_jniIsCreate(JNIEnv *env, jobject thiz) {
    // TODO: implement jniIsCreate()
}

extern "C"
JNIEXPORT jboolean JNICALL
Java_com_luckmerlin_player_LMPlayer_jniCreate(JNIEnv *env, jobject thiz, jstring debug) {
    // TODO: implement jniCreate()
}

extern "C"
JNIEXPORT jboolean JNICALL
Java_com_luckmerlin_player_LMPlayer_jniDestroy(JNIEnv *env, jobject thiz, jstring debug) {
    // TODO: implement jniDestroy()
}