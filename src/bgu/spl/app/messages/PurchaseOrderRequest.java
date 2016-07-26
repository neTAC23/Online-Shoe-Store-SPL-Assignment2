package bgu.spl.app.messages;

import bgu.spl.app.Receipt;
import bgu.spl.mics.Request;

/**
 * PurchaseOrderRequest is a message sent in order to ask for a shoe
 * @author dotan & neta
 *
 */

public class PurchaseOrderRequest implements Request<Receipt> {
	private String _shoeType;
	private String _clientName;
	private boolean _isDiscount;
	private int _requestTick;	

	
	/**
	 * 
	 * @param shoeType
	 * @param clientName
	 * @param isDiscount
	 * @param requestTick
	 */
	
	public PurchaseOrderRequest (String shoeType, String clientName, boolean isDiscount,int requestTick) {
		_shoeType=shoeType;
		_clientName=clientName;
		_isDiscount=isDiscount;
		_requestTick=requestTick;
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
	 * @return clientName
	 */
	
	public final String getClientName() {
		return _clientName;
	}
	
	/**
	 * Returns if the requester asked for discounted shoe 
	 * @return isDiscount
	 */
	
	public final boolean isDiscount() {
		return _isDiscount;
	}
	
	/**
	 * Returns the tick when the order requested requester
	 * @return requestTick
	 */
	
	public final int getRequestTick() {
		return _requestTick;
	}
}

