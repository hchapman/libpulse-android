package com.harrcharr.pulse;

public abstract class SourceInfoCallback extends InfoCallback<SourceInfo> {	
	@Override
	public final void run(long ptr) {
		run(new SourceInfo(mPulse, ptr));
	}

	public abstract void run(final SourceInfo node);
}
 