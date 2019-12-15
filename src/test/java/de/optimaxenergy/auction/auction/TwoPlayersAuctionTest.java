package de.optimaxenergy.auction.auction;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import de.optimaxenergy.auction.bidders.Bidder;
import de.optimaxenergy.auction.exceptions.InvalidAuctionBehavior;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class TwoPlayersAuctionTest {

  private static Integer CASH = 1000;
  private static Integer QUANTITY = 100;
  private static TwoPlayersAuction twoPlayersAuction;
  private Bidder participantOne = mock(Bidder.class);
  private Bidder participantTwo = mock(Bidder.class);

  @BeforeEach
  void init() {
    twoPlayersAuction = new TwoPlayersAuction(participantOne, CASH, participantTwo, CASH, QUANTITY);
  }

  @Test
  @DisplayName("Double call the same auction")
  void callDouble() {

    when(participantOne.placeBid()).thenReturn(10);
    when(participantTwo.placeBid()).thenReturn(10);
    twoPlayersAuction.call();
    Assertions.assertThrows(InvalidAuctionBehavior.class, () -> twoPlayersAuction.call());

  }

  @Test
  @DisplayName("First player win")
  void callFirstPlayerWin() {
    when(participantOne.placeBid()).thenReturn(20);
    when(participantTwo.placeBid()).thenReturn(10);
    twoPlayersAuction.call();
    Assertions.assertTrue(twoPlayersAuction.getReport(false)
        .contains("The first player's won"));
  }

  @Test
  @DisplayName("Second player win")
  void callSecondPlayerWin() {
    when(participantOne.placeBid()).thenReturn(10);
    when(participantTwo.placeBid()).thenReturn(20);
    twoPlayersAuction.call();
    Assertions.assertTrue(twoPlayersAuction.getReport(false)
        .contains("The second player's won"));
  }

  @Test
  @DisplayName("In a draw")
  void callInADraw() {
    when(participantOne.placeBid()).thenReturn(10);
    when(participantTwo.placeBid()).thenReturn(10);
    twoPlayersAuction.call();
    Assertions.assertTrue(twoPlayersAuction.getReport(false)
        .contains("has finished in a draw"));
  }


}