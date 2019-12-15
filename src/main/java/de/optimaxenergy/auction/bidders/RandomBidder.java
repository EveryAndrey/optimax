package de.optimaxenergy.auction.bidders;

import java.util.Optional;
import java.util.Random;

@Strategy(StrategyKind.RANDOM)
public class RandomBidder extends AbstractBidder {

  private static final float MIN_BOUND_PERCENT = 0.8f;

  @Override
  protected void afterInit() {
    addCondition(this::getBidCondition);
  }

  private Optional<Integer> getBidCondition() {
    int needToBuy = getQuantity() / MAX_PRIZE + 1 - getAcquiredQuantity();
    int maxBid = needToBuy == 0 ? 0 : MAX_PRIZE * Math.floorDiv(getRestCash(), needToBuy);

    return Optional.of(new Random().nextInt(maxBid - (int) Math.floor(maxBid * MIN_BOUND_PERCENT)) +
        (int) Math.floor(maxBid * MIN_BOUND_PERCENT));
  }

  @Override
  public String toString() {
    return String.format("%s:%s", StrategyKind.RANDOM.name(), getCash());
  }
}
