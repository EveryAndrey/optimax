package de.optimaxenergy.auction.bidders;

import static de.optimaxenergy.auction.bidders.AveragePriceBasedBidder.ZERO_STRATEGY_IDENTIFY;

import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ContraAveragePriceBasedBidderTest {

  private static Integer QUANTITY = 100;
  private static Integer CASH = 1000;

  private ContraAveragePriceBasedBidder contraAveragePriceBasedBidder;

  @BeforeEach
  void preTestInit() {
    contraAveragePriceBasedBidder = new ContraAveragePriceBasedBidder();
  }

  @Test
  @DisplayName("Check win amount calculation")
  void init() {
    contraAveragePriceBasedBidder.init(QUANTITY, CASH);
    Assertions.assertEquals(QUANTITY / 2 + 1, contraAveragePriceBasedBidder.winAmount);
  }

  @Test
  void fillConditions() {
    contraAveragePriceBasedBidder.fillConditions();
    Assertions.assertEquals(2, contraAveragePriceBasedBidder.getConditions().size());
  }

  @Test
  void zeroCheckCondition() {
    for (int i = 0; i < ContraAveragePriceBasedBidder.ZERO_STRATEGY_IDENTIFY; i++) {
      contraAveragePriceBasedBidder.bids(10, 0);
    }

    Assertions.assertEquals(1,
        contraAveragePriceBasedBidder.zeroCheckCondition(ZERO_STRATEGY_IDENTIFY).get());
  }

  @Test
  void counterAveragePriceBasedCondition() {
    contraAveragePriceBasedBidder.init(QUANTITY, CASH);
    Optional<Integer> firstBid = contraAveragePriceBasedBidder.contraAveragePriceBasedCondition();
    contraAveragePriceBasedBidder.bids(firstBid.get(), firstBid.get() - 1);
    Optional<Integer> secondBid = contraAveragePriceBasedBidder.contraAveragePriceBasedCondition();
    contraAveragePriceBasedBidder.bids(secondBid.get(), secondBid.get() - 1);
    Assertions.assertAll(() -> Assertions.assertEquals(45, firstBid.get()),
        () -> Assertions.assertEquals(45, secondBid.get()));
  }
}