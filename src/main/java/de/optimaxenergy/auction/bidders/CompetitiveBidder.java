package de.optimaxenergy.auction.bidders;

@Strategy(StrategyKind.COMPETE)
public class CompetitiveBidder extends AbstractBidder {

  @Override
  protected int getBid() {
    return 0;
  }
}
