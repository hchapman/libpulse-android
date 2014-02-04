/*******************************************************************************
*Copyright (c) 2012 Harrison Chapman.
*
*This file is part of Reverb.
*
*    Reverb is free software: you can redistribute it and/or modify
*    it under the terms of the GNU General Public License as published by
*    the Free Software Foundation, either version 2 of the License, or
*    (at your option) any later version.
*
*    Reverb is distributed in the hope that it will be useful,
*    but WITHOUT ANY WARRANTY; without even the implied warranty of
*    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
*    GNU General Public License for more details.
*
*    You should have received a copy of the GNU General Public License
*    along with Reverb.  If not, see <http://www.gnu.org/licenses/>.
*
*Contributors:
*    Harrison Chapman - initial API and implementation
*******************************************************************************/

#include <jni.h>
#include <string.h>
#include <unistd.h>

#include <sys/socket.h>
#include <errno.h>

#include <pulse/pulseaudio.h>

#include "jni_core.h"
#include "pulse.h"
#include "logging.h"

extern JavaVM *g_vm;

extern jclass jcls_volume;
extern jclass jcls_channel_map;

inline void set_field_string(JNIEnv *jenv,
		jobject jobj, jclass cls,
		const char *fname, const char *data) {
	jfieldID fid = (*jenv)->GetFieldID(jenv, cls, fname, "Ljava/lang/String;");
	if (fid == NULL) {
		LOGE("Unable to find field %s", fname);
		return; // Field not found
	}
	jstring jstr = (*jenv)->NewStringUTF(jenv, data);
	if (jstr == NULL) {
		return; // OOM
	}
	(*jenv)->SetObjectField(jenv, jobj, fid, jstr);
}

inline void set_field_int(JNIEnv *jenv,
		jobject jobj, jclass cls,
		const char *fname, int data) {
	jfieldID fid = (*jenv)->GetFieldID(jenv, cls, fname, "I");
	if (fid == NULL) {
		LOGE("Unable to find field %s", fname);
		return; // Field not found
	}

	(*jenv)->SetIntField(jenv, jobj, fid, (jint)data);
}

inline void set_field_boolean(JNIEnv *jenv,
		jobject jobj, jclass cls,
		const char *fname, int data) {
	jfieldID fid = (*jenv)->GetFieldID(jenv, cls, fname, "Z");
	if (fid == NULL) {
		LOGE("Unable to find field %s", fname);
		return; // Field not found
	}

	(*jenv)->SetBooleanField(jenv, jobj, fid, (jboolean)data);
}

inline void set_field_long(JNIEnv *jenv,
		jobject jobj, jclass cls,
		const char *fname, long data) {
	jfieldID fid = (*jenv)->GetFieldID(jenv, cls, fname, "J");
	if (fid == NULL) {
		LOGE("Unable to find field %s", fname);
		return; // Field not found
	}

	(*jenv)->SetLongField(jenv, jobj, fid, (jlong)data);
}

inline void set_field_volume(JNIEnv *jenv,
		jobject jobj, jclass cls,
		const char *fname, pa_cvolume* v) {
	jfieldID fid = (*jenv)->GetFieldID(jenv, cls, fname,
			"Lcom/harrcharr/pulse/Volume;");
	if (fid == NULL) {
		LOGE("Unable to find field %s", fname);
		return; // Field not found
	}

	jintArray vols;
	vols = (*jenv)->NewIntArray(jenv, v->channels);
	if (vols == NULL) {
		return; /* oom */
	}
	(*jenv)->SetIntArrayRegion(
			jenv, vols, 0, v->channels, v->values);

	jobject data;
	jmethodID init = (*jenv)->GetMethodID(jenv, jcls_volume,
			"<init>", "([I)V");
	data = (*jenv)->NewObject(jenv, jcls_volume,
			init, vols);

	(*jenv)->SetObjectField(jenv, jobj, fid, data);
}

