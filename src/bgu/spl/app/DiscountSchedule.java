package bgu.spl.app;

/**
 * 
 * @author dotan & neta
 *
 */

public final class DiscountSchedule {
	private String _shoeType;
	private int _amount;
	private int _tick;

	/**
	 * @param shoeType
	 * @param amount
	 * @param tick
	 */
	public DiscountSchedule(String shoeType, int amount, int tick){
		_shoeType = shoeType;
		_amount = amount;
		_tick = tick;
	}
	/**
	 * returns shoeType
	 * @return shoeType
	 */
	
	public final String getShoeType(){
		return _shoeType;
	}
	/**
	 * 
	 * @return amount
	 */
	public final int getAmount(){
		return _amount;
	}
	
	/**
	 * 
	 * @return tick
	 */
	
	public final int getTick() {
		return _tick;
	}	
}
