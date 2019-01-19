package com.chandan.geohash;

import java.util.LinkedList;
import java.util.Queue;

/**
 * Created by chandan on 5/8/16.
 */
public class BoundingBox {

    public final double minLat;
    public final double minLong;
    public final double maxLat;
    public final double maxLong;

    public BoundingBox(double minLat, double minLong, double maxLat, double maxLong) {
        this.minLat = minLat;
        this.minLong = minLong;
        this.maxLat = maxLat;
        this.maxLong = maxLong;
    }

    @Override
	public String toString() {
		return "BoundingBox{" +
				"minLat=" + minLat +
				", minLong=" + minLong +
				", maxLat=" + maxLat +
				", maxLong=" + maxLong +
				'}';
	}
}
