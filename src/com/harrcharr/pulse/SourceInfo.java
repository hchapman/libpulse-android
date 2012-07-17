package com.harrcharr.pulse;

public class SourceInfo extends OwnerStreamNode {	
	public SourceInfo(PulseContext pulse) {
		this(pulse, 0);
	}
	public SourceInfo(PulseContext pulse, long ptr) {
		super(pulse, ptr);
	}
	
	public void update(long ptr) {
		JNIPopulateStruct(ptr);
	}
	
	public void setMute(boolean mute, SuccessCallback cb) {
		mPulse.setSourceMute(mIndex, mute, cb);
	}
	public void setVolume(Volume volume, SuccessCallback cb) {
		mPulse.setSourceVolume(mIndex, volume, cb);
	}
	
	public int getSourceIndex() {
		return mIndex;
	}
	
	private final native void JNIPopulateStruct(long pSinkInfo);
}
