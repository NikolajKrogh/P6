package com.example.p6.classes;

import java.util.ArrayList;
import java.util.List;

public class PreProcessing {
    private static List<Row> timeSeriesArray = new ArrayList<>();
    static short minute = 0;
    static short prevMinute = -1;
    static short accumulatedHeartRate = 0;
    static short numberOfDataPointsInMinute = 0;
    static int stepCountAtStartOfMinute = 0;
    static int prevStepCount;
    static boolean isFirstDataPoint = true;
    public static void makeBudgetTimeSeries(List<Row> dataPointsToAddArray, String sessionId) {
        for (Row row : dataPointsToAddArray
             ) {
            minute = row.minutes;

            if (minute != prevMinute && !isFirstDataPoint){
                addTimeSeriesToArray(row, sessionId);
                resetValues();
            }

            if (stepCountAtStartOfMinute == 0){
                stepCountAtStartOfMinute = row.step_count;
            }

            accumulatedHeartRate += row.heartRate;
            prevMinute = minute;
            prevStepCount = row.step_count;

            isFirstDataPoint = false;
        }
    }

    private static void addTimeSeriesToArray(Row row, String sessionId){
        short avgHeartRate = (short) (accumulatedHeartRate / numberOfDataPointsInMinute);
        int stepCountDiff = stepCountAtStartOfMinute - prevStepCount;
        timeSeriesArray.add(new Row(avgHeartRate, stepCountDiff, row.label, sessionId));
    }

    private static void resetValues(){
        accumulatedHeartRate = 0;
        stepCountAtStartOfMinute = 0;
        numberOfDataPointsInMinute = 0;
    }

    public static void preprocessData(List<Row> dataPointsToAddArray){

    }
}
