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

import java.nio.ByteBuffer;
import java.util.Set;
import java.util.TreeSet;

public class PulseContext extends JNIObject {
	public static enum ContextState {
		UNCONNECTED,
		CONNECTING,
		AUTHORIZING,
		SETTING_NAME,
		READY,
		FAILED,
		TERMINATED;
		
		/*
		 *  Takes a value from C named PA_CONTEXT_[statename], 
		 *  returns Java equivalent. Take care to ensure consistency with
		 *  build version of libpulse headers.
		 */
		public static ContextState fromInt (int cValue) {
			switch(cValue) {
			case 0: return UNCONNECTED;
			case 1: return CONNECTING;
			case 2: return AUTHORIZING;
			case 3: return SETTING_NAME;
			case 4: return READY;
			case 5: return FAILED;
			case 6: return TERMINATED;
			}
			
			return null;
		}
	}
	
	protected Runnable cbStatusChanged;
	protected Mainloop mainloop;
	
	protected Runnable onConnecting;
	protected Runnable onAuthorizing;
	protected Runnable onSettingName;
	protected Runnable onReady;
	protected Runnable onFailure;
	protected Runnable onTerminate;
	
	protected TreeSet<JniCallback> mCallbacks;
	
	private boolean mSubscribed; 
	
	protected long mSubsCbsPointer;
	protected long mStatesCbsPointer;
	
	protected ByteBuffer mCbPtrs;
	
	protected ContextState mStatus;
	
	public PulseContext(Mainloop m) {
		super(JNICreate(m.getPointer()));
		mainloop = m;
		mStatus = ContextState.UNCONNECTED;
		mSubscribed = false;
		
		mCallbacks = new TreeSet<JniCallback>();
	}
	
	public long getMainloopPointer() {
		return mainloop.getPointer();
	}
	
	public long getEventCbsPointer() { return mSubsCbsPointer; }
	public long getStateCbsPointer() { return mStatesCbsPointer; }
	public void setEventCbsPointer(long ptr) { mSubsCbsPointer = ptr; }
	public void setStateCbsPointer(long ptr) { mStatesCbsPointer = ptr; }
	
	public final native void connect(String server)
		throws Exception;
	
	protected void subscribe() {
		if (mSubscribed)
			return;
		
		mSubsCbsPointer = JNISubscribe();
		mSubscribed = true;
	}
	private final native long JNISubscribe();
	
	public void subscribeSinkInput(SubscriptionCallback cb) {
		subscribe();
		JNISubscribeSinkInput(cb);
	}
	private final native void JNISubscribeSinkInput(SubscriptionCallback cb);
	
	public void subscribeSourceOutput(SubscriptionCallback cb) {
		subscribe();
		JNISubscribeSourceOutput(cb);
	}
	private final native void JNISubscribeSourceOutput(SubscriptionCallback cb);

	public Stream newStream(String name) {
		return new Stream(this, name);
	}

	public void subscribeSink(SubscriptionCallback cb) {
		subscribe();
		JNISubscribeSink(cb);
	}
	private final native void JNISubscribeSink(SubscriptionCallback cb);
	
	public void subscribeSource(SubscriptionCallback cb) {
		subscribe();
		JNISubscribeSource(cb);
	}
	private final native void JNISubscribeSource(SubscriptionCallback cb);
	
	
	public void setSinkVolume(int idx, Volume volume, SuccessCallback cb) {
		setSinkVolume(idx, volume.getVolumes(), cb);
	}
	public void setSinkInputVolume(int idx, Volume volume, SuccessCallback cb) {
		setSinkInputVolume(idx, volume.getVolumes(), cb);
	}
	public void setSourceVolume(int idx, Volume volume, SuccessCallback cb) {
		setSourceVolume(idx, volume.getVolumes(), cb);
	}
	public void setSourceOutputVolume(int idx, Volume volume, SuccessCallback cb) {
		setSourceOutputVolume(idx, volume.getVolumes(), cb);
	}
	
	public boolean isReady() {
		return mStatus == ContextState.READY;
	}
	
	public boolean isConnected() {
		return (getStatus() == 4);
	}
	
	protected void operationSuccess(int success) {

	}
	
	public void holdCallback(JniCallback cb) {
		mCallbacks.add(cb);
	}
	public void unholdCallback(JniCallback cb) {
		mCallbacks.remove(cb);
	}
	
	private static final native long JNICreate(long pMainloop);
	
	/*
	 * Closes the Context, and frees all unneeded C objects.
	 */
	public synchronized void close() {
		setConnectionReadyCallback(null);
		subscribeSinkInput(null);
		Set<JniCallback> removalSet = new TreeSet<JniCallback>();

		// Free all possible remaining callbacks
		// Possibly re-implement this using iterators and .remove()
		for (JniCallback callback : mCallbacks) {
			callback.freeGlobal(false);
			removalSet.add(callback);
		}
		mCallbacks.removeAll(removalSet);
		
		disconnect();
	}
	
	protected final native void disconnect();
	
	public final native void setConnectionReadyCallback(NotifyCallback cb);
	public final native void setConnectionFailedCallback(NotifyCallback cb);
	
	public final native int getStatus();

	// Sink
	public final native void getSinkInfo(int idx, InfoCallback<SinkInfo> cb);
	public final native void getSinkInfoList(InfoCallback<SinkInfo> cb);
	public final native void setSinkMute(int idx, boolean mute, SuccessCallback cb);
	private synchronized final native void setSinkVolume(int idx, int[] volumes, SuccessCallback cb);
	
	// Sink Input
	public final native void getSinkInputInfo(int idx, InfoCallback<SinkInput> cb);
	public final native void getSinkInputInfoList(InfoCallback<SinkInput> cb);
	public final native void setSinkInputMute(int idx, boolean mute, SuccessCallback cb);
	public final native void moveSinkInput(int idx, int sink_idx, SuccessCallback cb);
	private synchronized final native void setSinkInputVolume(int idx, int[] volumes, SuccessCallback cb);
	
	// Source
	public final native void getSourceInfo(int idx, InfoCallback<SourceInfo> cb);
	public final native void getSourceInfoList(InfoCallback<SourceInfo> cb);
	public final native void setSourceMute(int idx, boolean mute, SuccessCallback cb);
	private synchronized final native void setSourceVolume(int idx, int[] volumes, SuccessCallback cb);

	// Source Output
	public final native void getSourceOutputInfo(int idx, InfoCallback<SourceOutput> cb);
	public final native void getSourceOutputInfoList(InfoCallback<SourceOutput> cb);
	public final native void setSourceOutputMute(int idx, boolean mute, SuccessCallback cb);
	public final native void moveSourceOutput(int idx, int source_idx, SuccessCallback cb);
	private synchronized final native void setSourceOutputVolume(int idx, int[] volumes, SuccessCallback cb);
	
	// Client
	public final native void getClientInfo(int idx, InfoCallback<ClientInfo> cb);
	public final native void getClientInfoList(InfoCallback<ClientInfo> cb);
}
