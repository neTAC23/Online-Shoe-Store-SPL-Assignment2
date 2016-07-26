package bgu.spl.app.services;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.CountDownLatch;

import bgu.spl.app.Receipt;
import bgu.spl.app.messages.ManufacturingOrderRequest;
import bgu.spl.app.messages.TerminateBroadcast;
import bgu.spl.app.messages.TickBroadcast;
import bgu.spl.mics.MicroService;

/**
 * ShoeFactoryService is a Microservice that represents a factory
 * It subscribes to TerminateBroadcast,TickBroadcast and ManufacturingOrderRequests messages
 * It saves every Requests arrive and create one shoe for each Tick
 * Once finish a request sends back the result as a Receipt
 * 
 * @author dotan & neta
 *
 */
public class ShoeFactoryService extends MicroService {
	
	private int _currentTick;
	private int _counter=0;
	private Queue<ManufacturingOrderRequest> _factoryMissions;
	private CountDownLatch _startSignal;
	private CountDownLatch _endSignal;
	
	/**
	 * 
	 * @param name
	 * @param startSignal - CountDownLatch for starting before timer
	 * @param endSignal - CountDownLatch for finishing together before printing store file
	 */
	
	public ShoeFactoryService(String name, CountDownLatch startSignal, CountDownLatch endSignal) {
		super(name);
		_startSignal=startSignal;
		_endSignal=endSignal;
		_factoryMissions = new LinkedList <ManufacturingOrderRequest>();
	}
	
	/**
	 * Subscribing to TerminateBroadcast,TickBroadcast and ManufacturingOrderRequests messages
	 * EveryTick creates one shoe and once finish a Request send it back with a Receipt
	 */

	@Override
	protected void initialize() {	
		subscribeBroadcast (TerminateBroadcast.class, message-> { //Terminate message
			terminate();
			_endSignal.countDown();			
		});
		
		subscribeBroadcast (TickBroadcast.class, message-> { //Tick Message
			_currentTick= message.getCurrentTick();
			int timeToUse=_currentTick;
			if (_factoryMissions!=null && !_factoryMissions.isEmpty()) { //if there is shoes order to produce
				ManufacturingOrderRequest nextToHandle = _factoryMissions.peek();
				_counter=_counter+1;
				if (nextToHandle.getAmount()==_counter) { //if finishes to produce the order
					Receipt r = new Receipt(getName(), "store", nextToHandle.getShoeType(),
							false, timeToUse, nextToHandle.getTickIssued(), _counter);
					complete (nextToHandle,r);
					_factoryMissions.remove();
					_counter=0;
				}				
			}
		});		
		subscribeRequest (ManufacturingOrderRequest.class, message -> { //ManuFacturing Message
			if (_factoryMissions.size()==0)
				_counter--;
				_factoryMissions.add(message);			
		});			
		_startSignal.countDown();
	}
}