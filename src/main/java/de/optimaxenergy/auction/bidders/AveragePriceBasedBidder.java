package de.optimaxenergy.auction.bidders;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.apache.commons.lang3.tuple.Pair;

/**
 * Strategy implementation based on reasonable price Reasonable price in this strategy equals Cash /
 * (Quantities / 2 + 1)
 */
@SuppressWarnings("Duplicates")
@Strategy(StrategyKind.AVERAGE_PRICE_BASED)
public final class AveragePriceBasedBidder extends AbstractBidder {

  protected static final int SLIDING_PRICE_DEEP = 5;
  protected static final int ZERO_STRATEGY_IDENTIFY = 2;
  protected static final int NOT_ENOUGH_STATISTIC = -1;

  private int winAmount = 0;
  private int reasonablePrice = 0;


  @Override
  public void init(int quantity, int cash) {
    super.init(quantity, cash);
    winAmount = getQuantity() / MAX_PRIZE + 1;
    computeReasonablePrice();
  }

  @Override
  protected void fillConditions() {
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

  /**
   * Check whether it appropriate to save money.
   *
   * @return Optional with the 0, if the strategy should be applied, Optional.empty - otherwise
   */
  Optional<Integer> spareMoneyCondition() {
    return (getAcquiredQuantity() - getOpponentAcquiredQuantity() > 2 * MAX_PRIZE)
        ? Optional.of(0)
        : Optional.empty();
  }

  /**
   * Check whether could we loose any move or not.
   * @return Optional with the double reasonable price if yes, Optional.empty - otherwise
   */
  Optional<Integer> cantLooseCondition() {

    return getOpponentAcquiredQuantity() - getAcquiredQuantity() ==
        (getQuantity() / 2 - getCurrentStep()) * MAX_PRIZE
        ? Optional.of(MAX_PRIZE * reasonablePrice)
        : Optional.empty();
  }

  /**
   * Check whether should we bid all money.
   * @return Optional with the rest cash if the strategy is reasonable to use,
   * Optional.empty - otherwise
   */
  Optional<Integer> allInCondition() {
    return isTheLastStep() && getAcquiredQuantity() - getOpponentAcquiredQuantity() <= 0
        ? Optional.of(getRestCash())
        : Optional.empty();
  }

  /**
   * Check whether the opponent bankrupt or saving money.
   * @return Optional with the 1 if opponent bid 0 last N moves,
   * Optional.empty - otherwise
   */
  Optional<Integer> zeroCheckCondition(int zeroStrategyIdentify) {
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

  /**
   * Check whether we should set reasonable price or it doesn't make sense.
   * @return Optional with double reasonable price in case if we predict the win,
   * Optional.empty - otherwise
   */
  Optional<Integer> averagePriceBasedCondition() {
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
