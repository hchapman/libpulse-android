package com.harrcharr.pulse;

public abstract class SinkInfoCallback extends InfoCallback<SinkInfo> {	
	@Override
	public final void run(long ptr) {
		run(new SinkInfo(mPulse, ptr));
	}

	public abstract void run(final SinkInfo node);
}
