package bgu.spl.app;

/**
 * 
 * @author dotan & neta 
 *
 */

public final class PurchaseSchedule {
	private String _shoeType;
	private int _tick;	

	/**
	 * 
	 * @param shoeType
	 * @param tick
	 */
	
	public PurchaseSchedule(String shoeType, int tick){
		_shoeType = shoeType;
		_tick = tick;
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
	 * @return tick
	 */
	
	public final int getTick() {
		return _tick;
	}	
}
