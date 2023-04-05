package com.example.p6.classes;

import android.content.Context;
import android.util.Log;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;

public class NearestCentroid {
    enum HeaderValues {
        HEART_RATE,
        STEP_COUNT,
        LABEL,
        CENTROID_SIZE
    }
    CsvHandler csvHandler = new CsvHandler();
    public double[][] centroids = new double[csvHandler.NUMBER_OF_LABELS][csvHandler.NUMBER_OF_INPUT_PARAMETERS];
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
        for (double[] centroid: centroids) {
            result += String.format("%f,%f,%d,%d\n", centroid[HeaderValues.HEART_RATE.ordinal()],
                    centroid[HeaderValues.STEP_COUNT.ordinal()],
                    (int)centroid[HeaderValues.LABEL.ordinal()],
                    (int)centroid[HeaderValues.CENTROID_SIZE.ordinal()]);
        }
        return result;
    }

    //this function can both be used to write the general centroid but also to write the updated ones
    //here we should then pass file location as a parameter as well then
    public void writeCentroidsToFile(double[][] centroids, Context context) {
        String fileName = "centroids.csv";
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
