package com.example.p6.classes;

import android.content.Context;
import android.util.Log;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

public class CsvHandler {
    private enum AccuracyData {
        ACCURACY,
        CORRECT_PREDICTIONS,
        TOTAL_PREDICTIONS,
        SITTING_PREDICTIONS,
        WALKING_PREDICTIONS,
        RUNNING_PREDICTIONS,
        CYCLING_PREDICTIONS
    }


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

    public static String convertArrayOfCentroidsToString(Centroid[] centroids, String delimiter) {
        StringBuilder result = new StringBuilder();
        for (Centroid centroid : centroids) {
            result.append(centroid.toString());
            result.append(delimiter);
        }
        return result.toString();
    }

    public static void writeToCentroidFile(Centroid[] centroids, Context context){
        String content = Constants.centroidHeader;
        String fileName = "centroids.csv";
        content += CsvHandler.convertArrayOfCentroidsToString(centroids, "\n");
        CsvHandler.writeToFile(fileName, content, context, false);
    }

    public static void writeToCentroidHistory(Centroid[] centroids, String dateTime, Context context){
        String content = "";
        String fileName = "centroids_history.csv";

        if (fileIsEmpty(fileName, context)){
            content += Constants.centroidHistoryHeader;
            content += dateTime + ",";
            content += convertArrayOfCentroidsToString(NearestCentroid.generalModelCentroids, ",") +"\n";
        }
        content += dateTime + ",";
        content += CsvHandler.convertArrayOfCentroidsToString(centroids, ",") + "\n";
        CsvHandler.writeToFile(fileName, content, context, true);
    }

    public static void writeDataPointsToFile(String fileName, List<DataPoint> dataPoints, Context context) {
        String content = "";

        if (fileIsEmpty(fileName, context)){
           content += "minutes,heart_rate,step_count,label\n";
        }

        for (DataPoint dataPoint : dataPoints) {
            content += dataPoint.toString();
        }

        CsvHandler.writeToFile(fileName, content, context, true);
    }

    public static void writePredictedActivityToFile(String fileName, List<Constants.Activity> predictedActivities, double[] accuracyDataForActivity, Context context) {
        String content = "accuracy: " + accuracyDataForActivity[AccuracyData.ACCURACY.ordinal()] + "\n\n";

        content += "sitting predictions: " + accuracyDataForActivity[AccuracyData.SITTING_PREDICTIONS.ordinal()] + "\n";
        content += "walking predictions: " + accuracyDataForActivity[AccuracyData.WALKING_PREDICTIONS.ordinal()] + "\n";
        content += "running predictions: " + accuracyDataForActivity[AccuracyData.RUNNING_PREDICTIONS.ordinal()] + "\n";
        content += "cycling predictions: " + accuracyDataForActivity[AccuracyData.CYCLING_PREDICTIONS.ordinal()] + "\n";

        short i = 0;
        content += "\npredictions:\n";
        for (Constants.Activity activity : predictedActivities) {
            content += i + ": " + activity.name().toLowerCase() + "\n";
            i++;
        }

        CsvHandler.writeToFile(fileName, content, context, true);
    }

    public static void writeToAccuracyHistory(String fileName, double[] accuracyDataForActivity, double[] accuracyDataFromHistory, Context context){
        String content = Constants.accuracyHeader;

        accuracyDataFromHistory[AccuracyData.CORRECT_PREDICTIONS.ordinal()]
                += accuracyDataForActivity[AccuracyData.CORRECT_PREDICTIONS.ordinal()];
        accuracyDataFromHistory[AccuracyData.TOTAL_PREDICTIONS.ordinal()]
                += accuracyDataForActivity[AccuracyData.TOTAL_PREDICTIONS.ordinal()];
        accuracyDataFromHistory[AccuracyData.ACCURACY.ordinal()]
                = accuracyDataFromHistory[AccuracyData.CORRECT_PREDICTIONS.ordinal()]
                / accuracyDataFromHistory[AccuracyData.TOTAL_PREDICTIONS.ordinal()];
        accuracyDataFromHistory[AccuracyData.SITTING_PREDICTIONS.ordinal()]
                += accuracyDataForActivity[AccuracyData.SITTING_PREDICTIONS.ordinal()];
        accuracyDataFromHistory[AccuracyData.WALKING_PREDICTIONS.ordinal()]
                += accuracyDataForActivity[AccuracyData.WALKING_PREDICTIONS.ordinal()];
        accuracyDataFromHistory[AccuracyData.RUNNING_PREDICTIONS.ordinal()]
                += accuracyDataForActivity[AccuracyData.RUNNING_PREDICTIONS.ordinal()];
        accuracyDataFromHistory[AccuracyData.CYCLING_PREDICTIONS.ordinal()]
                += accuracyDataForActivity[AccuracyData.CYCLING_PREDICTIONS.ordinal()];

        for (AccuracyData accuracyData :AccuracyData.values()){
            content += String.format("%.4f,",accuracyDataFromHistory[accuracyData.ordinal()]);
        }

        CsvHandler.writeToFile(fileName, content, context, false);
    }

    public static double[] getAccuracyDataFromFile(String fileName, Context context){
        double accuracyDataForActivity[] = {0, 0, 0, 0, 0, 0, 0};
        try {
            File file = new File(context.getDir(fileName,Context.MODE_PRIVATE),fileName);
            if (file.length() == 0){
                return accuracyDataForActivity;
            }
            FileReader filereader = new FileReader(file);
            CSVReader csvReader = new CSVReader(filereader);
            csvReader.readNext(); //skip header
            // we are going to read data line by line

            String[] nextEntry = csvReader.readNext();

            for (AccuracyData accuracyData :AccuracyData.values()){
                accuracyDataForActivity[accuracyData.ordinal()] = Double.parseDouble(nextEntry[accuracyData.ordinal()]);
            }

        } catch (IOException | CsvValidationException e) {
            throw new RuntimeException(e);
        }
        return accuracyDataForActivity;
    }

    private static boolean fileIsEmpty(String fileName, Context context) {
        File file = new File(context.getDir(fileName, Context.MODE_APPEND).getPath(),fileName);
        if (file.length() == 0){
            return true;
        }
        return false;
    }

    public static Centroid[] getCentroidsFromFile(Context context) throws IOException, CsvValidationException {
        Centroid[] centroids = new Centroid[Constants.NUMBER_OF_LABELS];
        String fileName = "centroids.csv";
        try {
            File file = new File(context.getDir(fileName,Context.MODE_PRIVATE),fileName);
            if (file.length() == 0){
                writeToCentroidFile(NearestCentroid.generalModelCentroids, context);
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
