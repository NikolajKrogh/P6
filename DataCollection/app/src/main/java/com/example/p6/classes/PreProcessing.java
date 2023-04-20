package com.example.p6.classes;

import java.util.ArrayList;
import java.util.List;

public class PreProcessing {
    private static final byte NOT_SET = -1;
    public static List<DataPointAggregated> aggregatedDataPoints = new ArrayList<>();
    static short minute = 0;
    static short prevMinute = NOT_SET;
    static short accumulatedHeartRate = 0;
    static short numberOfDataPointsInMinute = 0;
    static int stepCountAtStartOfMinute = NOT_SET;
    static short prevStepCount;
    public static List<DataPointAggregated> makeBudgetTimeSeries(List<DataPointRaw> dataPointsToAdd) {
        for (DataPointRaw dataPoint : dataPointsToAdd) {
            minute = dataPoint.minutes;

            if (minute != prevMinute && numberOfDataPointsInMinute > 0) {
                addTimeSeriesToList();
                resetValues();
            }

            if (stepCountAtStartOfMinute == NOT_SET) {
                stepCountAtStartOfMinute = dataPoint.stepCount;
            }

            accumulatedHeartRate += dataPoint.heartRate;
            prevMinute = minute;
            prevStepCount = (short) dataPoint.stepCount;
            numberOfDataPointsInMinute++;
        }

        return aggregatedDataPoints;
    }

    private static void addTimeSeriesToList() {
        double avgHeartRate = (double) accumulatedHeartRate / numberOfDataPointsInMinute;
        double stepCountDiff = (double) prevStepCount - stepCountAtStartOfMinute;
        aggregatedDataPoints.add(new DataPointAggregated(avgHeartRate, stepCountDiff));
    }

    private static void resetValues() {
        accumulatedHeartRate = 0;
        stepCountAtStartOfMinute = NOT_SET;
        numberOfDataPointsInMinute = 0;
    }
}
