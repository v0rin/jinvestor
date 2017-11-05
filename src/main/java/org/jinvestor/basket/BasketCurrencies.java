package org.jinvestor.basket;

import static org.jinvestor.model.Instruments.BC1;
import static org.jinvestor.model.Instruments.CNY;
import static org.jinvestor.model.Instruments.EUR;
import static org.jinvestor.model.Instruments.GBP;
import static org.jinvestor.model.Instruments.JPY;
import static org.jinvestor.model.Instruments.USD;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Adam
 */
public class BasketCurrencies {

    private static Map<String, Map<String, Double>> basketCompositions = new HashMap<>();

    static {
        addBC1();
    }

    private BasketCurrencies() {
        throw new InstantiationError("This class should not be instantiated");
    }


    public static Map<String, Double> getBasketComposition(String basketCurrencyName) {
        return basketCompositions.get(basketCurrencyName);
    }

    @SuppressWarnings("checkstyle:magicnumber")
    private static void addBC1() {
        String basketCurrencyName = BC1;
        Map<String, Double> basketComposition = new HashMap<>();
        basketComposition.put(USD, 0.3);
        basketComposition.put(EUR, 0.3);
        basketComposition.put(CNY, 0.2);
        basketComposition.put(JPY, 0.15);
        basketComposition.put(GBP, 0.05);
        basketCompositions.put(basketCurrencyName, basketComposition);
    }
}
