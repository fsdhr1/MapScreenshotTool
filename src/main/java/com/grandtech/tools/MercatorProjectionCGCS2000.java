package com.grandtech.tools;

/*
 * Copyright 2010, 2011, 2012 mapsforge.org
 *
 * This program is free software: you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
//package org.mapsforge.map.writer.model;

/**
 * A static class that implements spherical mercator projection.
 */
public final class MercatorProjectionCGCS2000 {

    private MercatorProjectionCGCS2000() {
    }


    /** TODO OK
     * Convert a latitude coordinate (in degrees) to a pixel Y coordinate at a
     * certain zoom level.
     *
     * @param latitude the latitude coordinate that should be converted.
     * @param zoom     the zoom level at which the coordinate should be converted.
     * @return the pixel Y coordinate of the latitude value.
     */
    public static double latitudeToPixelY(double latitude, byte zoom) {
        double _resFact = 360.0 / TileInfo.TILE_SIZE;
        double res = _resFact / Math.pow(2,(double) zoom);
        return (90 - latitude) / res;
    }

    /**TODO OK
     * Convert a latitude coordinate (in degrees) to a tile Y number at a
     * certain zoom level.
     *
     * @param latitude the latitude coordinate that should be converted.
     * @param zoom     the zoom level at which the coordinate should be converted.
     * @return the tile Y number of the latitude value.
     */
    public static long latitudeToTileY(double latitude, byte zoom) {
        return pixelYToTileY(latitudeToPixelY(latitude, zoom), zoom);
    }

    /**TODO OK
     * Convert a longitude coordinate (in degrees) to a pixel X coordinate at a
     * certain zoom level.
     *
     * @param longitude the longitude coordinate that should be converted.
     * @param zoom      the zoom level at which the coordinate should be converted.
     * @return the pixel X coordinate of the longitude value.
     */
    public static double longitudeToPixelX(double longitude, byte zoom) {
        double _resFact = 360.0 / TileInfo.TILE_SIZE;
        double res = _resFact / Math.pow(2,(double) zoom);
        return (180 + longitude) / res;
    }

    /**TODO OK
     * Convert a longitude coordinate (in degrees) to the tile X number at a
     * certain zoom level.
     *
     * @param longitude the longitude coordinate that should be converted.
     * @param zoom      the zoom level at which the coordinate should be converted.
     * @return the tile X number of the longitude value.
     */
    public static long longitudeToTileX(double longitude, byte zoom) {
        return pixelXToTileX(longitudeToPixelX(longitude, zoom), zoom);
    }

    /**TODO OK
     * Convert a pixel X coordinate at a certain zoom level to a longitude
     * coordinate.
     *
     * @param pixelX the pixel X coordinate that should be converted.
     * @param zoom   the zoom level at which the coordinate should be converted.
     * @return the longitude value of the pixel X coordinate.
     */
    public static double pixelXToLongitude(double pixelX, byte zoom) {
        double _resFact = 360.0 / TileInfo.TILE_SIZE;
        double res = _resFact / Math.pow(2,(double) zoom);
        return  pixelX*res -180.0;
    }

    /**TODO OK
     * Convert a pixel X coordinate to the tile X number.
     *
     * @param pixelX the pixel X coordinate that should be converted.
     * @param zoom   the zoom level at which the coordinate should be converted.
     * @return the tile X number.
     */
    public static long pixelXToTileX(double pixelX, byte zoom) {
        return (long) Math.ceil(pixelX / (long) TileInfo.TILE_SIZE) - 1;
    }


    /**TODO OK
     * Convert a pixel Y coordinate at a certain zoom level to a latitude
     * coordinate.
     *
     * @param pixelY the pixel Y coordinate that should be converted.
     * @param zoom   the zoom level at which the coordinate should be converted.
     * @return the latitude value of the pixel Y coordinate.
     */
    public static double pixelYToLatitude(double pixelY, byte zoom) {
        double _resFact = 360.0 / TileInfo.TILE_SIZE;
        double res = _resFact / Math.pow(2,(double) zoom);
        return 90.0 -  res* pixelY;
    }

    /**TODO OK
     * Converts a pixel Y coordinate to the tile Y number.
     *
     * @param pixelY the pixel Y coordinate that should be converted.
     * @param zoom   the zoom level at which the coordinate should be converted.
     * @return the tile Y number.
     */
    public static long pixelYToTileY(double pixelY, byte zoom) {
        return (long) Math.ceil(pixelY / ((long) TileInfo.TILE_SIZE)) - 1;
    }

    /**TODO OK
     * Convert a tile X number at a certain zoom level to a longitude
     * coordinate.
     *
     * @param tileX the tile X number that should be converted.
     * @param zoom  the zoom level at which the number should be converted.
     * @return the longitude value of the tile X number.
     */
    public static double tileXToLongitude(long tileX, byte zoom) {
        return pixelXToLongitude((tileX+0)*((long) TileInfo.TILE_SIZE), zoom);
    }

    /**
     * Convert a tile Y number at a certain zoom level to a latitude coordinate.
     *
     * @param tileY the tile Y number that should be converted.
     * @param zoom  the zoom level at which the number should be converted.
     * @return the latitude value of the tile Y number.
     */
    public static double tileYToLatitude(long tileY, byte zoom) {
        return pixelYToLatitude((tileY+0) * TileInfo.TILE_SIZE, zoom);
    }


    /**
     * Convert a tile X number to a pixel X coordinate.
     *
     * @param tileX the tile X number that should be converted
     * @return the pixel X coordinate
     */
    public static double tileXToPixelX(long tileX) {
        return (tileX+0) * TileInfo.TILE_SIZE;
    }

    /**
     * Convert a tile Y number to a pixel Y coordinate.
     *
     * @param tileY the tile Y number that should be converted
     * @return the pixel Y coordinate
     */
    public static double tileYToPixelY(long tileY) {
        return (tileY+0) * TileInfo.TILE_SIZE;
    }

    public static void main(String[] args) {

        long startX = MercatorProjectionCGCS2000.latitudeToTileY(112.3, (byte) 14);
        long startY = MercatorProjectionCGCS2000.latitudeToTileY(58.3, (byte) 14);

        long endX = MercatorProjectionCGCS2000.longitudeToTileX(115.3, (byte) 14);
        long endY = MercatorProjectionCGCS2000.latitudeToTileY(53, (byte) 14);

        System.out.println(startX + "|" + startY);
        System.out.println(endX + "|" + endY);

        for (long x = startX; x <= endX; x++) {
            for (long y = startY; y <= endY; y++) {
                System.out.println(x + "|" + y);

            }
        }
    }
}