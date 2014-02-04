/*
 * stream.c
 *
 *  Created on: May 23, 2012
 *      Author: harrcharr
 */

#include <jni.h>
#include <pulse/pulseaudio.h>

#include "logging.h"
#include "jni_core.h"

extern jclass jcls_stream;

jobject get_stream_cb_globalref(JNIEnv *jenv, jobject stream, jobject ref) {
	return (*jenv)->NewGlobalRef(jenv, ref);
//	LOGD("About to get method id to store our ptr");
//	jclass cls = (*jenv)->GetObjectClass(jenv, ref);
//	LOGD("About to get method id to store our ptr 2");
//	jfieldID mid = (*jenv)->GetMethodID(jenv, cls, "storeGlobal", "(Lcom/harrcharr/pulse/PulseContext;J)V");
//
//	if (mid == NULL)
//		return; // We're in trouble
//
//	(*jenv)->CallVoidMethod(jenv, ref, mid, c, (jlong)global);
//	LOGD("Ptr stored");
//
//	return global;
}

jni_pa_cb_info_t *new_stream_cbinfo(JNIEnv *jenv, jobject jobj, jobject jcb, void *to_free) {
	jni_pa_cb_info_t *cbinfo = (jni_pa_cb_info_t*)malloc(sizeof(jni_pa_cb_info_t));
	if (jcb != NULL) {
		cbinfo->cb_runnable = get_stream_cb_globalref(jenv, jobj, jcb);
	} else {
		cbinfo->cb_runnable = NULL;
	}
	cbinfo->m = NULL;
	cbinfo->to_free = to_free;

	return cbinfo;
}

pa_stream *get_stream_ptr(JNIEnv *jenv, jobject jstream) {
	jfieldID fid = (*jenv)->GetFieldID(jenv, jcls_stream, "mPointer", "J");
	if (fid == NULL)
		return;

	return (*jenv)->GetLongField(jenv, jstream, fid);
}

pa_stream_request_cb_t read_cb(pa_stream *stream, size_t nbytes, void* userdata) {
	JNIEnv *env;
	jclass cls;
	jmethodID mid;
	jenv_status_t status;

	const void *data;
	double v;

	jni_pa_cb_info_t *cbdata = (jni_pa_cb_info_t*)userdata;

	if (cbdata->cb_runnable == NULL) {
		return;
	}

	if ((status = get_jnienv(&env)) == JENV_UNSUCCESSFUL) {
		return;
	}

	if (pa_stream_peek(stream, &data, &nbytes) < 0) {
		LOGE("Peek error.");
		return;
	}

	assert(length > 0);
	assert(length % sizeof(float) == 0);

	v = ((const float*) data)[nbytes / sizeof(float) -1];

	pa_stream_drop(stream);

	if (v < 0)
		v = 0;
	if (v > 1)
		v = 1;

	if ((cls = (*env)->GetObjectClass(env, cbdata->cb_runnable))) {
		if ((mid = (*env)->GetMethodID(env, cls, "run", "(D)V"))) {
			// Run the actual Java callback method
			(*env)->CallVoidMethod(env, cbdata->cb_runnable, mid, v);
		}
	}

	detach_jnienv(status);
}

JNIEXPORT jlong JNICALL
Java_com_harrcharr_pulse_Stream_JNINewStream(
		JNIEnv *jenv, jclass jcls, jlong c, jstring server) {
	pa_sample_spec ss;
	pa_stream *stream;

	ss.channels = 1;
	ss.format = PA_SAMPLE_FLOAT32;
	ss.rate = 25;

	pa_proplist *p = pa_proplist_new();
	pa_proplist_sets(p, PA_PROP_APPLICATION_NAME, "Reverb PulseAudio Remote");

	const char *sname;
	sname = (*jenv)->GetStringUTFChars(jenv, server, NULL);
	if (sname == NULL) {
		return NULL; /* OutOfMemoryError already thrown */
	}

	if (!(stream = pa_stream_new_with_proplist((pa_context*)c, sname, &ss, NULL, p))) {
		LOGE("Failed to create new stream");
		stream = NULL;
	}

	(*jenv)->ReleaseStringUTFChars(jenv, server, sname);
	return stream;
}

JNIEXPORT void JNICALL
Java_com_harrcharr_pulse_Stream_setMonitorStream(
		JNIEnv *jenv, jobject jstream, uint32_t index) {
	pa_stream *stream = get_stream_ptr(jenv, jstream);
    pa_stream_set_monitor_stream(stream, index);
}


JNIEXPORT void JNICALL
Java_com_harrcharr_pulse_Stream_connectRecord(
		JNIEnv *jenv, jobject jstream, jstring jdev) {
	pa_buffer_attr attr;
	pa_stream *stream = get_stream_ptr(jenv, jstream);

	memset(&attr, 0, sizeof(attr));
	attr.fragsize = sizeof(float);
	attr.maxlength = (uint32_t) -1;

    const char *dev;
    dev = (*jenv)->GetStringUTFChars(jenv, jdev, NULL);
    if (dev == NULL) {
        return; /* OutOfMemoryError already thrown */
    }

	if (pa_stream_connect_record(stream, dev, &attr,
			(pa_stream_flags_t) (PA_STREAM_DONT_INHIBIT_AUTO_SUSPEND|
					PA_STREAM_DONT_MOVE|PA_STREAM_PEAK_DETECT|
					PA_STREAM_ADJUST_LATENCY)) < 0) {
		LOGE("Failed to connect to stream");
		// Throw an exception to java
	}

    (*jenv)->ReleaseStringUTFChars(jenv, jdev, dev);
}

JNIEXPORT void JNICALL
Java_com_harrcharr_pulse_Stream_disconnect(
		JNIEnv *jenv, jobject jstream) {
	pa_stream *stream = get_stream_ptr(jenv, jstream);

	if (pa_stream_disconnect(stream) < 0) {
		LOGE("Failed to disconnect from stream");
		// Throw an exception to java
	}

	pa_stream_unref(stream);
}

JNIEXPORT void JNICALL
Java_com_harrcharr_pulse_Stream_setReadCallback(
		JNIEnv *jenv, jobject jstream, jobject jcb) {
	pa_stream *stream = get_stream_ptr(jenv, jstream);

	jni_pa_cb_info_t *userdata = new_stream_cbinfo(jenv, jstream, jcb, NULL);
	pa_stream_set_read_callback(stream, read_cb, userdata);
}
