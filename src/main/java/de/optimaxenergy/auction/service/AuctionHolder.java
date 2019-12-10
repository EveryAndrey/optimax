package de.optimaxenergy.auction.service;

import de.optimaxenergy.auction.auction.Auction;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class AuctionHolder {

  private final List<Auction> auctions;

  public AuctionHolder(List<Auction> auctions) {
    this.auctions = auctions;
  }

  public void hold() {

    List<Future<Auction>> futures = new ArrayList<>();

    auctions.forEach(auction -> {
      ExecutorService executorService = Executors.newFixedThreadPool(6);
      futures.add(executorService.submit(auction));
      System.out.println(auction.getReport());
    });

    while (futures.stream().anyMatch(auctionFuture -> !auctionFuture.isDone()
        && !auctionFuture.isCancelled())) {
      futures.stream().filter(Future::isDone)
          .forEach(auctionFuture -> {
            try {
              System.out
                  .println(auctionFuture.get().getReport());
            } catch (InterruptedException | ExecutionException e) {
              e.printStackTrace();
            }
          });
    }
  }
}