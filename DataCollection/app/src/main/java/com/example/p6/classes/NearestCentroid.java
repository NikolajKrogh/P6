package com.example.p6.classes;

import static com.example.p6.classes.Constants.Activity.SITTING;
import static java.lang.Math.pow;
import static java.lang.Math.sqrt;

import java.util.ArrayList;
import java.util.List;

public class NearestCentroid {

    public static Centroid[] centroids = new Centroid[Constants.NUMBER_OF_LABELS];
    private static final DataPointBasic tempDataPoint = new DataPointBasic(100, 10);
    private static final CentroidEdgeCases tempEdgeCases = new CentroidEdgeCases(tempDataPoint, tempDataPoint, tempDataPoint, tempDataPoint);
    public static Centroid[] generalModelCentroids = {new Centroid(75.02328727800564, 0.0, tempEdgeCases, (byte) 0, 180),
                                               new Centroid(103.66115908541717, 108.26506024096386, tempEdgeCases, (byte) 1, 215),
                                               new Centroid(168.35690810370753, 163.85714285714286, tempEdgeCases, (byte) 2, 96),
                                               new Centroid(117.41208256764986, 0.19672131147540983, tempEdgeCases, (byte) 3, 79)
                                              };

    // Calculates the distance from vectorToAddToCentroid to every centroid
    // Returns the label of the centroid that is closest to vectorToAddToCentroid
    public static Constants.Activity predict(DataPointAggregated dataPoint, Centroid[] model) {
        double[] distanceToCentroids={0,0,0,0};

        for (int i = 0; i < Constants.NUMBER_OF_LABELS; i++)
            distanceToCentroids[i] = sqrt(pow((model[i].heartRate - dataPoint.heartRate),2)) +
                    sqrt(pow((model[i].stepCount - dataPoint.stepCount),2));

        List<Constants.Activity> activitiesWhichOverlapDataPoint  = getActivitiesWhichOverlapDataPoint(dataPoint, model);

        if(activitiesWhichOverlapDataPoint.size() == 0) {
            return Constants.Activity.UNLABELED;
        }

        if (activitiesWhichOverlapDataPoint.size() == 1) {
            return activitiesWhichOverlapDataPoint.get(0);
        }

        return getActivityWithSmallestDistanceToDataPoint(distanceToCentroids, activitiesWhichOverlapDataPoint);
    }

    private static List<Constants.Activity> getActivitiesWhichOverlapDataPoint(DataPointAggregated dataPoint, Centroid[] model){
        List<Constants.Activity> activitiesWhichOverlapDataPoint = new ArrayList<>();
        for (Constants.Activity activity : Constants.Activity.values()
             ) {
            if (EllipseHandler.checkIfPointWithinEllipse(dataPoint, model[activity.ordinal()])){
                activitiesWhichOverlapDataPoint.add(activity);
            }
        }
        return activitiesWhichOverlapDataPoint;
    }

    // Returns the index that contains the smallest valued element.
    private static Constants.Activity getActivityWithSmallestDistanceToDataPoint(double[] distanceToCentroids, List<Constants.Activity> activitiesWhichOverlapDataPoint) {
        Constants.Activity activityClosestToDataPoint = activitiesWhichOverlapDataPoint.get(0);

        for (Constants.Activity activity : activitiesWhichOverlapDataPoint
             ) {
            if (distanceToCentroids[activity.ordinal()] < distanceToCentroids[activityClosestToDataPoint.ordinal()])
                activityClosestToDataPoint = activity;
        }

        return activityClosestToDataPoint;
    }

    public static Centroid updateModel(Constants.Activity activity, DataPointAggregated dataPoint) {
        int size =  centroids[activity.ordinal()].size;
        Centroid currentCentroid = centroids[activity.ordinal()];

        // Update heart rate
        currentCentroid.heartRate
                = addToAverage(currentCentroid.heartRate, size, dataPoint.heartRate);

        // Update step-count
        currentCentroid.stepCount
                = addToAverage(currentCentroid.stepCount, size, dataPoint.stepCount);

        // Update northern edge case
        currentCentroid.edgeCases.northernMostPoint.heartRate
                = addToAverage(currentCentroid.edgeCases.northernMostPoint.heartRate,
                size, dataPoint.edgeCases.northernMostPoint.heartRate);

        currentCentroid.edgeCases.northernMostPoint.stepCount
                = addToAverage(currentCentroid.edgeCases.northernMostPoint.stepCount,
                size, dataPoint.edgeCases.northernMostPoint.stepCount);

        // Update eastern edge case
        currentCentroid.edgeCases.easternMostPoint.heartRate
                = addToAverage(currentCentroid.edgeCases.easternMostPoint.heartRate,
                size, dataPoint.edgeCases.easternMostPoint.heartRate);

        currentCentroid.edgeCases.easternMostPoint.stepCount
                = addToAverage(currentCentroid.edgeCases.easternMostPoint.stepCount,
                size, dataPoint.edgeCases.easternMostPoint.stepCount);

        // Update southern edge case
        currentCentroid.edgeCases.southernMostPoint.heartRate
                = addToAverage(currentCentroid.edgeCases.southernMostPoint.heartRate,
                size, dataPoint.edgeCases.southernMostPoint.heartRate);

        currentCentroid.edgeCases.southernMostPoint.stepCount
                = addToAverage(currentCentroid.edgeCases.southernMostPoint.stepCount,
                size, dataPoint.edgeCases.southernMostPoint.stepCount);

        // Update western edge case
        currentCentroid.edgeCases.westernMostPoint.heartRate
                = addToAverage(currentCentroid.edgeCases.westernMostPoint.heartRate,
                size, dataPoint.edgeCases.westernMostPoint.heartRate);

        currentCentroid.edgeCases.northernMostPoint.stepCount
                = addToAverage(currentCentroid.edgeCases.northernMostPoint.stepCount,
                size, dataPoint.edgeCases.northernMostPoint.stepCount);

        // Update axis
        currentCentroid.semiMajorAxis = EllipseHandler.getSemiMajorAxis(currentCentroid);
        currentCentroid.semiMajorAxis = EllipseHandler.getSemiMinorAxis(currentCentroid);

        // Update size
        currentCentroid.size++;

        return centroids[activity.ordinal()];
    }

    private static double addToAverage(double average, double size, double value) {
        return (size * average + value) / (size + 1);
    }

}
