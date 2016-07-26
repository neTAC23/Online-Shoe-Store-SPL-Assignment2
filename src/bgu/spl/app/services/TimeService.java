package bgu.spl.app.services;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;

import bgu.spl.app.MyLogger;
import bgu.spl.app.messages.TerminateBroadcast;
import bgu.spl.app.messages.TickBroadcast;

import bgu.spl.mics.MicroService;

/**
 * TimeService is a Microservice represnting a timer which counts ticks
 * Ticks decided by the user inserting speed of each tick and the program duration
 * the timer inform the currentTick and the time to terminate
 * @author dotan & neta
 *
 */

public class TimeService extends MicroService {
	private Timer _timer;
	private int _speed;
	private int _duration;
	private AtomicInteger _counter;
	private CountDownLatch _startSignal;	
	private CountDownLatch _endSignal;
	
	/**
	 * 
	 * @param speed
	 * @param duration
	 * @param startSignal
	 * @param endSignal
	 */
	
	public TimeService(int speed, int duration, CountDownLatch startSignal, CountDownLatch endSignal) {
		super("timer");
		_speed=speed;
		_duration=duration;
		_timer= new Timer();
		_startSignal=startSignal;
		_endSignal = endSignal;
		_counter = new AtomicInteger (0);
	}
	
	/**
	 * Returns the speed of each tick
	 * @return speed
	 */
	
	public final int getSpeed() {
		return _speed;
	}
	
	/**
	 * Returns the duration(tick amount) of the program
	 * @return duration
	 */ 
	
	public final int getDuration() {
		return _duration;
	}
	
	/**
	 * subscribing to TerminateBroadcast message
	 * Creating a timer with {@value}speed and {@value}duration
	 * Sends TickBroadcast for each tick till currentTick equals duration then terminates
	 */

	@Override
	protected void initialize() {
		try {
			MyLogger.log(Level.WARNING, getName() + " is waiting for all threads to initialize");
			_startSignal.await();
		} catch (InterruptedException e) {
			MyLogger.log(Level.SEVERE, "InterruptedException");
			e.printStackTrace();
			terminate();
		}
		subscribeBroadcast (TerminateBroadcast.class, message-> { //Terminate message 
			_timer.cancel();
			terminate();
			_endSignal.countDown();
		});
		
		_timer.scheduleAtFixedRate(new TimerTask() {
			public void run() {
				if (_counter.get()==_duration) {				  
					sendBroadcast(new TerminateBroadcast()); //send terminate message
				}
				else {
					int counter=_counter.incrementAndGet();
					MyLogger.log(Level.INFO,"Tick number " + _counter +"\n" + "**********************" + "\n");
					//System.out.println("Tick number " + _counter);
					sendBroadcast(new TickBroadcast(counter,_duration)); //send tick message
				}
			}
		}, _speed, _speed);			
	}
}