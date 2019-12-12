package de.optimaxenergy.auction.bidders;

import java.util.Random;

@Strategy(StrategyKind.SILLY)
public class SillyBidder extends AbstractBidder {

  private int bidderCount = 0;

  @Override
  protected void onInit() {
    bidderCount = 0;
  }

  @Override
  protected int getBid() {
    int bid = new Random().nextInt(restCash / (quantity - bidderCount) + 1);
    bidderCount += 2;
    return bid;
  }

  @Override
  public String toString() {
    return String.format("%s:%s", StrategyKind.SILLY.name(), cash);
  }
}
