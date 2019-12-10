package de.optimaxenergy.auction.bidders;

import lombok.Getter;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Getter
public abstract class AbstractBidder implements Bidder {

    private final List<Pair<Integer, Integer>> bidsHistory;
    protected int quantity;
    protected int cash;
    protected int restCash;
    private int acquiredQuantity;

    AbstractBidder() {
        bidsHistory = new ArrayList<>();
    }

    public List<Pair<Integer, Integer>> getBidsHistory() {
        return Collections.unmodifiableList(bidsHistory);
    }

    @Override
    public void init(int quantity, int cash) {
        this.quantity = quantity;
        this.cash = cash;
        this.restCash = cash;
    }

    @Override
    public int placeBid() {
        if (restCash == 0) {
            return 0;
        }

        int bid = Math.min(getBid(), restCash);
        restCash -= bid;
        return bid;
    }

    @Override
    public void bids(int own, int other) {
        bidsHistory.add(new ImmutablePair<>(own, other));
        int prizeAmount = 0;
        if (own > other) {
            prizeAmount = 2;
        }
        if (own == other) {
            prizeAmount = 1;
        } else {
            //nothing to do here
        }
        acquiredQuantity += prizeAmount;
    }


    protected abstract int getBid();


}
