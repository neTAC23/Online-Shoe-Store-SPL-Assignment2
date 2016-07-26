package bgu.spl.app;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.logging.Level;

/**
 * The Store is a Singelton that managing the shoes and amounts
 * It and returns answers for purchaseRequests and updating amounts of shoes avialable
 * @author dotan & neta 
 *
 */

public class Store {
	private Map <String, ShoeStorageInfo> _shoeTypeMap;
	private LinkedList<Receipt> _receiptsList; 
	
	private static class StoreHolder {
		private static Store instance = new Store();		
	}
	
	private Store () {
		_receiptsList = new LinkedList<Receipt>();
		_shoeTypeMap = new HashMap<String, ShoeStorageInfo>();		
	}
	/**
	 * Returns the only one Instance of the store
	 * @return storeInstance
	 */
	
	public static Store getInstance() {
		return StoreHolder.instance;
	}
	
	/**
	 * Load the {@value}storage info given to the store
	 * @param storage
	 */
	
	public void load (ShoeStorageInfo[] storage) {
		MyLogger.log(Level.INFO,"Loading storage to store");
		for (int i=0; i<storage.length; i++) {
			addProductToMap(storage[i]);
		}				
	}
	
	private synchronized void addProductToMap (ShoeStorageInfo storage) {
		_shoeTypeMap.put(storage.getShoeType(), storage);
	}
	
	/**
	 * Returns the BuyResult answer to the specific {@value}shoeType asked
	 * answer will be NOT_ON_DISCOUNT if there will be shoes but not on discount in the store
	 * answer will be NOT_IN_STOCK if there wont be shoes in store right now.
	 * answer will be DISCOUNTED_PRICE if there is discounted shoe in store
	 * answer will be REGULAR_PRICE if there will be shoes in store but not on discount
	 * @param shoeType
	 * @param onlyDiscount
	 * @return BuyResult
	 */
	
	public synchronized BuyResult take (String shoeType, boolean onlyDiscount) {
		ShoeStorageInfo toCheck = _shoeTypeMap.get(shoeType);
		if (toCheck!=null && toCheck.getShoeAmount()!=0) {
			if (onlyDiscount && toCheck.getDiscountedShoeAmount()==0) {
				return BuyResult.NOT_ON_DISCOUNT;
			}
			else
				if (toCheck.getDiscountedShoeAmount()>0) {
					addDiscount(shoeType,-1);
					add(shoeType, -1);
					return BuyResult.DISCOUNTED_PRICE;					
				}
				else {
					add(shoeType, -1);
					return BuyResult.REGULAR_PRICE;						
				}							
		}
		else {
			if (toCheck!=null && onlyDiscount && toCheck.getDiscountedShoeAmount()==0)
				return BuyResult.NOT_ON_DISCOUNT;
			else
				return BuyResult.NOT_IN_STOCK;
		}
	}
	
	/**
	 * Adds a @{@value}amount of {@value}shoeType to store
	 * Will create new shoe if doesnt exist
	 * @param shoeType
	 * @param amount
	 */
	public synchronized void add (String shoeType, int amount) {
		ShoeStorageInfo toAdd = _shoeTypeMap.get(shoeType);
		if (toAdd!=null) {
			toAdd.updateShoeAmount(amount);
		}
		else {
			ShoeStorageInfo newShoe = new ShoeStorageInfo(shoeType,amount);
			addProductToMap(newShoe);
		}			
	}
	
	/**
	 * Adds a @{@value}amount of discounted {@value}shoeType to store
	 * Will create new shoe with 0 amount if doesnt exist
	 * @param shoeType
	 * @param amount
	 */
	
	public synchronized void addDiscount (String shoeType, int amount) {
		ShoeStorageInfo toAdd = _shoeTypeMap.get(shoeType);
		if (toAdd!=null) {
			toAdd.updateDiscountedShoeAmount(amount);
		}
		else {
			ShoeStorageInfo newShoe = new ShoeStorageInfo(shoeType,0); 
			addProductToMap(newShoe);
		}
	}
	
	/**
	 * File {@value}receipt to list
	 * @param receipt
	 */
	
	public final void file (Receipt receipt) {		
		_receiptsList.addFirst(receipt);	
	}
	
	/**
	 * Prints the amounts of shoes left in store and the receipts
	 */
	
	public final void print() {
		System.out.println("Start Printing: ---------------");
		System.out.println("");
		System.out.format("%-40s%-20s%-20s","SHOE_NAME", "SHOE_AMOUNT", "DISCOUNT_AMOUNT");
		for (ShoeStorageInfo value : _shoeTypeMap.values()) {
			System.out.println("");
			System.out.format("%-40s%-20d%-20d",value.getShoeType(), value.getShoeAmount(), value.getDiscountedShoeAmount());		
		}
		System.out.println("");
		System.out.format("%-4s%-12s%-20s%-10s%-15s%-15s%-15s", 
				"#", "SELLER", "CUSTOMER", "DISCOUNT", "ISSUED_TICK", "REQUEST_TICK", "AMOUNT_SOLD");
		for (int i=0; i<_receiptsList.size(); i++) {
			Receipt t= _receiptsList.get(i);
			System.out.println("");
			System.out.format("%-4d%-12s%-20s%-10s%-15d%-15d%-15s", i+1, t.getSeller(), t.getCustomer(),
					t.isDiscount(), t.getIssuedTick(), t.getRequestTick(), ""+t.getAmountSold());
		}	
	}
}