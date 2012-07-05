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

public abstract class OwnerStreamNode extends StreamNode {
	protected String mName;
	protected String mDescription;
	
	public OwnerStreamNode(PulseContext pulse) {
		this(pulse, 0);
	}
	public OwnerStreamNode(PulseContext pulse, long ptr) {
		super(pulse, ptr);
	}

	public String getDescription() {
		return mDescription;
	}
	
	public String toString() {
		return mName + "\n" + mDescription;
	}
	
	public Stream getNewStream(String name) {
		final Stream stream = mPulse.newStream(name);
		
		return stream;
	}
	
	public void connectRecordStream(Stream stream) {
		stream.connectRecord(getSourceIndex());
	}
	
	public void update(OwnerStreamNode n) {
		super.update(n);
		
		mName = n.mName;
		mDescription = n.mDescription;
	}
	
	@Override
	public String getDescriptiveName() {
		return getDescription();
	}
}
