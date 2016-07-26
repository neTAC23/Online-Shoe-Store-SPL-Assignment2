package bgu.spl.app.gson;

import java.util.ArrayList;
import java.util.Set;

/**
 * 
 * @author dotan & neta 
 * class to read file with gson
 */

public class ClientObject {	
	private String name;
	private Set<String> wishList;
	private ArrayList<PSObject> purchaseSchedule;
	
	/**
	 * 
	 * @return name
	 */
	
	public final String getName () {
		return name;
	}
	
	/**
	 * 
	 * @return wishList
	 */
	
	public final Set<String> getWishList () {
		return wishList;
	}	
	
	/**
	 * 
	 * @return purchaseSchedule
	 */
	
	public final ArrayList<PSObject> getPurchases () {
		return purchaseSchedule;
	}	
}
