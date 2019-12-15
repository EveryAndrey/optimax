package de.optimaxenergy.auction.config;

import de.optimaxenergy.auction.auction.Auction;
import de.optimaxenergy.auction.auction.TwoPlayersAuction;
import de.optimaxenergy.auction.bidders.Bidder;
import de.optimaxenergy.auction.bidders.BidderFactory;
import de.optimaxenergy.auction.bidders.StrategyKind;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.ValidationException;
import javax.validation.Validator;
import lombok.Getter;
import lombok.Setter;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

public class AuctionConfig {

  @Getter
  private List<Auction> auctions;

  private final Validator validator;

  public AuctionConfig() {
    this.auctions = new ArrayList<>();
    this.validator = Validation.buildDefaultValidatorFactory().getValidator();
  }

  public AuctionConfig init() {
    Yaml yaml = new Yaml(new Constructor(AuctionList.class));
    InputStream inputStream = this.getClass()
        .getClassLoader()
        .getResourceAsStream("application.yml");

    AuctionList auctionList = yaml.load(inputStream);
    auctions.clear();
    auctionList.getAuctions().forEach(auctionItem -> auctions.add(createAuction(auctionItem)));
    return this;
  }

  private Auction createAuction(AuctionItem item) {

    BidderFactory bidderFactory = BidderFactory.getInstance();
    Bidder firstParticipant = bidderFactory.createBidder(item.getTotalQuantity(),
        item.getFirstParticipant().getSum(), item.getFirstParticipant().getStrategy());
    Bidder secondParticipant = bidderFactory.createBidder(item.getTotalQuantity(),
        item.getSecondParticipant().getSum(), item.getSecondParticipant().getStrategy());

    Auction auction = new TwoPlayersAuction(firstParticipant, item.getFirstParticipant().getSum(),
        secondParticipant, item.getSecondParticipant().getSum(), item.getTotalQuantity());
    Set<ConstraintViolation<Auction>> validateSet = validator.validate(auction);
    if (!validateSet.isEmpty()) {
      validateSet.forEach(auctionConstraintViolation ->
          System.out.println(auctionConstraintViolation.getMessage()));
      throw new ValidationException("Auction isn't valid!");
    }

    return auction;
  }

  @Setter
  @Getter
  public static class AuctionList {

    private List<AuctionItem> auctions;
  }

  @Setter
  @Getter
  public static class AuctionItem {

    private Integer totalQuantity;
    private ParticipantItem firstParticipant;
    private ParticipantItem secondParticipant;
  }

  @Setter
  @Getter
  public static class ParticipantItem {

    private Integer sum;
    private StrategyKind strategy;
  }

}
