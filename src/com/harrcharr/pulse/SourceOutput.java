package com.harrcharr.pulse;

public class SourceOutput extends OwnedStreamNode {
	public SourceOutput(PulseContext pulse, long ptr) {
		super(pulse, ptr);
	}
	
	public void update(long ptr) {
		JNIPopulateStruct(ptr);
	}
	
	public void setMute(boolean mute, SuccessCallback cb) {
		mPulse.setSourceOutputMute(mIndex, mute, cb);
	}
	public void setVolume(Volume volume, SuccessCallback cb) {
		mPulse.setSourceOutputVolume(mIndex, volume, cb);
	}
	
	public void update(SourceOutput n) {
		super.update(n);
	}
	
	public void connectRecordStream(Stream s) {
		
	}
	
	protected void moveNodeByIndex(int index, SuccessCallback cb) {
		mPulse.moveSourceOutput(mIndex, index, cb);
	}

	private final native void JNIPopulateStruct(long pSinkInputInfo);
}
