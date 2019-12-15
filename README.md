# optimax

You can launch app using the command
"./gradlew clean build run"

Here you can find three strategies implemented  in classes:
1. AveragePriceBasedBidder
2. ContraAveragePriceBasedBidder
3. RandomBidder (this just for simple test)

In order to add your personal strategy you need to 
1. Add new implementation of Bidder interface into de.optimaxenergy.auction.bidders package.
2. Create new Strategy enum with unique name.
3. Set the annotation Strategy(your enum) on your implementation
4. In application.yml add the auction with your strategy name against any other (or the same
) strategy.
