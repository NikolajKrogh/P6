package com.example.p6.classes;

public class CentroidEdgeCases {
    public DataPointAggregated northernMostPoint;
    public DataPointAggregated easternMostPoint;
    public DataPointAggregated southernMostPoint;
    public DataPointAggregated westernMostPoint;

    public CentroidEdgeCases(DataPointAggregated northernMostPoint,
                             DataPointAggregated easternMostPoint,
                             DataPointAggregated southernMostPoint,
                             DataPointAggregated westernMostPoint){
        this.northernMostPoint = northernMostPoint;
        this.easternMostPoint = easternMostPoint;
        this.southernMostPoint = southernMostPoint;
        this.westernMostPoint = westernMostPoint;
    }
}
