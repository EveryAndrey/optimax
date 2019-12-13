package de.optimaxenergy.auction.bidders;

import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.lang3.tuple.Pair;

@Strategy(StrategyKind.AGGRESSIVE_COMPETE)
public class AggressiveCompetitiveBidder extends AbstractBidder {

  private static final int ANALYZE_DEEP = 5;
  private static final int NOT_ENOUGH_STATISTIC = -1;

  private int winAmount = 0;
  private int reasonablePrice = 0;

  @Override
  protected void onInit() {
    winAmount = getQuantity() / 2 + 1;
    computeReasonablePrice();
  }

  void computeReasonablePrice() {
    reasonablePrice = (winAmount - getAcquiredQuantity()) <= 0
        ? 0
        : (int) Math.ceil((double) getRestCash() / (winAmount - getAcquiredQuantity()));
  }

  @Override
  protected int getBid() {
    computeReasonablePrice();

    if (getAcquiredQuantity() - getOpponentAcquiredQuantity() > MAX_PRIZE) {
      return 0;
    }

    int predictableBid = getPredictableBid();

    if (predictableBid > reasonablePrice) {
      return 0;
    } else {
      return 2 * reasonablePrice;
    }

  }

  protected int getPredictableBid() {

    List<Pair<Integer, Integer>> bidsHistory = getBidsHistory();
    if (bidsHistory.size() < ANALYZE_DEEP) {
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
    int i = 0;
    for (i = valuableBids.size() - 1; i >= 0 && valuableBids.size() - i > ANALYZE_DEEP; i--) {
      sum += opponentBids.get(i);
    }
    return sum == 0 ? NOT_ENOUGH_STATISTIC : Math.floorDiv(sum, valuableBids.size() - i);
  }

  protected boolean isTheOpponentBankrupt(List<Integer> opponentBids) {
    int sum = 0;
    for (int i = opponentBids.size() - 1; i >= 0 && opponentBids.size() - i > ANALYZE_DEEP; i--) {
      sum += opponentBids.get(i);
    }
    return sum == 0;
  }


  @Override
  public String toString() {
    return String.format("%s:%s", StrategyKind.AGGRESSIVE_COMPETE.name(), getCash());
  }
}
