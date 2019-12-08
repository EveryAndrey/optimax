package de.optimaxenergy.auction.bidders;

import lombok.Getter;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayDeque;
import java.util.Queue;

public abstract class AbstractBidder implements Bidder {

    protected final Queue<Pair<Integer, Integer>> bidsHistory;
    @Getter
    protected int quantity;
    @Getter
    protected int cash;
    protected int restCash;
    protected int acquiredQuantity;

    public AbstractBidder() {
        bidsHistory = new ArrayDeque<>();
    }

    @Override
    public void init(int quantity, int cash) {
        this.quantity = quantity;
        this.cash = cash;
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
        int wonAmount = 0;
        if (own > other) {
            wonAmount = 2;
        }
        if (own == other) {
            wonAmount = 1;
        } else {
            //nothing to do here
        }
        acquiredQuantity += wonAmount;
    }

    protected abstract int getBid();


}
