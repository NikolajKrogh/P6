package com.example.p6;

import android.app.Activity;
import android.content.Context;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;

import android.content.Context;
import android.util.Log;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;

public class NearestCentroidActivity extends Activity {
    double[][] centroids = new double[3][5]; //should be [4][5] when we have running data



        /*{{0.0165657, 0.0223076, 0.01979614, 0.24783714, 0},
            {0.38013184,0.40207441,0.32950732,0.54220732,0.44964295},
            {0.2501793,0.16848506,0.16003128,0.61625866,0.22464558}};*/
    //implement such that we create the centroid file if it does not exists based on the above centroids
        /* put this into csv file:
        acc_x,acc_y,acc_z,heart_rate,step_count,label
        0.0165657,0.0223076,0.01979614,0.24783714,0,0
        0.38013184,0.40207441,0.32950732,0.54220732,0.44964295,1
        0.2501793,0.16848506,0.16003128,0.61625866,0.22464558,3
         */

    private double[] convertStringArrayToDoubleArray(String[] stringArray) {
        //double[] result = new double[];
        return new double[]{1.2};
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
                //vi håber den skipper header ellers skal vi gøre det et eller andet ved det
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


        /*
        try {
            path = context.getDir(fileName, Context.MODE_APPEND); // Use MODE_APPEND if you don't want to overwrite the content
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        try {
            File file = new File(path.getPath(), fileName);
            writer.write(content.getBytes());
            writer.close();

        */
    }
}


