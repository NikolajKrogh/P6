package com.example.p6.handlers;

import static com.example.p6.classes.Constants.NOT_SET;

import com.example.p6.classes.DataPointAggregated;
import com.example.p6.classes.DataPointRaw;

import java.util.ArrayList;
import java.util.List;

public class PreProcessingHandler {
    public static List<DataPointAggregated> aggregatedDataPoints = new ArrayList<>();
    static short minute = 0;
    static short prevMinute = NOT_SET;
    static short accumulatedHeartRate = 0;
    static short numberOfDataPointsInMinute = 0;
    static short prevStepCount;
    static int stepCountAtStartOfMinute;
    static double minHeartRate;
    static double maxHeartRate;
    static double minStepCount;
    static double maxStepCount;
    static boolean isFirstIteration = true;

    public static void aggregateDataPoints(List<DataPointRaw> dataPointsToAdd) {
        for (DataPointRaw dataPoint : dataPointsToAdd) {
            minute = dataPoint.minutes;

            if (minute != prevMinute && numberOfDataPointsInMinute > 0) {
                addTimeSeriesToList();
                resetValues();
            }

            if(isFirstIteration){
                setValuesOnFirstIteration(dataPoint);
            }
            updateEdgeCases(dataPoint);

            accumulatedHeartRate += dataPoint.heartRate;
            prevMinute = minute;
            prevStepCount = (short) dataPoint.stepCount;
            numberOfDataPointsInMinute++;
        }
    }

    private static void addTimeSeriesToList() {
        double avgHeartRate = (double) accumulatedHeartRate / numberOfDataPointsInMinute;
        double stepCountDiff = (double) prevStepCount - stepCountAtStartOfMinute;
        aggregatedDataPoints.add(new DataPointAggregated(
                avgHeartRate, minHeartRate, maxHeartRate,
                stepCountDiff, minStepCount, maxStepCount
        ));
    }

    private static void setValuesOnFirstIteration(DataPointRaw dataPoint){
        stepCountAtStartOfMinute = dataPoint.stepCount;
        minHeartRate = dataPoint.heartRate;
        maxHeartRate = dataPoint.heartRate;
        minStepCount = dataPoint.stepCount;
        maxStepCount = dataPoint.stepCount;
    }

    private static void resetValues() {
        accumulatedHeartRate = 0;
        numberOfDataPointsInMinute = 0;
        isFirstIteration = true;
    }

    private static void updateEdgeCases(DataPointRaw dataPoint){
        if (dataPoint.heartRate < minHeartRate){
            minHeartRate = dataPoint.heartRate;
        }

        if (dataPoint.heartRate > maxHeartRate){
            maxHeartRate = dataPoint.heartRate;
        }

        if (dataPoint.stepCount < minStepCount){
            minStepCount = dataPoint.stepCount;
        }

        if (dataPoint.stepCount > maxStepCount){
            maxStepCount = dataPoint.stepCount;
        }
    }
}
