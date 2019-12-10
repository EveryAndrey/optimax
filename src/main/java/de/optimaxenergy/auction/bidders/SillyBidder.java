package de.optimaxenergy.auction.bidders;

import java.util.Random;

@Strategy(StrategyKind.SILLY)
public class SillyBidder extends AbstractBidder {

    @Override
    protected int getBid() {
        return new Random().nextInt(restCash);
    }
}
