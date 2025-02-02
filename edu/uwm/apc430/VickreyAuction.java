package edu.uwm.apc430;

import java.io.PrintWriter;
import java.util.Scanner;

/**
 * A class to implement an auction using sealed secret bids in which
 * the winner (highest bidder) pays the <i>second</i> highest bid.
 * @see https://en.wikipedia.org/wiki/Vickrey_auction
 */
public class VickreyAuction {
	public static void main(String[] args) {
		try (Scanner input = new Scanner(System.in)) {
			try (PrintWriter pw = new PrintWriter(System.out)) {
				run(input,pw);
			}
		}
	}
	
	/**
	 * Run an auction reading bids from the input and writing messages to the output.
	 * @param input source of bids
	 * @param output place to report results.
	 */
	public static void run(Scanner input, PrintWriter output) {
		// TODO: 
		// 1. read bids
		// 2. check that we have enough bids
		// 3. figure out the winner and print the price they pay
	}
}
