package de.optimaxenergy.auction.bidders;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.apache.commons.lang3.tuple.Pair;

@Strategy(StrategyKind.AGGRESSIVE_COMPETE)
public class AggressiveCompetitiveBidder extends AbstractBidder {

  private static final int SLIDING_PRICE_DEEP = 5;
  private static final int NOT_ENOUGH_STATISTIC = -1;

  private int winAmount = 0;
  private int reasonablePrice = 0;

  @Override
  protected void afterInit() {
    winAmount = getQuantity() / 2 + 1;
    computeReasonablePrice();
    addCondition(this::spareMoneyCondition);
    addCondition(this::averagePriceBasedCondition);

  }

  @Override
  protected void beforePlaceBid() {
    computeReasonablePrice();
  }


  void computeReasonablePrice() {
    reasonablePrice = (winAmount - getAcquiredQuantity()) <= 0
        ? 0
        : (int) Math.ceil((double) getRestCash() / (winAmount - getAcquiredQuantity()));
  }

  private Optional<Integer> spareMoneyCondition() {
    return (getAcquiredQuantity() - getOpponentAcquiredQuantity() > MAX_PRIZE)
        ? Optional.of(0)
        : Optional.empty();
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
    return String.format("%s:%s", StrategyKind.AGGRESSIVE_COMPETE.name(), getCash());
  }
}
