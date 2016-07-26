package bgu.spl.app.messages;

import bgu.spl.mics.Broadcast;

/**
 * TickBroadcast is a message sent to inform the currentTick time of the timer
 * @author dotan & neta
 *
 */

public class TickBroadcast implements Broadcast {
	private int _currentTick;	
	
	/**
	 * 
	 * @param currentTick
	 * @param duration
	 */

	public TickBroadcast(int currentTick, int duration) {		
		_currentTick=currentTick;
	}
	
	/**
	 * 
	 * @return currentTick
	 */
	
	public final int getCurrentTick() {
		return _currentTick;
	}
}
