package org.jinvestor.datasource.converter;

import java.util.Map;

import org.jinvestor.datasource.IConverter;
import org.jinvestor.model.Bar;

/**
 *
 * @author Adam
 */
public class CsvBarToDbRowConverter extends FastRawCsvToDbRowConverter {

	private IConverter<String, String> dateTimeConverter;

	public CsvBarToDbRowConverter(Map<String, String> inputToOutputColumnMappings,
			  				   IConverter<String, String> dateTimeConverter) {
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
