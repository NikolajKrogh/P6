package com.example.p6.classes;

import android.content.Context;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;

import android.util.Log;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;

public class NearestCentroid {
    int NUMBER_OF_LABELS = 4;
    int NUMBER_OF_INPUT_PARAMETERS = 2;
    enum HeaderValues {
        HEART_RATE,
        STEP_COUNT,
        LABEL,
        CENTROID_SIZE
    }
    double[][] centroids = new double[NUMBER_OF_LABELS][NUMBER_OF_INPUT_PARAMETERS];
    public double[][] generalModelCentroids = {{75.02328727800564, 0.0, 0, 180},
                                               {103.66115908541717, 108.26506024096386, 1, 215},
                                               {168.35690810370753, 163.85714285714286, 2, 96},
                                               {117.41208256764986, 0.19672131147540983, 3, 79}};

    //implement such that we create the centroid file if it does not exists based on the above centroids
    private double[] convertStringArrayToDoubleArray(String[] stringArray) {
        int arrayLength = stringArray.length;
        double[] result = new double[arrayLength];
            for (int i = 0; i <= arrayLength; i++) {
                result[i] = Double.parseDouble(stringArray[i]);
            }
        return result;
    }

    public void getCentroidsFromFile(Context context) throws IOException, CsvValidationException {
        String fileName = "centroids/centroids.csv";
        String filePath = context.getFilesDir() + "/" + fileName;

        Log.i("filePath", filePath);
        try {
            FileReader filereader = new FileReader(filePath);
            CSVReader csvReader = new CSVReader(filereader);

            String[] nextEntry;
            int i = 0;
            // we are going to read data line by line
            while ((nextEntry = csvReader.readNext()) != null) {
                //vi håber den skipper header ellers skal vi gøre et eller andet ved det
                centroids[i] = convertStringArrayToDoubleArray(nextEntry);
            }
        }
        catch (IOException e) {
            throw new IOException();
        }
        catch (CsvValidationException e) {
            throw new CsvValidationException();
        }

    }


    public String makeStringToInsertIntoCsvFromCentroids(double[][] centroids) {
        String result = "heart_rate,step_count,label,centroid_size\n";
        for (int label = 0; label < NUMBER_OF_LABELS; label++) {
            result += String.format("%d,%d,%s,%s\n",centroids[HeaderValues.HEART_RATE.ordinal()],
                    centroids[HeaderValues.STEP_COUNT.ordinal()],
                    centroids[HeaderValues.LABEL.ordinal()],
                    centroids[HeaderValues.CENTROID_SIZE.ordinal()]);
        }
        return result;
    }

    //this function can both be used to write the general centroid but also to write the updated ones
    //here we should then pass file location as a parameter as well then
    public void writeCentroidsToFile(double[][] centroids, Context context) {
        String fileName = "centroids/centroids.csv";
        String content = makeStringToInsertIntoCsvFromCentroids(centroids);
        File path;

        try {
            path = context.getDir(fileName, Context.MODE_PRIVATE);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        try {
            File file = new File(path.getPath(),fileName);
            file.createNewFile(); // if file already exists, this will do nothing
            FileOutputStream writer = new FileOutputStream(file);
            writer.write(content.getBytes());
            writer.close();
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


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
