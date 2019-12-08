package de.optimaxenergy.auction.bidders;

public interface Bidder {
    void init(int quantity, int cash);

    int placeBid();

    void bids(int own, int other);
}
