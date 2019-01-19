# geohash-util

The repository can be used to convert bounding box to geohash and vice versa.

The geohashes are 64 bit encoded with maximum zoom level of 30.

Example usage :
```
BoundingBox world = new BoundingBox(-90.0, -180.0, 90.0, 180.0); // this is the bounding box of the world , zoom level is 1
BoundingBox bBox = new BoundingBox(44.0, 46.0, 50.0, 50.0);
long[] geoHashes = Util.geoHashesForBoundingBox(world, bBox, 3); // returns geohashes at zoom level 3 for given bbox
``` 