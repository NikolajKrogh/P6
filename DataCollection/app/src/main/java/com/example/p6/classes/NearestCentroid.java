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

    public double[][] updateModel(double[][] centroids, double[][] newCentroids) {
        //preprocessing(newCentroids);

        for(int i = 0; i < 4; i++){
            // maybe check if anything is empty
            centroids[i][HR_INDEX] = addToAverage(centroids[i][HR_INDEX],
                                    centroids[i][CENTROID_SIZE_INDEX], newCentroids[i][HR_INDEX]);
            centroids[i][STEP_COUNT_INDEX] = addToAverage(centroids[i][STEP_COUNT_INDEX],
                                    centroids[i][CENTROID_SIZE_INDEX], newCentroids[i][STEP_COUNT_INDEX]);

            centroids[i][CENTROID_SIZE_INDEX] = centroids[i][CENTROID_SIZE_INDEX] + newCentroids[i][CENTROID_SIZE_INDEX];
        }

        return centroids;
    }

    public String multiDimensionalArrayToString(double[][] updatedCentroids)
    {
        StringBuilder sb = new StringBuilder();

        for (double[] row : updatedCentroids) {
            for (double element: row)
                sb.append(element).append(",");
        }

        return sb.toString();
    }

    double addToAverage(double average, double size, double value)
    {
        return (size * average + value) / (size + 1);
    }

}
