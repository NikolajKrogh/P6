package com.example.p6;

import androidx.annotation.NonNull;

public class Row{
    String timestamp;
    String minutes;
    String heartRate;
    String acc_x;
    String acc_y;
    String acc_z;
    String step_count;
    String label;

    public Row(String timestamp, String minutes, String heart_rate, String acc_x, String acc_y, String acc_z, String step_count, String label)
    {
        this.timestamp = timestamp;
        this.minutes = minutes;
        this.heartRate = heart_rate;
        this.acc_x = acc_x;
        this.acc_y = acc_y;
        this.acc_z = acc_z;
        this.step_count = step_count;
        this.label = label;
    }
    @NonNull
    @Override
    public String toString(){
        return String.format("%s,%s,%s,%s,%s,%s,%s,%s\n", timestamp,minutes, heartRate,
                acc_x,acc_y,acc_z,step_count,label);
    }
}

