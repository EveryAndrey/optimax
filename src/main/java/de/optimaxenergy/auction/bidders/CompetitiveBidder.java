package de.optimaxenergy.auction.bidders;

import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.lang3.tuple.Pair;

@Strategy(StrategyKind.COMPETE)
public class CompetitiveBidder extends AbstractBidder {

  protected static final int SLIDING_PRICE_DEEP = 5;
  protected static final int ZERO_STRATEGY_IDENTIFY = 2;
  protected static final int NOT_ENOUGH_STATISTIC = -1;

  protected int winAmount = 0;
  protected int reasonablePrice = 0;

  @Override
  protected void onInit() {
    winAmount = getQuantity() / MAX_PRIZE + 1;
    computeReasonablePrice();
  }

  protected void computeReasonablePrice() {
    reasonablePrice = (winAmount - getAcquiredQuantity()) <= 0
        ? 0
        : (int) Math.ceil((double) getRestCash() / (winAmount - getAcquiredQuantity()));
  }

  @Override
  protected int getBid() {
    computeReasonablePrice();

    if (isReasonableToSpareMoney()) {
      return 0;
    }

    if (cantLooseAnymore()) {
      return MAX_PRIZE * reasonablePrice;
    }

    if (isTheLastStep() && getAcquiredQuantity() - getOpponentAcquiredQuantity() <= 0) {
      return getRestCash();
    }

    int predictableBid = getPredictableBid();

    if (predictableBid == NOT_ENOUGH_STATISTIC) {
      return MAX_PRIZE * reasonablePrice;
    }

    if (predictableBid > MAX_PRIZE * reasonablePrice) {
      return 0;
    } else if (predictableBid == 0) {
      return 1;
    } else {
      return Math.min(MAX_PRIZE * reasonablePrice, predictableBid + 1);
    }
  }

  private boolean cantLooseAnymore() {
    return getOpponentAcquiredQuantity() - getAcquiredQuantity() + MAX_PRIZE ==
        (getQuantity() / 2 - getCurrentStep()) * MAX_PRIZE;
  }

  private boolean isReasonableToSpareMoney() {
    return getAcquiredQuantity() - getOpponentAcquiredQuantity() > 2 * MAX_PRIZE;
  }

  protected int getPredictableBid() {

    List<Pair<Integer, Integer>> bidsHistory = getBidsHistory();
    if (bidsHistory.size() < SLIDING_PRICE_DEEP) {
      return NOT_ENOUGH_STATISTIC;
    }

    List<Integer> opponentBids = bidsHistory.stream().map(Pair::getRight).collect(
        Collectors.toList());

    if (isTheOpponentBankrupt(opponentBids)) {
      return 0;
    }

    List<Integer> valuableBids = opponentBids.stream().filter(integer -> integer != 0).collect(
        Collectors.toList());

    int sum = 0;
    int count = 0;
    for (int i = valuableBids.size() - 1; i >= 0 && valuableBids.size() - i <= SLIDING_PRICE_DEEP;
        i--) {
      sum += valuableBids.get(i);
      count++;
    }
    return sum == 0 ? NOT_ENOUGH_STATISTIC : Math.floorDiv(sum, count);
  }

  protected boolean isTheOpponentBankrupt(List<Integer> opponentBids) {
    int sum = 0;
    for (int i = opponentBids.size() - 1;
        i >= 0 && opponentBids.size() - i <= ZERO_STRATEGY_IDENTIFY; i--) {
      sum += opponentBids.get(i);
    }
    return sum == 0;
  }

  @Override
    public String toString() {
    return String.format("%s:%s", StrategyKind.COMPETE.name(), getCash());
    }
}
