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
        String result = "";
        for (Centroid centroid : centroids) {
            result += centroid.toString();
            result += delimiter;
        }
        return result;
    }

    public static Centroid[] getCentroidsFromFile(Context context) throws IOException, CsvValidationException {
        Centroid[] centroids = new Centroid[Constants.NUMBER_OF_LABELS];
        String fileName = "centroids.csv";
        try {
            File file = new File(context.getDir(fileName,Context.MODE_PRIVATE),fileName);
            FileReader filereader = new FileReader(file);
            CSVReader csvReader = new CSVReader(filereader);
            csvReader.readNext(); //skip header
            String[] nextEntry;
            int i = 0;
            // we are going to read data line by line
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
