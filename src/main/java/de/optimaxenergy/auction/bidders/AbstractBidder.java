package de.optimaxenergy.auction.bidders;

import java.util.ArrayDeque;
import java.util.Deque;
import lombok.Getter;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

@Getter
public abstract class AbstractBidder implements Bidder {

    static int MAX_PRIZE = 2;

    private final Deque<Pair<Integer, Integer>> bidsHistory;
    protected int quantity;
    protected int cash;
    protected int restCash;
    private int acquiredQuantity;
    private int opponentAcquiredQuantity;

    AbstractBidder() {
        bidsHistory = new ArrayDeque<>();
    }

    public Deque<Pair<Integer, Integer>> getBidsHistory() {
        return new ArrayDeque<>(bidsHistory);
    }

    @Override
    public void init(int quantity, int cash) {
        this.quantity = quantity;
        this.cash = cash;
        this.restCash = cash;
        bidsHistory.clear();
        acquiredQuantity = 0;
        opponentAcquiredQuantity = 0;
        onInit();
    }

    protected abstract void onInit();

    @Override
    public final int placeBid() {
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
            prizeAmount = MAX_PRIZE;
        }
        if (own == other) {
            prizeAmount = 1;
        } else {
            //nothing to do here
        }
        acquiredQuantity += prizeAmount;
        opponentAcquiredQuantity += (MAX_PRIZE - prizeAmount);
    }

    protected abstract int getBid();
}
