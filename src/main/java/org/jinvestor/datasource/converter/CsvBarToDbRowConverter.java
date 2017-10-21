package org.jinvestor.datasource.converter;

import java.util.Map;

import org.jinvestor.model.Bar;
import org.jinvestor.time.IDateTimeConverter;

/**
 *
 * @author Adam
 */
public class CsvBarToDbRowConverter extends FastRawCsvToDbRowConverter {

	private IDateTimeConverter<String, String> dateTimeConverter;

	public CsvBarToDbRowConverter(Map<String, String> inputToOutputColumnMappings,
			  				   IDateTimeConverter<String, String> dateTimeConverter) {
		super(inputToOutputColumnMappings, Bar.class);
		this.dateTimeConverter = dateTimeConverter;
	}

	@Override
	public Object[] apply(String[] strings) {
		Object[] objects = super.apply(strings);
		if (objects.length > 0) {
			objects[1] = dateTimeConverter.apply((String)objects[1]);
		}
		return objects;
	}
}
