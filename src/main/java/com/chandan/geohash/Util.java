package com.chandan.geohash;

import java.text.MessageFormat;
import java.util.*;

/**
 * Created by chandan on 5/8/16.
 */
public class Util {
    private final static int MAX_ZOOM_LEVEL = 30;
    private final static int BOTTOM_LEFT_QUAD = 0b00;
    private final static int BOTTOM_RIGHT_QUAD = 0b10;
    private final static int TOP_RIGHT_QUAD = 0b11;
    private final static int TOP_LEFT_QUAD = 0b01;

    static class GeoHashBBox {
        public final long geoHash;
        public final BoundingBox bBox;

        public GeoHashBBox(long geoHash, BoundingBox bBox) {
            this.geoHash = geoHash;
            this.bBox = bBox;
        }
    }


    public static long[] geoHashesForBoundingBox(BoundingBox world, BoundingBox bBox, int zoomLevel) {
        checkZoomLevel(zoomLevel);
        checkBoundingBox(world);
        checkBoundingBox(bBox);
        Queue<GeoHashBBox> geoHashBBoxQueue = new LinkedList<GeoHashBBox>();
        geoHashBBoxQueue.offer(new GeoHashBBox(1, new BoundingBox(world.minLat, world.minLong, world.maxLat, world.maxLong)));
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

    public static long[] geoHashesForGeohash(long geoHash, int zoomLevel) {
        checkZoomLevel(zoomLevel);
        int currentZoomLevel = Long.toBinaryString(geoHash).length()/2;
        if (currentZoomLevel > zoomLevel) {
            throw new IllegalArgumentException("current zoom level should be less than equal to zoomlevel");
        }
        if (currentZoomLevel == zoomLevel) {
            return new long[]{geoHash};
        }
        int diffZoomLevel = zoomLevel - currentZoomLevel;
        long[] geoHashes = new long[1 << (diffZoomLevel << 1)];
        geoHashes[0] = geoHash;
        for (int i = 1; i < geoHashes.length; i*=4) {
            for (int j = i - 1; j >= 0; j-=1) {
                long bottomLeft = getNextBottomLeftGeoHash(geoHashes[j]);
                long bottomRight = getNextBottomRightGeoHash(geoHashes[j]);
                long topRight = getNextTopLeftGeoHash(geoHashes[j]);
                long topLeft = getNextTopRightGeoHash(geoHashes[j]);
                int k = (j + 1)*4 - 1;
                geoHashes[k] = bottomLeft;
                geoHashes[k-1] = bottomRight;
                geoHashes[k-2] = topLeft;
                geoHashes[k-3] = topRight;
            }
        }
        return geoHashes;
    }

    public static int getZoomLevelFromGeohash(long geoHash) {
        return Long.toBinaryString(geoHash).length()/2;
    }





    public static BoundingBox getBoundingBoxFromGeoHash(long geoHash) {
        double minLat = -90.0;
        double minLng = -180.0;
        double maxLat = 90.0;
        double maxLng = 180.0;
        int zoomLevel = Long.toBinaryString(geoHash).length()/2;
        long highestBit = Long.highestOneBit(geoHash);
        for (int i = 1; i <= zoomLevel; i++) {
            double midLat = (minLat + maxLat)/2;
            double midLng = (minLng + maxLng)/2;
            highestBit = highestBit >> 1;
            if ((geoHash & highestBit) == highestBit) {
                // right
                minLng = midLng;
            } else {
                // left
                maxLng = midLng;
            }
            highestBit = highestBit >> 1;
            if ((geoHash & highestBit) == highestBit) {
                //top
                minLat = midLat;
            } else {
                //bottom
                maxLat = midLat;
            }
        }
        return new BoundingBox(minLat, minLng, maxLat, maxLng);
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
        if (bBox.minLat > bBox.maxLat) {
            throw new IllegalArgumentException(MessageFormat.format("minLat : {0} should be less than maxLat : {1}",
                    new Object[]{bBox.minLat, bBox.maxLat}));
        }
        if (bBox.minLong > bBox.maxLong) {
            throw new IllegalArgumentException(MessageFormat.format("minLong : {0} should be less than maxLong : {1}",
                    new Object[]{bBox.minLong, bBox.maxLong}));
        }
    }


    static void checkZoomLevel(int zoomLevel) {
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
