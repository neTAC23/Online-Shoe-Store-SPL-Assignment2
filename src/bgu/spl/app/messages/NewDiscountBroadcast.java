package bgu.spl.app.messages;

import bgu.spl.mics.Broadcast;

/**
 * NewDiscountBroadcast is a message sent in order to inform discount on a shoetype
 * @author dotan & neta
 *
 */

public class NewDiscountBroadcast implements Broadcast {
	private String _shoeType;
	private int _amount;
	
	/**
	 * 
	 * @param shoeType
	 * @param amount
	 */
	
	public NewDiscountBroadcast(String shoeType, int amount) {
		_shoeType=shoeType;
		_amount=amount;
	}
	
	/**
	 * 
	 * @return shoetype
	 */
	
	public final String getShoeType(){
		return _shoeType;
	}
	
	/**
	 * 
	 * @return amount
	 */
	
	public final int getAmount() {
		return _amount;
	}
}
