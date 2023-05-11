package com.example.p6.classes;

import java.text.DecimalFormat;
import java.time.format.DateTimeFormatter;

public class Constants {
    public final static byte NUMBER_OF_LABELS = 4;
    public final static byte TIME_WINDOW_SIZE = 1;
    public final static double DELTA = 0.0001;
    public static String accuracyHeader = "accuracy,correct,total,sitting,walking,running,cycling," +
            "unlabeled,sitting%,walking%,running%,cycling%,unlabeled%\n";
    public static String dataPointHeader = "minutes,heart_rate,step_count,label\n";
    public static String centroidHeader =
            "heart_rate,min_heart_rate,max_heart_rate,step_count,min_step_count,max_step_count,label,centroid_size\n";
    public static String centroidHistoryHeader = "date," +
            "heart_rate_sitting,min_heart_rate_sitting,max_heart_rate_sitting,step_count_sitting," +
            "min_step_count_sitting,max_step_count_sitting,label_sitting,centroid_size_sitting," +
            "heart_rate_walking,min_heart_rate_walking,max_heart_rate_walking,step_count_walking," +
            "min_step_count_walking,max_step_count_walking,label_walking,centroid_size_walking," +
            "heart_rate_running,min_heart_rate_running,max_heart_rate_running,step_count_running," +
            "min_step_count_running,max_step_count_running,label_running,centroid_size_running," +
            "heart_rate_cycling,min_heart_rate_cycling,max_heart_rate_cycling,step_count_cycling," +
            "min_step_count_cycling,max_step_count_cycling,label_cycling,centroid_size_cycling\n";

    public static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH.mm.ss");
    public static final DecimalFormat clockFormat = new DecimalFormat("#00");

    //region Enums
    public enum Activity {
        SITTING,
        WALKING,
        RUNNING,
        CYCLING,
        UNLABELED
    }

    public enum Screen {
        MAIN,
        SELECT,
        DISPLAY,
        VIEW_MODEL
    }

    public enum Mode {
        PREDICT_ACTIVITY,
        UPDATE_WITH_LABELS,
        TEST_ACCURACY,
        COLLECT_DATA,
        UPDATE_MODEL
    }

    public static final byte NOT_SET = -1;

    //endregion
}
