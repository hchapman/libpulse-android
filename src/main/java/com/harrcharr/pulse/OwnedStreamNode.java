/*******************************************************************************
 * Copyright (c) 2012 Harrison Chapman.
 * 
 * This file is part of Reverb.
 * 
 *     Reverb is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 2 of the License, or
 *     (at your option) any later version.
 * 
 *     Reverb is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 * 
 *     You should have received a copy of the GNU General Public License
 *     along with Reverb.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contributors:
 *     Harrison Chapman - initial API and implementation
 ******************************************************************************/
package com.harrcharr.pulse;

import android.util.Log;

public abstract class OwnedStreamNode extends StreamNode {	
	protected int mOwnerModuleIndex;
	protected int mClientIndex;
	protected int mOwnerStreamIndex;
	
	protected boolean mCorked;
	protected boolean mHasVolume;
	protected boolean mVolumeWritable;
	
	protected OwnerStreamNode mOwner;
	
	// More optional parameters
	protected String mAppName;
	
	public OwnedStreamNode(PulseContext pulse, long ptr) {
		super(pulse, ptr);
	}
	
	public String getName() {
		return mName;
	}
	public String getDescriptiveName() {
		return mName;
	}
	
	public String getAppName() {
		return mAppName;
	}

	public boolean isMuted() {
		return false;
	}
	
	public int getOwnerIndex() {
		return mOwnerStreamIndex;
	}
	
	public int getSourceIndex() {
		return mOwner.getSourceIndex();
	}
	
	public Stream getNewStream(String name) {
		final Stream stream = mPulse.newStream(name + " for " + getDescriptiveName());
		stream.setMonitorStream(this);
		
		return stream;
	}
	
	public void connectRecordStream(Stream stream) {
		stream.connectRecord(getSourceIndex());
	}
	
	public void setOwner(OwnerStreamNode n) {
		// Change to OwnerStreamNode when implemented
		mOwner = n;
	}
	
	public void moveNode(OwnerStreamNode n, SuccessCallback cb) {
		mOwner = n;
		moveNodeByIndex(mOwner.getIndex(), cb);
	}
	
	protected abstract void moveNodeByIndex(int index, SuccessCallback cb);
	
	public StreamNode getOwner() {
		return mOwner;
	}
	
	public void update(OwnedStreamNode n) {
		super.update(n);
		
		mOwnerModuleIndex = n.mOwnerModuleIndex;
		mClientIndex = n.mClientIndex;
		mOwnerStreamIndex = n.mOwnerStreamIndex;
		
		mCorked = n.mCorked;
		mHasVolume = n.mHasVolume;
		mVolumeWritable = n.mVolumeWritable;
		
		mAppName = n.mAppName;
	}
	
	public String toString() {
		return mName;
	}
}
