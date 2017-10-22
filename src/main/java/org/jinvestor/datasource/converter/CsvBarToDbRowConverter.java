package org.jinvestor.datasource.converter;

import java.util.Arrays;
import java.util.Map;

import org.jinvestor.datasource.IConverter;
import org.jinvestor.model.Bar;
import org.jinvestor.model.Currency;
import org.jinvestor.model.entity.EntityMetaDataFactory;
import org.jinvestor.model.entity.IEntityMetaData;
import org.jinvestor.time.IDateTimeConverter;

/**
 *
 * @author Adam
 */
public class CsvBarToDbRowConverter implements IConverter<String[], Object[]> {

    private IDateTimeConverter<String, String> dateTimeConverter;
    private Currency currency;
    private Map<String, String> inputToOutputColumnMappings;
    private IEntityMetaData<Bar> entityMetaData;

    private boolean isFirstTimeCalled = true;

    public CsvBarToDbRowConverter(Map<String, String> inputToOutputColumnMappings,
                                   IDateTimeConverter<String, String> dateTimeConverter,
                                   Currency currency) {
        this.inputToOutputColumnMappings = inputToOutputColumnMappings;
        this.entityMetaData = EntityMetaDataFactory.get(Bar.class);
        this.dateTimeConverter = dateTimeConverter;
        this.currency = currency;
    }

    @Override
    public Object[] apply(String[] strings) {
        if (isFirstTimeCalled) {
            validateInputColumns(strings, inputToOutputColumnMappings, entityMetaData);
            isFirstTimeCalled = false;
            return new Object[0];
        }

        String[] ostringsEnrichedWithCurrency = Arrays.copyOf(strings, strings.length + 1);
        ostringsEnrichedWithCurrency[1] = dateTimeConverter.apply((String)strings[1]);
        ostringsEnrichedWithCurrency[strings.length] = currency.getCode();
        return ostringsEnrichedWithCurrency;
    }

    private void validateInputColumns(String[] csvColumns,
                                      Map<String, String> inputToOutputColumnMappings,
                                      IEntityMetaData<Bar> entityMetaData) {
        String[] entityColumns = entityMetaData.getColumns();
        for (int i = 0; i < csvColumns.length; i++) {
            if (!inputToOutputColumnMappings.get(csvColumns[i]).equals(entityColumns[i])) {
                throw new UnsupportedOperationException("Csv columns are incorrect. " +
                        getAsString(csvColumns, inputToOutputColumnMappings, entityColumns) +
                        ". This is not supported in this fast implementation");
            }
        }
    }

    private String getAsString(String[] csvColumns,
                               Map<String, String> inputToOutputColumnMappings,
                               String[] entityColumns) {
        return new StringBuilder()
                .append("csv columns=").append(Arrays.asList(csvColumns))
                .append("; column mappings=").append(inputToOutputColumnMappings)
                .append("; entity column=").append(Arrays.asList(entityColumns))
                .toString();
    }
}
