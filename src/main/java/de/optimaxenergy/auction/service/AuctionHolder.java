package de.optimaxenergy.auction.service;

import de.optimaxenergy.auction.auction.Auction;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

public class AuctionHolder {

  private final List<Auction> auctions;

  public AuctionHolder(List<Auction> auctions) {
    this.auctions = auctions;
  }

  public void hold() {

    List<Future<Auction>> futures = new ArrayList<>();
    ExecutorService executorService = Executors.newFixedThreadPool(6);

    try {
      auctions.forEach(auction -> futures.add(executorService.submit(auction)));

      while (futures.stream().anyMatch(auctionFuture -> !auctionFuture.isDone()
          && !auctionFuture.isCancelled())) {
        List<Future<Auction>> collect = futures.stream().filter(Future::isDone)
            .collect(Collectors.toList());
        collect.forEach(future -> doProcess(futures, future));
      }

      List<Future<Auction>> collect = futures.stream().filter(Future::isDone)
          .collect(Collectors.toList());
      collect.forEach(future -> doProcess(futures, future));

    } finally {
      executorService.shutdownNow();
    }
  }

  private void doProcess(List<Future<Auction>> futures, Future<Auction> auctionFuture) {
    try {
      System.out
          .println(auctionFuture.get().getReport() + System.lineSeparator());
      futures.remove(auctionFuture);
    } catch (InterruptedException | ExecutionException e) {
      e.printStackTrace();
    }

  }


}
