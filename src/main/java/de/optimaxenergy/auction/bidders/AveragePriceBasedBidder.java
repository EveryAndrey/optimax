package de.optimaxenergy.auction.bidders;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.apache.commons.lang3.tuple.Pair;

@SuppressWarnings("Duplicates")
@Strategy(StrategyKind.AVERAGE_PRICE_BASED)
public final class AveragePriceBasedBidder extends AbstractBidder {

  protected static final int SLIDING_PRICE_DEEP = 5;
  protected static final int ZERO_STRATEGY_IDENTIFY = 2;
  protected static final int NOT_ENOUGH_STATISTIC = -1;

  private int winAmount = 0;
  private int reasonablePrice = 0;

  @Override
  protected void afterInit() {
    winAmount = getQuantity() / MAX_PRIZE + 1;
    computeReasonablePrice();
    addCondition(this::spareMoneyCondition);
    addCondition(this::cantLooseCondition);
    addCondition(this::allInCondition);
    addCondition(() -> zeroCheckCondition(ZERO_STRATEGY_IDENTIFY));
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


  protected Optional<Integer> spareMoneyCondition() {
    return (getAcquiredQuantity() - getOpponentAcquiredQuantity() > 2 * MAX_PRIZE)
        ? Optional.of(0)
        : Optional.empty();
  }

  protected Optional<Integer> cantLooseCondition() {

    return getOpponentAcquiredQuantity() - getAcquiredQuantity() ==
        (getQuantity() / 2 - getCurrentStep()) * MAX_PRIZE
        ? Optional.of(MAX_PRIZE * reasonablePrice)
        : Optional.empty();
  }

  protected Optional<Integer> allInCondition() {
    return isTheLastStep() && getAcquiredQuantity() - getOpponentAcquiredQuantity() <= 0
        ? Optional.of(getRestCash())
        : Optional.empty();
  }

  protected Optional<Integer> zeroCheckCondition(int zeroStrategyIdentify) {
    List<Integer> opponentBids = getBidsHistory().stream().map(Pair::getRight).collect(
        Collectors.toList());
    if (opponentBids.size() < zeroStrategyIdentify) {
      return Optional.empty();
    }

    int sum = 0;
    for (int i = opponentBids.size() - 1;
        i >= 0 && opponentBids.size() - i <= zeroStrategyIdentify; i--) {
      sum += opponentBids.get(i);
    }

    return sum == 0 ? Optional.of(1) : Optional.empty();
  }

  protected Optional<Integer> averagePriceBasedCondition() {
    List<Pair<Integer, Integer>> bidsHistory = getBidsHistory();

    if (bidsHistory.size() < SLIDING_PRICE_DEEP) {
      return Optional.of(MAX_PRIZE * reasonablePrice);
    }

    int predictableBid = getOpponentAveragePrice(bidsHistory.stream().map(Pair::getRight).collect(
        Collectors.toList()));

    if (predictableBid > MAX_PRIZE * reasonablePrice) {
      return Optional.of(0);
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
    return String.format("%s:%s", StrategyKind.AVERAGE_PRICE_BASED.name(), getCash());
  }
}
