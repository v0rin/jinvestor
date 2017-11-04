package org.jinvestor.datasource.converter;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jinvestor.model.Bar;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * @author Adam
 */
public class StandardBarsToJsonConverter implements IConverter<Stream<Bar>, String> {

    @Override
    public String apply(Stream<Bar> barStream) {
        List<DataPointWithTimestamp> dataPoints =
                barStream.map(bar -> new DataPointWithTimestamp(bar.getTimestamp(), bar.getClose()))
                         .collect(Collectors.toList());

        Gson gson = new GsonBuilder().create();
        return gson.toJson(dataPoints);
    }
}
