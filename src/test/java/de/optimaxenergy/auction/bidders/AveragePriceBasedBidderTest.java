package de.optimaxenergy.auction.bidders;

import static de.optimaxenergy.auction.bidders.AveragePriceBasedBidder.SLIDING_PRICE_DEEP;
import static de.optimaxenergy.auction.bidders.AveragePriceBasedBidder.ZERO_STRATEGY_IDENTIFY;

import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class AveragePriceBasedBidderTest {

  private static Integer QUANTITY = 100;
  private static Integer CASH = 10_000;

  private AveragePriceBasedBidder averagePriceBasedBidder;

  @BeforeEach
  void init() {
    averagePriceBasedBidder = new AveragePriceBasedBidder();
    averagePriceBasedBidder.init(QUANTITY, CASH);
  }

  @Test
  @DisplayName("Spare money condition strategy switched on")
  void spareMoneyConditionOn() {
    averagePriceBasedBidder.bids(10, 0);
    averagePriceBasedBidder.bids(10, 0);
    averagePriceBasedBidder.bids(10, 0);
    Assertions.assertEquals(0, averagePriceBasedBidder.spareMoneyCondition().get());
  }

  @Test
  @DisplayName("Spare money condition strategy switched off")
  void spareMoneyConditionOff() {
    averagePriceBasedBidder.bids(10, 0);
    averagePriceBasedBidder.bids(10, 0);
    Assertions.assertEquals(Optional.empty(), averagePriceBasedBidder.spareMoneyCondition());
  }

  @Test
  @DisplayName("Can't loose condition switched on")
  void cantLooseConditionOn() {
    for (int i = 0; i < QUANTITY / 4; i++) {
      averagePriceBasedBidder.bids(0, 10);
    }

    Assertions.assertEquals(2 * Math.floorDiv(CASH, 51),
        averagePriceBasedBidder.cantLooseCondition().get());
  }

  @Test
  @DisplayName("Can't loose condition switched off")
  void cantLooseConditionOff() {
    for (int i = 0; i < QUANTITY / 4 - 1; i++) {
      averagePriceBasedBidder.bids(0, 10);
    }

    Assertions.assertEquals(Optional.empty(),
        averagePriceBasedBidder.cantLooseCondition());
  }

  @Test
  void allInConditionOn() {
    for (int i = 0; i < QUANTITY / 2 - 1; i++) {
      averagePriceBasedBidder.bids(0, 0);
    }

    Assertions.assertEquals(CASH,
        averagePriceBasedBidder.allInCondition().get());
  }

  @Test
  void allInConditionOff() {
    for (int i = 0; i < QUANTITY / 2 - 2; i++) {
      averagePriceBasedBidder.bids(0, 0);
    }

    Assertions.assertEquals(Optional.empty(),
        averagePriceBasedBidder.allInCondition());

  }

  @Test
  void zeroCheckConditionOn() {
    averagePriceBasedBidder.bids(10, 0);
    averagePriceBasedBidder.bids(10, 0);

    Assertions.assertEquals(1,
        averagePriceBasedBidder.zeroCheckCondition(ZERO_STRATEGY_IDENTIFY).get());

  }

  @Test
  void zeroCheckConditionOff() {
    averagePriceBasedBidder.bids(10, 0);
    averagePriceBasedBidder.bids(10, 0);
    averagePriceBasedBidder.bids(10, 10);

    Assertions.assertEquals(Optional.empty(),
        averagePriceBasedBidder.zeroCheckCondition(2));

  }

  @Test
  void averagePriceBasedConditionNoEnoughHistory() {
    averagePriceBasedBidder.bids(0, 0);
    Assertions.assertEquals(2 * Math.floorDiv(CASH, 51),
        averagePriceBasedBidder.averagePriceBasedCondition().get());
  }

  @Test
  void averagePriceBasedConditionOpponentTooRich() {
    for (int i = 0; i < SLIDING_PRICE_DEEP; i++) {
      averagePriceBasedBidder.bids(0, CASH);
    }
    Assertions.assertEquals(0,
        averagePriceBasedBidder.averagePriceBasedCondition().get());
  }

  @Test
  void averagePriceBasedConditionOpponentIsWeaker() {
    for (int i = 0; i < SLIDING_PRICE_DEEP; i++) {
      averagePriceBasedBidder.bids(0, (int) Math.floor(CASH - 0.5 * CASH) / QUANTITY / 2);
    }
    Assertions.assertEquals(2 * Math.floorDiv(CASH, 51),
        averagePriceBasedBidder.averagePriceBasedCondition().get());
  }


}