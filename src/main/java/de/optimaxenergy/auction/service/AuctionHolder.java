package de.optimaxenergy.auction.service;

import de.optimaxenergy.auction.auction.Auction;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

/**
 * Responsible for holding auctions.
 */
public class AuctionHolder {

  private static Integer THREADS_NUM = 3;
  private final List<Auction> auctions;

  public AuctionHolder(List<Auction> auctions) {
    this.auctions = auctions;
  }

  /**
   * Launch every auction in list. After auction has finished, print the result.
   */
  public void hold() {

    List<Future<Auction>> futures = new ArrayList<>();
    ExecutorService executorService = Executors.newFixedThreadPool(THREADS_NUM);

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
          .println(auctionFuture.get().getReport(true) + System.lineSeparator());
      futures.remove(auctionFuture);
    } catch (InterruptedException | ExecutionException e) {
      e.printStackTrace();
    }

  }


}
