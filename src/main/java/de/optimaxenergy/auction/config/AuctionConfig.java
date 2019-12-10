package de.optimaxenergy.auction.config;

import de.optimaxenergy.auction.auction.Auction;
import de.optimaxenergy.auction.auction.TwoPlayersAuction;
import de.optimaxenergy.auction.bidders.Bidder;
import de.optimaxenergy.auction.bidders.BidderFactory;
import de.optimaxenergy.auction.bidders.StrategyKind;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

public class AuctionConfig {

  @Getter
  @Setter
  private List<Auction> auctions;

  public AuctionConfig() {
    this.auctions = new ArrayList<>();
  }

  public AuctionConfig init() {
    Yaml yaml = new Yaml(new Constructor(AuctionList.class));
    InputStream inputStream = this.getClass()
        .getClassLoader()
        .getResourceAsStream("application.yaml");

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
