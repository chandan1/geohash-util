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
        Assert.assertEquals("11100", Long.toBinaryString(geoHashes[0]));
    }

    @Test
    public void testGeoHashesForBoundingBoxZoomLevel3() {
        BoundingBox bBox = new BoundingBox(44.0, 46.0, 50.0, 50.0);
        long[] geoHashes = GeoHashUtil.geoHashesForBoundingBox(bBox, 3);
        Assert.assertEquals(2, geoHashes.length);
        Assert.assertEquals("1110011", Long.toBinaryString(geoHashes[0]));
        Assert.assertEquals("1110110", Long.toBinaryString(geoHashes[1]));
    }

    @Test
    public void testGeoHashesForBoundingBoxZoomLevel15() {

        BoundingBox bBox = new BoundingBox(12.924052, 77.669285, 12.928086, 77.673468);
        long[] geoHashes = GeoHashUtil.geoHashesForBoundingBox(bBox, 15);
        Assert.assertEquals(4, geoHashes.length);
        for (int i = 0; i < geoHashes.length; i++) {
            System.out.printf(" " + geoHashes[i]);
        }
        // geohash
        //tdr1x3
        Assert.assertEquals("1110010110010111000011110100011", Long.toBinaryString(geoHashes[0]));
        //tdr1x2
        Assert.assertEquals("1110010110010111000011110100010", Long.toBinaryString(geoHashes[1]));
        //tdr1x8
        Assert.assertEquals("1110010110010111000011110101000", Long.toBinaryString(geoHashes[2]));
        //tdr1x9
        Assert.assertEquals("1110010110010111000011110101001", Long.toBinaryString(geoHashes[3]));
    }

    @Test
    public void testBoundingBoxFromGeoHashZoomLevel15() {
        BoundingBox boundingBox = GeoHashUtil.getBoundingBoxFromGeoHash(1925941155);
        Assert.assertEquals(12.9254150390625, boundingBox.minLat, 0);
        Assert.assertEquals(77.662353515625, boundingBox.minLong, 0);
        Assert.assertEquals(12.930908203125, boundingBox.maxLat, 0);
        Assert.assertEquals(77.67333984375, boundingBox.maxLong, 0);
        boundingBox = GeoHashUtil.getBoundingBoxFromGeoHash(1925941154);
        Assert.assertEquals(12.919921875, boundingBox.minLat, 0);
        Assert.assertEquals(77.662353515625, boundingBox.minLong, 0);
        Assert.assertEquals(12.9254150390625, boundingBox.maxLat, 0);
        Assert.assertEquals(77.67333984375, boundingBox.maxLong, 0);
    }

    public static void main(String[] args) {
        System.out.println(Long.toBinaryString(852199331));//1925941155
        System.out.println(Long.parseLong("1110010110010111000011110100011", 2));
        System.out.println(Long.toBinaryString(852199330));//1925941154
        System.out.println(Long.parseLong("1110010110010111000011110100010", 2));
    }
}
