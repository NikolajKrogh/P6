package com.example.p6.handlers;

import static com.example.p6.classes.Constants.Mode.TEST_ACCURACY;

import android.content.Context;

import com.example.p6.activities.MainActivity;
import com.example.p6.classes.AccuracyData;
import com.example.p6.classes.Centroid;
import com.example.p6.classes.Constants;
import com.example.p6.classes.DataPointRaw;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class CsvHandler {
    public static void writeToCentroidHistory(Centroid[] centroids, String dateTime, Context context){
        String content = "";
        String fileName = "centroids_history.csv";

        if (fileIsEmpty(fileName, context)){
            content += Constants.centroidHistoryHeader;
            content += dateTime + ",";
            content += convertArrayOfCentroidsToString(NearestCentroidHandler.generalModelCentroids, ",") +"\n";
        }
        content += dateTime + ",";
        content += convertArrayOfCentroidsToString(centroids, ",") + "\n";
        writeToFile(fileName, content, context, true);
    }

    public static void writeDataPointsToFile(String fileName, List<DataPointRaw> dataPoints,
                                             Context context) {
        StringBuilder content = new StringBuilder();

        if (fileIsEmpty(fileName, context)){
           content.append(Constants.dataPointHeader);
        }

        for (DataPointRaw dataPoint : dataPoints) {
            content.append(dataPoint.toString());
        }

        writeToFile(fileName, content.toString(), context, true);
    }

    public static void writePredictedActivityToFile(String fileName, AccuracyData accuracyDataForActivity, List<Constants.Activity> predictedActivities, Context context) {

        StringBuilder content = new StringBuilder("accuracy: " + accuracyDataForActivity.accuracy + "\n\n");

        content.append("sitting predictions: ").append(accuracyDataForActivity.sittingPredictions).append("\n");
        content.append("walking predictions: ").append(accuracyDataForActivity.walkingPredictions).append("\n");
        content.append("running predictions: ").append(accuracyDataForActivity.runningPredictions).append("\n");
        content.append("cycling predictions: ").append(accuracyDataForActivity.cyclingPredictions).append("\n");

        content.append("\npredictions:\n");
        for (short i = 0; i < predictedActivities.size(); i++) {
            content.append(i);
            content.append(": ");
            content.append(predictedActivities.get(i).name().toLowerCase());
            content.append("\n");
        }

        writeToFile(fileName, content.toString(), context, true);
    }

    public static void writeToTotalAccuracyForActivity(String fileName, AccuracyData
            accuracyDataForActivity, AccuracyData accuracyDataFromFile, Context context){

        accuracyDataFromFile.correctPredictions += accuracyDataForActivity.correctPredictions;
        accuracyDataFromFile.totalPredictions += accuracyDataForActivity.totalPredictions;
        accuracyDataFromFile.accuracy = accuracyDataFromFile.getPercentage(accuracyDataFromFile.correctPredictions);

        accuracyDataFromFile.sittingPredictions += accuracyDataForActivity.sittingPredictions;
        accuracyDataFromFile.walkingPredictions += accuracyDataForActivity.walkingPredictions;
        accuracyDataFromFile.runningPredictions += accuracyDataForActivity.runningPredictions;
        accuracyDataFromFile.cyclingPredictions += accuracyDataForActivity.cyclingPredictions;
        accuracyDataFromFile.unlabeledPredictions += accuracyDataForActivity.unlabeledPredictions;

        accuracyDataFromFile.sittingPercentage = accuracyDataFromFile.getPercentage(accuracyDataFromFile.sittingPredictions);
        accuracyDataFromFile.walkingPercentage = accuracyDataFromFile.getPercentage(accuracyDataFromFile.walkingPredictions);
        accuracyDataFromFile.runningPercentage = accuracyDataFromFile.getPercentage(accuracyDataFromFile.runningPredictions);
        accuracyDataFromFile.cyclingPercentage = accuracyDataFromFile.getPercentage(accuracyDataFromFile.cyclingPredictions);
        accuracyDataFromFile.unlabeledPercentage = accuracyDataFromFile.getPercentage(accuracyDataFromFile.unlabeledPredictions);

        String content = Constants.accuracyHeader + accuracyDataFromFile;
        writeToFile(fileName, content, context, false);
    }

    public static AccuracyData getAccuracyDataFromFile(String fileName, Context context)
            throws IOException, CsvValidationException {

        AccuracyData accuracyDataForActivity = new AccuracyData();
        try {
            File file = new File(context.getDir(fileName,Context.MODE_PRIVATE),fileName);
            if (file.length() == 0){
                return accuracyDataForActivity;
            }
            FileReader fileReader = new FileReader(file);
            CSVReader csvReader = new CSVReader(fileReader);
            csvReader.readNext(); //skip header
            String[] accuracyDataFromFile = csvReader.readNext();

            accuracyDataForActivity.accuracy = Double.parseDouble(accuracyDataFromFile[0]);
            accuracyDataForActivity.correctPredictions = Short.parseShort((accuracyDataFromFile[1]));
            accuracyDataForActivity.totalPredictions = Short.parseShort((accuracyDataFromFile[2]));
            accuracyDataForActivity.sittingPredictions = Short.parseShort((accuracyDataFromFile[3]));
            accuracyDataForActivity.walkingPredictions = Short.parseShort((accuracyDataFromFile[4]));
            accuracyDataForActivity.runningPredictions = Short.parseShort((accuracyDataFromFile[5]));
            accuracyDataForActivity.cyclingPredictions = Short.parseShort((accuracyDataFromFile[6]));
            accuracyDataForActivity.unlabeledPredictions = Short.parseShort((accuracyDataFromFile[7]));
        }
        catch (IOException e) {
            throw new IOException();
        }
        catch (CsvValidationException e) {
            throw new CsvValidationException();
        }
        return accuracyDataForActivity;
    }

    public static void deleteFile(String fileName, Context context){
        File file = new File(context.getDir(fileName, Context.MODE_PRIVATE).getPath(),fileName);
        file.delete();
    }

    public static Centroid[] getCentroidsFromFile(Context context) throws IOException, CsvValidationException {
        Centroid[] centroids = new Centroid[Constants.NUMBER_OF_LABELS];
        String fileName = "centroids.csv";
        try {
            File file = new File(context.getDir(fileName,Context.MODE_PRIVATE),fileName);
            if (file.length() == 0){
                writeCentroidsToFile(NearestCentroidHandler.generalModelCentroids, context);
            }
            FileReader filereader = new FileReader(file);
            CSVReader csvReader = new CSVReader(filereader);
            csvReader.readNext(); //skip header
            // we are going to read data line by line
            int i = 0;
            String[] nextEntry;
            while ((nextEntry = csvReader.readNext()) != null) {
                centroids[i] = new Centroid(nextEntry[0],nextEntry[1],nextEntry[2],nextEntry[3],
                        nextEntry[4],nextEntry[5],nextEntry[6],nextEntry[7]);
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

    public static List<DataPointRaw> getDataPointsFromFile(Context context, String FileName) throws IOException, CsvValidationException {
        List<DataPointRaw> dataPoints = new ArrayList<>();
        BufferedReader reader;

        try {
            reader = new BufferedReader(
                    new InputStreamReader(context.getAssets().open(FileName)));

            CSVReader csvReader = new CSVReader(reader);
            csvReader.readNext(); //skip header
            String[] nextEntry;


            while ((nextEntry = csvReader.readNext()) != null) {
                dataPoints.add(new DataPointRaw(
                        Short.parseShort(nextEntry[1]),
                        Integer.parseInt(nextEntry[2]),
                        Byte.parseByte(nextEntry[3]),
                        Short.parseShort(nextEntry[0])));
            }

        }
        catch (IOException e) {
            throw new IOException();
        }
        catch (CsvValidationException e) {
            throw new CsvValidationException();
        }

        return dataPoints;
    }

    public static void writeCentroidsToFile(Centroid[] centroids, Context context){
        String content = Constants.centroidHeader;
        String fileName = "centroids.csv";
        content += convertArrayOfCentroidsToString(centroids, "\n");
        writeToFile(fileName, content, context, false);
    }

    public static void writeToAccuracyFileForActivity(AccuracyData accuracyDataForActivity, Context context) {
        String fileName = "";
        if (MainActivity.trackingMode == TEST_ACCURACY) {
            fileName += "test_";
        }
        fileName += "accuracy_for_" + MainActivity.activityToTrack.name().toLowerCase() + "_" +
                Constants.dateTimeFormatter.format(LocalDateTime.now()) + ".txt";
        CsvHandler.writePredictedActivityToFile(
                fileName,
                accuracyDataForActivity,
                PreProcessingHandler.predictedActivities,
                context);
    }

    public static void writeToTotalAccuracyFileForActivity(AccuracyData accuracyDataForActivity, Context context) throws CsvValidationException, IOException {
        String fileName = "accuracy_total_for_" + MainActivity.activityToTrack.name().toLowerCase() + ".csv";
        CsvHandler.writeToTotalAccuracyForActivity(
                fileName,
                accuracyDataForActivity,
                CsvHandler.getAccuracyDataFromFile(fileName, context),
                context);
    }


    private static void writeToFile(String fileName, String content, Context context, boolean appendMode){
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

    private static String convertArrayOfCentroidsToString(Centroid[] centroids, String delimiter) {
        StringBuilder result = new StringBuilder();
        for (Centroid centroid : centroids) {
            result.append(centroid.toString());
            result.append(delimiter);
        }
        return result.toString();
    }

    private static boolean fileIsEmpty(String fileName, Context context) {
        File file = new File(context.getDir(fileName, Context.MODE_PRIVATE).getPath(),fileName);
        return file.length() == 0;
    }
}
