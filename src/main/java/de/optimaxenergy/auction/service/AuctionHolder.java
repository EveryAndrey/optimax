package de.optimaxenergy.auction.service;

import de.optimaxenergy.auction.auction.Auction;
import java.util.List;

public class AuctionHolder {

  private final List<Auction> auctions;

  public AuctionHolder(List<Auction> auctions) {
    this.auctions = auctions;
  }

  public void hold() {
  }

}
