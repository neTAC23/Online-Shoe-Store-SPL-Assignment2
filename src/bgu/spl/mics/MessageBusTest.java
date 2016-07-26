package bgu.spl.mics;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.logging.Level;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import bgu.spl.app.MyLogger;
import bgu.spl.app.PurchaseSchedule;
import bgu.spl.app.Receipt;
import bgu.spl.app.messages.NewDiscountBroadcast;
import bgu.spl.app.messages.PurchaseOrderRequest;
import bgu.spl.app.messages.TickBroadcast;
import bgu.spl.app.services.SellingService;
import bgu.spl.app.services.WebsiteClientService;
import bgu.spl.mics.impl.MessageBusImpl;

/**
 * 
 * @author dotan & neta 
 * Tester to MessageBus Class
 */

public class MessageBusTest {
	ArrayList<PurchaseSchedule> listOfPurchases;
	SellingService seller;
	WebsiteClientService client;
	@Before
	public void setUp() throws Exception {
		MyLogger.getInstance();
		MyLogger.setLevel(Level.FINEST);
		listOfPurchases = new ArrayList<PurchaseSchedule>();
		PurchaseSchedule first = new PurchaseSchedule("sneakers", 2);
		PurchaseSchedule second = new PurchaseSchedule("flip-flops", 3);
		PurchaseSchedule third = new PurchaseSchedule("yellow boots", 5);
		listOfPurchases.add(first);
		listOfPurchases.add(second);
		listOfPurchases.add(third);
		seller = new SellingService("seller", null, null);
		client = new WebsiteClientService("client", null, null);
		MessageBusImpl.getInstance().register(seller);
		MessageBusImpl.getInstance().register(client);
	}

	@After
	public void tearDown() throws Exception {
		MessageBusImpl.getInstance().unregister(seller);
		MessageBusImpl.getInstance().unregister(client);
	}


	@Test
	public void testSubscribeRequest() throws InterruptedException {
		PurchaseOrderRequest purchReq = new PurchaseOrderRequest("sneakers", "john",false, 3);
		MessageBusImpl.getInstance().subscribeRequest(purchReq.getClass(), seller);
		MessageBusImpl.getInstance().sendRequest(purchReq, client);
		Message message = MessageBusImpl.getInstance().awaitMessage(seller);
		assertEquals(purchReq, message);	
	}
	
	@Test
	public void testComplete() {
		MessageBus a = MessageBusImpl.getInstance();
		String b = "b";
		MicroService c = new SellingService (b, null ,null);
		a.register(c);
		MicroService d = new WebsiteClientService ("web",null,null);
		a.register(d);
		c.subscribeRequest(PurchaseOrderRequest.class, null);
		PurchaseOrderRequest p = new PurchaseOrderRequest("tt", "cc", false, 3);
		boolean ans = d.sendRequest(p, completed-> {
			System.out.println("empty");
		});
		assertTrue(ans);

		d.sendRequest(p, completed-> {
			System.out.println("empty");
			Receipt r = new Receipt("a","b","c",false,1,2,3);
			c.complete(p,r);			
		});
		a.unregister(c);
		a.unregister(d);
	}
	@Test
	public void testSendBroadcast() throws InterruptedException{
		NewDiscountBroadcast discount = new NewDiscountBroadcast("sneakers", 3);
		MessageBusImpl.getInstance().subscribeBroadcast(discount.getClass(), seller);
		MessageBusImpl.getInstance().sendBroadcast(discount);
		Message message = MessageBusImpl.getInstance().awaitMessage(seller);
		assertEquals(discount, message);	
	}

	@Test
	public void testSendRequest() throws InterruptedException {
		PurchaseOrderRequest purchReq = new PurchaseOrderRequest("sneakers", "john",false, 3);
		MessageBusImpl.getInstance().subscribeRequest(purchReq.getClass(), seller);
		MessageBusImpl.getInstance().sendRequest(purchReq, client);
		Message message = MessageBusImpl.getInstance().awaitMessage(seller);
		assertEquals(purchReq, message);	
	}

	@Test
	public void testAwaitMessage() {
		MessageBus a = MessageBusImpl.getInstance();
		MicroService c = new SellingService ("b", null ,null);
		a.register(c);
		MicroService d = new WebsiteClientService ("web",null,null);
		a.register(d);
		c.subscribeRequest(PurchaseOrderRequest.class, null);
		PurchaseOrderRequest p = new PurchaseOrderRequest("tt", "cc", false, 3);
		d.sendRequest(p, completed-> {
			System.out.println("empty");
		});
		PurchaseOrderRequest q = new PurchaseOrderRequest("tt", "cc", false, 3);
		try {
			Message t = a.awaitMessage(c);
			assertEquals(t,(Message)p);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}		
	}
}
