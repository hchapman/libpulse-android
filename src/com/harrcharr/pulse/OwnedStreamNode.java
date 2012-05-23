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
	
	// More optional parameters
	protected String mAppName;
	
	public OwnedStreamNode(PulseContext pulse, long ptr) {
		super(pulse, ptr);
	}
	
	public String getName() {
		return mName;
	}
	public String getDescriptiveName() {
		if (mAppName != null) 
			return mAppName + " - " + mName;
		return mName;
	}
	
	public String getAppName() {
		return mAppName;
	}

	public boolean isMuted() {
		return false;
	}
	
	public void setMute(boolean mute, SuccessCallback cb) {
		mPulse.setSinkInputMute(mIndex, mute, cb);
	}
	public void setVolume(Volume volume, SuccessCallback cb) {
		mPulse.setSinkInputVolume(mIndex, volume, cb);
	}
	
	public String toString() {
		return mName;
	}
}
