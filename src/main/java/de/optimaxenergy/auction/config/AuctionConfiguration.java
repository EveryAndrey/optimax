package de.optimaxenergy.auction.config;

import de.optimaxenergy.auction.auction.Auction;

import java.lang.reflect.Constructor;
import java.util.List;
import java.util.Objects;

public class AuctionConfiguration {

    private static AuctionConfiguration instance;

    List<Auction> auctions;

    private AuctionConfiguration() {
        //close it
    }

    public static synchronized AuctionConfiguration getInstance() {
        if (Objects.isNull(instance)) {
            instance = getInstanceFromYml();
        }

        return instance;
    }

    private static AuctionConfiguration getInstanceFromYml() {
        Yaml yaml = new Yaml(new Constructor(Customer.class));
    }


}
