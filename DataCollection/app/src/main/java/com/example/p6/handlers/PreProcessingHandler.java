package com.example.p6.handlers;

import static com.example.p6.activities.DisplayActivity.displayPredictedActivity;
import static com.example.p6.classes.Constants.Mode.*;
import static com.example.p6.classes.Constants.NOT_SET;

import android.content.Context;

import com.example.p6.activities.MainActivity;
import com.example.p6.classes.Constants;
import com.example.p6.classes.DataPointAggregated;
import com.example.p6.classes.DataPointRaw;

import java.time.LocalDateTime;
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
    static public List<DataPointAggregated> aggregatedDataPointsSitting = new ArrayList<>();
    static public List<DataPointAggregated> aggregatedDataPointsWalking = new ArrayList<>();
    static public List<DataPointAggregated> aggregatedDataPointsRunning = new ArrayList<>();
    static public List<DataPointAggregated> aggregatedDataPointsCycling = new ArrayList<>();
    static public List<DataPointAggregated> aggregatedDataPointsUnlabeled = new ArrayList<>();
    static public List<Constants.Activity> predictedActivities = new ArrayList<>();
    private static boolean modelWasUpdated = false;

    public static void addAggregatedDataPointsToCorrespondingList(List<DataPointRaw> dataPointsToAdd){
        aggregatedDataPointsSitting.clear();
        aggregatedDataPointsWalking.clear();
        aggregatedDataPointsRunning.clear();
        aggregatedDataPointsCycling.clear();
        aggregatedDataPointsUnlabeled.clear();
        predictedActivities.clear();

        aggregateDataPoints(dataPointsToAdd, Constants.TIME_WINDOW_SIZE);

        for (DataPointAggregated dataPoint : aggregatedDataPoints) {
            Constants.Activity activity = MainActivity.activityToTrack;

            if (MainActivity.trackingMode == PREDICT_ACTIVITY || MainActivity.trackingMode == UPDATE_MODEL){
                activity = NearestCentroidHandler.predict(dataPoint, NearestCentroidHandler.centroids);

                if (MainActivity.trackingMode == PREDICT_ACTIVITY) {
                    displayPredictedActivity(activity);
                }
            }

            if (MainActivity.trackingMode == UPDATE_WITH_LABELS || MainActivity.trackingMode == TEST_ACCURACY){
                Constants.Activity predictedActivity = NearestCentroidHandler.predict(dataPoint, NearestCentroidHandler.centroids);
                displayPredictedActivity(predictedActivity);
                predictedActivities.add(predictedActivity);
            }

            switch (activity){
                case SITTING:
                    aggregatedDataPointsSitting.add(dataPoint);
                    break;
                case WALKING:
                    aggregatedDataPointsWalking.add(dataPoint);
                    break;
                case RUNNING:
                    aggregatedDataPointsRunning.add(dataPoint);
                    break;
                case CYCLING:
                    aggregatedDataPointsCycling.add(dataPoint);
                    break;
                case UNLABELED:
                    aggregatedDataPointsUnlabeled.add(dataPoint);
                    break;
                default:
                    throw new RuntimeException("Activity " + activity + " not recognized");
            }
        }
        System.out.println("UNLABELED: " + aggregatedDataPointsUnlabeled.size());
    }

    private static void aggregateDataPoints(List<DataPointRaw> dataPointsToAdd, byte timeWindowSize) {
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

    public static void updateModelForPredictedActivities(List<DataPointRaw> dataPointsToAdd, Context context) {
        addAggregatedDataPointsToCorrespondingList(dataPointsToAdd);
        for (short i = 0; i < Constants.NUMBER_OF_LABELS; i++) {
            List<DataPointAggregated> listForActivity = getListForActivity(Constants.Activity.values()[i]);
            System.out.println(Constants.Activity.values()[i] + ": " + listForActivity.size());
            updateCentroidForActivity(listForActivity, Constants.Activity.values()[i]);
        }
        if (modelWasUpdated){
            CsvHandler.writeCentroidsToFile(NearestCentroidHandler.centroids, context);
            CsvHandler.writeToCentroidHistory(
                    NearestCentroidHandler.centroids,
                    Constants.dateTimeFormatter.format(LocalDateTime.now()), context);
        }
    }

    private static List<DataPointAggregated> getListForActivity(Constants.Activity activity){
        switch (activity){
            case SITTING:
                return aggregatedDataPointsSitting;
            case WALKING:
                return aggregatedDataPointsWalking;
            case RUNNING:
                return aggregatedDataPointsRunning;
            case CYCLING:
                return  aggregatedDataPointsCycling;
            default:
                throw new RuntimeException("Activity " + activity + " does not correspond to any list");
        }
    }

    private static void updateCentroidForActivity(List<DataPointAggregated> aggregatedDataPointsForActivity, Constants.Activity activity) {
        int numberOfDataPoints = aggregatedDataPointsForActivity.size();

        if (numberOfDataPoints == 0) {
            return;
        }

        double totalHeartRate = 0;
        double minHeartRate = aggregatedDataPointsForActivity.get(0).minHeartRate;
        double maxHeartRate = 0;

        int totalStepCount = 0;
        double minStepCount = aggregatedDataPointsForActivity.get(0).stepCount;
        double maxStepCount = 0;

        for (DataPointAggregated dataPoint : aggregatedDataPointsForActivity) {
            totalHeartRate += dataPoint.heartRate;
            minHeartRate = Math.min(minHeartRate, dataPoint.minHeartRate);
            maxHeartRate = Math.max(maxHeartRate, dataPoint.maxHeartRate);

            totalStepCount += dataPoint.stepCount;
            minStepCount = Math.min(minStepCount, dataPoint.stepCount);
            maxStepCount = Math.max(maxStepCount, dataPoint.stepCount);
        }

        double averageHeartRate = totalHeartRate / numberOfDataPoints;
        double averageStepCount = totalStepCount / numberOfDataPoints;

        NearestCentroidHandler.centroids[activity.ordinal()] = NearestCentroidHandler.updateModel(
                activity, averageHeartRate, minHeartRate, maxHeartRate,
                averageStepCount, minStepCount, maxStepCount, numberOfDataPoints);

        modelWasUpdated = true;
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
