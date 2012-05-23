/*
 * stream.c
 *
 *  Created on: May 23, 2012
 *      Author: harrcharr
 */

#import <jni.h>
#import <pulse/pulseaudio.h>

JNIEXPORT jlong JNICALL
Java_com_harrcharr_pulse_PulseContext_JNINewStream(
		JNIEnv *jenv, jclass jcls, pa_context *c, jstring jstr) {
	pa_sample_spec ss;
	pa_stream *stream;

    ss.channels = 1;
    ss.format = PA_SAMPLE_FLOAT32;
    ss.rate = 25;

    const char *sname;
    sname = (*jenv)->GetStringUTFChars(jenv, server, NULL);
    if (sname == NULL) {
        return NULL; /* OutOfMemoryError already thrown */
    }

	if (!(stream = pa_stream_new(c, sname, &ss, NULL))) {
		LOGE("Failed to create new stream");
		stream = NULL;
	}

    (*jenv)->ReleaseStringUTFChars(jenv, server, sname);
	return stream;
}
