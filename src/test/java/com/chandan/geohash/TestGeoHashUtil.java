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
    public void geoHashesForBoundingBoxLevel1() {
        BoundingBox bBox = new BoundingBox(1.0, 1.0, 3.0, 3.0);
        long[] geoHashes = GeoHashUtil.geoHashesForBoundingBox(bBox, 2);
        Arrays.stream(geoHashes).forEach((geoHash) -> System.out.print(Long.toBinaryString(geoHash) + " "));
        bBox = new BoundingBox(44.0, 46.0, 50.0, 50.0);
        geoHashes = GeoHashUtil.geoHashesForBoundingBox(bBox, 2);
        Arrays.stream(geoHashes).forEach((geoHash) -> System.out.print(Long.toBinaryString(geoHash) + " "));
    }
}
