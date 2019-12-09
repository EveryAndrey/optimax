package de.optimaxenergy.auction.auction;

import de.optimaxenergy.auction.bidders.Bidder;
import de.optimaxenergy.auction.validation.Even;
import lombok.Getter;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Getter
public class TwoPlayersAuction implements Auction {
    @NotNull
    private final Bidder firstParticipant;
    @NotNull
    private final Bidder secondParticipant;
    @Positive
    @Even
    private final int totalQuantity;
    private final Pair<Integer, Integer> firstParticipantPosition;
    private final Pair<Integer, Integer> secondParticipantPosition;
    private boolean isAuctionFinished;

    public TwoPlayersAuction(Bidder firstParticipant, int firstParticipantCash, Bidder secondParticipant,
                             int secondParticipantCash,
                             int totalQuantity) {
        this.firstParticipant = firstParticipant;
        this.secondParticipant = secondParticipant;
        this.totalQuantity = totalQuantity;
        firstParticipantPosition = new ImmutablePair<>(firstParticipantCash, 0);
        secondParticipantPosition = new ImmutablePair<>(secondParticipantCash, 0);
    }

    @Override
    public void run() {
        for (int restQuantity = totalQuantity; restQuantity > 0; restQuantity -= 2) {
            int firstParticipantBid = firstParticipant.placeBid();
            int secondParticipantBid = secondParticipant.placeBid();
            spreadTheResult(firstParticipantBid, secondParticipantBid);
        }
        isAuctionFinished = true;
    }

    private void spreadTheResult(int firstParticipantBid, int secondParticipantBid) {
        firstParticipant.bids(firstParticipantBid, secondParticipantBid);
        secondParticipant.bids(secondParticipantBid, firstParticipantBid);
        int firsParticipantPrize;
        int secondParticipantPrize;
        if (firstParticipantBid > secondParticipantBid) {
            firsParticipantPrize = 2;
        } else if (firstParticipantBid < secondParticipantBid) {
            secondParticipantPrize = 2;
        } else {
            firsParticipantPrize = secondParticipantPrize = 1;
        }

        //auctionStatistic.apply(firstParticipant, firstParticipantBid, firsParticipantPrize);
        //auctionStatistic.apply(secondParticipant, secondParticipantBid, secondParticipantPrize);
    }

    @Override
    public Bidder getWinner() {
        return null;
    }

    @Override
    public String getReport() {
        return null;
    }
}
