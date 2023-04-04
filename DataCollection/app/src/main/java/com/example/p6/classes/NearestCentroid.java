package com.example.p6.classes;

public class NearestCentroid {
    enum HeaderValues {
        HEART_RATE,
        STEP_COUNT,
        LABEL,
        CENTROID_SIZE
    }
    public double[][] generalModelCentroids = {{75.02328727800564, 0.0, 0, 180},
                                               {103.66115908541717, 108.26506024096386, 1, 215},
                                               {168.35690810370753, 163.85714285714286, 2, 96},
                                               {117.41208256764986, 0.19672131147540983, 3, 79}};


    static final int HR_INDEX = 0;
    static final int STEP_COUNT_INDEX = 1;
    static final int CENTROID_SIZE_INDEX = 3;

    public double[] updateModel(double[] centroid, double[] vectorToAddToCentroid) {
        //preprocessing(vectorToAddToCentroid);

        // maybe check if anything is empty
        centroid[HR_INDEX] = addToAverage(centroid[HR_INDEX],
                                          centroid[CENTROID_SIZE_INDEX],
                                          vectorToAddToCentroid[HR_INDEX]);
        centroid[STEP_COUNT_INDEX] = addToAverage(centroid[STEP_COUNT_INDEX],
                                                  centroid[CENTROID_SIZE_INDEX],
                                                  vectorToAddToCentroid[STEP_COUNT_INDEX]);

        centroid[CENTROID_SIZE_INDEX] = centroid[CENTROID_SIZE_INDEX] + vectorToAddToCentroid[CENTROID_SIZE_INDEX];

        return centroid;
    }

    public String multiDimensionalArrayToString(double[] updatedCentroid)
    {
        StringBuilder sb = new StringBuilder();

        for (double element : updatedCentroid)
            sb.append(element).append(",");

        return sb.toString();
    }

    double addToAverage(double average, double size, double value)
    {
        return (size * average + value) / (size + 1);
    }

}
