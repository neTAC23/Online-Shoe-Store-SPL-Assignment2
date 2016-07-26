package bgu.spl.app.messages;

import bgu.spl.mics.Request;

/**
 * RestockRequest message is sent in order to inform shoe is sold out and asked for more
 * @author dotan & neta
 *
 */

public class RestockRequest implements Request<Boolean> {
	private String _shoeType;
	private String _requester;	
	
	/**
	 * 
	 * @param shoeType
	 * @param requester
	 */

	public RestockRequest(String shoeType,String requester ) {
		_shoeType=shoeType;
		_requester=requester;
	}
	
	/**
	 * 
	 * @return shoeType
	 */
	
	public String getShoeType () {
		return _shoeType;
	}
	
	/**
	 * 
	 * @return requester
	 */
	
	public String getRequester () {
		return _requester;
	}	
}


