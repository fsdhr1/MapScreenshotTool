package com.grandtech.tools;

import com.mapbox.geojson.BoundingBox;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;

import java.util.*;

public class TileUtilCGCS2000 {

    /**
     * 瓦片的Envelope 像素坐标的
     *
     * @param tiles

     * @return
     */
    public static Envelope xyz2Envelope2Pixel(long[] tile) {
        double minX = MercatorProjectionCGCS2000.tileXToPixelX(tile[1]);
        double minY = MercatorProjectionCGCS2000.tileYToPixelY(tile[2]);
        double maxX = MercatorProjectionCGCS2000.tileXToPixelX(tile[1]+1);
        double maxY = MercatorProjectionCGCS2000.tileYToPixelY(tile[2]+1);
        return new Envelope(minX, maxX, minY, maxY);
    }

    /**
     * 瓦片的Envelope 像素坐标的
     *
     * @param tiles

     * @return
     */
    public static Envelope xyzs2Envelope2Pixel(List<long[]> tiles) {
        double maxX=0;
        double minX=0;
        double maxY=0;
        double minY=0;
        for(long[] tile : tiles){
            double px = MercatorProjectionCGCS2000.tileXToPixelX(tile[1]);
            double py = MercatorProjectionCGCS2000.tileYToPixelY(tile[2]);
            double px1 = MercatorProjectionCGCS2000.tileXToPixelX(tile[1]+1);
            double py2 = MercatorProjectionCGCS2000.tileYToPixelY(tile[2]+1);
            maxX = px1>maxX?px1:maxX;
            minX = px<minX||minX==0?px:minX;
            maxY = py2>maxY?py2:maxY;
            minY = py<minY||minY==0?py:minY;
        }


        return new Envelope(minX, maxX, minY, maxY);
    }
    /**
     * @param west
     * @param south
     * @param east
     * @param north
     * @param z
     * @return
     */
    public static List<long[]> getBoundTile(double west, double south, double east, double north, long z) {
        long minX, maxX, minY, maxY;
        byte b = (byte) z;
        minX = MercatorProjectionCGCS2000.longitudeToTileX(west, b);
        maxX = MercatorProjectionCGCS2000.longitudeToTileX(east, b);
        minY = MercatorProjectionCGCS2000.latitudeToTileY(north, b);
        maxY = MercatorProjectionCGCS2000.latitudeToTileY(south, b);
        List<long[]> tiles = new ArrayList();
        for (long x = minX; x <= maxX; x++) {
            for (long y = minY; y <= maxY; y++) {
                tiles.add(new long[]{z, x, y});
            }
        }
        return tiles;
    }

    /**
     * @param west
     * @param south
     * @param east
     * @param north
     * @param z
     * @return
     */
    public static List<long[]> getBoundTileV2(double west, double south, double east, double north, long z) {
        long minX, maxX, minY, maxY;
        byte b = (byte) z;
        double pminx = MercatorProjectionCGCS2000.longitudeToPixelX(west,b)-100;
        double pmaxX = MercatorProjectionCGCS2000.longitudeToPixelX(east,b)+100;
        double pminY = MercatorProjectionCGCS2000.latitudeToPixelY(north,b)-100;
        double pmaxY = MercatorProjectionCGCS2000.latitudeToPixelY(south,b)+100;
        if(pmaxX-pminx>pmaxY-pminY){
            double cz = (pmaxX-pminx) - (pmaxY-pminY);
            pmaxY = pmaxY+cz/2;
            pminY = pminY-cz/2;
        }else {
            double cz =  (pmaxY-pminY)-(pmaxX-pminx);
            pmaxX = pmaxX+cz/2;
            pminx = pminx-cz/2;
        }
        minX = MercatorProjectionCGCS2000.pixelXToTileX(pminx,b);
        maxX = MercatorProjectionCGCS2000.pixelXToTileX(pmaxX,b);
        minY = MercatorProjectionCGCS2000.pixelYToTileY(pminY,b);
        maxY = MercatorProjectionCGCS2000.pixelYToTileY(pmaxY, b);
        List<long[]> tiles = new ArrayList<long[]>();
        for (long x = minX; x <= maxX; x++) {
            for (long y = minY; y <= maxY; y++) {
                tiles.add(new long[]{z, x, y});
            }
        }
        return tiles;
    }

    public static List<long[]> getBoundTileV3(double pminx, double pmaxX, double pminY, double pmaxY, long z) {
        byte b = (byte) z;
        long minX = MercatorProjectionCGCS2000.pixelXToTileX(pminx,b);
        long maxX = MercatorProjectionCGCS2000.pixelXToTileX(pmaxX,b);
        long minY = MercatorProjectionCGCS2000.pixelYToTileY(pminY,b);
        long maxY = MercatorProjectionCGCS2000.pixelYToTileY(pmaxY, b);
        List<long[]> tiles = new ArrayList<long[]>();
        for (long x = minX; x <= maxX; x++) {
            for (long y = minY; y <= maxY; y++) {
                tiles.add(new long[]{z, x, y});
            }
        }
        return tiles;
    }

    /**
     * 获取 z级别下boxes范围内的  瓦片
     *
     * @param boxes
     * @param z
     * @return
     */
    public static List<long[]> getBoundTile(List<BoundingBox> boxes, long z) {
        BoundingBox box;
        List<long[]> temp;
        Map<String, long[]> tiles = new HashMap<>();
        long[] tile;
        for (int i = 0, lenI = boxes.size(); i < lenI; i++) {
            box = boxes.get(i);
            temp = getBoundTile(box.west(), box.south(), box.east(), box.north(), z);
            for (int j = 0, lenJ = temp.size(); j < lenJ; j++) {
                tile = temp.get(j);
                tiles.put(tile[0] + "_" + tile[1] + "_" + tile[2], tile);
            }
        }
        List<long[]> res = new ArrayList<>();
        Iterator<long[]> iterator = tiles.values().iterator();
        while (iterator.hasNext()) {
            res.add(iterator.next());
        }
        return res;
    }




    /**
     * 计算 单块瓦片下的所有瓦片数量和
     *
     * @param sZ
     * @param eZ
     * @return
     */
    public static long computeTileCount(long sZ, long eZ) {
        long sum = 0;
        double count;
        for (long i = 0; i <= eZ - sZ; i++) {
            count = Math.pow(4, i);
            sum += count;
        }
        return sum;
    }


}
