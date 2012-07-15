package com.harrcharr.pulse;

public class Stream extends JNIObject {
	Stream(PulseContext pulse, String name) {
		super(JNINewStream(pulse.getPointer(), name));
	}
	private static native long JNINewStream(long cPtr, String name);
	
	public void setMonitorStream(OwnedStreamNode node) {
		setMonitorStream(node.getIndex());
	}
	public native void setMonitorStream(int idx);
	
	public native void setReadCallback(ReadCallback cb);
	public native void setSuspendedCallback(Runnable cb);
	
	public void connectRecord(StreamNode node){
		connectRecord(node.getSourceIndex());
	}
	public void connectRecord(int idx) {
		connectRecord(""+idx);
	}
	public native void connectRecord(String dev);
	public native void disconnect();
	
	public static interface ReadCallback {
		public void run(double vol);
	}
}
