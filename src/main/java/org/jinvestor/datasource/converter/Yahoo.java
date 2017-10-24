package org.jinvestor.datasource.converter;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Adam
 */
public class Yahoo {

    private Yahoo() {
        throw new InstantiationError("This class should not be instantiated");
    }

    public static Map<String, String> getStocksCsvToDbColumnsMappings() {
        Map<String, String> inputToOutputColumnMappings = new HashMap<>();
        inputToOutputColumnMappings.put("Symbol", "symbol");
        inputToOutputColumnMappings.put("Date", "timestamp");
        inputToOutputColumnMappings.put("Open", "open");
        inputToOutputColumnMappings.put("High", "high");
        inputToOutputColumnMappings.put("Low", "low");
        inputToOutputColumnMappings.put("Close", "close");
        inputToOutputColumnMappings.put("Volume", "volume");

        return inputToOutputColumnMappings;
    }
}
