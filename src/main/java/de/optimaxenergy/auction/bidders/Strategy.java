package de.optimaxenergy.auction.bidders;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Used for bidders in order to have possibility create them with {@link BidderFactory}
 */
@Target({TYPE})
@Retention(RUNTIME)
public @interface Strategy {

  StrategyKind value();
}