inline void set_field_channel_map(JNIEnv *jenv,
		jobject jobj, jclass cls,
		const char *fname, pa_channel_map* cm) {
	jfieldID fid = (*jenv)->GetFieldID(jenv, cls, fname,
			"Lcom/harrcharr/pulse/ChannelMap;");
	if (fid == NULL) {
		LOGE("Unable to find field %s", fname);
		return; // Field not found
	}

	jintArray map;
	map = (*jenv)->NewIntArray(jenv, cm->channels);
	if (map == NULL) {
		return; /* oom */
	}
	(*jenv)->SetIntArrayRegion(
			jenv, map, 0, cm->channels, cm->map);

	jobject data;
	jmethodID init = (*jenv)->GetMethodID(jenv, jcls_channel_map,
			"<init>", "([I)V");
	data = (*jenv)->NewObject(jenv, jcls_channel_map,
			init, map);

	(*jenv)->SetObjectField(jenv, jobj, fid, data);
}

//void set_field_proplist(JNIEnv *jenv,
//		jobject jobj, jclass cls,
//		char *fname, pa_proplist* p) {
//	jfieldID fid = (*jenv)->GetFieldID(jenv, cls, fname,
//			"Ljava/nio/ByteBuffer;");
//	if (fid == NULL) {
//		LOGE("Unable to find field %s", fname);
//		return; // Field not found
//	}
//
//	jobject bb = (*jenv)->NewDirectByteBuffer(jenv, p, sizeof(pa_proplist));
//	(*jenv)->SetObjectField(jenv, jobj, fid, bb);
//}

JNIEXPORT void JNICALL
Java_com_harrcharr_pulse_SinkInfo_JNIPopulateStruct(
		JNIEnv *jenv, jobject jobj, jlong i_ptr) {
	pa_sink_info *i = (pa_sink_info*)i_ptr;

	jclass cls = (*jenv)->GetObjectClass(jenv, jobj);
	set_field_string(jenv, jobj, cls, "mName", i->name);
	set_field_string(jenv, jobj, cls, "mDescription", i->description);

	set_field_int(jenv, jobj, cls, "mIndex", i->index);
	set_field_int(jenv, jobj, cls, "mMonitorSourceIndex", i->monitor_source);

	set_field_volume(jenv, jobj, cls, "mVolume", &(i->volume));
	set_field_channel_map(jenv, jobj, cls, "mChannelMap", &(i->channel_map));

	set_field_boolean(jenv, jobj, cls, "mMuted", i->mute);
}

JNIEXPORT void JNICALL
Java_com_harrcharr_pulse_SourceInfo_JNIPopulateStruct(
		JNIEnv *jenv, jobject jobj, jlong i_ptr) {
	pa_sink_info *i = (pa_sink_info*)i_ptr;

	jclass cls = (*jenv)->GetObjectClass(jenv, jobj);
	set_field_string(jenv, jobj, cls, "mName", i->name);
	set_field_string(jenv, jobj, cls, "mDescription", i->description);

	set_field_int(jenv, jobj, cls, "mIndex", i->index);

	set_field_volume(jenv, jobj, cls, "mVolume", &(i->volume));
	set_field_channel_map(jenv, jobj, cls, "mChannelMap", &(i->channel_map));

	set_field_boolean(jenv, jobj, cls, "mMuted", i->mute);
}

JNIEXPORT void JNICALL
Java_com_harrcharr_pulse_SinkInput_JNIPopulateStruct(
		JNIEnv *jenv, jobject jobj, jlong i_ptr) {
	pa_sink_input_info *i = (pa_sink_input_info*)i_ptr;

	jclass cls = (*jenv)->GetObjectClass(jenv, jobj);
	set_field_string(jenv, jobj, cls, "mName", i->name);

	set_field_int(jenv, jobj, cls, "mIndex", i->index);
	set_field_int(jenv, jobj, cls, "mOwnerModuleIndex", i->owner_module);
	set_field_int(jenv, jobj, cls, "mClientIndex", i->client);
	set_field_int(jenv, jobj, cls, "mOwnerStreamIndex", i->sink);

	set_field_volume(jenv, jobj, cls, "mVolume", &(i->volume));
	set_field_channel_map(jenv, jobj, cls, "mChannelMap", &(i->channel_map));

	set_field_boolean(jenv, jobj, cls, "mMuted", i->mute);
	set_field_boolean(jenv, jobj, cls, "mCorked", i->corked);
	set_field_boolean(jenv, jobj, cls, "mHasVolume", i->has_volume);
	set_field_boolean(jenv, jobj, cls, "mVolumeWritable", i->volume_writable);

	// Set important proplist values, should they exist.
	pa_proplist *p = i->proplist;
	if(pa_proplist_contains(p, PA_PROP_APPLICATION_NAME))
		set_field_string(jenv, jobj, cls, "mAppName",
				pa_proplist_gets(p, PA_PROP_APPLICATION_NAME));
}


