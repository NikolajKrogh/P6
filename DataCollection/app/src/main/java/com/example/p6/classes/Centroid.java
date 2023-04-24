package com.example.p6.classes;

import static com.example.p6.classes.Constants.*;
import static com.example.p6.classes.Constants.Direction.*;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;

public class Centroid {
    double heartRate;
    double stepCount;
    byte label;
    int size;
    double semiMajorAxis;
    double semiMinorAxis;
    CentroidEdgeCases edgeCases;

    //for before preprocessing
    public Centroid(double heartRate, double stepCount, CentroidEdgeCases edgeCases,
                    byte label, int size) {
        this.heartRate = heartRate;
        this.stepCount = stepCount;
        this.edgeCases = edgeCases;
        this.semiMajorAxis = EllipseHandler.getSemiMajorAxis(this);
        this.semiMinorAxis = EllipseHandler.getSemiMinorAxis(this);
        this.label = label;
        this.size = size;
    }

    public Centroid(double heartRate, double stepCount, CentroidEdgeCases edgeCases,
                    double semiMajorAxis, double semiMinorAxis, byte label, int size) {
        this.heartRate = heartRate;
        this.stepCount = stepCount;
        this.edgeCases = edgeCases;
        this.semiMajorAxis = semiMajorAxis;
        this.semiMinorAxis = semiMinorAxis;
        this.label = label;
        this.size = size;
    }

    public Centroid(String heartRate, String stepCount,
                    String northerMostPointX, String northernMostPointY,
                    String easternMostPointX, String easternMostPointY,
                    String southernMostPointX,String southernMostPointY,
                    String westernMostPointX, String westernMostPointY,
                    String semiMajorAxis, String semiMinorAxis,
                    String label, String size) {
        this(
                Double.parseDouble(heartRate),
                Double.parseDouble(stepCount),
                new CentroidEdgeCases(
                        new DataPointBasic(
                                Double.parseDouble(northerMostPointX),
                                Double.parseDouble(northernMostPointY)
                        ),
                        new DataPointBasic(
                                Double.parseDouble(easternMostPointX),
                                Double.parseDouble(easternMostPointY)
                        ),
                        new DataPointBasic(
                                Double.parseDouble(southernMostPointX),
                                Double.parseDouble(southernMostPointY)
                        ),
                        new DataPointBasic(
                                Double.parseDouble(westernMostPointX),
                                Double.parseDouble(westernMostPointY)
                        )
                ),
                Double.parseDouble(semiMajorAxis),
                Double.parseDouble(semiMinorAxis),
                Byte.parseByte(label),
                Integer.parseInt(size));
    }

    @NonNull
    @Override
    public String toString(){
        return String.format(Locale.US, "%f,%f,%f,%f,%f,%f,%f,%f,%f,%f,%f,%f,%d,%d",
                heartRate,
                stepCount,
                edgeCases.northernMostPoint.heartRate,
                edgeCases.northernMostPoint.stepCount,
                edgeCases.easternMostPoint.heartRate,
                edgeCases.easternMostPoint.stepCount,
                edgeCases.southernMostPoint.heartRate,
                edgeCases.southernMostPoint.stepCount,
                edgeCases.westernMostPoint.heartRate,
                edgeCases.westernMostPoint.stepCount,
                semiMajorAxis,
                semiMinorAxis,
                label,
                size
        );
    }

    public String toUIString(){
        return String.format(Locale.US, "%.2f, %.2f, %.2f, %.2f",
                heartRate, stepCount, semiMajorAxis, semiMinorAxis);
    }
}
