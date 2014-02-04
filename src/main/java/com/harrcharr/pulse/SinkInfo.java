package com.harrcharr.pulse;

public class SinkInfo extends OwnerStreamNode {	
	protected int mMonitorSourceIndex;

	public SinkInfo(PulseContext pulse) {
		this(pulse, 0);
	}
	public SinkInfo(PulseContext pulse, long ptr) {
		super(pulse, ptr);
	}
	
	public void update(long ptr) {
		JNIPopulateStruct(ptr);
	}
	
	public void setMute(boolean mute, SuccessCallback cb) {
		mPulse.setSinkMute(mIndex, mute, cb);
	}
	public void setVolume(Volume volume, SuccessCallback cb) {
		mPulse.setSinkVolume(mIndex, volume, cb);
	}
	
	public int getSourceIndex() {
		return mMonitorSourceIndex;
	}
	
	private final native void JNIPopulateStruct(long pSinkInfo);
}
