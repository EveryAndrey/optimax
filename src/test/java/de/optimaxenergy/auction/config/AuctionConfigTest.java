package de.optimaxenergy.auction.config;

import javax.validation.ValidationException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AuctionConfigTest {

  private AuctionConfig auctionConfig;

  @BeforeEach
  void preInit() {
    auctionConfig = new AuctionConfig();
  }

  @Test
  void initValidAuctions() {
    AuctionConfig auction = auctionConfig.init("application_valid.yml");
    Assertions.assertEquals(2, auction.getAuctions().size());
  }

  @Test
  void initAuctionHasOddQuantities() {
    Assertions.assertThrows(ValidationException.class, () -> auctionConfig.init(
        "application_odd_quantities.yml"));
  }

  @Test
  void initAuctionHasUnknownStrategy() {
    Assertions.assertThrows(RuntimeException.class, () -> auctionConfig.init(
        "application_unknown_strategy.yml"));
  }

}