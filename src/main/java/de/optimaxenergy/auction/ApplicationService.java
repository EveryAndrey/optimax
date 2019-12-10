package de.optimaxenergy.auction;

import de.optimaxenergy.auction.config.AuctionConfig;
import de.optimaxenergy.auction.service.AuctionHolder;

public class ApplicationService {

    public static void main(String[] args) throws InterruptedException {
        AuctionHolder auctionHolder = new AuctionHolder((new AuctionConfig()).init().getAuctions());
        Thread auctionHolderThread = new Thread(auctionHolder::hold);
        auctionHolderThread.start();
        auctionHolderThread.join();
    }

}
