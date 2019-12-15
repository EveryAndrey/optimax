package de.optimaxenergy.auction.bidders;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class AbstractBidderTest {

  AbstractBidder abstractBidder = new RandomBidder();

  @Test
  void init() {
    abstractBidder.init(100, 1000);
    Assertions.assertAll(() -> Assertions.assertEquals(abstractBidder.getQuantity(), 100),
        () -> Assertions.assertEquals(abstractBidder.getCash(), 1000));
  }

  @Test
  @DisplayName("Last step identify")
  void isTheLastStepTrue() {
    abstractBidder.init(4, 1000);
    abstractBidder.bids(0, 0);
    Assertions.assertTrue(abstractBidder.isTheLastStep());
  }

  @Test
  @DisplayName("Not last step identify")
  void isTheLastStepFalse() {
    abstractBidder.init(6, 1000);
    abstractBidder.placeBid();
    Assertions.assertFalse(abstractBidder.isTheLastStep());
  }

  @Test
  @DisplayName("Bids spread opponent win")
  void bidsOpponentWin() {
    abstractBidder.init(4, 1000);
    abstractBidder.bids(10, 50);

    Assertions.assertAll(
        () -> Assertions.assertEquals(0, abstractBidder.getAcquiredQuantity()),
        () -> Assertions.assertEquals(2, abstractBidder.getOpponentAcquiredQuantity()),
        () -> Assertions.assertEquals(1000 - 10, abstractBidder.getRestCash()));
  }

  @Test
  @DisplayName("Bids spread you win")
  void bidsYouWin() {
    abstractBidder.init(4, 1000);
    abstractBidder.bids(50, 10);
    Assertions.assertAll(
        () -> Assertions.assertEquals(2, abstractBidder.getAcquiredQuantity()),
        () -> Assertions.assertEquals(0, abstractBidder.getOpponentAcquiredQuantity()),
        () -> Assertions.assertEquals(1000 - 50, abstractBidder.getRestCash()));

  }

  @Test
  @DisplayName("Bids spread in a draw")
  void bidsInADraw() {
    abstractBidder.init(4, 1000);
    abstractBidder.bids(10, 10);
    Assertions.assertAll(
        () -> Assertions.assertEquals(1, abstractBidder.getAcquiredQuantity()),
        () -> Assertions.assertEquals(1, abstractBidder.getOpponentAcquiredQuantity()),
        () -> Assertions.assertEquals(1000 - 10, abstractBidder.getRestCash()));

  }

}