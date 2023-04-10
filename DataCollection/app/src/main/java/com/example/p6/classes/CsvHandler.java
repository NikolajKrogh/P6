package com.example.p6.classes;

import android.content.Context;
import android.util.Log;

import com.opencsv.CSVParser;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class CsvHandler {
    //region Centroid constants
    //endregion
    public static void writeToFile(String fileName, String content, Context context, boolean appendMode){
        int mode; //either Context.MODE_APPEND or Context.MODE_PRIVATE
        if (appendMode)
            mode = Context.MODE_APPEND;
        else
            mode = context.MODE_PRIVATE;
        try {
            File file = new File(context.getDir(fileName, mode),fileName);
            file.createNewFile(); // if file already exists, this will do nothing
            FileOutputStream writer = new FileOutputStream(file,appendMode);
            writer.write(content.getBytes());
            writer.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void writeDataPointsToFile(String fileName, List<DataPoint> dataPoints, Context context) {
        File path;
        try {
            path = context.getDir(fileName, Context.MODE_APPEND);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        try {
            File file = new File(path.getPath(),fileName);
            file.createNewFile(); // if file already exists, this will do nothing
            FileOutputStream writer = new FileOutputStream(file,true);
            if (file.length() == 0){
                String dataPointHeaderBeforePreprocessing = "minutes,heart_rate,step_count,label\n";
                writer.write(dataPointHeaderBeforePreprocessing.getBytes());
            }
            for (DataPoint dataPoint : dataPoints) {
                writer.write(dataPoint.toString().getBytes());
            }
            writer.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    //implement such that we create the centroid file if it does not exists based on the above centroids
    private double[] convertStringArrayToDoubleArray(String[] stringArray) {
        int arrayLength = stringArray.length;
        double[] result = new double[arrayLength];
        for (int i = 0; i <= arrayLength; i++) {
            result[i] = Double.parseDouble(stringArray[i]);
        }
        return result;
    }

    public static String convertArrayOfCentroidsToString(Centroid[] centroids, String delimiter) {
        StringBuilder result = new StringBuilder();
        for (Centroid centroid : centroids) {
            result.append(centroid.toString());
            result.append(delimiter);
        }
        return result.toString();
    }

    public static void writeCentroidsToFile(boolean shouldWriteToCentroidHistory, Centroid[] centroids, Context context) {
        String fileName;
        String delimiter;
        String content = "";
        boolean appendMode;
        if (shouldWriteToCentroidHistory) {
            fileName = "centroids_history.csv";
            delimiter = ",";
            appendMode = true;

            File file = new File(context.getDir(fileName, Context.MODE_APPEND).getPath(),fileName);
            if (file.length() == 0){
                content += Constants.centroidHistoryHeader;
            }
        }
        else {
            content += Constants.centroidHeader;
            fileName = "centroids.csv";
            delimiter = "\n";
            appendMode = false;
        }

        content += CsvHandler.convertArrayOfCentroidsToString(centroids, delimiter);
        if (shouldWriteToCentroidHistory)
            content += "\n";
        CsvHandler.writeToFile(fileName,content,context,appendMode);
    }

    public static Centroid[] getCentroidsFromFile(Context context) throws IOException, CsvValidationException {
        Centroid[] centroids = new Centroid[Constants.NUMBER_OF_LABELS];
        String fileName = "centroids.csv";
        try {
            File file = new File(context.getDir(fileName,Context.MODE_PRIVATE),fileName);
            if (file.length() == 0){
                writeCentroidsToFile(false, NearestCentroid.generalModelCentroids, context);
            }
            FileReader filereader = new FileReader(file);
            CSVReader csvReader = new CSVReader(filereader);
            csvReader.readNext(); //skip header
            // we are going to read data line by line
            int i = 0;
            String[] nextEntry;
            while ((nextEntry = csvReader.readNext()) != null) {
                centroids[i] = new Centroid(nextEntry[0],nextEntry[1],nextEntry[2],nextEntry[3]);
                i++;
            }
        }
        catch (IOException e) {
            throw new IOException();
        }
        catch (CsvValidationException e) {
            throw new CsvValidationException();
        }
        return centroids;
    }

}
