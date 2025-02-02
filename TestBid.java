import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Scanner;

import edu.uwm.apc430.Bid;
import edu.uwm.apc430.VickreyAuction;
import edu.uwm.cs.junit.LockedTestCase;


public class TestBid extends LockedTestCase {
    protected static void assertException(Class<? extends Throwable> c, Runnable r) {
    	try {
    		r.run();
    		assertFalse("Exception should have been thrown",true);
        } catch (RuntimeException ex) {
        	assertTrue("should throw exception of " + c + ", not of " + ex.getClass(), c.isInstance(ex));
        }	
    }	

    protected static Scanner makeScanner(String s) {
    	return new Scanner(new StringReader(s));
    }
        
	// "locked" tests:
	// This test will put up a pop-up asking YOU
	// for the answer to some questions.  You need to
	// answer what should substitute for a ???
	// If being asked for a string, don't put quotes around it.
	public void test() {
		Bid b = new Bid("Sandy",3.25);
		assertEquals(Ts(229113287),b.getBidder());
		assertEquals(Td(230176110),b.getPrice());
		
		b = Bid.fromString("1 John 7-8");
		assertEquals(Ts(1878964434), b.getBidder());
		assertEquals(Td(808346218),b.getPrice());
		
		b = Bid.fromString("    300\t100 200 - Foo  ");
		assertEquals(Ts(1312439570), b.getBidder()); // fortunately no tab characters needed
		assertEquals(Td(1598668921),b.getPrice());
	}
	
	
	/// test0X: testing the Bid constructor
	
	public void test00() {
		Bid b = new Bid("Pat",100);
		assertEquals("Pat",b.getBidder());
		assertEquals(100.0d,b.getPrice());
	}
	
	public void test01() {
		Bid b = new Bid(" <no one> ", -1);
		assertEquals(" <no one> ",b.getBidder());
		assertEquals(-1.0d,b.getPrice());
	}
	
	public void test02() {
		Bid b = new Bid(null,0.0/0.0);
		assertNull(b.getBidder());
		double p =-b.getPrice();
		assertTrue(Double.isNaN(p));
	}
	
	
	/// test1X: testing Bid.fromString
	
	public void test10() {
		Bid b = Bid.fromString("100 Alice");
		assertEquals("Alice",b.getBidder());
		assertEquals(100.0d,b.getPrice());
	}
	
	public void test11() {
		Bid b = Bid.fromString(" -200 Bob ");
		assertEquals("Bob",b.getBidder());
		assertEquals(-200.0d,b.getPrice());
	}
	
	public void test12() {
		Bid b = Bid.fromString("\t30.01\tCharlie\t");
		assertEquals("Charlie",b.getBidder());
		assertEquals(30.01d,b.getPrice());
	}
	
	public void test13() {
		Bid b = Bid.fromString("    13.31    John Boyland  <boyland@uwm.edu>");
		assertEquals("John Boyland  <boyland@uwm.edu>",b.getBidder());
		assertEquals(13.31,b.getPrice());
	}
	
	public void test14() {
		Bid b = Bid.fromString("\t14.28\tK. Lee\tklee@mail.com\t991-24-9023\t");
		assertEquals("K. Lee\tklee@mail.com\t991-24-9023", b.getBidder());
		assertEquals(14.28,b.getPrice());
	}
	
	public void test15() {
		Bid b = Bid.fromString("\t  \t42  \t  Eve \t evE\t  \t");
		assertEquals("Eve \t evE",b.getBidder());
		assertEquals(42.0,b.getPrice());
	}
	
	public void test16() {
		Bid b = Bid.fromString("16.6 \t0");
		assertEquals("0", b.getBidder());
		assertEquals(16.6, b.getPrice());
	}
	
	
	public void test17() {
		assertException(NumberFormatException.class,() -> Bid.fromString("1.0x3 Jo"));
	}
	
	public void test18() {
		assertException(NumberFormatException.class, () -> Bid.fromString("1000000"));
	}
	
	public void test19() {
		assertException(NumberFormatException.class, () -> Bid.fromString("1+2 = 3"));
	}
	
	
	/// test2X: testing Bid.readBids
	
