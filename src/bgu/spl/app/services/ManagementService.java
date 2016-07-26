package bgu.spl.app.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import bgu.spl.app.DiscountSchedule;
import bgu.spl.app.Store;
import bgu.spl.app.messages.ManufacturingOrderRequest;
import bgu.spl.app.messages.NewDiscountBroadcast;
import bgu.spl.app.messages.RestockRequest;
import bgu.spl.app.messages.TerminateBroadcast;
import bgu.spl.app.messages.TickBroadcast;
import bgu.spl.mics.MicroService;

/**
 * ManagamentService is a Microservice representing a store manager
 * It subscribes to TerminateBroadcast, TickBroadcast and RestockRequest messages
 * It sends DiscountedBroadcast if matches to currentTick
 * It sends ManufacturingOrderRequest if the specific shoe requested is not already on creation
 * 
 * @author dotan & neta
 *
 */
public class ManagementService extends MicroService {
	
	private int _currentTick;
	private HashMap <String, Integer> _shoesInProduction;
	private Store _store;
	private CountDownLatch _startSignal;
	private CountDownLatch _endSignal;
	private List<DiscountSchedule> _discountSchedule;
	private HashMap <String, LinkedList<RestockRequest>> _requestsForShoes;
	private int counter=0;
	/**
	 * 
	 * @param startSignal
	 * @param endSignal
	 */
	
	public ManagementService(CountDownLatch startSignal, CountDownLatch endSignal)  {
		super("manager");
		_discountSchedule = new ArrayList<DiscountSchedule>();
		_shoesInProduction = new HashMap <String, Integer>();
		_requestsForShoes = new HashMap <String, LinkedList<RestockRequest>>();
		_store = Store.getInstance();
		_startSignal=startSignal;
		_endSignal=endSignal;
	}
	
	/**
	 * add {@value}next discount to discountSchedule
	 * @param next
	 */
	
	public void addToList(DiscountSchedule next) {
		_discountSchedule.add(next);		
	}
	
	/**
	 * Subscribing to TerminateBroadcast, TickBroadcast and RestockRequest messages
	 * For each tick it itereate through the discountSchedule and send a DiscountBroadcast if tick matches
	 * When gets a RestockRequest checks if the shoeType already in progress of creation and still wasnt save.
	 * if not sends a ManuFacturingOrderRequest to create more shoes
	 */

	@Override
	protected void initialize() {	
		subscribeBroadcast (TerminateBroadcast.class, message-> { //Terminate Message
			terminate();
			_endSignal.countDown();
		});
		
		subscribeBroadcast (TickBroadcast.class, message-> { //Tick Message
			_currentTick= message.getCurrentTick();	
			int TimeToUse=_currentTick;
		    Iterator<DiscountSchedule> iter = _discountSchedule.iterator(); 
		    while (iter.hasNext()) { //iter through discounts list
		    	DiscountSchedule temp = iter.next();
		    	if (temp.getTick() == TimeToUse) { //if tick match discount time send discount message
		    		_store.addDiscount (temp.getShoeType(), temp.getAmount());
		    		sendBroadcast (new NewDiscountBroadcast(temp.getShoeType(), temp.getAmount()));
		    		iter.remove();
		    	}
		    }
		});	
		
		subscribeRequest (RestockRequest.class ,message -> {

			String shoe = message.getShoeType();
			if (!_shoesInProduction.containsKey(shoe) || _shoesInProduction.get(shoe)==0) { //if there is no request yet or not more available
				counter++;
				int amountToOrder = (_currentTick%5)+1; 
				ManufacturingOrderRequest newOrder = new ManufacturingOrderRequest (getName(), shoe, amountToOrder, _currentTick);
				boolean success = sendRequest (newOrder, receipt-> {
					_store.file(receipt);
					for (int i=0; i<newOrder.getAmount() && _requestsForShoes.get(shoe).size()>0; i++) {
						complete(_requestsForShoes.get(shoe).getFirst(), true);
						_requestsForShoes.get(shoe).removeFirst();						
					}
					if (_requestsForShoes.get(shoe).size()==0) { //The only RestockRequest for this shoe
						_store.add(shoe, _shoesInProduction.get(shoe));	//update store
						_requestsForShoes.remove(shoe);
						_shoesInProduction.remove(shoe);
					}						
				});
				
				if (success) { //if messaage accepted by a factory
					_shoesInProduction.put(shoe, amountToOrder-1); //add to maps
					if (!_requestsForShoes.containsKey(shoe)) { //if doesnt have other orders still waiting
						LinkedList<RestockRequest> restocklist = new LinkedList<RestockRequest>(); 
						_requestsForShoes.put(shoe, restocklist);
					}
					_requestsForShoes.get(shoe).addLast(message);
				}
				else {
					complete(message,false); //no factory - receipt with null				
				}
			}			
			else {
				_shoesInProduction.put(shoe, (_shoesInProduction.get(shoe))-1); //take one
				_requestsForShoes.get(shoe).addLast(message); //add request to list	
			}					
		});
		_startSignal.countDown();		
	}
}