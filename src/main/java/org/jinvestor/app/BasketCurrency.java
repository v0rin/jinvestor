package org.jinvestor.app;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author Adam
 */
public class BasketCurrency {

    private static final Logger LOG = LogManager.getLogger();

    private BasketCurrency() {
        throw new InstantiationError("This class should not be instantiated");
    }


    public static void main(String[] args) throws Exception {
        // TODO (AF)
    }
}
