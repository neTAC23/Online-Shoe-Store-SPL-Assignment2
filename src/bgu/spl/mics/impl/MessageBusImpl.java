package bgu.spl.mics.impl;
import java.util.LinkedList;

import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;

import bgu.spl.app.MyLogger;
import bgu.spl.mics.*;

/**
 * This class is an Implementation of the MessageBus abstract class
 * This class is the heart of the appliaction, managing the messaged between the services
 * @author dotan & neta
 *
 */
public class MessageBusImpl implements MessageBus {
	private Map <MicroService, BlockingQueue <Message>> specificMessagesMap; //messages queue for each microservice
	private Map <Class<? extends Message>, LinkedList<MicroService>> typeMessagesMap; //registered microservices for each message type
	private Map <Request<?>, MicroService> sentTheReqMap; //specific message and who sent it
	private int index; //index for round-robin
	
	private static class MessageBusHolder { //singelton
		private static MessageBusImpl instance = new MessageBusImpl();
	}
	
	private MessageBusImpl () { 
		specificMessagesMap = new ConcurrentHashMap <MicroService,BlockingQueue <Message>>();
		typeMessagesMap = new ConcurrentHashMap <Class<? extends Message>, LinkedList<MicroService>>();
		sentTheReqMap = new ConcurrentHashMap <Request<?>, MicroService>();
	}	
	
	/**
	 * 
	 * @return the instance of the MessageBus
	 */
	
	public static MessageBusImpl getInstance() {
		return MessageBusHolder.instance;
	}
	
	/**
	 * subscribing a service {@value}m to the type of message request {@value}type.
	 * @param type the type to subscribe to
     * @param m    the subscribing micro-service
	 */

	public void subscribeRequest(Class<? extends Request> type, MicroService m) {
    	MyLogger.log(Level.INFO, m.getName() + " subscribing to Request-type " + type);
		subscribeMessage (type,m);
	}
	
	/**
	 * subscribing a service {@value}m to the type of message Broadcast {@value}type.
	 * @param type the type to subscribe to
     * @param m    the subscribing micro-service
	 */
	
