#include <jni.h>
#include <string>
#include <stdlib.h>
#include <stdio.h>
#include <err.h>
#include <unistd.h>
#include <fcntl.h>
#include <android/log.h>
#include <jni.h>
#include <vector>

#include <LzmaEnc.h>

extern "C"
jstring
Java_timeshifter_dtcq_yunlong_dtcqtimeshifter_PatchUtils_beginPrepareAPK(
        JNIEnv* env,
        jobject /* this */) {
    std::string hello = "刀塔传奇时光倒流";
    return env->NewStringUTF(hello.c_str());
}
char* jstringTostring(JNIEnv* env, jstring jstr)
{
    char* rtn = NULL;
    jclass clsstring = env->FindClass("java/lang/String");
    jstring strencode = env->NewStringUTF("utf-8");
    jmethodID mid = env->GetMethodID(clsstring, "getBytes", "(Ljava/lang/String;)[B");
    jbyteArray barr= (jbyteArray)env->CallObjectMethod(jstr, mid, strencode);
    jsize alen = env->GetArrayLength(barr);
    jbyte* ba = env->GetByteArrayElements(barr, JNI_FALSE);
    if (alen > 0)
    {
        rtn = (char*)malloc(alen + 1);

        memcpy(rtn, ba, alen);
        rtn[alen] = 0;
    }
    env->ReleaseByteArrayElements(barr, ba, 0);
    return rtn;
}

jstring stoJstring(JNIEnv* env, const char* pat)
{
    jclass strClass = env->FindClass("Ljava/lang/String;");
    jmethodID ctorID = env->GetMethodID(strClass, "<init>", "([BLjava/lang/String;)V");
    jbyteArray bytes = env->NewByteArray(strlen(pat));
    env->SetByteArrayRegion(bytes, 0, strlen(pat), (jbyte*)pat);
    jstring encoding = env->NewStringUTF("utf-8");
    return (jstring)env->NewObject(strClass, ctorID, bytes, encoding);
}

SRes OnProgress(void *p, UInt64 inSize, UInt64 outSize)
{
    // Update progress bar.
    return SZ_OK;
}
extern "C"
{
static ICompressProgress g_ProgressCallback = { &OnProgress };
static void * AllocForLzma(void *p, size_t size) { return malloc(size); }
static void FreeForLzma(void *p, void *address) { free(address); }
static ISzAlloc SzAllocForLzma = { &AllocForLzma, &FreeForLzma };

void Compress2(
        std::vector<unsigned char> &outBuf,
        const std::vector<unsigned char> &inBuf)
{
    unsigned propsSize = LZMA_PROPS_SIZE;
    unsigned destLen = inBuf.size() + inBuf.size() / 3 + 128;
    outBuf.resize(propsSize + destLen);

    CLzmaEncProps props;
    LzmaEncProps_Init(&props);
    props.dictSize = 1 << 16; // 64 KB
    props.writeEndMark = 1; // 0 or 1

    //int res = LzmaEncode(&outBuf[LZMA_PROPS_SIZE], &destLen, &inBuf[0], inBuf.size(), &props, &outBuf[0], &propsSize, props.writeEndMark, &g_ProgressCallback, &SzAllocForLzma, &SzAllocForLzma);
    //assert(res == SZ_OK && propsSize == LZMA_PROPS_SIZE);

    outBuf.resize(propsSize + destLen);
}
}

void duplicatefile( char* dstfile ,char* srcfile)
{
    FILE *srcFile = fopen(srcfile,"rb");
    if(srcFile == NULL) exit(-1);
    FILE *dstFile = fopen(dstfile,"wb");
    if(dstFile == NULL) exit(-1);
    char buffer[10000];
    for(;;){
        size_t readlen = 0;
        readlen = fread(buffer,1,sizeof(buffer),srcFile);
        //1代表从srcFile源文件按1字节1字节读
        if(readlen == 0) break;
        size_t writelen = 0;
        while(writelen < readlen){
            writelen += fwrite(buffer + writelen,1,(readlen - writelen),dstFile); //1字节1字节写入dstFile文件，buffer + writelen表示从上一次处接着写
        }
    }
    fclose(srcFile);
    fclose(dstFile);
}

int applypatch(int argc, char * argv[]) {
    FILE * f, *cpf, *dpf, *epf;
    int cbz2err, dbz2err, ebz2err;
    int fd;
    ssize_t oldsize, newsize;

    if (argc != 4)
        errx(1, "usage: %s oldfile newfile patchfile\n", argv[0]);

    duplicatefile(argv[2], argv[1]); //2->1
    //duplicatefile(argv[2], argv[3]); //2->1

    if ((f = fopen(argv[3], "rb")) == NULL)
        err(1, "fopen(%s)", argv[3]);

    if (fclose(f))
        err(1, "fclose(%s)", argv[3]);

    return 0;
}


extern "C"
JNIEXPORT jint JNICALL Java_timeshifter_dtcq_yunlong_dtcqtimeshifter_PatchUtils_combinePatch(
        JNIEnv* env,
        jobject /* this */
        ,jstring oldapk, jstring newapk, jstring patchzip
)
{
    char * ch[4];
    ch[0] = "bspatch";
    ch[1] = jstringTostring(env, oldapk);
    ch[2] = jstringTostring(env, newapk);
    ch[3] = jstringTostring(env, patchzip);

    __android_log_print(ANDROID_LOG_INFO, "ApkPatchLibrary", "oldapk = %s ", ch[1]);
    __android_log_print(ANDROID_LOG_INFO, "ApkPatchLibrary", "newapk = %s ", ch[2]);
    __android_log_print(ANDROID_LOG_INFO, "ApkPatchLibrary", "patchzip = %s ", ch[3]);

    int ret = applypatch(4, ch);

    __android_log_print(ANDROID_LOG_INFO, "ApkPatchLibrary", "applypatch result = %d ", ret);

    std::string hello = "刀塔传奇时光倒流";
    return 0;
}

