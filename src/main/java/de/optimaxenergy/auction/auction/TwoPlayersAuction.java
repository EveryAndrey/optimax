package de.optimaxenergy.auction.auction;

import de.optimaxenergy.auction.bidders.Bidder;
import de.optimaxenergy.auction.validation.Even;
import lombok.Getter;
import org.apache.commons.lang3.tuple.MutablePair;

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

    private final MutablePair<Integer, Integer> firstParticipantPosition;
    private final MutablePair<Integer, Integer> secondParticipantPosition;
    private boolean isAuctionFinished;

    public TwoPlayersAuction(Bidder firstParticipant, int firstParticipantCash,
        Bidder secondParticipant, int secondParticipantCash, int totalQuantity) {
        this.firstParticipant = firstParticipant;
        this.secondParticipant = secondParticipant;
        this.totalQuantity = totalQuantity;
        firstParticipantPosition = new MutablePair<>(firstParticipantCash, 0);
        secondParticipantPosition = new MutablePair<>(secondParticipantCash, 0);
    }

    @Override
    public String getReport() {
        StringBuilder sb = new StringBuilder(String.format("Auction on {%s} quantities between ({%s}) vs ({%s}): ",
                totalQuantity, firstParticipant, secondParticipant))
                .append(System.lineSeparator());
        int wonSide = 0;
        wonSide = secondParticipantPosition.getRight().compareTo(firstParticipantPosition.getRight());
        if (wonSide == 0) {
            wonSide = secondParticipantPosition.getLeft().compareTo(firstParticipantPosition.getLeft());
        }

        if (wonSide == -1) {
            sb.append("The first player's won ");
        } else if (wonSide == 1) {
            sb.append("The second player's won ");
        } else {
            sb.append("The auction has finished in a draw ");
        }

        sb.append(System.lineSeparator()).append(String.format("with the results: %s quantities against %s " +
                "quantities, ", firstParticipantPosition.getRight(), secondParticipantPosition.getRight()))
                .append(System.lineSeparator())
                .append(String.format("%s cash against %s cash", firstParticipantPosition.getLeft(),
                        secondParticipantPosition.getLeft()));

        return sb.toString();
    }

    @Override
    public Auction call() {
        for (int restQuantity = totalQuantity; restQuantity > 0; restQuantity -= 2) {
            int firstParticipantBid = firstParticipant.placeBid();
            int secondParticipantBid = secondParticipant.placeBid();
            spreadTheResult(firstParticipantBid, secondParticipantBid);
        }
        isAuctionFinished = true;
        return this;
    }

    private void spreadTheResult(int firstParticipantBid, int secondParticipantBid) {
        firstParticipant.bids(firstParticipantBid, secondParticipantBid);
        secondParticipant.bids(secondParticipantBid, firstParticipantBid);
        int firstParticipantPrize = 0;
        int secondParticipantPrize = 0;
        if (firstParticipantBid > secondParticipantBid) {
            firstParticipantPrize = 2;
        } else if (firstParticipantBid < secondParticipantBid) {
            secondParticipantPrize = 2;
        } else {
            firstParticipantPrize = secondParticipantPrize = 1;
        }

        apply(firstParticipantPosition, firstParticipantBid, firstParticipantPrize);
        apply(secondParticipantPosition, secondParticipantBid, secondParticipantPrize);
    }

    private void apply(MutablePair<Integer, Integer> position, int bid, int prize) {
        position.setLeft(position.getLeft() - bid);
        position.setRight(position.getRight() + prize);
    }


}
