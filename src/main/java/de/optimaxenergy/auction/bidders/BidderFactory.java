package de.optimaxenergy.auction.bidders;

import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfoList;
import io.github.classgraph.ScanResult;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class BidderFactory {

  private static BidderFactory bidderFactory;
  private final Map<StrategyKind, String> bidders;

  private BidderFactory() throws ClassNotFoundException {
    bidders = new HashMap<>();
    try (ScanResult result = new ClassGraph().enableClassInfo().enableAnnotationInfo()
        .whitelistPackages(getClass().getPackage().getName()).scan()) {
      ClassInfoList classInfos = result.getClassesWithAnnotation(Strategy.class.getName());
      for (var classInfo : classInfos) {
        bidders.put(
            Class.forName(classInfo.getName()).getAnnotation(Strategy.class).value(),
            classInfo.getName());
      }
    }
  }

  public static synchronized BidderFactory getInstance() {
    if (Objects.isNull(bidderFactory)) {
      try {
        bidderFactory = new BidderFactory();
      } catch (ClassNotFoundException e) {
        throw new RuntimeException(e);
      }
    }
    return bidderFactory;
  }

  public Bidder createBidder(int quantity, int cash, StrategyKind strategyKind) {
    try {
      Class<?> clazz = Class.forName(bidders.get(strategyKind));
      Constructor<?> ctor = clazz.getConstructor();
      Bidder bidder = (Bidder) ctor.newInstance();
      bidder.init(quantity, cash);
      return bidder;
    } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException
        | IllegalAccessException | InvocationTargetException e) {
      throw new RuntimeException(e);
    }
  }

}
