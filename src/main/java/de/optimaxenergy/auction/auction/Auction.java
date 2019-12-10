package de.optimaxenergy.auction.auction;

import java.util.concurrent.Callable;

public interface Auction extends Callable<Auction> {
    String getReport();
}
