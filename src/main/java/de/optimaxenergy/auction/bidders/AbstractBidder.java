package de.optimaxenergy.auction.bidders;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

@Getter
public abstract class AbstractBidder implements Bidder {

    static int MAX_PRIZE = 2;

    private final List<Pair<Integer, Integer>> bidsHistory;
    private int quantity;
    private int cash;
    private int restCash;
    private int acquiredQuantity;
    private int opponentAcquiredQuantity;
    private int currentStep;

    AbstractBidder() {
        bidsHistory = new ArrayList<>();
    }

    public List<Pair<Integer, Integer>> getBidsHistory() {
        return new ArrayList<>(bidsHistory);
    }

    @Override
    public void init(int quantity, int cash) {
        this.quantity = quantity;
        this.cash = cash;
        this.restCash = cash;
        bidsHistory.clear();
        acquiredQuantity = 0;
        opponentAcquiredQuantity = 0;
        currentStep = 0;
        onInit();
    }

    protected abstract void onInit();

    protected boolean isTheLastStep() {
        return quantity / 2 == currentStep + 1;
    }

    @Override
    public final int placeBid() {
        if (restCash == 0) {
            return 0;
        }

        int bid = Math.min(Math.max(getBid(), 0), restCash);
        restCash -= bid;
        currentStep++;
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
