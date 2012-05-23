package com.harrcharr.pulse;

public class SourceOutput extends OwnedStreamNode {
	public SourceOutput(PulseContext pulse, long ptr) {
		super(pulse, ptr);
	}
	
	public void update(long ptr) {
		JNIPopulateStruct(ptr);
	}

	private final native void JNIPopulateStruct(long pSinkInputInfo);
}
