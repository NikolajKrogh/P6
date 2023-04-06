package com.example.p6.classes;

import static com.example.p6.classes.Constants.Activity.*;
import static java.lang.Math.*;

import android.content.Context;

import com.example.p6.activities.MainActivity;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;

import java.io.File;
import java.io.FileOutputStream;

public class NearestCentroid {
    int NUMBER_OF_LABELS = 4;
    int NUMBER_OF_INPUT_PARAMETERS = 2;

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

    // Calculates the distance from vectorToAddToCentroid to every centroid
    // Returns the label of the centroid that is closest to vectorToAddToCentroid
    public Constants.Activity runNearestCentroidAlgorithm(Row vectorToAddToCentroid, Centroid[] model) {
        double[] delta={0,0,0,0};

        for (int i = 0; i < NUMBER_OF_LABELS; i++)
            delta[i] = sqrt(pow((model[i].heartRate - vectorToAddToCentroid.heartRate),2)) +
                    sqrt(pow((model[i].stepCount - vectorToAddToCentroid.stepCount),2));

        // Returns the label that is closest to vectorToAddToCentroid
        return getMinimumDistanceCentroid(delta);
    }

    // Returns the index that contains the smallest valued element.
    private Constants.Activity getMinimumDistanceCentroid(double[] delta)
    {
        Constants.Activity minValueIndex = SITTING;
        for (int i = 1; i < delta.length; i++)
            if (delta[i] < delta[minValueIndex.ordinal()])
                minValueIndex = Constants.Activity.values()[i];

        return minValueIndex;
    }

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