	public void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m) {
    	MyLogger.log(Level.INFO, m.getName() + " subscribing to BroadCast-type " + type);
		subscribeMessage (type,m);		
	}
	
	private synchronized void subscribeMessage(Class<? extends Message> type, MicroService m) {
		if (!typeMessagesMap.containsKey(type)) {
			LinkedList <MicroService> serviceList = new LinkedList <MicroService>();
			typeMessagesMap.put(type,serviceList);
		}
		if (!typeMessagesMap.get(type).contains(m)) {
			typeMessagesMap.get(type).addLast(m);	
		}
	}
	
	/**
	 * Informing the MessageBus that the requet {@value}r was completed and its {@value}result
	 * sending back a message to the requester
	 * @param <T>    the type of the result expected by the completed request
     * @param r      the completed request
     * @param result the result of the completed request
	 */

	public <T> void complete(Request<T> r, T result) {
		RequestCompleted<T> rc = new RequestCompleted<T>(r,result);
		MicroService senderService = sentTheReqMap.get(r);
		try {
			MyLogger.log(Level.INFO, r.getClass() + " completed and sent back to " + senderService.getName());
			BlockingQueue<Message> t= specificMessagesMap.get(senderService);			
			if (t!=null) {
				t.put(rc); //sending message back to the requester
			}				
		} catch (InterruptedException e) {				
			e.printStackTrace();
		}
	}
	
	/**
	 * sending the Broadcast message to every service that registered to this type.
	 * @param b message of type Broadcast to send
	 */

	public void sendBroadcast(Broadcast b) {
		LinkedList<MicroService> sendTo=typeMessagesMap.get(b.getClass());
		if (sendTo!=null) {
			for (int i=0;i<sendTo.size(); i++) {				
				if (specificMessagesMap.containsKey(sendTo.get(i))) { //check if service exists 
					try {
						specificMessagesMap.get(sendTo.get(i)).put(b); //notify all when inserts message
					} catch (InterruptedException e) {
					    MyLogger.log(Level.SEVERE, "InterruptedException");
						e.printStackTrace();
					}
				}				
			}	
		}
	}
	
	/**
	 * sending the Request message to the next service that registered to this type.
	 * saving the requester for informing after complete method
	 * @param r messeage of type Request to send
	 * @param requester the service which sent the message
	 */

	public boolean sendRequest(Request<?> r, MicroService requester) {
		sentTheReqMap.put(r, requester);
		if (typeMessagesMap.get(r.getClass())!=null) { //if there is any one to send the message to
			MyLogger.log(Level.INFO, r.getClass() +" sent by " + requester.getName());
			MicroService temp=takeNext (typeMessagesMap.get(r.getClass()),r,index); //take the next one, remove unregistered if needed
			if (temp!=null) {
				int msg = typeMessagesMap.get(r.getClass()).size();
				index=(index+1)%msg; //next index with round-robin using index
				try {
					specificMessagesMap.get(temp).put(r); //notify all when inserts message
				} catch (InterruptedException e) {
				    MyLogger.log(Level.SEVERE, "InterruptedException");
					e.printStackTrace();
				}				
				return true;				
			}
			else
				return false;
		}
		else return false;
	}

	private synchronized MicroService takeNext (LinkedList<MicroService> check,Request<?> r, int index) {
		if (index>=check.size()) //checking index for bounds
			index=check.size()-1;
		EraseIfDoesntExist (check,index); //erase if unregistered
		if (check.size()!=0) {
			index = index%check.size(); //modulo
			return typeMessagesMap.get(r.getClass()).get(index); //return next service in list
		}
		else return null;
	}
	
	private synchronized void EraseIfDoesntExist (LinkedList<MicroService> check, int index) {
		if (check.size()>0 && index>=check.size()) //.checking index for bounds
			index=check.size()-1;
		while (!check.isEmpty() && !specificMessagesMap.containsKey(check.get(index))) { //next service is unregistered
			check.remove(index);
			if (!check.isEmpty())  //modulo
				index=index%(check.size());						
		}
	}
	
	/**
	 * Registring the service to the messagebus. Creating a Queue of messages for him.
	 * @param m service to register
	 */
	
	public void register(MicroService m) {
		MyLogger.log(Level.INFO, m.getName() +" is registering");
		BlockingQueue<Message> messagesQueue = new LinkedBlockingQueue <Message> (); 
		specificMessagesMap.put(m,messagesQueue);
	}
	
	/**
	 * UnRegistring the service from the messagebus. Deleting the Queue of messages and removing
	 * @param m service to unregister
	 */

	public synchronized void unregister(MicroService m) { 
	    MyLogger.log(Level.INFO, m.getName() + " is unregistering gracefully...");
		if (specificMessagesMap.containsKey(m)) {
			specificMessagesMap.get(m).clear();
			specificMessagesMap.remove(m);
		}
	}
	
	/**
	 * Service is getting the nextmessage to handle
	 * Waiting if his Queue is empty till next message arrives.
	 * @param m microservice waiting to get next message	 * 
	 * @exception InterruptedException while {@value} m is waiting 
	 * @exception IllegalStateException if {@value} m was never registered
	 */
	
	public Message awaitMessage(MicroService m) throws InterruptedException {
		if (!specificMessagesMap.containsKey(m)) {
		    MyLogger.log(Level.SEVERE, "IllegalStateException - MicroService " + m + " was never registered.");
			throw new IllegalStateException ("MicroService " + m + " was never registered.");
		}
		else {
			BlockingQueue<Message> q = specificMessagesMap.get(m); //waits if empty till next message inserted
			Message send = q.take();
			return send;	
		}		
	}
}