	private Bid[] bids;
	
	private static final String[] EMPTY_ARRAY = new String[0];
	
	/**
	 * Send the input to be read line by line as a series of bids.
	 * Return the error message (if any).
	 * The bid array itself is assigned to field {@link #bids}.
	 * @param input lines of input, must not be null
	 * @return error messages output.
	 */
	protected String[] callReadBids(String... input) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		for (String s: input) {
			pw.println(s);
		}
		pw.close();
		Scanner sc = new Scanner(sw.toString());
		sw = new StringWriter();
		pw = new PrintWriter(sw);
		bids = Bid.readBids(sc, pw);
		sc.close();
		pw.close();
		String[] result =  sw.toString().split("\\R"); // split on newlines
		// special case, because "split" does the wrong thing here:
		if (sw.toString().isEmpty()) return EMPTY_ARRAY;
		return result;
	}
	
	public void test20() {
		String[] errors = callReadBids("100 Alice","200 Bob","-200 Charlie");
		
		assertEquals(3,bids.length);
		assertEquals(0,errors.length);
		
		assertEquals("Alice",bids[0].getBidder());
		assertEquals(-200.0,bids[2].getPrice());
	}
	
	public void test21() {
		String[] errors = callReadBids(" -100 Alice <alice@uwm.edu>","100000","\t0 Charlie \t");

		assertEquals(2,bids.length);
		assertEquals(1,errors.length);
		
		assertEquals(-100.0,bids[0].getPrice());
		assertEquals("Charlie",bids[1].getBidder());
	}
	
	public void test22() {
		String[] errors = callReadBids("-42.1\tPat",".999 Eve","Sam 1000","","0  Hi ");

		assertEquals(3,bids.length);
		assertEquals(2,errors.length);
		
		assertEquals(-42.1,bids[0].getPrice());
		assertEquals("Hi",bids[2].getBidder());
	}
	
	
	/// test5X: testing Vickrey auction
	
	private String[] runVickreyAuction(String... input) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		for (String s: input) {
			pw.println(s);
		}
		pw.close();
		Scanner sc = new Scanner(sw.toString());
		sw = new StringWriter();
		pw = new PrintWriter(sw);
		VickreyAuction.run(sc, pw);
		sc.close();
		pw.close();
		String[] result =  sw.toString().split("\\R"); // split on newlines
		// special case, because "split" does the wrong thing here:
		if (sw.toString().isEmpty()) return EMPTY_ARRAY;
		return result;	
	}
	
	public void test50() {
		String[] result = runVickreyAuction();
		assertEquals(1,result.length); // no bids
	}
	
	public void test51() {
		String[] result = runVickreyAuction("100 Alice");
		assertEquals(1,result.length); // only one bid
	}
	
	public void test52() {
		String[] result = runVickreyAuction("100 Alice","200 Bob");
		assertEquals(2,result.length);
		assertEquals("Winner: Bob",result[0]);
		assertEquals("Price: 100.0",result[1]);
	}
	
	public void test53() {
		String[] result = runVickreyAuction("100 Alice","-200 Bob", "0 Charlie");
		assertEquals(2,result.length);
		assertEquals("Winner: Alice",result[0]);
		assertEquals("Price: 0.0",result[1]);
	}
	
	public void test54() {
		String[] result = runVickreyAuction("100 Alice", "0 Bob", "100 Charlie");
		assertEquals(2,result.length);
		assertEquals("Winner: Alice",result[0]);
		assertEquals("Price: 100.0",result[1]);
	}
	
	public void test55() {
		String[] result = runVickreyAuction("-100 Alice","-200 Bob","-300 Charlie");
		assertEquals(2,result.length);
		assertEquals("Winner: Alice",result[0]);
		assertEquals("Price: -200.0",result[1]);
	}
	
	public void test56() {
		String[] result = runVickreyAuction("10000","100 Pat","101 Sandy","1+1 Chris","110 Sam");
		assertEquals(4,result.length);
		assertEquals("Winner: Sam",result[2]);
		assertEquals("Price: 101.0",result[3]);
	}
}
