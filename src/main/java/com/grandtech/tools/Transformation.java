package com.grandtech.tools;

import com.google.gson.JsonObject;
import com.mapbox.geojson.*;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.io.WKBReader;
import com.vividsolutions.jts.io.WKTReader;
import com.vividsolutions.jts.io.WKTWriter;
import org.geotools.geojson.geom.GeometryJSON;

import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by zy on 2018/4/12.
 */

public final class Transformation {



    /**
     * GeoJson转JTS Geometry
     *
     * @param geoJSON
     * @return
     */
    public static com.vividsolutions.jts.geom.Geometry geoJson2JstGeometry(GeoJson geoJSON) {
        if (geoJSON == null) return null;
        String _geoJSON = geoJSON.toJson();
        return geoJsonStr2JstGeometry(_geoJSON);
    }

    /**
     * GeoJson String 转JTS Geometry
     *
     * @param geoJSON
     * @return
     */
    public static com.vividsolutions.jts.geom.Geometry geoJsonStr2JstGeometry(String geoJSON) {
        if (geoJSON == null) return null;
        com.vividsolutions.jts.geom.Geometry geometry;
        GeometryJSON geometryJSON = new GeometryJSON(9);
        Reader reader = new StringReader(geoJSON);
        try {
            geometry = geometryJSON.read(reader);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return geometry;
    }

    public static GeoJson geoJsonStr2GeoJson(String geoJSON) {
        com.vividsolutions.jts.geom.Geometry geometry = geoJsonStr2JstGeometry(geoJSON);
        return jstGeometry2GeoJson(geometry);
    }

    /**
     * jstGeometry转GeoJson
     *
     * @param geometry
     * @return
     */
    public static GeoJson jstGeometry2GeoJson(com.vividsolutions.jts.geom.Geometry geometry) {
        if (geometry == null) return null;
        String wkt = jstGeometry2Wkt(geometry);
        return wkt2BoxGeometry(wkt);
    }

    public static String jstGeometry2Wkt(com.vividsolutions.jts.geom.Geometry geometry) {
        try {
            if (geometry == null) return null;
            WKTWriter wktWriter = new WKTWriter();
            StringWriter stringWriter = new StringWriter();
            wktWriter.write(geometry, stringWriter);
            String wkt = stringWriter.toString();
            return wkt;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * GeoJson转wkt
     *
     * @param geometry
     * @return
     */
    public static String boxGeometry2Wkt(GeoJson geometry) {
        if (geometry == null) return null;
        String _geoJSON = geometry.toJson();
        com.vividsolutions.jts.geom.Geometry _geometry = geoJsonStr2JstGeometry(_geoJSON);
        return _geometry.toText();
    }

    public static BoundingBox getBoxRatio(BoundingBox boundingBox, double ratio) {
        double west = boundingBox.west();
        double east = boundingBox.east();
        double south = boundingBox.south();
        double north = boundingBox.north();
        double dixX, dixY;
        if (ratio >= 1) { //表示放大
            dixX = (ratio - 1) * (east - west) / 2;
            dixY = (ratio - 1) * (north - south) / 2;
            return BoundingBox.fromLngLats(west - dixX, south - dixY, east + dixX, north + dixY);
        } else { //表示缩小
            dixX = (1 - ratio) * (east - west) / 2;
            dixY = (1 - ratio) * (north - south) / 2;
            return BoundingBox.fromLngLats(west + dixX, south + dixY, east - dixX, north - dixY);
        }
    }


    public static BoundingBox getGeoJsonBox(GeoJson geoJson) {
        GeoJson _geoJson = null;
        if (geoJson instanceof Feature) {
            _geoJson = ((Feature) geoJson).geometry();
        } else if (geoJson instanceof Geometry) {
            _geoJson = geoJson;
        }
        if (_geoJson == null) return null;
        com.vividsolutions.jts.geom.Geometry geometry = geoJson2JstGeometry(_geoJson);
        Envelope envelope = null;
        if (geometry instanceof com.vividsolutions.jts.geom.Point) {
            com.vividsolutions.jts.geom.Point point = (com.vividsolutions.jts.geom.Point) geometry;
        } else {
            envelope = geometry.getEnvelopeInternal();
        }
        return BoundingBox.fromLngLats(envelope.getMinX(), envelope.getMinY(), envelope.getMaxX(), envelope.getMaxY());
    }

    public static String getSimpleBoxStr(GeoJson geoJson) {
        BoundingBox boundingBox = getGeoJsonBox(geoJson);
        String bounds = boundingBox.west() + "," + boundingBox.south() + "," + boundingBox.east() + "," + boundingBox.north();
        return bounds;
    }

    public static String getSimpleBoxRatioStr(GeoJson geoJson, double ratio) {
        BoundingBox boundingBox = getBoxRatio(geoJson, ratio);
        String bounds = boundingBox.west() + "," + boundingBox.south() + "," + boundingBox.east() + "," + boundingBox.north();
        return bounds;
    }

    public static String mergeBoxFeatureBoundStr(double ratio, GeoJson... geoJsons) {
        BoundingBox boundingBox = mergeBoxFeatureBound(ratio, geoJsons);
        String bounds = boundingBox.west() + "," + boundingBox.south() + "," + boundingBox.east() + "," + boundingBox.north();
        return bounds;
    }

    public static BoundingBox mergeBoxFeatureBound(double ratio, GeoJson... geoJsons) {
        if (geoJsons == null || geoJsons.length == 0) return null;
        GeoJson geoJson;
        BoundingBox boundingBox;
        double east = Double.MIN_VALUE, north = Double.MIN_VALUE, west = Double.MAX_VALUE, south = Double.MAX_VALUE;
        for (int i = 0; i < geoJsons.length; i++) {
            geoJson = geoJsons[i];
            boundingBox = getGeoJsonBox(geoJson);
            if (east < boundingBox.east()) {
                east = boundingBox.east();
            }
            if (north < boundingBox.north()) {
                north = boundingBox.north();
            }
            if (west > boundingBox.west()) {
                west = boundingBox.west();
            }
            if (south > boundingBox.south()) {
                south = boundingBox.south();
            }
        }
        BoundingBox box = BoundingBox.fromLngLats(west, south, east, north);
        return getBoxRatio(box, ratio);
    }

    /**
     * 缩放外包矩形(外包矩形都是正的，没有斜的)
     *
     * @param geoJson
     * @param ratio   放大率(边长)
     * @return
     */
    public static BoundingBox getBoxRatio(GeoJson geoJson, double ratio) {
        BoundingBox boundingBox = getGeoJsonBox(geoJson);
        double minX = boundingBox.west();
        double maxX = boundingBox.east();
        double minY = boundingBox.south();
        double maxY = boundingBox.north();

        double dixX, dixY;
        if (ratio >= 1) { //表示放大
            dixX = (ratio - 1) * (maxX - minX) / 2;
            dixY = (ratio - 1) * (maxY - minY) / 2;
            return BoundingBox.fromLngLats(minX - dixX, minY - dixY, maxX + dixX, maxY + dixY);
        } else { //表示缩小
            dixX = (1 - ratio) * (maxX - minX) / 2;
            dixY = (1 - ratio) * (maxY - minY) / 2;
            return BoundingBox.fromLngLats(minX + dixX, minY + dixY, maxX - dixX, maxY - dixY);
        }
    }




    /**
     * WKB 转 GeoJson MultiPolygon
     *
     * @param bytes
     * @return
     * @throws Exception
     */
    public static GeoJson wkb2BoxMultiPolygon(byte[] bytes) throws Exception {
        WKBReader geomReader = new WKBReader();
        StringWriter writer = new StringWriter();
        GeometryJSON geometryJSON = new GeometryJSON(9);
        geometryJSON.write(geomReader.read(bytes), writer);
        return MultiPolygon.fromJson(writer.toString());
    }

    /**
     * WKB 转 GeoJson MultiPolygon
     *
     * @param bytes
     * @return
     * @throws Exception
     */
    public static GeoJson wkb2BoxMultiPolygon(WKBReader geomReader, GeometryJSON geometryJSON, byte[] bytes) throws Exception {
        StringWriter writer = new StringWriter();
        geometryJSON.write(geomReader.read(bytes), writer);
        return MultiPolygon.fromJson(writer.toString());
    }

    /**
     * wkb转GeoJson字符串
     */
    public static String wkb2BoxJsonString(WKBReader geomReader, GeometryJSON geometryJSON, byte[] bytes) throws Exception {
        StringWriter writer = new StringWriter();
        geometryJSON.write(geomReader.read(bytes), writer);
        return writer.toString();
    }

    public static String wkb2BoxJsonString(byte[] bytes) throws Exception {
        WKBReader wkbReader = new WKBReader();
        StringWriter writer = new StringWriter();
        GeometryJSON geometryJSON = new GeometryJSON(9);
        com.vividsolutions.jts.geom.Geometry geometry = wkbReader.read(bytes);
        geometryJSON.write(geometry, writer);
        return writer.toString();
    }

    /**
     * wkt转GeoJson
     *
     * @param wkt
     * @return
     */
    public static Geometry wkt2BoxGeometry(String wkt) {
        if (wkt == null) return null;
        String upperWkt = wkt.toUpperCase();
        try {
            if (upperWkt.startsWith("MULTIPOLYGON")) {
                return wkt2BoxMultiPolygon(wkt);
            }
            if (upperWkt.startsWith("POLYGON")) {
                return wkt2BoxPolygon(wkt);
            }
            if (upperWkt.startsWith("MULTIPOINT")) {
                return wkt2BoxMultiPoint(wkt);
            }
            if (upperWkt.startsWith("POINT")) {
                return wkt2BoxPoint(wkt);
            }
            if (upperWkt.startsWith("MULTILINESTRING")) {
                return wkt2BoxMultiLineString(wkt);
            }
            if (upperWkt.startsWith("LINESTRING")) {
                return wkt2BoxLineString(wkt);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }



    public static enum BoxGeometryType {
        MULTIPOLYGON,
        POLYGON,
        MULTIPOINT,
        POINT,
        MULTILINESTRING,
        LINESTRING
    }

    public static BoxGeometryType getBoxGeometryType(GeoJson geoJson) {
        String wkt = boxGeometry2Wkt(geoJson);
        if (wkt == null) return null;
        String upperWkt = wkt.toUpperCase();
        try {
            if (upperWkt.startsWith("MULTIPOLYGON")) {
                return BoxGeometryType.MULTIPOLYGON;
            }
            if (upperWkt.startsWith("POLYGON")) {
                return BoxGeometryType.POLYGON;
            }
            if (upperWkt.startsWith("MULTIPOINT")) {
                return BoxGeometryType.MULTIPOINT;
            }
            if (upperWkt.startsWith("POINT")) {
                return BoxGeometryType.POINT;
            }
            if (upperWkt.startsWith("MULTILINESTRING")) {
                return BoxGeometryType.MULTILINESTRING;
            }
            if (upperWkt.startsWith("LINESTRING")) {
                return BoxGeometryType.LINESTRING;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Geometry wkt2BoxPolygonCenter(String wkt) {
        try {
            com.vividsolutions.jts.geom.Geometry geometry = wkt2JtsGeometry(wkt);
            com.vividsolutions.jts.geom.Point point = geometry.getCentroid();
            return Point.fromLngLat(point.getX(), point.getY());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    public static com.vividsolutions.jts.geom.Geometry wkt2JtsGeometry(String wkt) throws Exception {
        WKTReader wktReader = new WKTReader();
        return wktReader.read(wkt);
    }

    /**
     * WKT 转 GeoJson MultiPolygon
     *
     * @param wkt
     * @return
     * @throws Exception
     */
    public static MultiPolygon wkt2BoxMultiPolygon(String wkt) throws Exception {
        WKTReader wktReader = new WKTReader();
        StringWriter writer = new StringWriter();
        GeometryJSON geometryJSON = new GeometryJSON(9);
        geometryJSON.write(wktReader.read(wkt), writer);
        return MultiPolygon.fromJson(writer.toString());
    }

    public static Polygon wkt2BoxPolygon(String wkt) throws Exception {
        WKTReader wktReader = new WKTReader();
        StringWriter writer = new StringWriter();
        GeometryJSON geometryJSON = new GeometryJSON(9);
        geometryJSON.write(wktReader.read(wkt), writer);
        return Polygon.fromJson(writer.toString());
    }

    public static Point wkt2BoxPoint(String wkt) throws Exception {
        WKTReader wktReader = new WKTReader();
        StringWriter writer = new StringWriter();
        GeometryJSON geometryJSON = new GeometryJSON(9);
        geometryJSON.write(wktReader.read(wkt), writer);
        return Point.fromJson(writer.toString());
    }

    public static MultiPoint wkt2BoxMultiPoint(String wkt) throws Exception {
        WKTReader wktReader = new WKTReader();
        StringWriter writer = new StringWriter();
        GeometryJSON geometryJSON = new GeometryJSON(9);
        geometryJSON.write(wktReader.read(wkt), writer);
        return MultiPoint.fromJson(writer.toString());
    }

    public static LineString wkt2BoxLineString(String wkt) throws Exception {
        WKTReader wktReader = new WKTReader();
        StringWriter writer = new StringWriter();
        GeometryJSON geometryJSON = new GeometryJSON(9);
        geometryJSON.write(wktReader.read(wkt), writer);
        return LineString.fromJson(writer.toString());
    }

    public static MultiLineString wkt2BoxMultiLineString(String wkt) throws Exception {
        WKTReader wktReader = new WKTReader();
        StringWriter writer = new StringWriter();
        GeometryJSON geometryJSON = new GeometryJSON(9);
        geometryJSON.write(wktReader.read(wkt), writer);
        return MultiLineString.fromJson(writer.toString());
    }

    public static Feature _2Feature(String wkt, Map<String, Object> map) {
        Geometry geometry = wkt2BoxGeometry(wkt);
        JsonObject properties = map2JsonObject(map);
        return Feature.fromGeometry(geometry, properties);
    }


    public static JsonObject map2JsonObject(Map<String, Object> map) {
        JsonObject properties = new JsonObject();
        Object val;
        for (String key : map.keySet()) {
            val = map.get(key);
            if (val == null) continue;
            if (val instanceof String) {
                properties.addProperty(key, (String) val);
            }
            if (val instanceof Double) {
                properties.addProperty(key, (Double) val);
            }
            if (val instanceof Float) {
                properties.addProperty(key, (Float) val);
            }
            if (val instanceof Integer) {
                properties.addProperty(key, (Integer) val);
            }
            if (val instanceof Number) {
                properties.addProperty(key, (Number) val);
            }
            if (val instanceof Boolean) {
                properties.addProperty(key, (Boolean) val);
            }
            if (val instanceof Character) {
                properties.addProperty(key, (Character) val);
            }
        }
        return properties;
    }

    public static GeoJson geoJson2CenterPoint(GeoJson geoJson) {
        if (geoJson == null) return null;
        com.vividsolutions.jts.geom.Geometry geometry = geoJson2JstGeometry(geoJson);
        if (geometry == null) return null;
        com.vividsolutions.jts.geom.Geometry center = geometry.getCentroid();
        return jstGeometry2GeoJson(center);
    }



    public static BoundingBox boxGeometry2BoxLatLngBounds(Geometry geo) {
        try {
            com.vividsolutions.jts.geom.Geometry geometry = wkt2JtsGeometry(boxGeometry2Wkt(geo));
            Envelope envelope = geometry.getEnvelopeInternal();
            BoundingBox latLngBounds = BoundingBox.fromLngLats(envelope.getMinX(), envelope.getMinY(), envelope.getMaxX(), envelope.getMaxY());
            return latLngBounds;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    public static Geometry unionGeometries(List<Geometry> geometries) {
        if (geometries == null) return null;
        com.mapbox.geojson.Geometry geometry;
        com.vividsolutions.jts.geom.Geometry jtsGeometry = null;
        com.vividsolutions.jts.geom.Geometry otherJtsGeometry = null;
        for (int i = 0; i < geometries.size(); i++) {
            geometry = geometries.get(i);
            if (jtsGeometry == null) {
                jtsGeometry = geoJson2JstGeometry(geometry);
                continue;
            }
            otherJtsGeometry = geoJson2JstGeometry(geometry);
            jtsGeometry = jtsGeometry.union(otherJtsGeometry);
        }
        if (jtsGeometry == null) return null;
        return (Geometry) jstGeometry2GeoJson(jtsGeometry);
    }



    /**
     * 判断点在不在面内
     *
     * @param wkt
     * @param x   ,y
     * @return
     */
    public static boolean isWithinJTS(String wkt, double x, double y) {
        try {
            GeometryFactory geometryFactory = new GeometryFactory();
            com.vividsolutions.jts.geom.Geometry geometry = Transformation.wkt2JtsGeometry(wkt);
            Coordinate coord = new Coordinate(x, y);
            com.vividsolutions.jts.geom.Point point = geometryFactory.createPoint(coord);
            return point.within(geometry);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 判断点在不在面内
     *
     * @param geometry
     * @param point
     * @return
     */
    public static boolean isWithinJTS(Geometry geometry, Point point) {
        try {
            GeometryFactory geometryFactory = new GeometryFactory();
            com.vividsolutions.jts.geom.Geometry geometryJts = Transformation.wkt2JtsGeometry(Transformation.boxGeometry2Wkt(geometry));
            Coordinate coord = new Coordinate(point.longitude(), point.latitude());
            com.vividsolutions.jts.geom.Point pointJts = geometryFactory.createPoint(coord);
            return pointJts.within(geometryJts);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 返回第一个在面内的点
     *
     * @param geometryWith
     * @param Points
     * @return
     */
    public static Point getFristWithinJTS(Geometry geometryWith, List<Geometry> Points) {
        try {
            for (Geometry geometryP : Points) {
                if (geometryP instanceof Point) {
                    Point point = (Point) geometryP;
                    if (Transformation.isWithinJTS(geometryWith, point)) {
                        return point;
                    }
                }
            }
        } catch (Exception e) {
            return null;
        }
        return null;
    }



    //根据计算多边形点位与中心点的方向，判断标注点位置
    public static String calTextAnchor(Point lblPt, Point centPt) {
        String anchor = "center";
        double x, y, cx, cy;
        x = lblPt.longitude();
        y = lblPt.latitude();
        cx = centPt.longitude();
        cy = centPt.latitude();

        if (x <= cx) {
            if (y <= cy) {
                anchor = "top-right";
            } else {
                anchor = "bottom-right";
            }
        } else {
            if (y <= cy) {
                anchor = "top-left";
            } else {
                anchor = "bottom-left";
            }
        }
        return anchor;
    }


    /**
     * 将经纬度转换为度分秒格式
     *
     * @param du 116.418847
     * @return 116°25'7.85"
     */
    public static String changeToDFM(double du) {
        int du1 = (int) du;
        double tp = (du - du1) * 60;
        int fen = (int) tp;
        String miao = String.format("%.2f", Math.abs(((tp - fen) * 60)));
        return du1 + "°" + Math.abs(fen) + "'" + miao + "\"";
    }
}
