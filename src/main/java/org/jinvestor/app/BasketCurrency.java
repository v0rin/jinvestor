package org.jinvestor.app;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jinvestor.BasketCurrencyCreator;
import org.jinvestor.IBasketCurrencyCreator;
import org.jinvestor.model.Bar;
import org.jinvestor.model.Currency;
import org.jinvestor.model.Currency.Code;

/**
 *
 * @author Adam
 */
public class BasketCurrency {

    private static final Logger LOG = LogManager.getLogger();

    private BasketCurrency() {
        new InstantiationError("This class should not be instantiated");
    }


    public static void main(String[] args) throws Exception {
        testStreamConcat();
    }


    @SuppressWarnings("checkstyle:magicnumber")
    private static void testStreamConcat() {
        Stream<Integer> stream1 = Stream.of(1, 2, 3);
        Stream<Integer> stream2 = Stream.of(3, 2, 1);

        LOG.info(Stream.concat(stream1,  stream2).reduce(0, (x, y) -> x + y).intValue());
    }


    @SuppressWarnings("checkstyle:magicnumber")
    public static void workInProgress() throws Exception {
        //#### CONFIGURATION #####
        String dbPath = "datasource/sqlite/bar_daily.sqlite";
        String basketCurrencyName = "BC1";
        Instant from = Instant.parse("2000-01-01T00:00:00.000Z");
        Instant to = Instant.parse("2000-01-10T23:59:59.999Z");

        Map<Currency, Double> basketComposition = new HashMap<>();
        basketComposition.put(Currency.of(Code.USD), 0.3d);
        basketComposition.put(Currency.of(Code.EUR), 0.3d);
        basketComposition.put(Currency.of(Code.CNY), 0.2d);
        basketComposition.put(Currency.of(Code.JPY), 0.1d);
        basketComposition.put(Currency.of(Code.GBP), 0.1d);
        //########################

        IBasketCurrencyCreator creator = new BasketCurrencyCreator(basketCurrencyName, basketComposition);

        Stream<Bar> basketCurrencyBars = creator.create(from, to);

        basketCurrencyBars.forEach(LOG::info);
//        String dbConnectionString = Configuration.INSTANCE.getString(ConfKeys.BAR_DAILY_DB_CONNECTION_STRING);
//        try (IWriter<Bar> barDbWriter = new BarDbWriter(dbConnectionString + dbPath)) {
//            barDbWriter.write(basketCurrencyBars);
//        }
    }
}
