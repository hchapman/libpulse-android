package com.harrcharr.pulse;

public class Stream extends JNIObject {
	Stream(PulseContext pulse, String name) {
		super(JNINewStream(pulse.getPointer(), name));
	}
	private static native long JNINewStream(long cPtr, String name);
	
	public void setMonitorStream(StreamNode node) {
		setMonitorStream(node.getIndex());
	}
	public native void setMonitorStream(int idx);
	
	public native void setReadCallback(Runnable cb);
	public native void setSuspendedCallback(Runnable cb);
	public native void connectRecord();
}
