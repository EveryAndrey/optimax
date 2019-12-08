package de.optimaxenergy.auction.auction;

import de.optimaxenergy.auction.bidders.Bidder;

public interface Auction extends Runnable {
    Bidder getWinner();

    String getReport();
}
