package com.grandtech.tools;

import com.mapbox.api.staticmap.v1.MapboxStaticMap;
import com.mapbox.api.staticmap.v1.StaticMapCriteria;
import com.mapbox.api.staticmap.v1.models.StaticMarkerAnnotation;
import com.mapbox.api.staticmap.v1.models.StaticPolylineAnnotation;
import com.mapbox.core.BuildConfig;
import com.mapbox.core.utils.ColorUtils;
import com.mapbox.geojson.*;
import com.mapbox.geojson.Point;
import com.sun.javafx.css.converters.ColorConverter;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class a {


    public static void main(String a[]) throws Exception {

      
        System.out.println("aaa".split(",")[0]);
        List<String> l = new ArrayList<>();
        l.add("农户姓名:蔡霞");
        l.add("投保面积:170000.6亩");
        l.add("地块数量:799");
        l.add("地点:xxxx");
        String jarpath = System.getProperty("user.dir");
        String imgpath = jarpath + "\\finalImg"+0+".jpg";
        String str = readJsonFile(jarpath + "\\e.json");
        String wkt = "POLYGON((106.241577 28.305915,106.24154 28.305932,106.24151833550069 28.30594620492812,106.241511851549 28.306045085192,106.241495758295 28.306155055761,106.241459548473 28.306247591972,106.241405904293 28.306325376034,106.241320073605 28.30638974905,106.241230219603 28.306444734335,106.241096109152 28.306474238634,106.240992844105 28.306468874216,106.24092310667 28.306463509798,106.24087974822724 28.306452670187074,106.240835 28.306481,106.240965 28.306508,106.241097 28.306522,106.241298 28.306466,106.241383 28.306425,106.241471 28.306359,106.241519 28.306287,106.241534 28.306223,106.241567 28.306081,106.241577 28.305915))";
        Geometry geometry = Transformation.wkt2BoxGeometry(wkt);
        Feature feature = Feature.fromGeometry(geometry);

        String wkt1 = "POLYGON((115.2765027 35.9441251,115.2779709 35.9438517,115.27794 35.9437076,115.277833 35.943024,115.27722 35.9431269,115.2762974 35.9432786,115.2763086 35.9433371,115.2763426 35.943449,115.2764407 35.9438908,115.2765027 35.9441251),(115.2770601 35.9434363,115.277227 35.943193,115.2776419 35.9432934,115.2775084 35.9435406,115.2770601 35.9434363))";
        Geometry geometry1 = Transformation.wkt2BoxGeometry(wkt1);
        Feature feature1 = Feature.fromGeometry(geometry1);

        String wkt2 = "MULTIPOLYGON(((109.9971562 40.4338734,109.9961748 40.4338339,109.9962363 40.4337055,109.9971588 40.4337352,109.9971562 40.4338734)))";
        Geometry geometry2 = Transformation.wkt2BoxGeometry(wkt2);
        Feature feature2 = Feature.fromGeometry(geometry2);
        List<Feature> features = new ArrayList<>();
        features.add(feature);
        //features.add(feature1);
        //features.add(feature2);
      //  FeatureCollection featureCollection = FeatureCollection.fromFeature(feature);
        FeatureCollection featureCollection = FeatureCollection.fromFeatures(features);
        // BufferedImage finalImg = MapScreenshotTool.createMapScreenshot(
        //     "MultiPolygon\",\"coordinates\":[[[[115.3190832,35.9610111],[115.318602,35.9593635],[115.318634,35.9593561],[115.318711,35.959349],[115.3193114,35.9592348],[115.3197923,35.9608494],[115.3197736,35.9608951],[115.3193715,35.9609651],[115.3190832,35.9610111]]]]}  "MULTIPOLYGON(((109.9971562 40.4338734,109.9961748 40.4338339,109.9962363 40.4337055,109.9971588 40.4337352,109.9971562 40.4338734)))", l);
     //   List<String> list = new ArrayList<>();
     //   list.add("POLYGON((115.2765027 35.9441251,115.2779709 35.9438517,115.27794 35.9437076,115.277833 35.943024,115.27722 35.9431269,115.2762974 35.9432786,115.2763086 35.9433371,115.2763426 35.943449,115.2764407 35.9438908,115.2765027 35.9441251),(115.2770601 35.9434363,115.277227 35.943193,115.2776419 35.9432934,115.2775084 35.9435406,115.2770601 35.9434363))");
    //    list.add("MULTIPOLYGON(((109.9971562 40.4338734,109.9961748 40.4338339,109.9962363 40.4337055,109.9971588 40.4337352,109.9971562 40.4338734)))");
        List<String> waterList = new ArrayList<>();
      /*  waterList.add("地点:" + "");
        waterList.add("地块数量:" + "");
        waterList.add("投保面积:" + "(亩)");
        waterList.add("农户姓名:" + "");*/
        ScreenshotsSetting screenshotsSetting = prepareScreenshotsSetting(null);
        screenshotsSetting.setWidth(256);
        screenshotsSetting.setHeight(256);
        screenshotsSetting.setMargin(100);
        screenshotsSetting.setDrawRect(false);
       // screenshotsSetting.setFillColor(Color.red);
        List<Object> mapScreenshots = MapScreenshotToolCGCS2000.createMapScreenshots(featureCollection.toJson(), waterList, "https://t0.tianditu.gov.cn/DataServer?T=img_c&x={x}&y={y}&l={z}&tk=9859bd0c23cc35a037ced54d1e8a753f", screenshotsSetting);
        BufferedImage finalImg = (BufferedImage)mapScreenshots.get(0);
        BoundingBox boundingBox = (BoundingBox)mapScreenshots.get(1);
        //BufferedImage finalImg = MapScreenshotTool.createMapScreenshots( featureCollection,"https://omap.geo-compass.com/v1/img_w/wmts?SERVICE=WMTS&REQUEST=GetTile&VERSION=1.0.0&LAYER=img&STYLE=default&TILEMATRIXSET=w&FORMAT=tiles&TILECOL={x}&TILEROW={y}&TILEMATRIX={z}&tdtkey=9rThVrRfPPsooA8CwuAWf3uhF5YP5JqU",screenshotsSetting);
         imgpath = jarpath + "\\finalImg1.jpg";
        // String imgpath2 = jarpath + "\\finalImg2.jpg";
        ImageIO.write(finalImg, "png", new File(imgpath));
        //zoomImage(imgpath,imgpath2,512,512);
        System.out.println("完成拼接！");
        }

    private static ScreenshotsSetting prepareScreenshotsSetting(String color){
        ScreenshotsSetting screenshotsSetting = new ScreenshotsSetting();
        Font font = new Font("宋体", Font.BOLD, 12);
        screenshotsSetting.setFont(font);
        screenshotsSetting.setImageScale(1.0f);
        screenshotsSetting.setSocketTimeout(5000);

        if(null!=color){
           /* RGB rgb = ColorConverter.toRGB(color);
            Color fillColor = new Color(rgb.getRed(), rgb.getGreen(), rgb.getBlue());
            screenshotsSetting.setFillColor(fillColor);
            screenshotsSetting.setFillType(ScreenshotsSetting.FILL);*/
        }else{
            screenshotsSetting.setFillColor(null);
        }
        return screenshotsSetting;
    }
      /*  for (Feature feature1 : features) {

        }*/


   /*     MapboxStaticMap staticImage = MapboxStaticMap.builder()
                .accessToken("pk.eyJ1IjoiZ3lraiIsImEiOiJjbDA5YjBjYzIwYmc4M2pwOXd4dHh3Mzc5In0.54AifzzOuqzO5M3f75zt1A")
                .styleId(StaticMapCriteria.LIGHT_STYLE)
                .cameraPoint(Point.fromLngLat(115.2765027, 35.9441251)) // Image's centerpoint on map
                .cameraZoom(13)
                .width(320) // Image width
                .height(320) // Image height
                .geoJson(geometry)
                .retina(true) // Retina 2x image will be returned
                .build();
        String imageUrl = staticImage.url().toString();
        System.out.println(imageUrl);*/

      /*  List<StaticMarkerAnnotation> markers = new ArrayList<>();
        List<StaticPolylineAnnotation> polylines = new ArrayList<>();


        markers.add(StaticMarkerAnnotation.builder().name(StaticMapCriteria.LARGE_PIN)
                .lnglat(Point.fromLngLat(-122.46589, 37.77343))
                .color(ColorUtils.toHexString(
                        Color.MAGENTA.getRed(),
                        Color.MAGENTA.getGreen(),
                        Color.MAGENTA.getBlue()))
                .label("a").build());

        markers.add(StaticMarkerAnnotation.builder().name(StaticMapCriteria.LARGE_PIN)
                .lnglat(Point.fromLngLat(-122.42816,37.75965))
                .color(Color.GREEN.getRed(),
                        Color.GREEN.getGreen(),
                        Color.GREEN.getBlue())
                .label("b")
                .build());

        polylines.add(StaticPolylineAnnotation.builder().polyline("abcdef").build());

        MapboxStaticMap mapboxStaticMap = MapboxStaticMap.builder()
                .accessToken("pk.eyJ1IjoiZ3lraiIsImEiOiJjbDA5YjBjYzIwYmc4M2pwOXd4dHh3Mzc5In0.54AifzzOuqzO5M3f75zt1A")
                .width(500)
                .height(300)
                .retina(true)
                .cameraAuto(true)
                .staticMarkerAnnotations(markers)
                .staticPolylineAnnotations(polylines)
                .build();

        System.out.println(mapboxStaticMap.url());*/


    public static String readJsonFile(String fileName) {
        String jsonStr = "";
        try {
            File jsonFile = new File(fileName);
            FileReader fileReader = new FileReader(jsonFile);
            Reader reader = new InputStreamReader(new FileInputStream(jsonFile),"utf-8");
            int ch = 0;
            StringBuffer sb = new StringBuffer();
            while ((ch = reader.read()) != -1) {
                sb.append((char) ch);
            }
            fileReader.close();
            reader.close();
            jsonStr = sb.toString();
            return jsonStr;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 返回黑色占图片的像素比例
     * @param finalImg BufferedImage
     * @return
     */
    public static double getBlackScale(BufferedImage finalImg)  {
            //图片的宽度
            int width = finalImg.getWidth();
            //图片的高度
            int height = finalImg.getHeight();
            //图片起始点X
            int minX = 0;
            //图片起始点Y
            int minY = 0;
            double points = width*height;
            double blacksize = 0;
            for (int i1 = minX; i1 < width; i1++) {
                for (int j = minY; j < height; j++) {
                    // 获取具体像素，并以object类型表示
                    Object data = finalImg.getRaster().getDataElements(i1, j, null);
                    int r = finalImg.getColorModel().getRed(data);
                    int g = finalImg.getColorModel().getGreen(data);
                    int b = finalImg.getColorModel().getBlue(data);
                    if((r+b+g)==0){
                        blacksize++;
                    }
                }
            }
            return blacksize/points;
    }
}
