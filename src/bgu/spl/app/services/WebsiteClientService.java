package bgu.spl.app.services;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CountDownLatch;

import bgu.spl.mics.Broadcast;
import bgu.spl.mics.Callback;
import bgu.spl.mics.MicroService;
import bgu.spl.app.PurchaseSchedule;
import bgu.spl.app.messages.NewDiscountBroadcast;
import bgu.spl.app.messages.PurchaseOrderRequest;
import bgu.spl.app.messages.TerminateBroadcast;
import bgu.spl.app.messages.TickBroadcast;

/**
 * WebSiteClientService is a MicroService that represents a client
 * It receives Tick Broadcast Messages, Terminate Broadcast Messages and NewDiscount Broadcast Messages
 * It sends PurchaseOrderRequest Messages decided by his wishList and his purchaseSchedule
 * 
 * @author dotan & neta
 *
 */
public class WebsiteClientService extends MicroService {
	Set<String> _wishList;
	List<PurchaseSchedule> _purchaseSchedule; 
	int _currentTick;
	private CountDownLatch _startSignal;
	private CountDownLatch _endSignal;
	
	/**
	 * 
	 * @param name
	 * @param wishList
	 * @param startSignal
	 * @param endSignal 
	 */
	
	public WebsiteClientService(String name, CountDownLatch startSignal, CountDownLatch endSignal) {
		super(name);
		_wishList=new HashSet<String>();
		_purchaseSchedule=new ArrayList<PurchaseSchedule>();
		_startSignal=startSignal;
		_endSignal=endSignal;
	}
	
	/**
	 * Adding the next shoe string to his wish-list
	 * @param next
	 */
	
	public void addToWL (String next) {
		_wishList.add(next);
	}
	
	/**
	 * Adding the next PurchaseSchedule to his list
	 * @param next
	 */
	
	public void addToPS (PurchaseSchedule next) {
		_purchaseSchedule.add(next);
	}
	
	/**
	 * 
	 * @return purchaseSchedule
	 */
	
	public List<PurchaseSchedule> getPurchase () {
		return _purchaseSchedule;
	}
	
	/**
	 * 
	 * @return wishList
	 */
	
	public Set<String> getWishList () {
		return _wishList;
	}
	
	/**
	 * Checks if finished his wishList and his purchaseSchedule
	 * If so he will make gracefully terminating.
	 */

	private void isFinished() { //if not exist future purchases and wishes
		if(_wishList.isEmpty() && _purchaseSchedule.isEmpty()) {
			terminate();
			_endSignal.countDown();
		}
	}
	
	/**
	 * Subscribing to TerminateBroadcast,NewDiscountBroadcast and TickBroadcast.
	 * iterate through his wishList and send PurchaseOrderRequest if there is a discount
	 * iterate through his purchaseSchedule and send PurchaseOrderRequest if Tick matches 
	 */
	
	@Override
	protected void initialize() {
		subscribeBroadcast (TerminateBroadcast.class, message-> { //Terminate Message
			terminate();
			_endSignal.countDown();
		});
		
		subscribeBroadcast (NewDiscountBroadcast.class, message-> { //Discount Message
		    Iterator<String> iter = _wishList.iterator();
		    boolean flag=false; 
		    while (iter.hasNext()&& !flag) { //iter through wish-list
		    	String temp = iter.next();
		    	if (temp.equals(message.getShoeType())) { //if discounted shoe in wishlist send orderRequest
		    		PurchaseOrderRequest item = new PurchaseOrderRequest (temp, getName(), true, _currentTick);
			    	sendRequest(item, receipt-> {
			    	if (receipt!=null) {
			    		iter.remove();	
			    		isFinished();
		    		}		    			
		    	  });		    	  
		    	  flag=true;
		      }
		    }			
		});	
		
		subscribeBroadcast(TickBroadcast.class, message -> { //Tick Message
			_currentTick= message.getCurrentTick();
			int timeToCheck=_currentTick;
			Iterator<PurchaseSchedule> iter = _purchaseSchedule.iterator();
			while (iter.hasNext()) { //iter through purchases
				PurchaseSchedule temp= iter.next();
				if(temp.getTick()==timeToCheck) { //if tick match remove from list and send orderRequest
					iter.remove(); 
					PurchaseOrderRequest item = new PurchaseOrderRequest(temp.getShoeType(), getName(), false,temp.getTick());
					sendRequest (item, receipt -> {
						isFinished();
					});
				}
			}
		});	
		_startSignal.countDown();
	}
}