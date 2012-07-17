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
	
	public void update(SinkInput n) {
		super.update(n);
	}

	protected void moveNodeByIndex(int index, SuccessCallback cb) {
		mPulse.moveSinkInput(mIndex, index, cb);
	}

	private final native void JNIPopulateStruct(long pSinkInputInfo);
}
