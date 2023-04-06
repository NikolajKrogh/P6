package com.example.p6.classes;

import java.util.ArrayList;
import java.util.List;

public class PreProcessing {
    private static final byte NOT_SET = -1;
    public static List<DataPoint> timeSeries = new ArrayList<>();
    static short minute = 0;
    static short prevMinute = NOT_SET;
    static short accumulatedHeartRate = 0;
    static short numberOfDataPointsInMinute = 0;
    static int stepCountAtStartOfMinute = NOT_SET;
    static short prevStepCount;
    public static List<DataPoint> makeBudgetTimeSeries(List<DataPoint> dataPointsToAdd, String sessionId) {
        for (DataPoint dataPoint : dataPointsToAdd) {
            minute = dataPoint.minutes;

            if (minute != prevMinute && numberOfDataPointsInMinute > 0) {
                addTimeSeriesToList(sessionId);
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

        return timeSeries;
    }

    private static void addTimeSeriesToList(String sessionId) {
        short avgHeartRate = (short) (accumulatedHeartRate / numberOfDataPointsInMinute);
        short stepCountDiff = (short) (prevStepCount - stepCountAtStartOfMinute);
        timeSeries.add(new DataPoint(avgHeartRate, stepCountDiff, sessionId));
    }

    private static void resetValues() {
        accumulatedHeartRate = 0;
        stepCountAtStartOfMinute = NOT_SET;
        numberOfDataPointsInMinute = 0;
    }
}