JNIEXPORT void JNICALL
Java_com_harrcharr_pulse_SourceOutput_JNIPopulateStruct(
		JNIEnv *jenv, jobject jobj, jlong i_ptr) {
	pa_source_output_info *i = (pa_source_output_info*)i_ptr;

	jclass cls = (*jenv)->GetObjectClass(jenv, jobj);
	set_field_string(jenv, jobj, cls, "mName", i->name);

	set_field_int(jenv, jobj, cls, "mIndex", i->index);
	set_field_int(jenv, jobj, cls, "mOwnerModuleIndex", i->owner_module);
	set_field_int(jenv, jobj, cls, "mClientIndex", i->client);
	set_field_int(jenv, jobj, cls, "mOwnerStreamIndex", i->source);

	set_field_volume(jenv, jobj, cls, "mVolume", &(i->volume));
	set_field_channel_map(jenv, jobj, cls, "mChannelMap", &(i->channel_map));

	set_field_boolean(jenv, jobj, cls, "mMuted", i->mute);
	set_field_boolean(jenv, jobj, cls, "mCorked", i->corked);
	set_field_boolean(jenv, jobj, cls, "mHasVolume", i->has_volume);
	set_field_boolean(jenv, jobj, cls, "mVolumeWritable", i->volume_writable);

	// Set important proplist values, should they exist.
	pa_proplist *p = i->proplist;
	if(pa_proplist_contains(p, PA_PROP_APPLICATION_NAME))
		set_field_string(jenv, jobj, cls, "mAppName",
				pa_proplist_gets(p, PA_PROP_APPLICATION_NAME));
//	LOGD(pa_proplist_to_string(i->proplist));
//	set_field_proplist(jenv, jobj, cls, "mProplist", i->proplist);
}

JNIEXPORT void JNICALL
Java_com_harrcharr_pulse_ClientInfo_JNIPopulateStruct(
		JNIEnv *jenv, jobject jobj, jlong i_ptr) {
	pa_client_info *i = (pa_client_info*)i_ptr;
	jstring jstr;
	jfieldID fid;

	LOGI("Name of client, %s", i->name);

	jclass cls = (*jenv)->GetObjectClass(jenv, jobj);
	set_field_string(jenv, jobj, cls, "sName", i->name);
}

JNIEXPORT jstring JNICALL
Java_com_harrcharr_pulse_PulseNode_getProps(
		JNIEnv *jenv, jobject jobj, jstring key) {
	pa_proplist *p = (pa_proplist *)get_long_field(jenv, jobj, "mProplist");

//	LOGD(pa_proplist_gets(p, key));
	return key;
}

JNIEXPORT jstring JNICALL
Java_com_harrcharr_pulse_ChannelMap_JNIPositionPrettyPrintString(
		JNIEnv *jenv, jclass jcls, jint mapping) {
	jstring jstr = (*jenv)->NewStringUTF(jenv,
			pa_channel_position_to_pretty_string(mapping));
	if (jstr == NULL) {
		return; // OOM
	}
	return jstr;
}

JNIEXPORT jlong JNICALL
Java_com_harrcharr_pulse_Mainloop_JNINew(
		JNIEnv *jenv, jclass jcls) {
	pa_threaded_mainloop *m = pa_threaded_mainloop_new();

	return m;
}

JNIEXPORT void JNICALL
Java_com_harrcharr_pulse_Mainloop_JNIStart(
		JNIEnv *jenv, jclass jcls, jlong ptr_m) {
	pa_threaded_mainloop_start((pa_threaded_mainloop*)ptr_m);
}



