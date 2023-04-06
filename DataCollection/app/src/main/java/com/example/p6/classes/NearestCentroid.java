package com.example.p6.classes;

import android.content.Context;

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
    private Centroid[] centroids = new Centroid[csvHandler.NUMBER_OF_LABELS];
    public Centroid[] generalModelCentroids = {new Centroid(75.02328727800564, 0.0, (byte) 0, 180),
                                               new Centroid(103.66115908541717, 108.26506024096386, (byte) 1, 215),
                                               new Centroid(168.35690810370753, 163.85714285714286, (byte) 2, 96),
                                               new Centroid(117.41208256764986, 0.19672131147540983, (byte) 3, 79)
                                              };
    private void getCentroidsFromFile(Context context) throws IOException, CsvValidationException {
        String fileName = "centroids.csv";
        String filePath = context.getFilesDir() + "/" + fileName;

        try {
            FileReader filereader = new FileReader(filePath);
            CSVReader csvReader = new CSVReader(filereader);

            String[] nextEntry;
            int i = 0;
            // we are going to read data line by line
            while ((nextEntry = csvReader.readNext()) != null) {
                //vi håber den skipper header ellers skal vi gøre et eller andet ved det
                centroids[i] = new Centroid(nextEntry[0],nextEntry[1],nextEntry[2],nextEntry[3]);
            }
        }
        catch (IOException e) {
            throw new IOException();
        }
        catch (CsvValidationException e) {
            throw new CsvValidationException();
        }

    }

    //this function can both be used to write the general centroid but also to write the updated ones
    //here we should then pass file location as a parameter as well then
    public void writeCentroidsToFile(Centroid[] centroids, Context context) {
        String fileName = "centroids.csv";
        String content = centroids.toString();
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

    private Centroid updateModel(Centroid centroid, Row row) {
        // maybe check if anything is empty
        centroid.heartRate = addToAverage(centroid.heartRate, centroid.size, row.heartRate);
        centroid.step_count = addToAverage(centroid.step_count, centroid.size, row.stepCount);
        centroid.size++;

        return centroid;
    }

    double addToAverage(double average, double size, double value)
    {
        return (size * average + value) / (size + 1);
    }

}
