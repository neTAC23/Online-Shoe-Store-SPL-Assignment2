package bgu.spl.app.gson;

/**
 * 
 * @author dotan & neta 
 * class to read file with gson
 */

public class Services {
	private TimerObject time;
	private ManagerObject manager;
	private int factories;
	private int sellers;
	private ClientObject[] customers;	
	
	/**
	 * 
	 * @return time
	 */

	public TimerObject getTime () {
		return time;
	}
	
	/**
	 * 
	 * @return manager
	 */
	
	public ManagerObject getManager () { 
		return manager;
	}
	
	/**
	 * 
	 * @return factories
	 */
	
	public int getFactories () {
		return factories;
	}
	
	/**
	 * 
	 * @return sellers
	 */
	
	public int getSellers () {
		return sellers;
	}

	/**
	 * 
	 * @return customers
	 */
	
	public ClientObject[] getCustomers () {
		return customers;
	}
}
