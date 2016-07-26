package bgu.spl.app;

/**
 * 
 * @author dotan & neta 
 *
 */

public class Receipt {
	private String _seller;
	private String _customer;
	private String _shoeType;
	private boolean _discount; 
	private int _issuedTick;
	private int _requestTick;
	private int _amountSold;
	
	
	/**
	 * 
	 * @param seller
	 * @param customer
	 * @param shoeType
	 * @param discount
	 * @param issuedTick
	 * @param requestTick
	 * @param amountSold
	 */
	
	public Receipt (String seller, String customer, String shoeType, boolean discount,
			int issuedTick, int requestTick, int amountSold) {
		_seller = seller;
		_customer = customer;
		_shoeType = shoeType;
		_discount = discount;
		_issuedTick = issuedTick; 
		_requestTick = requestTick;
		_amountSold = amountSold;
	}
	
	/**
	 * 
	 * @return seller
	 */
	public final String getSeller() {
		return _seller;		
	}
	
	/**
	 * 
	 * @return customer
	 */	
	
	public final String getCustomer() {
		return _customer;
	}
	
	/**
	 * 
	 * @return shoetype
	 */
	
	public final String getShoeType() {
		return _shoeType;
	}
	
	/**
	 * Returns true if shoe is on discount
	 * @return discount
	 */
	
	public final boolean isDiscount() {
		return _discount;
	}
	
	/**
	 * Returns the tick the shoe was issued
	 * @return issuedTick
	 */
	
	public final int getIssuedTick() {
		return _issuedTick;
	}
	
	/**
	 * Returns the tick the shoe was requested
	 * @return requestTick
	 */
	
	public final int getRequestTick() {
		return _requestTick;
	}
	
	/**
	 * 
	 * @return amountsold
	 */
	
	public final int getAmountSold() {
		return _amountSold;
	}	
}