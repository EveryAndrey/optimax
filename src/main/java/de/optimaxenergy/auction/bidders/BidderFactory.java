package de.optimaxenergy.auction.bidders;

public class BidderFactory {

    public static Bidder createBidder(int quantity, int cash) {
        SillyBidder sillyBidder = new SillyBidder();
        sillyBidder.init(quantity, cash);
    }

}
