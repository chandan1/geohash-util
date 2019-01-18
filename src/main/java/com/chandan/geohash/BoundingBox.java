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


    public long[] geoHashesForBoundingBox(BoundingBox bBox, int zoomLevel) {
        Util.checkZoomLevel(zoomLevel);
        Queue<Util.GeoHashBBox> geoHashBBoxQueue = new LinkedList<>();
        geoHashBBoxQueue.offer(new Util.GeoHashBBox(1, new BoundingBox(-90.0, -180, 90.0, 180)));
        int currentZoomLevel = 0;
        while (currentZoomLevel < zoomLevel) {
            Queue<Util.GeoHashBBox> tempQueue = new LinkedList<>();
            while (!geoHashBBoxQueue.isEmpty()) {
                Util.GeoHashBBox[] geoHashBBoxes = Util.getNextZoomLevelGeoHash(geoHashBBoxQueue.poll());
                if (Util.boundingBoxesIntersect(geoHashBBoxes[0].bBox, bBox)) {
                    tempQueue.offer(geoHashBBoxes[0]);
                }
                if (Util.boundingBoxesIntersect(geoHashBBoxes[1].bBox, bBox)) {
                    tempQueue.offer(geoHashBBoxes[1]);
                }
                if (Util.boundingBoxesIntersect(geoHashBBoxes[2].bBox, bBox)) {
                    tempQueue.offer(geoHashBBoxes[2]);
                }
                if (Util.boundingBoxesIntersect(geoHashBBoxes[3].bBox, bBox)) {
                    tempQueue.offer(geoHashBBoxes[3]);
                }
            }
            geoHashBBoxQueue.addAll(tempQueue);
            currentZoomLevel++;
        }
        long[] geoHashes = new long[geoHashBBoxQueue.size()];
        int index = 0;
        while (!geoHashBBoxQueue.isEmpty()) {
            geoHashes[index] = geoHashBBoxQueue.poll().geoHash;
            index++;
        }
        return geoHashes;
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
