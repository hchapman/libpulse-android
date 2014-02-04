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

import java.util.HashMap;

public class ChannelMap {	
	private static final HashMap<Integer, String> MAP_NAMES = new HashMap<Integer, String>();

	char mChannels;
	int[] mChannelMap;
	
	public ChannelMap(int[] map) {
		mChannelMap = map;
		mChannels = (char)map.length;
	}
	
	public int[] getChannelMap() {
		return mChannelMap;
	}
	public int getNumChannels() {
		return mChannelMap.length;
	}
	
	public String getChannelNameByIndex(int index) {
		return getChannelName(mChannelMap[index]);
	}
	public String getChannelName(int mapping) {
		String name = MAP_NAMES.get(Integer.valueOf(mapping));
		if (name == null) {
			name = JNIPositionPrettyPrintString(mapping);
			MAP_NAMES.put(Integer.valueOf(mapping), name);
		}
		
		return name;
	}
	private static native String JNIPositionPrettyPrintString(int mapping);
}
