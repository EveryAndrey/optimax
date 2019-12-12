package de.optimaxenergy.auction.bidders;

import java.util.ArrayDeque;
import java.util.Comparator;
import java.util.Deque;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.lang3.tuple.Pair;

@Strategy(StrategyKind.COMPETE)
public class CompetitiveBidder extends AbstractBidder {

  protected static final int ANALYZE_DEEP = 5;
  protected static final int NOT_ENOUGH_STATISTIC = -1;

  protected int winAmount = 0;
  protected int reasonablePrice = 0;

  @Override
  protected void onInit() {
    winAmount = quantity / 2 + 1;
    computeReasonablePrice();
  }

  protected void computeReasonablePrice() {
    reasonablePrice = (winAmount - getAcquiredQuantity()) <= 0
        ? 0
        : Math.floorDiv(restCash, winAmount - getAcquiredQuantity());
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
      return reasonablePrice;
    }

  }

  protected int getPredictableBid() {

    Deque<Pair<Integer, Integer>> bidsHistory = getBidsHistory();
    if (bidsHistory.size() < ANALYZE_DEEP) {
      return NOT_ENOUGH_STATISTIC;
    }

    ArrayDeque<Integer> opponentBids = bidsHistory.stream().map(Pair::getRight).collect(
        Collectors.toCollection(ArrayDeque::new));

    if (isTheOpponentBankrupt(opponentBids)) {
      return 0;
    }

    List<Integer> lastValuableBids = opponentBids.stream().filter(integer -> integer != 0)
        .sorted(Comparator.reverseOrder())
        .limit(ANALYZE_DEEP).collect(Collectors.toList());

    return lastValuableBids.stream().reduce(Integer::sum)
        .map(integer -> Math.floorDiv(integer, lastValuableBids.size()))
        .orElse(NOT_ENOUGH_STATISTIC);
  }

  protected boolean isTheOpponentBankrupt(ArrayDeque<Integer> opponentBids) {
    opponentBids.descendingIterator().forEachRemaining();
    return opponentBids.stream().sorted(Comparator.reverseOrder()).limit(ANALYZE_DEEP)
        .allMatch(integer -> integer == 0);
  }

  @Override
    public String toString() {
      return String.format("%s:%s", StrategyKind.COMPETE.name(), cash);
    }
}
