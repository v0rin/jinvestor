package org.jinvestor.datasource.converter;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.jinvestor.datasource.IConverter;
import org.jinvestor.model.Bar;
import org.jinvestor.model.IInstrument;
import org.jinvestor.model.entity.EntityMetaDataFactory;
import org.jinvestor.model.entity.IEntityMetaData;
import org.jinvestor.time.IDateTimeConverter;

import static com.google.common.base.Preconditions.checkArgument;

/**
 *
 * @author Adam
 */
public class CsvBarToDbRowConverter implements IConverter<String[], Object[]> {

    private Map<String, String> inputToOutputColumnMappings;
    private IDateTimeConverter<String, String> dateTimeConverter;
    private IInstrument instrument;
    private Long volume;
    private String currencyCode;

    private IEntityMetaData<Bar> entityMetaData;
    private int addedColumnsCount;

    private boolean isFirstTimeCalled = true;

    private CsvBarToDbRowConverter(Map<String, String> inputToOutputColumnMappings,
                                   IDateTimeConverter<String, String> dateTimeConverter,
                                   IInstrument instrument,
                                   Long volume,
                                   String currencyCode) {
        this.inputToOutputColumnMappings = inputToOutputColumnMappings;
        this.entityMetaData = EntityMetaDataFactory.get(Bar.class);
        this.dateTimeConverter = dateTimeConverter;
        this.instrument = instrument;
        this.volume = volume;
        this.currencyCode = currencyCode;

        if (instrument != null) addedColumnsCount++;
        if (volume != null) addedColumnsCount++;
        // always add one for currency
        addedColumnsCount++;
    }

    @Override
    public Object[] apply(String[] strings) {
        if (isFirstTimeCalled) {
            validateInputColumns(strings, inputToOutputColumnMappings, entityMetaData);
            isFirstTimeCalled = false;
            return new Object[0];
        }

        String[] enrichedStrings = new String[strings.length + addedColumnsCount];
        int inputCounter = 0;
        int outputCounter = 0;
        if (instrument != null) {
            enrichedStrings[outputCounter++] = instrument.getSymbol();
        }
        else {
            enrichedStrings[outputCounter++] = strings[inputCounter++];
        }
        enrichedStrings[outputCounter++] = dateTimeConverter.apply((String)strings[inputCounter++]);
        // open, high, low, close
        final int closeColumnNo = 5;
        while (outputCounter <= closeColumnNo) {
            enrichedStrings[outputCounter++] = strings[inputCounter++];
        }
        if (volume != null) {
            enrichedStrings[outputCounter++] = String.valueOf(volume);
        }
        else {
            enrichedStrings[outputCounter++] = strings[inputCounter];
        }
        enrichedStrings[outputCounter] = currencyCode;

        return enrichedStrings;
    }

    private void validateInputColumns(String[] csvColumns,
                                      Map<String, String> inputToOutputColumnMappings,
                                      IEntityMetaData<Bar> entityMetaData) {
        List<String> entityColumns = Arrays.asList(entityMetaData.getColumns());
        checkArgument(entityColumns.containsAll(inputToOutputColumnMappings.values()),
                      "Csv columns are incorrect. " +
                      getAsString(csvColumns, inputToOutputColumnMappings, entityColumns) +
                      ". This is not supported in this fast implementation");
    }

    private String getAsString(String[] csvColumns,
                               Map<String, String> inputToOutputColumnMappings,
                               List<String> entityColumns) {
        return new StringBuilder()
                .append("csv columns=").append(Arrays.asList(csvColumns))
                .append("; column mappings=").append(inputToOutputColumnMappings)
                .append("; entity column=").append(entityColumns)
                .toString();
    }

    public static class Builder {
        private Map<String, String> inputToOutputColumnMappings;
        private IDateTimeConverter<String, String> dateTimeConverter;
        private IInstrument instrument;
        private Long volume;
        private String currencyCode;

        public Builder(Map<String, String> inputToOutputColumnMappings,
                       IDateTimeConverter<String, String> dateTimeConverter,
                       String currencyCode) {
            this.inputToOutputColumnMappings = inputToOutputColumnMappings;
            this.dateTimeConverter = dateTimeConverter;
            this.currencyCode = currencyCode;
        }

        public Builder volume(Long volume) {
            this.volume = volume;
            return this;
        }

        public Builder instrument(IInstrument instrument) {
            this.instrument = instrument;
            return this;
        }

        public CsvBarToDbRowConverter build() {
            return new CsvBarToDbRowConverter(inputToOutputColumnMappings,
                                              dateTimeConverter,
                                              instrument,
                                              volume,
                                              currencyCode);
        }
    }
}
