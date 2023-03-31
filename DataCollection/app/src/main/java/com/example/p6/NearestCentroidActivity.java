package com.example.p6;

import android.app.Activity;
import android.content.Context;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;

import android.os.Bundle;
import android.util.Log;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;

public class NearestCentroidActivity extends Activity {
    int NUMBER_OF_LABELS = 4;
    int NUMBER_OF_INPUT_PARAMETER = 2;
    enum HeaderValues {
        HEART_RATE,
        STEP_COUNT,
        LABEL,
        CENTROID_SIZE
    }
    double[][] centroids = new double[NUMBER_OF_LABELS][NUMBER_OF_INPUT_PARAMETER];
    double[][] generalModelCentroids = {{ 75.02328728,0},
    {103.66115909,108.26506024},
    {168.3569081,163.85714286},
    {117.41208257,0.19672131}};

    //implement such that we create the centroid file if it does not exists based on the above centroids
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Context context = getApplicationContext();
        String fileName = "centroids/centroids.csv";
        String filePath = context.getFilesDir() + "/" + fileName;
        File csvFile = new File(filePath);
        if (!csvFile.exists())
        {
            writeCentroidsToFile(generalModelCentroids);
        }



    }
    private double[] convertStringArrayToDoubleArray(String[] stringArray) {
        int arrayLength = stringArray.length;
        double[] result = new double[arrayLength];
            for (int i = 0; i <= arrayLength; i++) {
                result[i] = Double.parseDouble(stringArray[i]);
            }
        return result;
    }

    public void getCentroidsFromFile() throws IOException, CsvValidationException {
        Context context = getApplicationContext();
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
        catch (IOException e)
        {
            throw new IOException();
        }
        catch (CsvValidationException e)
        {
            throw new CsvValidationException();
        }

    }


    public String makeStringToInsertIntoCsvFromCentroids(double[][] centroids)
    {
        String result = "heart_rate,step_count,label,centroid_size,update_threshold\n";
        for (int label = 0; label < NUMBER_OF_LABELS; label++) {
            result += String.format("%d,%d,%s,%s,0\n",centroids[HeaderValues.HEART_RATE.ordinal()],
                    centroids[HeaderValues.STEP_COUNT.ordinal()],
                    centroids[HeaderValues.LABEL.ordinal()],
                    centroids[HeaderValues.CENTROID_SIZE.ordinal()]);
        }
        return result;
    }

    //this function can both be used to write the general centroid but also to write the updated ones
    //here we should then pass file location as a parameter as well then
    public void writeCentroidsToFile(double[][] centroids) {
        Context context = getApplicationContext();
        String fileName = "centroids/centroids.csv";
        String content = makeStringToInsertIntoCsvFromCentroids(centroids);
        File path;

        try {
            path = context.getDir(fileName, Context.MODE_PRIVATE); // Use MODE_APPEND if you don't want to overwrite the content
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        try {
            File file = new File(path.getPath(),fileName);
            file.createNewFile(); // if file already exists, this will do nothing
            FileOutputStream writer = new FileOutputStream(file);
            writer.write(content.getBytes());
            writer.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }


    }


}


