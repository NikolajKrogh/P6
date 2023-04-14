package com.example.p6.classes;

import android.content.Context;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

public class CsvHandler {
    //region Centroid constants
    //endregion
    public static void writeToFile(String fileName, String content, Context context, boolean appendMode){
        int mode; //either Context.MODE_APPEND or Context.MODE_PRIVATE
        if (appendMode)
            mode = Context.MODE_APPEND;
        else
            mode = Context.MODE_PRIVATE;
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
        StringBuilder content = new StringBuilder();

        if (fileIsEmpty(fileName, context)){
           content.append("minutes,heart_rate,step_count,label\n");
        }

        for (DataPoint dataPoint : dataPoints) {
            content.append(dataPoint.toString());
        }

        CsvHandler.writeToFile(fileName, content.toString(), context, true);
    }

    public static void writePredictedActivityToFile(String fileName, AccuracyData accuracyDataForActivity, List<Constants.Activity> predictedActivities, Context context) {

        StringBuilder content = new StringBuilder("accuracy: " + accuracyDataForActivity.accuracy + "\n\n");

        content.append("sitting predictions: ").append(accuracyDataForActivity.sittingPredictions).append("\n");
        content.append("walking predictions: ").append(accuracyDataForActivity.walkingPredictions).append("\n");
        content.append("running predictions: ").append(accuracyDataForActivity.runningPredictions).append("\n");
        content.append("cycling predictions: ").append(accuracyDataForActivity.cyclingPredictions).append("\n");

        short i = 0;
        content.append("\npredictions:\n");
        for (Constants.Activity activity : predictedActivities) {
            content.append(i).append(": ").append(activity.name().toLowerCase()).append("\n");
            i++;
        }

        CsvHandler.writeToFile(fileName, content.toString(), context, true);
    }

    public static void writeToTotalAccuracyForActivity(String fileName, AccuracyData accuracyDataForActivity, AccuracyData accuracyDataFromFile, Context context){
        accuracyDataFromFile.correctPredictions += accuracyDataForActivity.correctPredictions;
        accuracyDataFromFile.totalPredictions += accuracyDataForActivity.totalPredictions;
        accuracyDataFromFile.accuracy = (double) accuracyDataFromFile.correctPredictions / (double) accuracyDataFromFile.totalPredictions;
        accuracyDataFromFile.sittingPredictions += accuracyDataForActivity.sittingPredictions;
        accuracyDataFromFile.walkingPredictions += accuracyDataForActivity.walkingPredictions;
        accuracyDataFromFile.runningPredictions += accuracyDataForActivity.runningPredictions;
        accuracyDataFromFile.cyclingPredictions += accuracyDataForActivity.cyclingPredictions;

        String content = Constants.accuracyHeader + accuracyDataFromFile;
        CsvHandler.writeToFile(fileName, content, context, false);
    }

    public static AccuracyData getAccuracyDataFromFile(String fileName, Context context){
        AccuracyData accuracyDataForActivity = new AccuracyData(
                1, (short) 0, (short) 0, (short) 0, (short) 0, (short) 0, (short) 0);

        try {
            File file = new File(context.getDir(fileName,Context.MODE_PRIVATE),fileName);
            if (file.length() == 0){
                return accuracyDataForActivity;
            }
            FileReader filereader = new FileReader(file);
            CSVReader csvReader = new CSVReader(filereader);
            csvReader.readNext(); //skip header
            String[] accuracyDataFromFile = csvReader.readNext();

            accuracyDataForActivity.accuracy = Double.parseDouble(accuracyDataFromFile[0]);
            accuracyDataForActivity.correctPredictions = Short.parseShort((accuracyDataFromFile[1]));
            accuracyDataForActivity.totalPredictions = Short.parseShort((accuracyDataFromFile[2]));
            accuracyDataForActivity.sittingPredictions = Short.parseShort((accuracyDataFromFile[3]));
            accuracyDataForActivity.walkingPredictions = Short.parseShort((accuracyDataFromFile[4]));
            accuracyDataForActivity.runningPredictions = Short.parseShort((accuracyDataFromFile[5]));
            accuracyDataForActivity.cyclingPredictions = Short.parseShort((accuracyDataFromFile[6]));

        } catch (IOException | CsvValidationException e) {
            throw new RuntimeException(e);
        }
        return accuracyDataForActivity;
    }

    private static boolean fileIsEmpty(String fileName, Context context) {
        File file = new File(context.getDir(fileName, Context.MODE_APPEND).getPath(),fileName);
        return file.length() == 0;
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
