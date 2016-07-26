package bgu.spl.app;

/**
 * The ShoeStorageInfo holding the details
 * {@value} shoeType {@value}amount {@valued}discountedAmount
 * for each shoe in the store
 * @author dotan & neta 
 *
 */

public class ShoeStorageInfo {
	private String _shoeType;
	private int _amountOnStorage;
	private int _discountedAmount;	

	/**
	 * 
	 * @param shoeType
	 * @param amountOnStorage
	 */
	
	public ShoeStorageInfo (String shoeType, int amountOnStorage) {
		_shoeType=shoeType;
		_amountOnStorage=amountOnStorage;
		_discountedAmount=0;
	}
	
	/**
	 * 
	 * @returns shoeType
	 */
	
	public final String getShoeType () {
		return _shoeType;
	}
	
	/**
	 * 
	 * @return amountOnStorage
	 */
	public final int getShoeAmount () {
		return _amountOnStorage;
	}
	
	/**
	 * 
	 * @return discountedAmount
	 */
	public final int getDiscountedShoeAmount () {
		return _discountedAmount;
	}
	/**
	 * Increase the shoe amount on storage with the {@value}changer param
	 * @param changer
	 */
	public synchronized void updateShoeAmount(int changer) {
		_amountOnStorage=_amountOnStorage+changer;
	}
	
	/**
	 * Increase the discounted shoe amount on storage with the {@value}changer param
	 * If discoutned amount greater then sotrage amount will Increase to sotrage amount
	 * @param changer
	 */
	
	public synchronized void updateDiscountedShoeAmount (int changer) {
		if (_discountedAmount+changer>_amountOnStorage) {
			_discountedAmount=_amountOnStorage;
		}
		else
			_discountedAmount=_discountedAmount+changer;
	}
}
