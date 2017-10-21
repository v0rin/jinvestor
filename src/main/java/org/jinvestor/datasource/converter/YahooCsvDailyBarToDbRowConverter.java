package org.jinvestor.datasource.converter;

import java.util.HashMap;
import java.util.Map;

import org.jinvestor.time.DateTimeConverterFactory;

/**
 *
 * @author Adam
 */
public class YahooCsvDailyBarToDbRowConverter extends CsvBarToDbRowConverter {

	public YahooCsvDailyBarToDbRowConverter() {
		super(getCsvToDbColumnsMappings(), DateTimeConverterFactory.getDateToDateTimeConverter());
	}

	public static Map<String, String> getCsvToDbColumnsMappings() {
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