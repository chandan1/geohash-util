package com.chandan.geohash;

import java.text.MessageFormat;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Created by chandan on 5/8/16.
 */
public class GeoHashUtil {
    private final static int MAX_ZOOM_LEVEL = 20;
    private final static int BOTTOM_LEFT_QUAD = 0b00;
    private final static int BOTTOM_RIGHT_QUAD = 0b10;
    private final static int TOP_RIGHT_QUAD = 0b11;
    private final static int TOP_LEFT_QUAD = 0b01;

    private static class GeoHashBBox {
        private final long geoHash;
        private final BoundingBox bBox;

        public GeoHashBBox(long geoHash, BoundingBox bBox) {
            this.geoHash = geoHash;
            this.bBox = bBox;
        }
    }

    public static long[] geoHashesForBoundingBox(BoundingBox bBox, int zoomLevel) {
        if (zoomLevel <= 1) {
            throw new IllegalArgumentException("zoomLevel should be greater than 1");
        }
        if (bBox == null) {
            throw new IllegalArgumentException("bBox cannot be null");
        }
        if (bBox.getMinLat() >= bBox.getMaxLat()) {
            throw new IllegalArgumentException(MessageFormat.format("minLat : {0} should be less than maxLat : {1}",
                    new Object[]{bBox.getMinLat(), bBox.getMaxLat()}));
        }
        if (bBox.getMinLong() >= bBox.getMaxLong()) {
            throw new IllegalArgumentException(MessageFormat.format("minLong : {0} should be less than maxLong : {1}",
                    new Object[]{bBox.getMinLong(), bBox.getMaxLong()}));
        }
        if (bBox.getMinLat() >= 180 || bBox.getMinLat() < -180) {
            throw new IllegalArgumentException();
        }
        Queue<GeoHashBBox> geoHashBBoxQueue = new LinkedList<>();
        geoHashBBoxQueue.offer(new GeoHashBBox(0, new BoundingBox(-90.0, -180, 90.0, 180)));
        int currentZoomLevel = 1;
        while (currentZoomLevel <= zoomLevel || !geoHashBBoxQueue.isEmpty()) {
            GeoHashBBox[] geoHashBBoxes = getNextZoomLevelGeoHash(geoHashBBoxQueue.poll());
            if (boundingBoxesIntersect(geoHashBBoxes[0].bBox, bBox)) {
                geoHashBBoxQueue.offer(geoHashBBoxes[0]);
            }
            if (boundingBoxesIntersect(geoHashBBoxes[1].bBox, bBox)) {
                geoHashBBoxQueue.offer(geoHashBBoxes[1]);
            }
            if (boundingBoxesIntersect(geoHashBBoxes[2].bBox, bBox)) {
                geoHashBBoxQueue.offer(geoHashBBoxes[2]);
            }
            if (boundingBoxesIntersect(geoHashBBoxes[3].bBox, bBox)) {
                geoHashBBoxQueue.offer(geoHashBBoxes[3]);
            }
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

    /*private*/ static boolean boundingBoxesIntersect(BoundingBox bBox1, BoundingBox bBox2) {
        if ((bBox1.getMinLat() > bBox2.getMaxLat())
                || (bBox1.getMaxLat() < bBox2.getMinLat())
                || (bBox1.getMinLong() > bBox2.getMaxLong())
                || (bBox1.getMaxLong() < bBox2.getMinLong())) {
            return false;
        }
        return true;
    }

    private static GeoHashBBox[] getNextZoomLevelGeoHash(GeoHashBBox geoHashBBox) {
        GeoHashBBox[] geoHashBBoxes = new GeoHashBBox[4];
        double midLat = (geoHashBBox.bBox.getMaxLat() + geoHashBBox.bBox.getMinLat())/2;
        double midLong = (geoHashBBox.bBox.getMaxLong() + geoHashBBox.bBox.getMinLong())/2;
        geoHashBBoxes[0] = new GeoHashBBox(geoHashBBox.geoHash << 2 | BOTTOM_LEFT_QUAD, new BoundingBox(geoHashBBox.bBox.getMinLat(),
                geoHashBBox.bBox.getMinLong(), midLat, midLong));
        geoHashBBoxes[1] = new GeoHashBBox(geoHashBBox.geoHash << 2 | TOP_RIGHT_QUAD, new BoundingBox(midLat, midLong, geoHashBBox.bBox.getMaxLat(),
                geoHashBBox.bBox.getMaxLong()));
        geoHashBBoxes[2] = new GeoHashBBox(geoHashBBox.geoHash << 2 | BOTTOM_RIGHT_QUAD, new BoundingBox(geoHashBBox.bBox.getMinLat(), midLong, midLat ,
                geoHashBBox.bBox.getMaxLong()));
        geoHashBBoxes[3] = new GeoHashBBox(geoHashBBox.geoHash << 2 | TOP_LEFT_QUAD, new BoundingBox(midLat, geoHashBBox.bBox.getMinLong(),
                geoHashBBox.bBox.getMaxLat(), midLong));
        return geoHashBBoxes;
    }
}
