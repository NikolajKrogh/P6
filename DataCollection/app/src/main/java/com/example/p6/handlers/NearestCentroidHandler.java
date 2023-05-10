package com.example.p6.handlers;

import com.example.p6.classes.Centroid;
import com.example.p6.classes.Constants;
import com.example.p6.classes.DataPointAggregated;

import java.util.ArrayList;
import java.util.List;

public class NearestCentroidHandler {

    public static Centroid[] centroids = new Centroid[Constants.NUMBER_OF_LABELS];
    public static Centroid[] generalModelCentroids = {
            new Centroid(70.89044447734003,54.3448275862069,85.0,0.0,0.0,0.0,(byte) 0,243),
            new Centroid(111.96882037612905,69.22413793103448,156.25862068965517,111.19583333333334,79.0,139.0,(byte) 1,250),
            new Centroid(163.32964324429457,122.70731707317073,178.80357142857142,157.8181818181818,124.0,174.0,(byte) 2,249),
            new Centroid(129.62290932844044,87.33333333333333,162.82456140350877,0.0,0.0,0.0,(byte) 3,276)
    };
    public static Constants.Activity predict(DataPointAggregated dataPoint, Centroid[] model) {
        List<Constants.Activity> activitiesWhichContainDataPoint
                = getActivitiesWhichContainDataPoint(dataPoint, model, (byte) 0);

        // Is the activity within any ellipse? If not, check if it is within any buffer
        if(activitiesWhichContainDataPoint.size() == 0)
            activitiesWhichContainDataPoint
                    = getActivitiesWhichContainDataPoint(dataPoint, model, (byte) 1.1);

        // Is the activity not within any ellipse or buffer?
        if(activitiesWhichContainDataPoint.size() == 0)
            return Constants.Activity.UNLABELED;

        // Is the activity within exactly one ellipse or buffer?
        if (activitiesWhichContainDataPoint.size() == 1)
            return activitiesWhichContainDataPoint.get(0);

        // If the activity is within multiple ellipses or buffers, find nearest centroid
        for (Constants.Activity activity : activitiesWhichContainDataPoint)
            dataPoint.distanceToCentroids[activity.ordinal()]
                    = getDistanceToCentroid(dataPoint, model[activity.ordinal()]);

        return getActivityWithSmallestDistanceToDataPoint(dataPoint, activitiesWhichContainDataPoint);
    }

    public static Centroid updateModel(Constants.Activity activity,
                                       double averageHeartRate, double minHeartRate, double maxHeartRate,
                                       double averageStepCount, double minStepCount, double maxStepCount,
                                       int numberOfNewDataPoints) {

        int numberOfOldDataPoints =  centroids[activity.ordinal()].size;
        Centroid currentCentroid = centroids[activity.ordinal()];

        // Update heart rate
        currentCentroid.heartRate
                = addToAverage(currentCentroid.heartRate, numberOfOldDataPoints,
                averageHeartRate, numberOfNewDataPoints);

        // Update min heart rate
        if (minHeartRate < currentCentroid.ellipse.minHeartRate){
            currentCentroid.ellipse.minHeartRate = minHeartRate;
        }
        else {
            currentCentroid.ellipse.minHeartRate = addToAverage(
                    currentCentroid.ellipse.minHeartRate, numberOfOldDataPoints,
                    minHeartRate, numberOfNewDataPoints);
        }

        // Update max heart rate
        if (maxHeartRate > currentCentroid.ellipse.maxHeartRate){
            currentCentroid.ellipse.maxHeartRate = maxHeartRate;
        }
        else {
            currentCentroid.ellipse.maxHeartRate
                    = addToAverage(currentCentroid.ellipse.maxHeartRate, numberOfOldDataPoints,
                    maxHeartRate, numberOfNewDataPoints);
        }

        // Update step-count
        currentCentroid.stepCount
                = addToAverage(currentCentroid.stepCount, numberOfOldDataPoints,
                averageStepCount, numberOfNewDataPoints);

        // Update min step count
        if (minStepCount < currentCentroid.ellipse.minStepCount){
            currentCentroid.ellipse.minStepCount = minStepCount;
        }
        else {
            currentCentroid.ellipse.minStepCount = addToAverage(
                    currentCentroid.ellipse.minStepCount, numberOfOldDataPoints,
                    minStepCount, numberOfNewDataPoints);
        }

        // Update max step count
        if (maxStepCount > currentCentroid.ellipse.getMaxStepCount()){
            currentCentroid.ellipse.setMaxStepCount(maxStepCount);
        }
        else {
            currentCentroid.ellipse.setMaxStepCount(addToAverage(
                    currentCentroid.ellipse.getMaxStepCount(), numberOfOldDataPoints,
                    maxStepCount, numberOfNewDataPoints));
        }

        // Update size
        currentCentroid.size++;

        currentCentroid.setEllipse(currentCentroid.ellipse.minHeartRate, currentCentroid.ellipse.maxHeartRate,
                currentCentroid.ellipse.minStepCount, currentCentroid.ellipse.getMaxStepCount());

        return currentCentroid;
    }

    private static double getDistanceToCentroid(DataPointAggregated dataPoint, Centroid centroid) {
        double x1 = dataPoint.heartRate;
        double y1 = dataPoint.stepCount;
        double x2 = centroid.heartRate;
        double y2 = centroid.stepCount;

        return Math.sqrt(Math.pow((y2 - y1), 2) + Math.pow((x2 - x1), 2));
    }

    private static List<Constants.Activity> getActivitiesWhichContainDataPoint(
            DataPointAggregated dataPoint, Centroid[] model, byte bufferDecimal){
        List<Constants.Activity> activitiesWhichContainDataPoint = new ArrayList<>();
        for (int i = 0; i < Constants.NUMBER_OF_LABELS; i++) {
            if (model[i].ellipse.contains(dataPoint, bufferDecimal)){
                activitiesWhichContainDataPoint.add(Constants.Activity.values()[i]);
            }
        }
        return activitiesWhichContainDataPoint;
    }

    // Returns the index that contains the smallest valued element.
    private static Constants.Activity getActivityWithSmallestDistanceToDataPoint(
            DataPointAggregated dataPoint, List<Constants.Activity> activitiesWhichContainDataPoint) {
        Constants.Activity activityClosestToDataPoint = activitiesWhichContainDataPoint.get(0);

        for (Constants.Activity activity : activitiesWhichContainDataPoint) {
            if (dataPoint.distanceToCentroids[activity.ordinal()]
                    < dataPoint.distanceToCentroids[activityClosestToDataPoint.ordinal()]) {
                activityClosestToDataPoint = Constants.Activity.values()[activity.ordinal()];
            }
        }

        return activityClosestToDataPoint;
    }

    private static double addToAverage(double oldAverage, double oldSize, double newAverage, double newSize) {
        return (oldAverage * oldSize + newAverage * newSize) / (oldSize + newSize);
    }

}
