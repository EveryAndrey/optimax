package de.optimaxenergy.auction.bidders;

@Strategy(StrategyKind.COMPETE)
public class CompetitiveBidder extends AbstractBidder {


  @Override
  protected int getBid() {
    return 0;
  }

    @Override
    public String toString() {
      return String.format("%s:%s", StrategyKind.COMPETE.name(), cash);
    }
}
