package com.chandan.geohash;

/**
 * Created by chandan on 5/8/16.
 */
public class BoundingBox {
    private final double minLat;
    private final double minLong;
    private final double maxLat;
    private final double maxLong;

    public BoundingBox(double minLat, double minLong, double maxLat, double maxLong) {
        this.minLat = minLat;
        this.minLong = minLong;
        this.maxLat = maxLat;
        this.maxLong = maxLong;
    }

    public double getMinLat() {
        return minLat;
    }

    public double getMinLong() {
        return minLong;
    }

    public double getMaxLat() {
        return maxLat;
    }

    public double getMaxLong() {
        return maxLong;
    }
}
