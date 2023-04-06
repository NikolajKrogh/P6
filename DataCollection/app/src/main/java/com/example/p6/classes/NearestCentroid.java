package com.example.p6.classes;

import static com.example.p6.classes.Constants.Activity.SITTING;
import static java.lang.Math.pow;
import static java.lang.Math.sqrt;

public class NearestCentroid {

    public static Centroid[] centroids = new Centroid[Constants.NUMBER_OF_LABELS];
    public Centroid[] generalModelCentroids = {new Centroid(75.02328727800564, 0.0, (byte) 0, 180),
                                               new Centroid(103.66115908541717, 108.26506024096386, (byte) 1, 215),
                                               new Centroid(168.35690810370753, 163.85714285714286, (byte) 2, 96),
                                               new Centroid(117.41208256764986, 0.19672131147540983, (byte) 3, 79)
                                              };

    // Calculates the distance from vectorToAddToCentroid to every centroid
    // Returns the label of the centroid that is closest to vectorToAddToCentroid
    public Constants.Activity runNearestCentroidAlgorithm(DataPoint dataPoint, Centroid[] model) {
        double[] distanceToCentroids={0,0,0,0};

        for (int i = 0; i < Constants.NUMBER_OF_LABELS; i++)
            distanceToCentroids[i] = sqrt(pow((model[i].heartRate - dataPoint.heartRate),2)) +
                    sqrt(pow((model[i].stepCount - dataPoint.stepCount),2));

        // Returns the label that is closest to vectorToAddToCentroid
        return getActivityWithSmallestDistanceToDataPoint(distanceToCentroids);
    }

    // Returns the index that contains the smallest valued element.
    private Constants.Activity getActivityWithSmallestDistanceToDataPoint(double[] distanceToCentroids) {
        Constants.Activity activity = SITTING;
        byte length = (byte)distanceToCentroids.length;
        for (byte i = 1; i < length; i++)
            if (distanceToCentroids[i] < distanceToCentroids[activity.ordinal()])
                activity = Constants.Activity.values()[i];

        return activity;
    }

    private Centroid updateModel(Centroid centroid, DataPoint dataPoint) {
        // maybe check if anything is empty
        centroid.heartRate = addToAverage(centroid.heartRate, centroid.size, dataPoint.heartRate);
        centroid.stepCount = addToAverage(centroid.stepCount, centroid.size, dataPoint.stepCount);
        centroid.size++;

        return centroid;
    }

    double addToAverage(double average, double size, double value) {
        return (size * average + value) / (size + 1);
    }

}
