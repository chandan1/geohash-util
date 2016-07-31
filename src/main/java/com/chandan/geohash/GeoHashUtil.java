package com.chandan.geohash;

import java.text.MessageFormat;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Created by chandan on 5/8/16.
 */
public class GeoHashUtil {
    private final static int MAX_ZOOM_LEVEL = 30;
    private final static int BOTTOM_LEFT_QUAD = 0b00;
    private final static int BOTTOM_RIGHT_QUAD = 0b10;
    private final static int TOP_RIGHT_QUAD = 0b11;
    private final static int TOP_LEFT_QUAD = 0b01;

    private static class GeoHashBBox {
        public final long geoHash;
        public final BoundingBox bBox;

        public GeoHashBBox(long geoHash, BoundingBox bBox) {
            this.geoHash = geoHash;
            this.bBox = bBox;
        }
    }


    public static long[] geoHashesForBoundingBox(BoundingBox bBox, int zoomLevel) {
        checkZoomLevel(zoomLevel);
        checkBoundingBox(bBox);
        Queue<GeoHashBBox> geoHashBBoxQueue = new LinkedList<GeoHashBBox>();
        geoHashBBoxQueue.offer(new GeoHashBBox(0, new BoundingBox(-90.0, -180, 90.0, 180)));
        int currentZoomLevel = 0;
        while (currentZoomLevel < zoomLevel) {
            Queue<GeoHashBBox> tempQueue = new LinkedList<GeoHashBBox>();
            while (!geoHashBBoxQueue.isEmpty()) {
                GeoHashBBox[] geoHashBBoxes = getNextZoomLevelGeoHash(geoHashBBoxQueue.poll());
                if (boundingBoxesIntersect(geoHashBBoxes[0].bBox, bBox)) {
                    tempQueue.offer(geoHashBBoxes[0]);
                }
                if (boundingBoxesIntersect(geoHashBBoxes[1].bBox, bBox)) {
                    tempQueue.offer(geoHashBBoxes[1]);
                }
                if (boundingBoxesIntersect(geoHashBBoxes[2].bBox, bBox)) {
                    tempQueue.offer(geoHashBBoxes[2]);
                }
                if (boundingBoxesIntersect(geoHashBBoxes[3].bBox, bBox)) {
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

    public static long getNextBottomLeftGeoHash(long geoHash) {
        return geoHash << 2 | BOTTOM_LEFT_QUAD;
    }

    public static long getNextBottomRightGeoHash(long geoHash) {
        return geoHash << 2 | BOTTOM_RIGHT_QUAD;
    }

    public static long getNextTopLeftGeoHash(long geoHash) {
        return geoHash << 2 | TOP_LEFT_QUAD;
    }

    public static long getNextTopRightGeoHash(long geoHash) {
        return geoHash << 2 | TOP_RIGHT_QUAD;
    }

    public static BoundingBox getNextBottomLeftBoundingBox(BoundingBox boundingBox) {
        double midLat = (boundingBox.maxLat + boundingBox.minLat) / 2;
        double midLong = (boundingBox.maxLong + boundingBox.minLong) / 2;
        return new BoundingBox(boundingBox.minLat, boundingBox.minLong, midLat, midLong);
    }

    public static BoundingBox getNextBottomRightBoundingBox(BoundingBox boundingBox) {
        double midLat = (boundingBox.maxLat + boundingBox.minLat) / 2;
        double midLong = (boundingBox.maxLong + boundingBox.minLong) / 2;
        return new BoundingBox(boundingBox.minLat, midLong, midLat, boundingBox.maxLong);
    }

    public static BoundingBox getNextTopLeftBoundingBox(BoundingBox boundingBox) {
        double midLat = (boundingBox.maxLat + boundingBox.minLat) / 2;
        double midLong = (boundingBox.maxLong + boundingBox.minLong) / 2;
        return new BoundingBox(midLat, boundingBox.minLong,
                boundingBox.maxLat, midLong);
    }

    public static BoundingBox getNextTopRightBoundingBox(BoundingBox boundingBox) {
        double midLat = (boundingBox.maxLat + boundingBox.minLat) / 2;
        double midLong = (boundingBox.maxLong + boundingBox.minLong) / 2;
        return new BoundingBox(midLat, midLong, boundingBox.maxLat, boundingBox.maxLong);
    }

    public static long getParentGeoHash(long geoHash) {
        return geoHash >> 2;
    }


    /*private*/
    static boolean boundingBoxesIntersect(BoundingBox bBox1, BoundingBox bBox2) {
        if ((bBox1.minLat > bBox2.maxLat)
                || (bBox1.maxLat < bBox2.minLat)
                || (bBox1.minLong > bBox2.maxLong)
                || (bBox1.maxLong < bBox2.minLong)) {
            return false;
        }
        return true;
    }

    private static void checkBoundingBox(BoundingBox bBox) {
        if (bBox == null) {
            throw new IllegalArgumentException("bBox cannot be null");
        }
        if (bBox.minLat >= bBox.maxLat) {
            throw new IllegalArgumentException(MessageFormat.format("minLat : {0} should be less than maxLat : {1}",
                    new Object[]{bBox.minLat, bBox.maxLat}));
        }
        if (bBox.minLong >= bBox.maxLong) {
            throw new IllegalArgumentException(MessageFormat.format("minLong : {0} should be less than maxLong : {1}",
                    new Object[]{bBox.minLong, bBox.maxLong}));
        }
        if (!(bBox.minLat >= -90.0 && bBox.minLat < 90.0)) {
            throw new IllegalArgumentException(MessageFormat.format("minLat : {0} should be >= -90.0 and < 90.0",
                    new Object[]{bBox.minLat}));
        }
        if (!(bBox.maxLat > -90.0 && bBox.maxLat <= 90.0)) {
            throw new IllegalArgumentException(MessageFormat.format("maxLat : {0} should be > -90.0 and <= -90",
                    new Object[]{bBox.maxLat}));
        }
        if (!(bBox.minLong >= -180.0 && bBox.minLong < 180)) {
            throw new IllegalArgumentException(MessageFormat.format("minLong : {0} should be >= -180.0 and < 180.0",
                    new Object[]{bBox.minLong}));
        }
        if (!(bBox.maxLong > -180.0 && bBox.maxLong <= 180.0)) {
            throw new IllegalArgumentException(MessageFormat.format("maxLat : {0} should be > -180.0 and <= 180.0",
                    new Object[]{bBox.maxLong}));
        }
    }

    private static void checkZoomLevel(int zoomLevel) {
        if (zoomLevel < 0) {
            throw new IllegalArgumentException("zoomLevel should be >= 0");
        }
        if (zoomLevel > MAX_ZOOM_LEVEL) {
            throw new IllegalArgumentException(MessageFormat.format("zoomLevel should be <= {0}",
                    new Object[]{zoomLevel}));
        }
    }

    private static GeoHashBBox[] getNextZoomLevelGeoHash(GeoHashBBox geoHashBBox) {
        GeoHashBBox[] geoHashBBoxes = new GeoHashBBox[4];
        geoHashBBoxes[0] = new GeoHashBBox(getNextBottomLeftGeoHash(geoHashBBox.geoHash), getNextBottomLeftBoundingBox(geoHashBBox.bBox));
        geoHashBBoxes[1] = new GeoHashBBox(getNextTopRightGeoHash(geoHashBBox.geoHash), getNextTopRightBoundingBox(geoHashBBox.bBox));
        geoHashBBoxes[2] = new GeoHashBBox(getNextBottomRightGeoHash(geoHashBBox.geoHash), getNextBottomRightBoundingBox(geoHashBBox.bBox));
        geoHashBBoxes[3] = new GeoHashBBox(getNextTopLeftGeoHash(geoHashBBox.geoHash), getNextTopLeftBoundingBox(geoHashBBox.bBox));
        return geoHashBBoxes;
    }
}
