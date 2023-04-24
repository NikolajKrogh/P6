package com.example.p6.classes;

public class Constants {
    public final static int NUMBER_OF_LABELS = 4;
    public static String accuracyHeader = "accuracy,correct,total,sitting,walking,running,cycling\n";
    public static String dataPointHeader = "minutes,heart_rate,step_count,label\n";
    public static String centroidHeader = "heart_rate,step_count," +
            "northern_most_point_x, northern_most_point_y," +
            "eastern_most_point_x, eastern_most_point_y," +
            "southern_most_point_x, southern_most_point_y," +
            "western_most_point_x, western_most_point_y," +
            "label,centroid_size\n";
    public static String centroidHistoryHeader = "date," +
            "heart_rate_sitting,step_count_sitting,label_sitting,centroid_size_sitting," +
            "heart_rate_walking,step_count_walking,label_walking,centroid_size_walking," +
            "heart_rate_running,step_count_running,label_running,centroid_size_running," +
            "heart_rate_cycling,step_count_cycling,label_cycling,centroid_size_cycling\n";

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

    public enum Direction {
        NORTH,
        EAST,
        SOUTH,
        WEST
    }

    //endregion
}
