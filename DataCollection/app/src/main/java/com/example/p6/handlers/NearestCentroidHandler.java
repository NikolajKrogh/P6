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
                    = getActivitiesWhichContainDataPoint(dataPoint, model, (byte) 10);

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

    public static Centroid updateModel(Constants.Activity activity, DataPointAggregated dataPoint) {
        int size =  centroids[activity.ordinal()].size;
        Centroid currentCentroid = centroids[activity.ordinal()];

        // Update heart rate
        currentCentroid.heartRate
                = addToAverage(currentCentroid.heartRate, size, dataPoint.heartRate);

        currentCentroid.ellipse.minHeartRate
                = addToAverage(currentCentroid.ellipse.minHeartRate, size, dataPoint.minHeartRate);

        currentCentroid.ellipse.maxHeartRate
                = addToAverage(currentCentroid.ellipse.maxHeartRate, size, dataPoint.maxHeartRate);

        // Update step-count
        currentCentroid.stepCount
                = addToAverage(currentCentroid.stepCount, size, dataPoint.stepCount);

        currentCentroid.ellipse.minStepCount
                = addToAverage(currentCentroid.ellipse.minStepCount, size, dataPoint.stepCount);

        currentCentroid.ellipse.setMaxStepCount(
                addToAverage(currentCentroid.ellipse.getMaxStepCount(), size, dataPoint.stepCount));

        // Update size
        currentCentroid.size++;

        return centroids[activity.ordinal()];
    }

    private static double getDistanceToCentroid(DataPointAggregated dataPoint, Centroid centroid) {
        double x1 = dataPoint.heartRate;
        double y1 = dataPoint.stepCount;
        double x2 = centroid.heartRate;
        double y2 = centroid.stepCount;

        return Math.sqrt(Math.pow((y2 - y1), 2) + Math.pow((x2 - x1), 2));
    }

    private static List<Constants.Activity> getActivitiesWhichContainDataPoint(
            DataPointAggregated dataPoint, Centroid[] model, byte buffer){
        List<Constants.Activity> activitiesWhichContainDataPoint = new ArrayList<>();
        for (int i = 0; i < Constants.NUMBER_OF_LABELS; i++) {
            if (model[i].ellipse.contains(dataPoint, (byte) buffer)){
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

    private static double addToAverage(double average, double size, double value) {
        return (size * average + value) / (size + 1);
    }

}
