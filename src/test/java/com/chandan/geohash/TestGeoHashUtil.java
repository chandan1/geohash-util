package com.chandan.geohash;

import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;

/**
 * Created by chandan on 5/8/16.
 */
public class TestGeoHashUtil {

    @Test
    public void testBoundingBoxIntersection() {
        BoundingBox box1 = new BoundingBox(0.0 ,0.0 ,2.0 , 2.0);
        BoundingBox box2 = new BoundingBox(2.1 ,2.1 , 3.3, 3.3);
        Assert.assertFalse(GeoHashUtil.boundingBoxesIntersect(box1, box2));
        box2 = new BoundingBox(0.0, 2.000001, 2.0 , 3.0);
        Assert.assertFalse(GeoHashUtil.boundingBoxesIntersect(box1, box2));
        box2 = new BoundingBox(2.000001, 0.0, 3.0, 3.0);
        Assert.assertFalse(GeoHashUtil.boundingBoxesIntersect(box1, box2));
        box2 = new BoundingBox(-0.000001, 0.0, -2.0, 2.0);
        Assert.assertFalse(GeoHashUtil.boundingBoxesIntersect(box1, box2));
        box2 = new BoundingBox(0.0, -4.0, 2.0, -2.000001);
        Assert.assertFalse(GeoHashUtil.boundingBoxesIntersect(box1, box2));

        box2 = new BoundingBox(1.5, 1.5, 1.8, 1.8);
        Assert.assertTrue(GeoHashUtil.boundingBoxesIntersect(box1, box2));

        box2 = new BoundingBox(1.5, 1.5, 2.5, 2.5);
        Assert.assertTrue(GeoHashUtil.boundingBoxesIntersect(box1, box2));
    }

    @Test
    public void testGeoHashesForBoundingBoxZoomLevel2() {
        BoundingBox bBox = new BoundingBox(1.0, 1.0, 3.0, 3.0);
        long[] geoHashes = GeoHashUtil.geoHashesForBoundingBox(bBox, 2);
        Assert.assertEquals(1, geoHashes.length);
        Assert.assertEquals("1100", Long.toBinaryString(geoHashes[0]));
    }

    @Test
    public void testGeoHashesForBoundingBoxZoomLevel3() {
        BoundingBox bBox = new BoundingBox(44.0, 46.0, 50.0, 50.0);
        long[] geoHashes = GeoHashUtil.geoHashesForBoundingBox(bBox, 3);
        Assert.assertEquals(2, geoHashes.length);
        Assert.assertEquals("110011", Long.toBinaryString(geoHashes[0]));
        Assert.assertEquals("110110", Long.toBinaryString(geoHashes[1]));
    }

    @Test
    public void testGeoHashesForBoundingBoxZoomLevel15() {

        BoundingBox bBox = new BoundingBox(12.924052, 77.669285, 12.928086, 77.673468);
        long[] geoHashes = GeoHashUtil.geoHashesForBoundingBox(bBox, 15);
        Assert.assertEquals(4, geoHashes.length);
        Assert.assertEquals("110010110010111000011110100011", Long.toBinaryString(geoHashes[0]));
        Assert.assertEquals("110010110010111000011110100010", Long.toBinaryString(geoHashes[1]));
        Assert.assertEquals("110010110010111000011110101000", Long.toBinaryString(geoHashes[2]));
        Assert.assertEquals("110010110010111000011110101001", Long.toBinaryString(geoHashes[3]));
    }
}
