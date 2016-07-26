package bgu.spl.app;

import bgu.spl.app.gson.ClientObject;
import bgu.spl.app.gson.DSObject;
import bgu.spl.app.gson.Insert;
import bgu.spl.app.gson.PSObject;
import bgu.spl.app.gson.StorageObject;
import bgu.spl.app.services.ManagementService;
import bgu.spl.app.services.SellingService;
import bgu.spl.app.services.ShoeFactoryService;
import bgu.spl.app.services.TimeService;
import bgu.spl.app.services.WebsiteClientService;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gson.Gson;

/**
 *  The ShoeStoreRunner is the main Function -
 *  Recives Information from a json file using Gson.
 *  Creates a Singelton of a Log and a Store
 *  Arrange all data to create Threads.
 *  Execute all Threads - send them with 2 CountDown Latch
 *  startSignal - to initilaize all threads before timer
 *  endSignal - to terminate all threads before printing store *  
 * 
 * @author dotan & neta 
 *
 */

public class ShoeStoreRunner {
	
	public static void main(String[] args) {		
		MyLogger.getInstance();
		MyLogger.setLevel(Level.FINEST);
		MyLogger.log(Level.INFO, "Logger Name: " + MyLogger.getName());
		Gson gson = new Gson();
		BufferedReader br=null;
		System.out.println("Insert FileName: ");	
		int counterThread=0;
		try {		
			br = new BufferedReader (new FileReader(System.getProperty("user.dir") + "/src/" + args[0] + ".json"));
			MyLogger.log(Level.INFO, "Getting txt From File");
		}
		catch (FileNotFoundException e) {
			MyLogger.log(Level.SEVERE, "FileNotFoundException");
			e.printStackTrace();

		}			

		Insert appData = gson.fromJson(br, Insert.class); //getting text from file		
		try {
			if (br!=null) br.close();
		} catch (IOException e1) {
			MyLogger.log(Level.SEVERE, "FileException");
			e1.printStackTrace();
		}
		
		StorageObject[] TempStorage = appData.getInitialStorage();
		ShoeStorageInfo[] storage= new ShoeStorageInfo[TempStorage.length];
		counterThread=appData.getServices().getSellers()+appData.getServices().getFactories()+appData.getServices().getCustomers().length+2;
		MyLogger.log(Level.INFO, "Number of Threads " + counterThread);
		CountDownLatch startSignal = new CountDownLatch(counterThread-1); //countdown for starting timer
		CountDownLatch endSignal = new CountDownLatch(counterThread);//countdown for printing store	
		
		for (int i=0; i<storage.length; i++) { //build Store with ShoeStorageInfo array
			storage[i] = new ShoeStorageInfo(TempStorage[i].getShoeType(), TempStorage[i].getAmount());	
			MyLogger.log(Level.INFO, storage[i].getShoeType()+" " +storage[i].getShoeAmount()+" " +storage[i].getDiscountedShoeAmount());
		}
		ArrayList<DSObject> discountsTemp = appData.getServices().getManager().getDiscountSchedule();		
		ManagementService manager = new ManagementService (startSignal,endSignal);
		for (int i=0; i<discountsTemp.size(); i++) { //Build manager
			DiscountSchedule temp = new DiscountSchedule(discountsTemp.get(i).getShoeType(),
					discountsTemp.get(i).getAmount(),discountsTemp.get(i).getTick());
			manager.addToList(temp);	
			MyLogger.log(Level.INFO, temp.getShoeType()+" "+temp.getAmount()+" "+temp.getTick());		
		}		
		
		ShoeFactoryService[] factories = new ShoeFactoryService[appData.getServices().getFactories()];
		for (int i=0 ; i<factories.length; i++) { //Build factories
			String nick = ("Factory" + (i+1));
			factories[i] = new ShoeFactoryService (nick, startSignal,endSignal);
			MyLogger.log(Level.INFO, factories[i].getName());	
		}
		
		SellingService[] sellers = new SellingService[appData.getServices().getSellers()];
		for (int i=0 ; i<sellers.length; i++) { //Build sellers
			String nick = ("Seller" + (i+1));
			sellers[i] = new SellingService (nick, startSignal, endSignal);	
			MyLogger.log(Level.INFO, sellers[i].getName());	
		}
		
		WebsiteClientService[] clients = new WebsiteClientService[appData.getServices().getCustomers().length];
		for (int i=0; i<clients.length ; i++) { //Build clients
			ClientObject tempClient = appData.getServices().getCustomers()[i];			
			ArrayList<PSObject> tempPurchase = tempClient.getPurchases();	
			clients[i]=new WebsiteClientService (tempClient.getName(), startSignal, endSignal);				
			for (int j=0; j<appData.getServices().getCustomers()[i].getPurchases().size(); j++) { //add purchaseSchedule to client
				PurchaseSchedule temp= new PurchaseSchedule (tempPurchase.get(j).getShoeType(), tempPurchase.get(j).getTick());
				clients[i].addToPS(temp);
			}
			for (String next : appData.getServices().getCustomers()[i].getWishList()) {
			    clients[i].addToWL(next);
			}
			MyLogger.log(Level.INFO, clients[i].getName() + " " + clients[i].getWishList()  + " " +clients[i].getPurchase().size());
		}
		TimeService timer = new TimeService (appData.getServices().getTime().getSpeed(), 
				appData.getServices().getTime().getDuration(), startSignal, endSignal);
		Store store = Store.getInstance();
		store.load(storage);
		ExecutorService runner = Executors.newFixedThreadPool(counterThread); //run executor for each thread
		for (int i=0; i<sellers.length; i++) {	
			runner.execute(sellers[i]);
		}
		for (int i=0; i<factories.length; i++) {
			runner.execute(factories[i]);
		}
		for (int i=0; i<clients.length; i++) {
			runner.execute(clients[i]);			
		}
		
		runner.execute(manager);
		runner.execute(timer);
		
		try {
			endSignal	.await(); //wait till all threads finish
		} catch (InterruptedException e) {
			MyLogger.log(Level.SEVERE, "InterruptedException");
			e.printStackTrace();
		}
		try {
			Thread.sleep(200);
		} catch (InterruptedException e) {
			MyLogger.log(Level.SEVERE, "InterruptedException");
			e.printStackTrace();
		}
		store.print();
		runner.shutdown();
	}
}