package com.grandtech.tools;

import java.io.File;

/**
 * @program: MapScreenshot
 * @description:
 * @author: 冯帅
 * @create: 2023-09-18 13:52
 **/

public class Test {

    public static void main(String a[]) throws Exception {
        String basepath = "D:\\temp1\\";
        MapScreenshotTool.downLoadImage(
                new double[]{26.612287759780884,106.55866026878357,26.609026193618774,106.55647158622742 },
                16,null,basepath + File.separator + "image.png",basepath  + File.separator + "image.png.aux.xml");
        /* 2024-06-20 17:21:12.360  INFO 27432 --- [MessageThread_1] g.i.r.f.r.s.FeatureRegularizationService : 26.7217218875885
2024-06-20 17:21:12.360  INFO 27432 --- [MessageThread_1] g.i.r.f.r.s.FeatureRegularizationService : 106.67017579078674
2024-06-20 17:21:12.360  INFO 27432 --- [MessageThread_1] g.i.r.f.r.s.FeatureRegularizationService : 26.56023144721985
2024-06-20 17:21:12.360  INFO 27432 --- [MessageThread_1] g.i.r.f.r.s.FeatureRegularizationService : 106.46233677864075*/
       // long l = MercatorProjection.latitudeToTileY(116.5026437376, (byte) 13);

       // long l1 = MercatorProjection.longitudeToTileX(40.021272, (byte) 13);
       // System.out.println(l+",,"+l1);
        //Double[] args = new Double[]{41.82065462272939,123.43218691923107,41.817995012638676,123.43024229625854};
       // MapScreenshotTool.downLoadImage( 41.81989150780569 ,123.4322462779615 ,args,18,"http://172.16.0.173:8080/tdt-proxy-server/wmts/img_w?x={x}&y={y}&l={z}","D:\\temp\\image\\image.png","D:\\temp\\image\\image.png.aux.xml");
/*
        String basepath = "D:\\temp\\";
        long l = System.currentTimeMillis();
        String[] split = pointstr.split(":");
        File f = new File(basepath+l+"/out");
        f.mkdirs();
        //下载影像
        MapScreenshotTool.downLoadImage( Double.valueOf(split[1]) ,Double.valueOf(split[0]) ,bounds,zoom,imageUrl,basepath+l+"/image.png",basepath+l+"/image.png.aux.xml");
        System.out.println("下载影像完成");*/

    }
}
