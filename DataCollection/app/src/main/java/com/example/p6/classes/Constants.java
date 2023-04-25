package com.example.p6.classes;

public class Constants {
    public final static int NUMBER_OF_LABELS = 4;
    public static String accuracyHeader = "accuracy,correct,total,sitting,walking,running,cycling\n";
    public static String dataPointHeader = "minutes,heart_rate,step_count,label\n";
    public static String centroidHeader = "heart_rate,min_heart_rate,max_heart_rate" +
            "step_count,min_step_count,max_step_count,label,centroid_size\n";
    public static String centroidHistoryHeader = "date," +
            "heart_rate_sitting,step_count_sitting,semi_major_axis_sitting,semi_minor_axis_sitting,centroid_size_sitting," +
            "heart_rate_walking,step_count_walking,semi_major_axis_walking,semi_minor_axis_walking,centroid_size_walking," +
            "heart_rate_running,step_count_running,semi_major_axis_running,semi_minor_axis_running,centroid_size_running," +
            "heart_rate_cycling,step_count_cycling,semi_major_axis_cycling,semi_minor_axis_cycling,centroid_size_cycling\n";

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
        COLLECT_DATA,
    }

    public static final byte NOT_SET = -1;

    //endregion
}
