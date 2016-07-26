package bgu.spl.app.services;

import java.util.concurrent.CountDownLatch;
import java.util.logging.Level;

import bgu.spl.app.BuyResult;
import bgu.spl.app.MyLogger;
import bgu.spl.app.messages.*;
import bgu.spl.app.Store;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.Request;
import bgu.spl.app.Receipt;

/**
 * SellingService is a Microservice representing a seller
 * It subscribes to TerminateBroadcast, TickBroadcast and PurchaseOrderRequest messages
 * It tries to take a shoe from the store and get an answer.
 * It sends RestockRequest message if no shoes avialable in store.
 *
 * @author dotan & neta
 *
 */

public class SellingService extends MicroService {
	private Store _store;
	private int _currentTick;
	private CountDownLatch _startSignal;
	private CountDownLatch _endSignal;
	
	/**
	 * 
	 * @param name
	 * @param startSignal
	 * @param endSignal
	 */
	
	public SellingService(String name, CountDownLatch startSignal, CountDownLatch endSignal) { 
		super(name);
		_store = Store.getInstance();
		_startSignal=startSignal;
		_endSignal = endSignal;
	}
	
	private void addReceipt(boolean isDiscount, PurchaseOrderRequest message) {
		Receipt r = new Receipt(getName(), message.getClientName(), message.getShoeType(),
				isDiscount, _currentTick, message.getRequestTick(), 1);
		_store.file(r);
		complete(message,r);		
	}
	
	/**
	 * Subscribes to TerminateBroadcast, TickBroadcast and PurchaseOrderRequest
	 * For each PurchaseOrderRequest it try to take the specific shoe from the store
	 * It recives one of four possible answers and act accordenly:
	 * NOT IN STOCK - send a RestockRequest to try and get more shoes -
	 * NOT ON DISCOUNT - finish the transaction with no sell
	 * REGULAR PRICE - sell in a regular price because no discounted shoes available
	 * DISCOUNTED PRICE - sell in a discounted price
	 */ 
	@Override
	protected void initialize() {
		subscribeBroadcast (TerminateBroadcast.class, message-> { //Terminate Message
			terminate();
			_endSignal.countDown();
		});
		subscribeBroadcast (TickBroadcast.class, message-> { //Tick Message
			_currentTick= message.getCurrentTick();			
		});
		
		subscribeRequest(PurchaseOrderRequest.class, message-> { //PurchaseOrderRequest			
			BuyResult result = _store.take(message.getShoeType(), message.isDiscount());
			switch (result) {
				case NOT_IN_STOCK:					
					RestockRequest request = new RestockRequest(message.getShoeType(), getName());
					sendRequest (request, completed -> { 
						if (completed) { 	
							MyLogger.log(Level.INFO, "adding from NOT IN STOCK (RestockRequest sent)");
							addReceipt(false,message);
						}
						else {
							MyLogger.log(Level.INFO, "NOT IN STOCK (RestockRequest sent no Factories avialable)");
							complete (message,null);
						}						
					});
					break;
					
				case NOT_ON_DISCOUNT:
					MyLogger.log(Level.INFO, "NOT IN DISCOUNT (no shoe sold)");
					complete (message,null);
					break;
					
				case REGULAR_PRICE:	
					MyLogger.log(Level.INFO, "adding from REGULAR PRICE");
					addReceipt(false,message);
					break;
					
				case DISCOUNTED_PRICE:
					MyLogger.log(Level.INFO, "adding from DISCOUNTED PRICE");
					addReceipt(true,message);
					break;
				default: ;
	    }			
		});
		_startSignal.countDown();
	}
}