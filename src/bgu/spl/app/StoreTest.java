package bgu.spl.app;

import static org.junit.Assert.*;

import java.util.logging.Level;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * 
 * @author dotan & neta 
 * Tester to Store Class
 */

public class StoreTest {
	Store a = Store.getInstance();
	@Before
	public void setUp() throws Exception {
		MyLogger.getInstance();
		MyLogger.setLevel(Level.FINEST);
	}

	@After
	public void tearDown() throws Exception {
		a.print();
	}

	@Test
	public void testGetInstance() {
		Store a = Store.getInstance();
		Store b = Store.getInstance();
		assertEquals(a,b);
	}
	
	@Test
	public void testTake() {
		Store a= Store.getInstance();
		ShoeStorageInfo[] b = new ShoeStorageInfo[1];
		b[0] = new ShoeStorageInfo("try", 1);
		a.load(b);
		BuyResult ans= a.take("a", true);
		assertEquals(ans,BuyResult.NOT_IN_STOCK);
		ans=a.take("try", true);
		assertEquals(ans,BuyResult.NOT_ON_DISCOUNT);		
		ans=a.take("try", false);
		assertEquals(ans,BuyResult.REGULAR_PRICE);
	}

	@Test
	public void testLoad() {
		Store a= Store.getInstance();
		ShoeStorageInfo[] b = new ShoeStorageInfo[2];
		b[0] = new ShoeStorageInfo("a", 1);
		b[1] = new ShoeStorageInfo("b", 2);
		a.load(b);
		BuyResult ans= a.take("a", true);
		assertEquals(ans,BuyResult.NOT_ON_DISCOUNT);
		ans=a.take("a", false);
		assertEquals(ans,BuyResult.REGULAR_PRICE);
		ans=a.take("a", false);
		assertEquals(ans,BuyResult.NOT_IN_STOCK);
		ans=a.take("b", false);
		assertEquals(ans,BuyResult.REGULAR_PRICE);
		ans=a.take("b", false);
		assertFalse(ans.equals(BuyResult.NOT_IN_STOCK));
	}

	@Test
	public void testAdd() {
		Store a= Store.getInstance();
		ShoeStorageInfo[] b = new ShoeStorageInfo[2];
		b[0] = new ShoeStorageInfo("a", 1);
		b[1] = new ShoeStorageInfo("b", 2);
		a.load(b);
		BuyResult ans=a.take("c", false);
		assertEquals(ans, BuyResult.NOT_IN_STOCK);
		a.add("c", 1);
		ans = a.take("c", false);
		assertEquals(ans, BuyResult.REGULAR_PRICE);
		a.add("a", 1);
		ans = a.take("a", false);
		assertEquals(ans, BuyResult.REGULAR_PRICE);
		ans = a.take("a", false);
		assertEquals(ans, BuyResult.REGULAR_PRICE);
		ans = a.take("a", false);
		assertEquals(ans, BuyResult.NOT_IN_STOCK);		
	}

	@Test
	public void testAddDiscount() {
		Store a= Store.getInstance();
		ShoeStorageInfo[] b = new ShoeStorageInfo[2];
		b[0] = new ShoeStorageInfo("a", 1);
		b[1] = new ShoeStorageInfo("b", 2);
		a.load(b);

		a.addDiscount("a", 2);
		a.addDiscount("b", 1);		
		a.addDiscount("c", 3);
		BuyResult ans=a.take("a", true);
		assertEquals(ans, BuyResult.DISCOUNTED_PRICE);
		ans = a.take("a", true);
		assertEquals(ans, BuyResult.NOT_ON_DISCOUNT);
		ans = a.take("b", true);
		assertEquals(ans, BuyResult.DISCOUNTED_PRICE);
		ans = a.take("b", false);
		assertEquals(ans, BuyResult.REGULAR_PRICE);		
		ans = a.take("c", true);
		assertEquals(ans, BuyResult.NOT_ON_DISCOUNT);
		a.add("b", 2);
		a.addDiscount("b", 2);
		ans = a.take("b", false);
		assertEquals(ans, BuyResult.DISCOUNTED_PRICE);		
	}
}
