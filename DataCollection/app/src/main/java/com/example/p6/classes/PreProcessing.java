package com.example.p6.classes;

import android.util.Log;

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
    public static List<Row> makeBudgetTimeSeries(List<Row> dataPointsToAddArray, String sessionId) {
        for (Row row : dataPointsToAddArray){
            minute = row.minutes;

            if (minute != prevMinute && numberOfDataPointsInMinute > 0){
                addTimeSeriesToArray(row, sessionId);
                resetValues();
            }

            if (stepCountAtStartOfMinute == 0){
                stepCountAtStartOfMinute = row.step_count;
            }

            accumulatedHeartRate += row.heartRate;
            prevMinute = minute;
            prevStepCount = row.step_count;
            numberOfDataPointsInMinute++;
        }

        return timeSeriesArray;
    }

    private static void addTimeSeriesToArray(Row row, String sessionId){
        short avgHeartRate = (short) (accumulatedHeartRate / numberOfDataPointsInMinute);
        int stepCountDiff = prevStepCount - stepCountAtStartOfMinute;

        // should these rows have labels though?
        timeSeriesArray.add(new Row(avgHeartRate, stepCountDiff, row.label, sessionId));
    }

    private static void resetValues(){
        accumulatedHeartRate = 0;
        stepCountAtStartOfMinute = 0;
        numberOfDataPointsInMinute = 0;
    }
}
