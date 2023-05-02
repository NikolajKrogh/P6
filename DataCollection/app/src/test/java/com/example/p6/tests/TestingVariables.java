package com.example.p6.tests;

import com.example.p6.classes.Centroid;
import com.example.p6.classes.DataPointAggregated;
import com.example.p6.handlers.NearestCentroidHandler;

public class TestingVariables {

    public static Centroid[] centroids = {
            new Centroid(70.89044447734003,54.3448275862069,85.0,0.0,0.0,0.0,(byte) 0,243),
            new Centroid(111.96882037612905,69.22413793103448,156.25862068965517,111.19583333333334,79.0,139.0,(byte) 1,250),
            new Centroid(163.32964324429457,122.70731707317073,178.80357142857142,157.8181818181818,124.0,174.0,(byte) 2,249),
            new Centroid(129.62290932844044,87.33333333333333,162.82456140350877,0.0,0.0,0.0,(byte) 3,276)
    };
    public static DataPointAggregated[] sittingDataPoints = {
            new DataPointAggregated(60, 0, 0, 0),
            new DataPointAggregated(80, 0, 0, 0),
            new DataPointAggregated(60, 0, 0, 5),
            new DataPointAggregated(80, 0, 0, 5)};

    public static DataPointAggregated[] walkingDataPoints = {
            new DataPointAggregated(90, 0, 0, 90),
            new DataPointAggregated(140, 0, 0, 90),
            new DataPointAggregated(90, 0, 0, 130),
            new DataPointAggregated(140, 0, 0, 130)};

    public static DataPointAggregated[] runningDataPoints = {
            new DataPointAggregated(140, 0, 0, 140),
            new DataPointAggregated(170, 0, 0, 140),
            new DataPointAggregated(140, 0, 0, 160),
            new DataPointAggregated(170, 0, 0, 160)};

    public static DataPointAggregated[] cyclingDataPoints = {
            new DataPointAggregated(110, 0, 0, 0),
            new DataPointAggregated(150, 0, 0, 0),
            new DataPointAggregated(110, 0, 0, 5),
            new DataPointAggregated(150, 0, 0, 5)};

    public static DataPointAggregated[] unlabeledDataPoints = {
            new DataPointAggregated(0, 0, 0, 0),
            new DataPointAggregated(300, 0, 0, 0),
            new DataPointAggregated(0, 0, 0, 300),
            new DataPointAggregated(300, 0, 0, 300)};
}
