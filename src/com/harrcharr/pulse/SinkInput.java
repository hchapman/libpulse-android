package com.harrcharr.pulse;

public class SinkInput extends OwnedStreamNode {
	public SinkInput(PulseContext pulse, long ptr) {
		super(pulse, ptr);
	}
	
	public void update(long ptr) {
		JNIPopulateStruct(ptr);
	}
	
	public void setMute(boolean mute, SuccessCallback cb) {
		mPulse.setSinkInputMute(mIndex, mute, cb);
	}
	public void setVolume(Volume volume, SuccessCallback cb) {
		mPulse.setSinkInputVolume(mIndex, volume, cb);
	}

	private final native void JNIPopulateStruct(long pSinkInputInfo);
}
