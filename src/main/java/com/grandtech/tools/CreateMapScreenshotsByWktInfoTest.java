package com.grandtech.tools;

import com.mapbox.geojson.FeatureCollection;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @program: MapScreenshot
 * @description:
 * @author: 冯帅
 * @create: 2022-04-01 14:41
 **/

public class CreateMapScreenshotsByWktInfoTest {

    public static void main(String a[]) throws Exception {

    /*    List<String> waters = new ArrayList<>();
        waters.add("农户姓名:蔡霞");
        waters.add("投保面积:170000.6亩");
        waters.add("地块数量:799");
        waters.add("地点:xxxx");
        String url = "https://a.tiles.mapbox.com/v4/mapbox.satellite/{z}/{x}/{y}.png?access_token=pk.eyJ1IjoiZ3lraiIsImEiOiJjbDA5YjBjYzIwYmc4M2pwOXd4dHh3Mzc5In0.54AifzzOuqzO5M3f75zt1A";
        String wkt = "POLYGON((115.2765027 35.9441251,115.2779709 35.9438517,115.27794 35.9437076,115.277833 35.943024,115.27722 35.9431269,115.2762974 35.9432786,115.2763086 35.9433371,115.2763426 35.943449,115.2764407 35.9438908,115.2765027 35.9441251),(115.2770601 35.9434363,115.277227 35.943193,115.2776419 35.9432934,115.2775084 35.9435406,115.2770601 35.9434363))";
        WktInfo wktInfo = new WktInfo();
        wktInfo.setWkt(wkt);
        wktInfo.setColor(Color.RED);
        wktInfo.setMark(true);
        ArrayList<WktInfo> wktInfos = new ArrayList<>();
        wktInfos.add(wktInfo);
        String wkt1 = "MULTIPOLYGON(((111.841141896 27.6662461280001,111.840969385 27.6662461280001,111.840909899 27.6661806930001,111.840856361 27.666043874,111.840951539 27.665966543,111.841082409 27.66579998,111.841189485 27.6657702360001,111.841320356 27.66579998,111.841457175 27.6657940310001,111.841671327 27.6657821330001,111.841689172 27.6656691100001,111.841516661 27.6656691100001,111.841385791 27.665681007,111.841296561 27.6656691100001,111.841129999 27.6656274690001,111.841088358 27.665591777,111.841201383 27.6654430610001,111.841314407 27.6653181390001,111.841403636 27.665282446,111.841742711 27.6652883950001,111.841802196 27.6653538310001,111.841814094 27.665425215,111.841956862 27.6655620330001,111.841921169 27.6656691090001,111.842284038 27.6657048010001,111.842307833 27.665651263,111.842480343 27.6656691090001,111.842569573 27.6657048000001,111.842617163 27.6658059280001,111.842670699 27.665901107,111.842742084 27.6659605930001,111.84284916 27.6660795670001,111.842783724 27.6660974120001,111.842706392 27.6661390520001,111.842599316 27.6661212070001,111.842397062 27.666008182,111.842212654 27.6660974120001,111.842266192 27.666263975,111.842254294 27.6663115640001,111.842165065 27.666341306,111.842260243 27.666525715,111.842272141 27.6665792530001,111.842176962 27.6666803800001,111.842016348 27.6667041760001,111.841974708 27.666733918,111.841861683 27.6668885820001,111.841665378 27.667067042,111.841582097 27.667108683,111.841510713 27.667108683,111.841308459 27.6670610940001,111.841195434 27.6669123780001,111.841206108 27.666860969,111.841224376 27.6667952010001,111.841225474 27.666708063,111.84128778 27.666670665,111.841344872 27.6665975,111.84124076 27.6665016360001,111.841218442 27.6664141320001,111.841207129 27.6663854660001,111.841173322 27.6662998030001,111.841141896 27.6662461280001)))";
        WktInfo wktInfo1 = new WktInfo();
        wktInfo1.setWkt(wkt1);
        Color color = new Color(20,205,180);
        wktInfo1.setColor(color);
        wktInfo1.setMark(false);
        wktInfo1.setCenterSymbol("中心点标注");
        wktInfos.add(wktInfo1);
        BufferedImage finalImg = MapScreenshotTool.createMapScreenshotsByWktInfo(wktInfos,waters,url,null);
        String jarpath = System.getProperty("user.dir");
        String imgpath = jarpath + "\\finalImg1.jpg";
        String imgpath2 = jarpath + "\\finalImg2.jpg";
        ImageIO.write(finalImg, "png", new File(imgpath));
        //zoomImage(imgpath,imgpath2,512,512);
        System.out.println("完成拼接！");*/

        String basepath = "D:\\temp\\";
        long l = System.currentTimeMillis();
        // String[] split = mobileSampleQueryParam.getPointstr().split(":");
        File f = new File(basepath + l + "/out");
        f.mkdirs();
        //解析shp
        // FeatureCollection featureCollection = ShpUtil.getShpFileGeometry(shapeFilePath);
        double[] shpBounds = new double[]{ 33.53019833558477 ,118.23051810282823 , 33.48708987200007,118.17599415780421};
        String path = "http://gykj123.cn:9000/middleground-rsanalysis-img-dynamictile/api/v1/wmts/7b2b126df8fe4a1db64275e1001c00e9?x={x}&y={y}&l={z}&type=img";
        MapScreenshotTool.downLoadImage(shpBounds,17,path,basepath+l+File.separator+"image.png",basepath+l+File.separator+"image.png.aux.xml");
    }

}
