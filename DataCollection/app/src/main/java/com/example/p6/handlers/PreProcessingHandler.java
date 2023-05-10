package com.example.p6.handlers;

import static com.example.p6.classes.Constants.NOT_SET;

import com.example.p6.classes.DataPointAggregated;
import com.example.p6.classes.DataPointRaw;

import java.util.ArrayList;
import java.util.List;

public class PreProcessingHandler {
    public static List<DataPointAggregated> aggregatedDataPoints = new ArrayList<>();
    private static short prevMinute = NOT_SET;
    private static short firstMinuteInTimeWindow = NOT_SET;
    private static short accumulatedHeartRate = 0;
    private static short numberOfDataPointsInTimeWindow = 0;
    private static short prevStepCount;
    private static int stepCountAtStartOfTimeWindow;
    private static double minHeartRate;
    private static double maxHeartRate;
    private static boolean isFirstIteration = true;

    public static void aggregateDataPoints(List<DataPointRaw> dataPointsToAdd, byte timeWindowSize) {
        aggregatedDataPoints.clear();
        resetValues();

        for (DataPointRaw dataPoint : dataPointsToAdd) {

            if (!isFirstIteration && prevMinute != dataPoint.minutes && numberOfDataPointsInTimeWindow > 0) {
                int diffBetweenMinutes = prevMinute - firstMinuteInTimeWindow;
                if (diffBetweenMinutes == timeWindowSize - 1) {
                    addAggregatedDataPointsToList();
                    resetValues();
                }
            }

            if(isFirstIteration){
                setValuesOnFirstIteration(dataPoint);
            }

            accumulatedHeartRate += dataPoint.heartRate;
            prevMinute = dataPoint.minutes;
            prevStepCount = (short) dataPoint.stepCount;
            updateHeartRateEdgeCases(dataPoint);

            numberOfDataPointsInTimeWindow++;
        }
    }

    private static void addAggregatedDataPointsToList() {
        double avgHeartRate = (double) accumulatedHeartRate / numberOfDataPointsInTimeWindow;
        double stepCountDiff = (double) prevStepCount - stepCountAtStartOfTimeWindow;

        aggregatedDataPoints.add(new DataPointAggregated(
                avgHeartRate, minHeartRate, maxHeartRate, stepCountDiff
        ));
    }

    private static void setValuesOnFirstIteration(DataPointRaw dataPoint){
        firstMinuteInTimeWindow = dataPoint.minutes;
        stepCountAtStartOfTimeWindow = dataPoint.stepCount;
        minHeartRate = dataPoint.heartRate;
        maxHeartRate = dataPoint.heartRate;
        isFirstIteration = false;
    }

    private static void resetValues() {
        accumulatedHeartRate = 0;
        numberOfDataPointsInTimeWindow = 0;
        prevMinute = NOT_SET;
        isFirstIteration = true;
    }

    private static void updateHeartRateEdgeCases(DataPointRaw dataPoint){
        if (dataPoint.heartRate < minHeartRate){
            minHeartRate = dataPoint.heartRate;
        }

        if (dataPoint.heartRate > maxHeartRate){
            maxHeartRate = dataPoint.heartRate;
        }
    }
}
