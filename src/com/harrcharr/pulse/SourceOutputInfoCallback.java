package com.harrcharr.pulse;

public abstract class SourceOutputInfoCallback extends InfoCallback<SourceOutput> {	
	@Override
	public final void run(long ptr) {
		run(new SourceOutput(mPulse, ptr));
	}

	public abstract void run(final SourceOutput node);
}
