package de.optimaxenergy.auction.bidders;

import static de.optimaxenergy.auction.bidders.StrategyKind.CONTRA_AVERAGE_PRICE;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.apache.commons.lang3.tuple.Pair;

/**
 * Strategy implementation based on contra reasonable price The main idea here is that, when you bid
 * more than average price, the AveragePriceBasedBidder looses the move that leads to more intensive
 * decreasing average price for the opponent.
 */
@SuppressWarnings("Duplicates")
@Strategy(CONTRA_AVERAGE_PRICE)
public final class ContraAveragePriceBasedBidder extends AbstractBidder {

  private static final int ZERO_STRATEGY_IDENTIFY = 2;
  private static final double UPPER_AVERAGE_PRICE_COEFF = 0.2;

  private int winAmount = 0;

  @Override
  protected void afterInit() {
    winAmount = getQuantity() / MAX_PRIZE + 1;
    addCondition(() -> zeroCheckCondition(ZERO_STRATEGY_IDENTIFY));
    addCondition(this::counterAveragePriceBasedCondition);

  }

  /**
   * Check whether the opponent bankrupt or saving money.
   *
   * @return Optional with the 1 if opponent bid 0 last N moves, Optional.empty - otherwise
   */
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

  /**
   * This strategy tries to find the bid which would be higher than average, but less than maximum
   * X, which allow to be in better condition on the next move.
   *
   * @return Optional with bid. Optional.empty is not possible here.
   */
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
    return String.format("%s:%s", StrategyKind.CONTRA_AVERAGE_PRICE.name(), getCash());
  }

}
