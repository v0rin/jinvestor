package org.jinvestor.datasource.converter;


import java.sql.Timestamp;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jinvestor.model.Bar;

import pl.luwi.series.reducer.EpsilonHelper;
import pl.luwi.series.reducer.Point;
import pl.luwi.series.reducer.SeriesReducer;

/**
 * Using Ramer–Douglas–Peucker reducing algoritym<br>
 * {@linkplain https://en.wikipedia.org/wiki/Ramer%E2%80%93Douglas%E2%80%93Peucker_algorithm}
 *
 * @author Adam
 */
public class BarsToDataPointsReducingConverter implements IConverter<Stream<Bar>, List<DataPointWithTimestamp>> {

    private static final int DEFAULT_TARGET_DATA_POINTS = 500;
    private static final double AVG_DEVIATION_REDUCE_RATIO = 1.8;

    private int targetDataPoints = DEFAULT_TARGET_DATA_POINTS;

    @Override
    public List<DataPointWithTimestamp> apply(Stream<Bar> barStream) {
        List<Bar> bars = barStream.collect(Collectors.toList());

        List<DataPointWithTimestamp> dataPointsWithTimestamp = null;
        if (bars.size() > targetDataPoints) {
            dataPointsWithTimestamp = reduceDataPoints(bars, (double)bars.size() / targetDataPoints);
        }
        else {
            dataPointsWithTimestamp = bars.stream().map(bar -> new DataPointWithTimestamp(bar.getTimestamp(),
                                                                                          bar.getClose()))
                                                   .collect(Collectors.toList());
        }

        return dataPointsWithTimestamp;
    }


    private List<DataPointWithTimestamp> reduceDataPoints(List<Bar> bars, double reduceRatio) {
        List<Point> dataPoints =  bars.stream().map(DataPoint::new)
                                      .collect(Collectors.toList());

        double[] deviations = EpsilonHelper.deviations(dataPoints);
        double avgDev = EpsilonHelper.avg(deviations);
        List<Point> reduced = SeriesReducer.reduce(dataPoints, avgDev * reduceRatio / AVG_DEVIATION_REDUCE_RATIO);

        return reduced.stream()
                      .map(point -> new DataPointWithTimestamp(new Timestamp((long)point.getX()), point.getY()))
                      .collect(Collectors.toList());
    }


    private static class DataPoint implements Point {
        private double x;
        private double y;

        DataPoint(Bar bar) {
            this.x = bar.getTimestamp().getTime();
            this.y = bar.getClose();
        }

        @Override
        public double getX() {
            return x;
        }

        @Override
        public double getY() {
            return y;
        }
    }
}
