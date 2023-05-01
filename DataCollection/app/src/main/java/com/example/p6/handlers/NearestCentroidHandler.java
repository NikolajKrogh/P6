package com.example.p6.handlers;

import com.example.p6.classes.Centroid;
import com.example.p6.classes.Constants;
import com.example.p6.classes.DataPointAggregated;

import java.util.ArrayList;
import java.util.List;

public class NearestCentroidHandler {

    public static Centroid[] centroids = new Centroid[Constants.NUMBER_OF_LABELS];
    public static Centroid[] generalModelCentroids = {
            new Centroid(70.89044447734003,49,109,0.0,0.0,70.64912280701755,(byte) 0,243),
            new Centroid(111.9688301170213,66,162,111.19583333333334,108.41818181818182,119.0,(byte) 1,250),
            new Centroid(163.32964324429457,106,187,157.8181818181818,158.35087719298247,160.0,(byte) 2,249),
            new Centroid(129.62290932844044,77,167,0.12546125461254612,0.0,127.94642857142857,(byte) 3,276)
    };
    public static Constants.Activity predict(DataPointAggregated dataPoint, Centroid[] model) {
        List<Constants.Activity> activitiesWhichContainDataPoint  = getActivitiesWhichContainDataPoint(dataPoint, model);

        if(activitiesWhichContainDataPoint.size() == 0)
            return Constants.Activity.UNLABELED;

        if (activitiesWhichContainDataPoint.size() == 1)
            return activitiesWhichContainDataPoint.get(0);

        for (Constants.Activity activity : activitiesWhichContainDataPoint)
            dataPoint.distanceToCentroids[activity.ordinal()] = getDistanceToCentroid(dataPoint, model[activity.ordinal()]);

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

        currentCentroid.ellipse.maxStepCount
                = addToAverage(currentCentroid.ellipse.maxStepCount, size, dataPoint.stepCount);

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
            DataPointAggregated dataPoint, Centroid[] model){
        List<Constants.Activity> activitiesWhichContainDataPoint = new ArrayList<>();
        for (int i = 0; i < Constants.NUMBER_OF_LABELS; i++) {
            if (model[i].ellipse.contains(dataPoint)){
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
