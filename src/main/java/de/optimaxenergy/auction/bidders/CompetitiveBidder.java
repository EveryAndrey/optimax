package de.optimaxenergy.auction.bidders;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.apache.commons.lang3.tuple.Pair;

@Strategy(StrategyKind.COMPETE)
public final class CompetitiveBidder extends AbstractBidder {

  private static final int SLIDING_PRICE_DEEP = 5;
  private static final int ZERO_STRATEGY_IDENTIFY = 2;
  private static final int NOT_ENOUGH_STATISTIC = -1;

  private int winAmount = 0;
  private int reasonablePrice = 0;

  @Override
  protected void afterInit() {
    winAmount = getQuantity() / MAX_PRIZE + 1;
    computeReasonablePrice();
    addCondition(this::spareMoneyCondition);
    addCondition(this::cantLooseCondition);
    addCondition(this::allInCondition);
    addCondition(this::opponentIsBankrupt);
    addCondition(this::averagePriceBasedCondition);
  }

  @Override
  protected void beforePlaceBid() {
    computeReasonablePrice();
  }

  private void computeReasonablePrice() {
    reasonablePrice = (winAmount - getAcquiredQuantity()) <= 0
        ? 0
        : Math.floorDiv(getRestCash(), (winAmount - getAcquiredQuantity()));
  }


  private Optional<Integer> spareMoneyCondition() {
    return (getAcquiredQuantity() - getOpponentAcquiredQuantity() > 2 * MAX_PRIZE)
        ? Optional.of(0)
        : Optional.empty();
  }

  private Optional<Integer> cantLooseCondition() {

    return getOpponentAcquiredQuantity() - getAcquiredQuantity() + MAX_PRIZE ==
        (getQuantity() / 2 - getCurrentStep()) * MAX_PRIZE
        ? Optional.of(MAX_PRIZE * reasonablePrice)
        : Optional.empty();
  }

  private Optional<Integer> allInCondition() {
    return isTheLastStep() && getAcquiredQuantity() - getOpponentAcquiredQuantity() <= 0
        ? Optional.of(getRestCash())
        : Optional.empty();
  }

  private Optional<Integer> opponentIsBankrupt() {
    List<Integer> opponentBids = getBidsHistory().stream().map(Pair::getRight).collect(
        Collectors.toList());
    if (opponentBids.size() < ZERO_STRATEGY_IDENTIFY) {
      return Optional.empty();
    }

    int sum = 0;
    for (int i = opponentBids.size() - 1;
        i >= 0 && opponentBids.size() - i <= ZERO_STRATEGY_IDENTIFY; i--) {
      sum += opponentBids.get(i);
    }

    return sum == 0 ? Optional.of(1) : Optional.empty();
  }

  private Optional<Integer> averagePriceBasedCondition() {
    List<Pair<Integer, Integer>> bidsHistory = getBidsHistory();

    if (bidsHistory.size() < SLIDING_PRICE_DEEP) {
      return Optional.of(MAX_PRIZE * reasonablePrice);
    }

    int predictableBid = getOpponentAveragePrice(bidsHistory.stream().map(Pair::getRight).collect(
        Collectors.toList()));

    if (predictableBid > MAX_PRIZE * reasonablePrice) {
      return Optional.of(0);
    } else if (predictableBid == 0) {
      return Optional.of(1);
    } else {
      return Optional.of(MAX_PRIZE * reasonablePrice);
    }
  }

  private int getOpponentAveragePrice(List<Integer> opponentBids) {
    List<Integer> valuableBids = opponentBids.stream().filter(integer -> integer != 0).collect(
        Collectors.toList());

    int sum = 0;
    int count = 0;
    for (int i = valuableBids.size() - 1; i >= 0 && valuableBids.size() - i <= SLIDING_PRICE_DEEP;
        i--) {
      sum += valuableBids.get(i);
      count++;
    }
    return count == 0 ? NOT_ENOUGH_STATISTIC : Math.floorDiv(sum, count);
  }

  @Override
  public String toString() {
    return String.format("%s:%s", StrategyKind.COMPETE.name(), getCash());
  }
}
