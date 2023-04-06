package com.example.p6.classes;

import java.util.ArrayList;
import java.util.List;

public class PreProcessing {
    private static final byte NOT_SET = -1;
    private static List<Row> timeSeries = new ArrayList<>();
    static short minute = 0;
    static short prevMinute = NOT_SET;
    static short accumulatedHeartRate = 0;
    static short numberOfDataPointsInMinute = 0;
    static int stepCountAtStartOfMinute = NOT_SET;
    static int prevStepCount;
    public static List<Row> makeBudgetTimeSeries(List<Row> dataPointsToAddArray, String sessionId) {
        for (Row row : dataPointsToAddArray){
            minute = row.minutes;

            if (minute != prevMinute && numberOfDataPointsInMinute > 0){
                addTimeSeriesToList(sessionId);
                resetValues();
            }

            if (stepCountAtStartOfMinute == NOT_SET){
                stepCountAtStartOfMinute = row.step_count;
            }

            accumulatedHeartRate += row.heartRate;
            prevMinute = minute;
            prevStepCount = row.step_count;
            numberOfDataPointsInMinute++;
        }

        return timeSeries;
    }

    private static void addTimeSeriesToList(String sessionId){
        short avgHeartRate = (short) (accumulatedHeartRate / numberOfDataPointsInMinute);
        int stepCountDiff = prevStepCount - stepCountAtStartOfMinute;
        timeSeries.add(new Row(avgHeartRate, stepCountDiff, sessionId));
    }

    private static void resetValues(){
        accumulatedHeartRate = 0;
        stepCountAtStartOfMinute = NOT_SET;
        numberOfDataPointsInMinute = 0;
    }
}
