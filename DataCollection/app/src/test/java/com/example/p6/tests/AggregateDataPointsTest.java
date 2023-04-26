package com.example.p6.tests;
import static org.junit.Assert.*;

import com.example.p6.classes.DataPointAggregated;
import com.example.p6.classes.DataPointRaw;
import com.example.p6.handlers.PreProcessingHandler;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class AggregateDataPointsTest {
    //region Input for test

    private static List<DataPointRaw> dataPoints = new ArrayList<>();

    private void addDataPointsToArray(){
        dataPoints.add(new DataPointRaw((short)60, 10, (byte)0, (short)0));
        dataPoints.add(new DataPointRaw((short)70, 20, (byte)0, (short)0));
        dataPoints.add(new DataPointRaw((short)80, 30, (byte)0, (short)0));

        dataPoints.add(new DataPointRaw((short)60, 40, (byte)0, (short)1));
        dataPoints.add(new DataPointRaw((short)60, 45, (byte)0, (short)1));
        dataPoints.add(new DataPointRaw((short)60, 50, (byte)0, (short)1));

        dataPoints.add(new DataPointRaw((short)60, 50, (byte)1, (short)2));
        dataPoints.add(new DataPointRaw((short)60, 50, (byte)1, (short)2));
        dataPoints.add(new DataPointRaw((short)90, 50, (byte)1, (short)2));
    }

    private static DataPointAggregated[] expectedAggregatedDataPoints = {
            new DataPointAggregated(70, 60, 80, 20),
            new DataPointAggregated(60, 60, 60, 10),
            new DataPointAggregated(70, 60, 90, 0)};

    //endregion

    @Test
    public void dataPointsAreAggregatedCorrectly() {
        addDataPointsToArray();
        PreProcessingHandler.aggregateDataPoints(dataPoints);

        boolean expectedAndActualArraysHaveSameLength =
                PreProcessingHandler.aggregatedDataPoints.size() != expectedAggregatedDataPoints.length;

        assertEquals("Expected length: " + expectedAggregatedDataPoints.length + "\n " +
                "Actual length: " + expectedAggregatedDataPoints.length + "\n",
                true, expectedAndActualArraysHaveSameLength);

        for (int i = 0; i < PreProcessingHandler.aggregatedDataPoints.size(); i++) {
            boolean actual =  PreProcessingHandler.aggregatedDataPoints.get(i).
                    equals(expectedAggregatedDataPoints[i]);
            assertEquals("Expected: " + expectedAggregatedDataPoints[i] + "\n" +
                            "Actual: " + PreProcessingHandler.aggregatedDataPoints.get(i).toString() + "\n",
                    true, actual);
        }
    }
}
