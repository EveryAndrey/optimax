package de.optimaxenergy.auction.bidders;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Callable;
import lombok.Getter;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

@Getter
public abstract class AbstractBidder implements Bidder {

  static int MAX_PRIZE = 2;

  private final List<Pair<Integer, Integer>> bidsHistory;
  private final List<Callable<Optional<Integer>>> conditions;
  private int quantity;
  private int cash;
  private int restCash;
  private int acquiredQuantity;
  private int opponentAcquiredQuantity;
  private int currentStep;

  AbstractBidder() {
    this.bidsHistory = new ArrayList<>();
    this.conditions = new ArrayList<>();
  }

  public List<Pair<Integer, Integer>> getBidsHistory() {
    return new ArrayList<>(bidsHistory);
  }

  @Override
  public void init(int quantity, int cash) {
    beforeInit();
    this.quantity = quantity;
    this.cash = cash;
    this.restCash = cash;
    clearInternalAttributes();
    afterInit();
  }

  protected void beforeInit() {
  }

  protected void afterInit() {
  }

  private void clearInternalAttributes() {
    this.bidsHistory.clear();
    this.conditions.clear();
    this.acquiredQuantity = 0;
    this.opponentAcquiredQuantity = 0;
    this.currentStep = 0;
  }

  @Override
  public final int placeBid() {
    beforePlaceBid();
    if (restCash == 0) {
      return 0;
    }

    int bid = Math.min(Math.max(getBid(), 0), restCash);
    restCash -= bid;
    currentStep++;
    return bid;
  }

  protected void beforePlaceBid() {
  }

  private int getBid() {
    for (var optionalCallable : conditions) {
      try {
        Optional<Integer> call = optionalCallable.call();
        if (call.isPresent()) {
          return call.get();
        }
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    }
    return 0;
  }

  protected final boolean isTheLastStep() {
    return quantity / 2 == currentStep + 1;
  }

  protected void addCondition(Callable<Optional<Integer>> condition) {
    this.conditions.add(condition);
  }

  @Override
  public void bids(int own, int other) {
    bidsHistory.add(new ImmutablePair<>(own, other));
    int prizeAmount;
    if (own > other) {
      prizeAmount = MAX_PRIZE;
    } else if (own == other) {
      prizeAmount = 1;
    } else {
      prizeAmount = 0;
    }
    acquiredQuantity += prizeAmount;
    opponentAcquiredQuantity += (MAX_PRIZE - prizeAmount);
  }

}
