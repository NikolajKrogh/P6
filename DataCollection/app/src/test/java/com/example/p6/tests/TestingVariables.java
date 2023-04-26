package com.example.p6.tests;

import com.example.p6.classes.Centroid;
import com.example.p6.classes.DataPointAggregated;
import com.example.p6.handlers.NearestCentroidHandler;

public class TestingVariables {

    public static final double DELTA = 0.01;

    public static Centroid[] centroids = NearestCentroidHandler.generalModelCentroids;
    public static DataPointAggregated[] sittingDataPoints = {
            new DataPointAggregated(60, 0, 0, 0),
            new DataPointAggregated(90, 0, 0, 0),
            new DataPointAggregated(60, 0, 0, 10),
            new DataPointAggregated(90, 0, 0, 10)};

    public static DataPointAggregated[] walkingDataPoints = {
            new DataPointAggregated(90, 0, 0, 80),
            new DataPointAggregated(110, 0, 0, 80),
            new DataPointAggregated(90, 0, 0, 200),
            new DataPointAggregated(110, 0, 0, 200)};

    public static DataPointAggregated[] runningDataPoints = {
            new DataPointAggregated(150, 0, 0, 140),
            new DataPointAggregated(170, 0, 0, 140),
            new DataPointAggregated(150, 0, 0, 160),
            new DataPointAggregated(170, 0, 0, 160)};

    public static DataPointAggregated[] cyclingDataPoints = {
            new DataPointAggregated(110, 0, 0, 0),
            new DataPointAggregated(160, 0, 0, 0),
            new DataPointAggregated(110, 0, 0, 10),
            new DataPointAggregated(160, 0, 0, 10)};

    public static DataPointAggregated[] unlabeledDataPoints = {
            new DataPointAggregated(0, 0, 0, 0),
            new DataPointAggregated(300, 0, 0, 0),
            new DataPointAggregated(0, 0, 0, 300),
            new DataPointAggregated(300, 0, 0, 300)};
}
