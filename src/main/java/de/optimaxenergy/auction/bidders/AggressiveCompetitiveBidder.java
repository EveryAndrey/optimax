package de.optimaxenergy.auction.bidders;

@Strategy(StrategyKind.AGGRESSIVE_COMPETE)
public class AggressiveCompetitiveBidder extends CompetitiveBidder {

  @Override
  protected void computeReasonablePrice() {
    reasonablePrice = (winAmount - getAcquiredQuantity()) <= 0
        ? 0
        : (int) Math.ceil((double) restCash / (winAmount - getAcquiredQuantity()));
  }
}
