package com.example.p6.handlers;

import static com.example.p6.classes.Constants.Activity.*;

import com.example.p6.classes.Centroid;
import com.example.p6.classes.Constants;
import com.example.p6.classes.DataPointAggregated;

import java.util.ArrayList;
import java.util.List;

public class NearestCentroidHandler {

    public static Centroid[] centroids = new Centroid[Constants.NUMBER_OF_LABELS];
    public static Centroid[] generalModelCentroids = {
            new Centroid(76.96141393842203,54.495219885277244,105.07047619047619,0.0,0.0,18.0,(byte) 0,180),
            new Centroid(102.65760703838312,71.9047619047619,123.06796116504854,108.5632911392405,0.0,262.0,(byte) 1,215),
            new Centroid(165.58992489087657,140.3695652173913,175.43103448275863,165.1970802919708,133.0,174.0,(byte) 2,178),
            new Centroid(122.26366054900114,65.28023032629558,169.44343891402715,0.3458646616541353,0.0,34.0,(byte) 3,171)};

    // Calculates the distance from vectorToAddToCentroid to every centroid
    // Returns the label of the centroid that is closest to vectorToAddToCentroid
    public static Constants.Activity predict(DataPointAggregated dataPoint, Centroid[] model) {
        for (int i = 0; i < Constants.NUMBER_OF_LABELS; i++) {
            dataPoint.distanceToCentroids[i]
                    = getDistanceToCentroid(dataPoint, model[i]);
        }

        List<Constants.Activity> activitiesWhichContainDataPoint  = getActivitiesWhichContainDataPoint(dataPoint, model);

        if(activitiesWhichContainDataPoint.size() == 0) {
            return Constants.Activity.UNLABELED;
        }

        if (activitiesWhichContainDataPoint.size() == 1) {
            return activitiesWhichContainDataPoint.get(0);
        }

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

        for (int i = 0; i < Constants.NUMBER_OF_LABELS; i++) {
            if (dataPoint.distanceToCentroids[i]
                    < dataPoint.distanceToCentroids[activityClosestToDataPoint.ordinal()]) {
                activityClosestToDataPoint = Constants.Activity.values()[i];
            }
        }
        return activityClosestToDataPoint;
    }

    private static double addToAverage(double average, double size, double value) {
        return (size * average + value) / (size + 1);
    }

}
