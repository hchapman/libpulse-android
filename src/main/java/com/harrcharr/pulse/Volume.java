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

public class Volume {
	public static int NORM = 0x10000;
	public static int MUTED = 0;
	
	char mChannels;
	int[] mVolumes;
	
	public Volume(int[] vols) {
		mVolumes = vols;
		mChannels = (char)vols.length;
	}
	
	public int[] getVolumes() {
		return mVolumes;
	}
	public int getNumChannels() {
		return mVolumes.length;
	}
	public int get() {
		return mVolumes[0];
	}
	
	public void setVolume(int channel, int volume) {
		mVolumes[channel] = volume;
	}
	public void setVolume(int volume) {
		for (int i = 0; i < mVolumes.length; i++) {
			mVolumes[i] = volume;
		}
	}
	
	public static double asPercent(int volume, int precision) {
		return ((long)((double)volume * 100.0 * Math.pow(10, precision) / (double)Volume.NORM)) / Math.pow(10, precision);
	}
	public static double asLinear(int volume) {
		return Math.pow(((double)volume / (double)Volume.NORM), 3);
	}
	
	public static double asDecibels(int volume) {
		return (20.0 * Math.log10(asLinear(volume)));
	}
	public static double asDecibels(int volume, int precision) {
		return ((long)(asDecibels(volume) * Math.pow(10, precision))) / Math.pow(10, precision);
	}
//	public Volume(char channels, int[] values) {
//		// init it somehow maybe
//	}
	
	public final native int getMax();
}
