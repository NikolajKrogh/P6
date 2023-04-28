package com.example.p6.tests;
import static org.junit.Assert.*;

import com.example.p6.classes.DataPointAggregated;
import com.example.p6.classes.DataPointRaw;
import com.example.p6.handlers.PreProcessingHandler;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class AggregateDataPointsTest {

    @Test
    public void dataPointsAreAggregatedCorrectly() {
        List<DataPointRaw> dataPoints = new ArrayList<>();

        dataPoints.add(new DataPointRaw((short)60, 10, (byte)0, (short)0));
        dataPoints.add(new DataPointRaw((short)70, 20, (byte)0, (short)0));
        dataPoints.add(new DataPointRaw((short)80, 30, (byte)0, (short)0));

        dataPoints.add(new DataPointRaw((short)60, 40, (byte)0, (short)1));
        dataPoints.add(new DataPointRaw((short)60, 45, (byte)0, (short)1));
        dataPoints.add(new DataPointRaw((short)60, 50, (byte)0, (short)1));

        dataPoints.add(new DataPointRaw((short)60, 50, (byte)1, (short)2));
        dataPoints.add(new DataPointRaw((short)60, 50, (byte)1, (short)2));
        dataPoints.add(new DataPointRaw((short)90, 50, (byte)1, (short)2));

        dataPoints.add(new DataPointRaw((short)70, 55, (byte)1, (short)3));

        DataPointAggregated[] expectedAggregatedDataPointsFor1MinuteWindowSize = {
                new DataPointAggregated(70, 60, 80, 20),
                new DataPointAggregated(60, 60, 60, 10),
                new DataPointAggregated(70, 60, 90, 0)};

        DataPointAggregated[] expectedAggregatedDataPointsFor2MinuteWindowSize = {
                new DataPointAggregated(65, 60, 80, 40)};

        DataPointAggregated[] expectedAggregatedDataPointsFor3MinuteWindowSize = {
                new DataPointAggregated(66.666667, 60, 90, 40)};

        DataPointAggregated[] expectedAggregatedDataPoints;

        for (int i = 1; i <= 3; i++){   // i describes the window size
            switch (i){
                case 1:
                    expectedAggregatedDataPoints = expectedAggregatedDataPointsFor1MinuteWindowSize;
                    break;
                case 2:
                    expectedAggregatedDataPoints = expectedAggregatedDataPointsFor2MinuteWindowSize;
                    break;
                case 3:
                    expectedAggregatedDataPoints = expectedAggregatedDataPointsFor3MinuteWindowSize;
                    break;
                default:
                    throw new RuntimeException("Window size " + i + " not supported");
            }

            PreProcessingHandler.aggregateDataPoints(dataPoints, (byte) i);

            byte sizeOfExpectedArray = (byte) expectedAggregatedDataPoints.length;
            byte sizeOfActualArray = (byte) PreProcessingHandler.aggregatedDataPoints.size();
            assertEquals("i: " + i + "\n", sizeOfExpectedArray, sizeOfActualArray);

            for (int j = 0; j < PreProcessingHandler.aggregatedDataPoints.size(); j++) {
                boolean actual =  PreProcessingHandler.aggregatedDataPoints.get(j).
                        equals(expectedAggregatedDataPoints[j]);
                assertTrue("i: " + i + "\n" +
                        "Expected: " + expectedAggregatedDataPoints[j] + "\n" +
                        "Actual: " + PreProcessingHandler.aggregatedDataPoints.get(j).toString() + "\n", actual);
            }
        }
    }
}
