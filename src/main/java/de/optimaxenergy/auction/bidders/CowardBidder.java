package de.optimaxenergy.auction.bidders;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.lang3.tuple.Pair;

@Strategy(StrategyKind.COWARD)
public class CowardBidder extends CompetitiveBidder {

  @Override
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

    List<Integer> lastValuableBids = opponentBids.stream()
        .sorted(Comparator.reverseOrder())
        .limit(ANALYZE_DEEP).collect(Collectors.toList());

    return lastValuableBids.stream().reduce(Integer::sum)
        .map(integer -> Math.floorDiv(integer, lastValuableBids.size()))
        .orElse(NOT_ENOUGH_STATISTIC);
  }
}
