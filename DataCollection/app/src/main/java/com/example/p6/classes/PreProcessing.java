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
    static CentroidEdgeCases edgeCases = new CentroidEdgeCases(
            new DataPointBasic(0, 0),
            new DataPointBasic(0, 0),
            new DataPointBasic(0, 0),
            new DataPointBasic(0, 0)
            );

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

            checkIfDataPointIsNewEdgeCase(dataPoint);

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
        aggregatedDataPoints.add(new DataPointAggregated(avgHeartRate, stepCountDiff, edgeCases));
    }

    private static void resetValues() {
        accumulatedHeartRate = 0;
        stepCountAtStartOfMinute = NOT_SET;
        numberOfDataPointsInMinute = 0;
    }

    private static void checkIfDataPointIsNewEdgeCase(DataPointRaw dataPoint){
        // Check if dataPoint is northern-most-point
        if (dataPoint.heartRate > edgeCases.northernMostPoint.heartRate){
            edgeCases.northernMostPoint = new DataPointBasic(
                    dataPoint.heartRate,
                    dataPoint.stepCount);
        }

        // Check if dataPoint is eastern-most-point
        if (dataPoint.stepCount > edgeCases.easternMostPoint.stepCount){
            edgeCases.easternMostPoint = new DataPointBasic(
                    dataPoint.heartRate,
                    dataPoint.stepCount);
        }

        // Check if dataPoint is southern-most-point
        if (dataPoint.heartRate < edgeCases.southernMostPoint.heartRate){
            edgeCases.southernMostPoint = new DataPointBasic(
                    dataPoint.heartRate,
                    dataPoint.stepCount);
        }

        // Check if dataPoint is western-most-point
        if (dataPoint.stepCount < edgeCases.westernMostPoint.stepCount){
            edgeCases.westernMostPoint = new DataPointBasic(
                    dataPoint.heartRate,
                    dataPoint.stepCount);
        }
    }
}
