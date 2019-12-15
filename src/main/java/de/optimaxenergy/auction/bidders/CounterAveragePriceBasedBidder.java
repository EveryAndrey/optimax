package de.optimaxenergy.auction.bidders;

import static de.optimaxenergy.auction.bidders.StrategyKind.COUNTER_AVERAGE_PRICE;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.apache.commons.lang3.tuple.Pair;

@Strategy(COUNTER_AVERAGE_PRICE)
public final class CounterAveragePriceBasedBidder extends AbstractBidder {

  private static final int ZERO_STRATEGY_IDENTIFY = 2;
  private static final double UPPER_AVERAGE_PRICE_COEFF = 0.2;

  private int winAmount = 0;

  @Override
  protected void afterInit() {
    winAmount = getQuantity() / MAX_PRIZE + 1;
    addCondition(() -> zeroCheckCondition(ZERO_STRATEGY_IDENTIFY));
    addCondition(this::counterAveragePriceBasedCondition);

  }

  private Optional<Integer> zeroCheckCondition(int zeroStrategyIdentify) {
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

  private Optional<Integer> counterAveragePriceBasedCondition() {
    int averagePrice = getAveragePrice();
    int optimisticUpBorder = getOptimisticUpBorder();
    int bidPrice = getBidPrice(averagePrice, optimisticUpBorder);

    return Optional.of(bidPrice);
  }

  private int getBidPrice(int averagePrice, int optimisticUpBorder) {
    if (optimisticUpBorder < 2 * averagePrice) {
      return 2 * averagePrice;
    }

    return (int) Math.floor(2 * averagePrice +
        UPPER_AVERAGE_PRICE_COEFF * (optimisticUpBorder - 2 * averagePrice));

  }

  private int getOptimisticUpBorder() {

    if ((winAmount - getAcquiredQuantity()) <= 0) {
      return 0;
    }

    return (int) Math.floor(getRestCash() -
        getRestCash() * Math.pow(getQuantity() - 2 * getAcquiredQuantity() - 2, 2) /
            Math.pow(getQuantity() - 2 * getAcquiredQuantity() + 2, 2));
  }

  private int getAveragePrice() {
    return (winAmount - getAcquiredQuantity()) <= 0
        ? 0
        : Math.floorDiv(getRestCash(), (winAmount - getAcquiredQuantity()));
  }


  @Override
  public String toString() {
    return String.format("%s:%s", StrategyKind.COUNTER_AVERAGE_PRICE.name(), getCash());
  }

}
