package com.example.p6.classes;

public class Constants {
    public final static int NUMBER_OF_LABELS = 4;
    public static String centroidHeader = "heart_rate,step_count,label,centroid_size\n";

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
    //endregion
}
