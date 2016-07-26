package bgu.spl.app.messages;

import bgu.spl.app.Receipt;
import bgu.spl.mics.Request;

/**
 * ManufacturingOrderRequest is a message sent in order to create more shoes
 * 
 * @author dotan & neta
 *
 */

public class ManufacturingOrderRequest implements Request<Receipt> {
	private String _managerName;
	private String _shoeType;
	private int _amount;
	private int _tickRequested;	
	
	/**
	 * 
	 * @param managerName
	 * @param shoeType
	 * @param amount
	 * @param tickRequested
	 */
	
	public ManufacturingOrderRequest (String managerName,String shoeType, int amount, int tickRequested) {
		_managerName = managerName;
		_shoeType=shoeType;
		_amount=amount;
		_tickRequested=tickRequested;
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
	 * @return managerName
	 */
	
	public final String getManagerName() {
		return _managerName;
	}
	
	/**
	 * 
	 * @return amount
	 */
	
	public final int getAmount() {
		return _amount;
	}
	
	/**
	 * Returns the tick when the request happened
	 * @return tickRequested
	 */
	
	public final int getTickIssued() {
		return _tickRequested;
	}
}
