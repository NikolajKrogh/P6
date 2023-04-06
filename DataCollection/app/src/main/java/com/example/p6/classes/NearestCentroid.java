package com.example.p6.classes;

import android.content.Context;

import java.io.File;
import java.io.FileOutputStream;

public class NearestCentroid {
    enum HeaderValues {
        HEART_RATE,
        STEP_COUNT,
        LABEL,
        CENTROID_SIZE
    }
    public static Centroid[] centroids = new Centroid[Constants.NUMBER_OF_LABELS];
    public Centroid[] generalModelCentroids = {new Centroid(75.02328727800564, 0.0, (byte) 0, 180),
                                               new Centroid(103.66115908541717, 108.26506024096386, (byte) 1, 215),
                                               new Centroid(168.35690810370753, 163.85714285714286, (byte) 2, 96),
                                               new Centroid(117.41208256764986, 0.19672131147540983, (byte) 3, 79)
                                              };

    private Centroid updateModel(Centroid centroid, Row row) {
        // maybe check if anything is empty
        centroid.heartRate = addToAverage(centroid.heartRate, centroid.size, row.heartRate);
        centroid.stepCount = addToAverage(centroid.stepCount, centroid.size, row.stepCount);
        centroid.size++;

        return centroid;
    }

    double addToAverage(double average, double size, double value)
    {
        return (size * average + value) / (size + 1);
    }

}
