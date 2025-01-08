package com.grandtech.tools;


import com.mapbox.geojson.*;
import com.mapbox.geojson.Point;
import com.mapbox.geojson.Polygon;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;
import net.coobird.thumbnailator.Thumbnails;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.ssl.TrustStrategy;

import javax.imageio.ImageIO;
import javax.net.ssl.SSLContext;
import javax.xml.ws.Response;
import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.*;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

import static com.grandtech.tools.ScreenshotsSetting.FILL;

/**
 * @program: MapScreenshot
 * @description: 坐落图生成工具类
 * @author: 冯帅
 * @create: 2020-09-23 13:21
 **/

public class MapScreenshotTool {



    /**
     *
     * @param latitude
     * @param longitude
     * @param imageUrl
     * @param outPath
     * @param outPathmsg
     */
    public static void downLoadImage(double latitude,double longitude,int zoom,String imageUrl,String outPath,String outPathmsg){
        long y = MercatorProjection.latitudeToTileY(latitude, (byte) zoom);
        long x = MercatorProjection.longitudeToTileX(longitude, (byte) zoom);
        y = y-4;
        x = x-4;
        List<long[]> lastTiles = new ArrayList<>();
        for(int i= 0;i<9;i++){
            for(int j= 0;j<9;j++){
                lastTiles.add(new long[]{16,x+i,y+j});
            }
        }
        lastTiles.add(new long[]{16,x,y});

        Envelope envelope = TileUtil.xyzs2Envelope2Pixel(lastTiles);
        BufferedImage finalImg = new BufferedImage(2304, 2304, 1);
        final Graphics2D graphics2D = finalImg.createGraphics();
        //绘制影像
        ScreenshotsSetting screenshotsSetting = new ScreenshotsSetting();
        screenshotsSetting.setSocketTimeout(6000);
        List<FutureTask<String>> futureTasks = drawTileImageAsync(lastTiles, imageUrl, envelope, graphics2D, screenshotsSetting);
        for (FutureTask<String> futureTask : futureTasks) {
            try {
                futureTask.get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }
        File outputFile = null;
        try {
            // 设置输出文件路径和文件名
            outputFile = new File(outPath);
            // 写入图像文件
            ImageIO.write(finalImg, "png", outputFile);
            System.out.println("图像已写入文件：" + outputFile.getAbsolutePath());
        } catch (IOException e) {
            System.err.println("写入图像文件时出错：" + e.getMessage());
        } finally {
            // 释放资源
            if (finalImg != null) {
                finalImg.flush();
            }
            if (outputFile != null) {
                outputFile.deleteOnExit();
            }
        }

        String msg = "<PAMDataset>\n" +
                "  <SRS dataAxisToSRSAxisMapping=\"2,1\">GEOGCS[\"WGS 84\",DATUM[\"WGS_1984\",SPHEROID[\"WGS 84\",6378137,298.25722356049,AUTHORITY[\"EPSG\",\"7030\"]],AUTHORITY[\"EPSG\",\"6326\"]],PRIMEM[\"Greenwich\",0],UNIT[\"degree\",0.0174532925199433,AUTHORITY[\"EPSG\",\"9122\"]],AXIS[\"Latitude\",NORTH],AXIS[\"Longitude\",EAST],AUTHORITY[\"EPSG\",\"4326\"]]</SRS>\n" +
                "  <GeoTransform> param</GeoTransform>\n" +
                "  <Metadata domain=\"IMAGE_STRUCTURE\">\n" +
                "    <MDI key=\"INTERLEAVE\">PIXEL</MDI>\n" +
                "  </Metadata>\n" +
                "  <Metadata>\n" +
                "    <MDI key=\"AREA_OR_POINT\">Area</MDI>\n" +
                "  </Metadata>\n" +
                "</PAMDataset>\n";
        double xx = MercatorProjection.tileXToLongitude(x,(byte) zoom);
        double yy = MercatorProjection.tileYToLatitude(y,(byte) zoom);

        double xxe = MercatorProjection.tileXToLongitude(x+8,(byte) zoom);
        double yye = MercatorProjection.tileYToLatitude(y+8,(byte) zoom);

        double xc = Math.abs(xxe-xx);
        double yc = Math.abs(yye-yy);
        double p = 256*8.0;
        double xp = xc/p;
        double yp = yc/p;


        String  adfGeoTransform0 = xx+" , "+xp+" , "+0.0+" , "+yy+" , "+0.0+" , "+-yp;
        msg = msg.replace("param",adfGeoTransform0);


        try {
            File file = new File(outPathmsg);
            FileWriter writer = new FileWriter(file);
            writer.write(msg);
            writer.close();
            System.out.println("Successfully wrote to file.");
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }


    /**
     *
     * @param latitude
     * @param longitude
     * @param imageUrl
     * @param outPath
     * @param outPathmsg
     */
    public static void downLoadImage(double latitude,double longitude,Double[] bounds,int zoom,String imageUrl,String outPath,String outPathmsg){

        List<long[]> lastTiles = new ArrayList<>();
        long miny = MercatorProjection.latitudeToTileY(bounds[0], (byte) zoom);
        long maxy = MercatorProjection.latitudeToTileY(bounds[2], (byte) zoom);
        long maxx = MercatorProjection.longitudeToTileX(bounds[1], (byte) zoom);
        long minx = MercatorProjection.longitudeToTileX(bounds[3], (byte) zoom);

        for(long i= 0;i<((maxx-minx)+1);i++){
            for(int j= 0;j<((maxy-miny)+1);j++){
                lastTiles.add(new long[]{zoom,minx+i,miny+j});
            }
        }
        if(lastTiles.size()>81){
            downLoadImage(latitude,longitude,zoom,imageUrl,outPath,outPathmsg);
            return;
        }

        Envelope envelope = TileUtil.xyzs2Envelope2Pixel(lastTiles);
        BufferedImage finalImg = new BufferedImage((int)((maxx-minx)+1)*256, (int)((maxy-miny)+1)*256, 1);
        final Graphics2D graphics2D = finalImg.createGraphics();
        //绘制影像
        ScreenshotsSetting screenshotsSetting = new ScreenshotsSetting();
        screenshotsSetting.setSocketTimeout(6000);
        List<FutureTask<String>> futureTasks = drawTileImageAsync(lastTiles, imageUrl, envelope, graphics2D, screenshotsSetting);
        for (FutureTask<String> futureTask : futureTasks) {
            try {
                futureTask.get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }
        File outputFile = null;
        try {
            // 设置输出文件路径和文件名
            outputFile = new File(outPath);
            // 写入图像文件
            ImageIO.write(finalImg, "png", outputFile);
            System.out.println("图像已写入文件：" + outputFile.getAbsolutePath());
        } catch (IOException e) {
            System.err.println("写入图像文件时出错：" + e.getMessage());
        } finally {
            // 释放资源
            if (finalImg != null) {
                finalImg.flush();
            }
            if (outputFile != null) {
                outputFile.deleteOnExit();
            }
        }

        String msg = "<PAMDataset>\n" +
                "  <SRS dataAxisToSRSAxisMapping=\"2,1\">GEOGCS[\"WGS 84\",DATUM[\"WGS_1984\",SPHEROID[\"WGS 84\",6378137,298.25722356049,AUTHORITY[\"EPSG\",\"7030\"]],AUTHORITY[\"EPSG\",\"6326\"]],PRIMEM[\"Greenwich\",0],UNIT[\"degree\",0.0174532925199433,AUTHORITY[\"EPSG\",\"9122\"]],AXIS[\"Latitude\",NORTH],AXIS[\"Longitude\",EAST],AUTHORITY[\"EPSG\",\"4326\"]]</SRS>\n" +
                "  <GeoTransform> param</GeoTransform>\n" +
                "  <Metadata domain=\"IMAGE_STRUCTURE\">\n" +
                "    <MDI key=\"INTERLEAVE\">PIXEL</MDI>\n" +
                "  </Metadata>\n" +
                "  <Metadata>\n" +
                "    <MDI key=\"AREA_OR_POINT\">Area</MDI>\n" +
                "  </Metadata>\n" +
                "</PAMDataset>\n";
        double xx = MercatorProjection.tileXToLongitude(minx,(byte) zoom);
        double yy = MercatorProjection.tileYToLatitude(miny,(byte) zoom);

        double xxe = MercatorProjection.tileXToLongitude(maxx,(byte) zoom);
        double yye = MercatorProjection.tileYToLatitude(maxy,(byte) zoom);

        double xc = Math.abs(xxe-xx);
        double yc = Math.abs(yye-yy);

        double xp = xc/(256*(maxx-minx));
        double yp = yc/(256*(maxy-miny));


        String  adfGeoTransform0 = xx+" , "+xp+" , "+0.0+" , "+yy+" , "+0.0+" , "+-yp;
        msg = msg.replace("param",adfGeoTransform0);
        try {
            File file = new File(outPathmsg);
            FileWriter writer = new FileWriter(file);
            writer.write(msg);
            writer.close();
            System.out.println("Successfully wrote to file.");
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }


    /**
     *
     * @param latitude
     * @param longitude
     * @param imageUrl
     * @param outPath
     * @param outPathmsg
     */
    public static boolean downLoadImage(double[] bounds,int zoom,String imageUrl,String outPath,String outPathmsg){
        List<long[]> lastTiles = new ArrayList<>();
        Map<String,Object> param = new HashMap<>();
        lastTiles = getTiles(bounds, zoom,param);
        if(lastTiles.size() == 0){
            return false;
        }
        long miny = (long)param.get("miny");
        long maxy = (long)param.get("maxy");
        long maxx =  (long)param.get("maxx");
        long minx = (long)param.get("minx");
        zoom = (int)param.get("zoom");
        Envelope envelope = TileUtil.xyzs2Envelope2Pixel(lastTiles);
        BufferedImage finalImg = new BufferedImage((int)((maxx-minx)+1)*256, (int)((maxy-miny)+1)*256, 1);
        final Graphics2D graphics2D = finalImg.createGraphics();
        //绘制影像
        ScreenshotsSetting screenshotsSetting = new ScreenshotsSetting();
        screenshotsSetting.setSocketTimeout(6000);
        List<FutureTask<String>> futureTasks = drawTileImageAsync(lastTiles, imageUrl, envelope, graphics2D, screenshotsSetting);
        for (FutureTask<String> futureTask : futureTasks) {
            try {
                futureTask.get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }
        File outputFile = null;
        try {
            // 设置输出文件路径和文件名
            outputFile = new File(outPath);
            // 写入图像文件
            ImageIO.write(finalImg, "png", outputFile);
            System.out.println("图像已写入文件：" + outputFile.getAbsolutePath());

        } catch (IOException e) {
            System.err.println("写入图像文件时出错：" + e.getMessage());
        } finally {
            // 释放资源
            if (finalImg != null) {
                finalImg.flush();
            }
           /* if (outputFile != null) {
                outputFile.deleteOnExit();
            }*/
        }

        String msg = "<PAMDataset>\n" +
                "  <SRS dataAxisToSRSAxisMapping=\"2,1\">GEOGCS[\"WGS 84\",DATUM[\"WGS_1984\",SPHEROID[\"WGS 84\",6378137,298.25722356049,AUTHORITY[\"EPSG\",\"7030\"]],AUTHORITY[\"EPSG\",\"6326\"]],PRIMEM[\"Greenwich\",0],UNIT[\"degree\",0.0174532925199433,AUTHORITY[\"EPSG\",\"9122\"]],AXIS[\"Latitude\",NORTH],AXIS[\"Longitude\",EAST],AUTHORITY[\"EPSG\",\"4326\"]]</SRS>\n" +
                "  <GeoTransform> param</GeoTransform>\n" +
                "  <Metadata domain=\"IMAGE_STRUCTURE\">\n" +
                "    <MDI key=\"INTERLEAVE\">PIXEL</MDI>\n" +
                "  </Metadata>\n" +
                "  <Metadata>\n" +
                "    <MDI key=\"AREA_OR_POINT\">Area</MDI>\n" +
                "  </Metadata>\n" +
                "</PAMDataset>\n";
        double xx = MercatorProjection.tileXToLongitude(minx,(byte) zoom);
        double yy = MercatorProjection.tileYToLatitude(miny,(byte) zoom);

        double xxe = MercatorProjection.tileXToLongitude(maxx,(byte) zoom);
        double yye = MercatorProjection.tileYToLatitude(maxy,(byte) zoom);

        double xc = Math.abs(xxe-xx);
        double yc = Math.abs(yye-yy);

        double xp = xc/(256*(maxx-minx));
        double yp = yc/(256*(maxy-miny));


        String  adfGeoTransform0 = xx+" , "+xp+" , "+0.0+" , "+yy+" , "+0.0+" , "+-yp;
        msg = msg.replace("param",adfGeoTransform0);
        try {
            File file = new File(outPathmsg);
            FileWriter writer = new FileWriter(file);
            writer.write(msg);
            writer.close();
            System.out.println("Successfully wrote to file.");
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
        return true;
    }

    private static List<long[]>  getTiles (double[] bounds,int zoom,Map<String,Object> param){
        List<long[]> lastTiles = new ArrayList<>();

        long miny = MercatorProjection.latitudeToTileY(bounds[0], (byte) zoom);
        long maxy = MercatorProjection.latitudeToTileY(bounds[2], (byte) zoom);
        long maxx = MercatorProjection.longitudeToTileX(bounds[1], (byte) zoom);
        long minx = MercatorProjection.longitudeToTileX(bounds[3], (byte) zoom);
        /*if(zoom<5){
            return lastTiles;
        }*/
        /*if(((maxx-minx)*(maxy-miny))>100){
            lastTiles = getTiles(bounds,zoom-1,param);
            return lastTiles;
        }*/
       /* if(((maxx-minx)*(maxy-miny))>4000){
            lastTiles = getTiles(bounds,zoom-1,param);
            return lastTiles;
        }*/
        long difference = (maxx - minx) - 12;
        if(difference < 0 ){
            minx = minx+difference;
        }
        difference = (maxy - miny) - 12;
        if(difference < 0 ){
            miny = miny+difference;
        }
        param.put("miny",miny);
        param.put("maxy",maxy);
        param.put("maxx",maxx);
        param.put("minx",minx);
        param.put("zoom",zoom);
        for(long i= 0;i<((maxx-minx)+1);i++){
            for(int j= 0;j<((maxy-miny)+1);j++){
                lastTiles.add(new long[]{zoom,minx+i,miny+j});
            }
        }
        return lastTiles;
    }

    public static BufferedImage createMapScreenshot(String wkt, List<String> waters) {
        return createMapScreenshot(wkt, waters, null);
    }


    public static BufferedImage createMapScreenshot(String wkt, List<String> waters, final String imgCustomUrl) {
        try {
            Geometry[] geometries = new Geometry[1];
            geometries[0] = Transformation.wkt2BoxGeometry(wkt);
            BoundingBox boundingBox = Transformation.mergeBoxFeatureBound(1.1, geometries);
            //Envelope envelope =TileUtil.bound2PixelEnvelope(boundingBox,16);
            //确定截图的级别，预览图截图接近1024*768 坐落图接近256*256
            int[] arr = new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17};
            //int[] arr = new int[] { 16 };
            List<long[]> tiles = searchZoomTilesByArea(arr, boundingBox, 0.6);
            final Envelope envelope = TileUtil.xyzs2Envelope2Pixel(tiles);
            int w = (int) (envelope.getMaxX() - envelope.getMinX());
            int h = (int) ((envelope.getMaxY() - envelope.getMinY()));
            BufferedImage finalImg = new BufferedImage(w, h, 1);
            final Graphics2D graphics2D = finalImg.createGraphics();
            Font font = new Font("方正黑体", Font.BOLD, 12);
            graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            byte zoom = (byte) tiles.get(0)[0];
            List<FutureTask<String>> futureTasks = new ArrayList<>();
            for (final long[] tile : tiles) {
                FutureTask<String> futureTask = new FutureTask<String>(new Callable<String>() {
                    @Override
                    public String call() throws Exception {
                        String imgUrl = null;
                        if (imgCustomUrl != null && !"".equals(imgCustomUrl)) {
                            imgUrl = imgCustomUrl.replace("{x}", String.valueOf(tile[1]))
                                    .replace("{y}", String.valueOf(tile[2]))
                                    .replace("{z}", String.valueOf(tile[0]));
                        } else {
                            imgUrl = MAPTYPE.DAY_MAP.getUrl().replace("{x}", String.valueOf(tile[1]))
                                    .replace("{y}", String.valueOf(tile[2]))
                                    .replace("{z}", String.valueOf(tile[0]));
                        }
                        InputStream in = toRequestImgTile(imgUrl, 3000);
                        System.out.println(imgUrl);
                        //最后用mapbox的再试一下
                        if (in == null) {
                            imgUrl = MAPTYPE.MAPBOX_MAP.getUrl().replace("{x}", String.valueOf(tile[1]))
                                    .replace("{y}", String.valueOf(tile[2]))
                                    .replace("{z}", String.valueOf(tile[0]));
                            in = toRequestImgTile(imgUrl, 3000);
                        }
                        //还是null就没招了
                        if (in == null) return "1";
                        BufferedImage image = ImageIO.read(in);
                        Envelope envelopet = TileUtil.xyz2Envelope2Pixel(tile);
                        int wt = (int) (envelopet.getMinX() - envelope.getMinX());
                        int ht = (int) ((envelopet.getMinY() - envelope.getMinY()));
                        graphics2D.drawImage(image, wt, ht, null);
                        return "133";
                    }
                });
                futureTasks.add(futureTask);
                ThreadPoolUtil.execute(futureTask);
            }

            for (FutureTask<String> futureTask : futureTasks) {
                futureTask.get();
            }
            double pminx = MercatorProjection.longitudeToPixelX(boundingBox.west(), (byte) zoom) - 100 - envelope.getMinX();
            double pmaxX = MercatorProjection.longitudeToPixelX(boundingBox.east(), (byte) zoom) + 100 - envelope.getMinX();
            double pminY = MercatorProjection.latitudeToPixelY(boundingBox.north(), (byte) zoom) - 100 - envelope.getMinY();
            double pmaxY = MercatorProjection.latitudeToPixelY(boundingBox.south(), (byte) zoom) + 100 - envelope.getMinY();
            if (pmaxX - pminx > pmaxY - pminY) {
                double cz = (pmaxX - pminx) - (pmaxY - pminY);
                pmaxY = pmaxY + cz / 2;
                pminY = pminY - cz / 2;
            } else {
                double cz = (pmaxY - pminY) - (pmaxX - pminx);
                pmaxX = pmaxX + cz / 2;
                pminx = pminx - cz / 2;
            }
            graphics2D.setStroke(new BasicStroke(2));
            graphics2D.setFont(font);
            for (int i = 0; i < geometries.length; i++) {
                List<List<Point>> cs = new ArrayList<>();
                Geometry geometries1 = geometries[i];
                if (geometries1 instanceof Polygon) {
                    cs = ((com.mapbox.geojson.Polygon) geometries1).coordinates();
                }
                if (geometries1 instanceof MultiPolygon) {
                    List<List<List<Point>>> csss = ((com.mapbox.geojson.MultiPolygon) geometries1).coordinates();
                    for (List<List<Point>> css : csss) {
                        cs.addAll(css);
                    }
                }
                //做标注避让重叠判断
                List<Envelope> envelopes = new ArrayList<Envelope>();
                for (List<Point> points : cs) {
                    int nPoints = points.size();
                    int[] xPoints = new int[nPoints];
                    int[] yPoints = new int[nPoints];
                    for (int j = 0, len = points.size(); j < len; j++) {
                        Point c = points.get(j);
                        xPoints[j] = (int) ((MercatorProjection.longitudeToPixelX(c.longitude(), zoom)) - envelope.getMinX());
                        yPoints[j] = (int) ((MercatorProjection.latitudeToPixelY(c.latitude(), zoom)) - envelope.getMinY());
                    }
                    //绘制标注需要做避让
                    graphics2D.setColor(new Color(255, 255, 255));
                    List<List<Point>> polyg = new ArrayList<>();
                    polyg.add(points);
                    Polygon polygon = Polygon.fromLngLats(polyg);
                    //得到多边形的最小凸壳多边形
                    com.vividsolutions.jts.geom.Geometry _convexHull = Transformation.wkt2JtsGeometry(Transformation.boxGeometry2Wkt(polygon)).convexHull();
                    Coordinate[] _convexHullcs = _convexHull.getCoordinates();
                    for (int j = 0, len = _convexHullcs.length; j < len; j++) {
                        Coordinate c = _convexHullcs[j];
                        com.mapbox.geojson.Point point = com.mapbox.geojson.Point.fromLngLat(c.x, c.y);
                        com.mapbox.geojson.Point pointCenter = com.mapbox.geojson.Point.fromLngLat(_convexHull.getCentroid().getX(), _convexHull.getCentroid().getY());
                        String anchor = Transformation.calTextAnchor(point, pointCenter);
                        double pixelx = MercatorProjection.longitudeToPixelX(c.x, zoom);
                        double pixely = MercatorProjection.latitudeToPixelY(c.y, zoom);
                        if (anchor.equals("top-right") || anchor.equals("bottom-right")) {
                            double xp1 = pixelx - graphics2D.getFontMetrics().charsWidth((c.x + "").toCharArray(), 0, (c.x + "").length());
                            double yp1 = pixely - graphics2D.getFontMetrics().getHeight() * 2;
                            Envelope e = new Envelope(MercatorProjection.pixelXToLongitude(xp1, zoom), c.x, MercatorProjection.pixelYToLatitude(yp1, zoom), c.y);
                            boolean isIn = false;
                            for (Envelope envelope1 : envelopes) {
                                if (envelope1.intersects(e)) {
                                    isIn = true;
                                    break;
                                }
                            }
                            if (!isIn) {
                                envelopes.add(e);
                                graphics2D.drawString(c.x + "",
                                        (int) (pixelx - envelope.getMinX()) - graphics2D.getFontMetrics().charsWidth((c.x + "").toCharArray(), 0, (c.x + "").length()),
                                        (int) (pixely - envelope.getMinY()));
                                graphics2D.drawString(c.y + "",
                                        (int) (pixelx - envelope.getMinX()) - graphics2D.getFontMetrics().charsWidth((c.x + "").toCharArray(), 0, (c.x + "").length()),
                                        (int) (pixely - envelope.getMinY()) + graphics2D.getFontMetrics().getHeight());
                            }

                        }
                        if (anchor.equals("top-left") || anchor.equals("bottom-left")) {
                            double xp1 = pixelx + graphics2D.getFontMetrics().charsWidth((c.x + "").toCharArray(), 0, (c.x + "").length());
                            double yp1 = pixely + graphics2D.getFontMetrics().getHeight() * 2;
                            Envelope e = new Envelope(c.x, MercatorProjection.pixelXToLongitude(xp1, zoom), c.y, MercatorProjection.pixelYToLatitude(yp1, zoom));
                            boolean isIn = false;
                            for (Envelope envelope1 : envelopes) {
                                if (envelope1.intersects(e)) {
                                    isIn = true;
                                    break;
                                }
                            }
                            if (!isIn) {
                                envelopes.add(e);

                                graphics2D.drawString(c.x + "",
                                        (int) (pixelx - envelope.getMinX()),
                                        (int) (pixely - envelope.getMinY()));
                                graphics2D.drawString(c.y + "",
                                        (int) (pixelx - envelope.getMinX()),
                                        (int) (pixely - envelope.getMinY()) + graphics2D.getFontMetrics().getHeight());
                            }
                        }
                    }
                    //绘制图形
                    graphics2D.setColor(new Color(247, 22, 3));
                    graphics2D.drawPolygon(xPoints, yPoints, nPoints);
                }

            }
            graphics2D.dispose();
            finalImg.flush();

            //把图片裁剪成最佳状态 即：地块加坐标点的外接矩形为截图的边界

            BufferedImage bufferedImageCut = null;
            //写入图像内容
            bufferedImageCut = new BufferedImage(new Double(pmaxX - pminx).intValue(), new Double(pmaxY - pminY).intValue(), finalImg.getType());
            Graphics2D gr = bufferedImageCut.createGraphics();
            gr.setFont(font);
            gr.drawImage(finalImg, 0, 0,
                    new Double(pmaxX - pminx).intValue(), new Double(pmaxY - pminY).intValue(),
                    new Double(pminx).intValue(),
                    new Double(pminY).intValue(),
                    new Double(pmaxX).intValue(), new Double(pmaxY).intValue(),
                    null);
            gr.setStroke(new BasicStroke(2));
            //绘制水印默认左下角由下到上绘制
            if (waters != null) {
                gr.setColor(Color.white);
                gr.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                gr.setStroke(new BasicStroke(100.0f));
                for (int i = 0; i < waters.size(); i++) {
                    gr.drawString(waters.get(i), 5, (bufferedImageCut.getHeight() - 5) - gr.getFontMetrics().getHeight() * i);
                }
            }
            gr.dispose();
            bufferedImageCut.flush();
            return bufferedImageCut;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } finally {
        }
    }

    public static BufferedImage createMapScreenshot(String wkt, List<String> waters, final String imgCustomUrl,final String imgCustomUrl2) {
        try {
            Geometry[] geometries = new Geometry[1];
            geometries[0] = Transformation.wkt2BoxGeometry(wkt);
            BoundingBox boundingBox = Transformation.mergeBoxFeatureBound(1.1, geometries);
            //Envelope envelope =TileUtil.bound2PixelEnvelope(boundingBox,16);
            //确定截图的级别，预览图截图接近1024*768 坐落图接近256*256
            int[] arr = new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17};
            //int[] arr = new int[] { 16 };
            List<long[]> tiles = searchZoomTilesByArea(arr, boundingBox, 0.6);
            final Envelope envelope = TileUtil.xyzs2Envelope2Pixel(tiles);
            int w = (int) (envelope.getMaxX() - envelope.getMinX());
            int h = (int) ((envelope.getMaxY() - envelope.getMinY()));
            BufferedImage finalImg = new BufferedImage(w, h, 1);
            final Graphics2D graphics2D = finalImg.createGraphics();
            Font font = new Font("方正黑体", Font.BOLD, 12);
            graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            byte zoom = (byte) tiles.get(0)[0];
            List<FutureTask<String>> futureTasks = new ArrayList<>();
            for (final long[] tile : tiles) {
                FutureTask<String> futureTask = new FutureTask<String>(new Callable<String>() {
                    @Override
                    public String call() throws Exception {
                        String imgUrl = null;
                        if (imgCustomUrl != null && !"".equals(imgCustomUrl)) {
                            imgUrl = imgCustomUrl.replace("{x}", String.valueOf(tile[1]))
                                    .replace("{y}", String.valueOf(tile[2]))
                                    .replace("{z}", String.valueOf(tile[0]));
                        } else {
                            imgUrl = MAPTYPE.DAY_MAP.getUrl().replace("{x}", String.valueOf(tile[1]))
                                    .replace("{y}", String.valueOf(tile[2]))
                                    .replace("{z}", String.valueOf(tile[0]));
                        }
                        InputStream in = toRequestImgTile(imgUrl, 3000);
                        System.out.println(imgUrl);
                        //最后用mapbox的再试一下
                        if (in == null) {
                            imgUrl = MAPTYPE.MAPBOX_MAP.getUrl().replace("{x}", String.valueOf(tile[1]))
                                    .replace("{y}", String.valueOf(tile[2]))
                                    .replace("{z}", String.valueOf(tile[0]));
                            in = toRequestImgTile(imgUrl, 3000);
                        }
                        //还是null就没招了
                        if (in == null) return "1";
                        BufferedImage image = ImageIO.read(in);
                        Envelope envelopet = TileUtil.xyz2Envelope2Pixel(tile);
                        int wt = (int) (envelopet.getMinX() - envelope.getMinX());
                        int ht = (int) ((envelopet.getMinY() - envelope.getMinY()));
                        graphics2D.drawImage(image, wt, ht, null);
                        //上面一层影像
                        imgUrl = null;
                        if (imgCustomUrl2 != null && !"".equals(imgCustomUrl2)) {
                            imgUrl = imgCustomUrl2.replace("{x}", String.valueOf(tile[1]))
                                    .replace("{y}", String.valueOf(tile[2]))
                                    .replace("{z}", String.valueOf(tile[0]));
                            in = toRequestImgTile(imgUrl, 3000);
                            //还是null就没招了
                            if (in == null) return "1";
                            image = ImageIO.read(in);
                            envelopet = TileUtil.xyz2Envelope2Pixel(tile);
                            wt = (int) (envelopet.getMinX() - envelope.getMinX());
                            ht = (int) ((envelopet.getMinY() - envelope.getMinY()));
                            graphics2D.drawImage(image, wt, ht, null);
                        }
                        return "133";
                    }
                });
                futureTasks.add(futureTask);
                ThreadPoolUtil.execute(futureTask);
            }

            for (FutureTask<String> futureTask : futureTasks) {
                futureTask.get();
            }
            double pminx = MercatorProjection.longitudeToPixelX(boundingBox.west(), (byte) zoom) - 100 - envelope.getMinX();
            double pmaxX = MercatorProjection.longitudeToPixelX(boundingBox.east(), (byte) zoom) + 100 - envelope.getMinX();
            double pminY = MercatorProjection.latitudeToPixelY(boundingBox.north(), (byte) zoom) - 100 - envelope.getMinY();
            double pmaxY = MercatorProjection.latitudeToPixelY(boundingBox.south(), (byte) zoom) + 100 - envelope.getMinY();
            if (pmaxX - pminx > pmaxY - pminY) {
                double cz = (pmaxX - pminx) - (pmaxY - pminY);
                pmaxY = pmaxY + cz / 2;
                pminY = pminY - cz / 2;
            } else {
                double cz = (pmaxY - pminY) - (pmaxX - pminx);
                pmaxX = pmaxX + cz / 2;
                pminx = pminx - cz / 2;
            }
            graphics2D.setStroke(new BasicStroke(2));
            graphics2D.setFont(font);
            for (int i = 0; i < geometries.length; i++) {
                List<List<Point>> cs = new ArrayList<>();
                Geometry geometries1 = geometries[i];
                if (geometries1 instanceof Polygon) {
                    cs = ((com.mapbox.geojson.Polygon) geometries1).coordinates();
                }
                if (geometries1 instanceof MultiPolygon) {
                    List<List<List<Point>>> csss = ((com.mapbox.geojson.MultiPolygon) geometries1).coordinates();
                    for (List<List<Point>> css : csss) {
                        cs.addAll(css);
                    }
                }
                //做标注避让重叠判断
                List<Envelope> envelopes = new ArrayList<Envelope>();
                for (List<Point> points : cs) {
                    int nPoints = points.size();
                    int[] xPoints = new int[nPoints];
                    int[] yPoints = new int[nPoints];
                    for (int j = 0, len = points.size(); j < len; j++) {
                        Point c = points.get(j);
                        xPoints[j] = (int) ((MercatorProjection.longitudeToPixelX(c.longitude(), zoom)) - envelope.getMinX());
                        yPoints[j] = (int) ((MercatorProjection.latitudeToPixelY(c.latitude(), zoom)) - envelope.getMinY());
                    }
                    //绘制标注需要做避让
                    graphics2D.setColor(new Color(255, 255, 255));
                    List<List<Point>> polyg = new ArrayList<>();
                    polyg.add(points);
                    Polygon polygon = Polygon.fromLngLats(polyg);
                    //得到多边形的最小凸壳多边形
                    com.vividsolutions.jts.geom.Geometry _convexHull = Transformation.wkt2JtsGeometry(Transformation.boxGeometry2Wkt(polygon)).convexHull();
                    Coordinate[] _convexHullcs = _convexHull.getCoordinates();
                    for (int j = 0, len = _convexHullcs.length; j < len; j++) {
                        Coordinate c = _convexHullcs[j];
                        com.mapbox.geojson.Point point = com.mapbox.geojson.Point.fromLngLat(c.x, c.y);
                        com.mapbox.geojson.Point pointCenter = com.mapbox.geojson.Point.fromLngLat(_convexHull.getCentroid().getX(), _convexHull.getCentroid().getY());
                        String anchor = Transformation.calTextAnchor(point, pointCenter);
                        double pixelx = MercatorProjection.longitudeToPixelX(c.x, zoom);
                        double pixely = MercatorProjection.latitudeToPixelY(c.y, zoom);
                        if (anchor.equals("top-right") || anchor.equals("bottom-right")) {
                            double xp1 = pixelx - graphics2D.getFontMetrics().charsWidth((c.x + "").toCharArray(), 0, (c.x + "").length());
                            double yp1 = pixely - graphics2D.getFontMetrics().getHeight() * 2;
                            Envelope e = new Envelope(MercatorProjection.pixelXToLongitude(xp1, zoom), c.x, MercatorProjection.pixelYToLatitude(yp1, zoom), c.y);
                            boolean isIn = false;
                            for (Envelope envelope1 : envelopes) {
                                if (envelope1.intersects(e)) {
                                    isIn = true;
                                    break;
                                }
                            }
                            if (!isIn) {
                                envelopes.add(e);
                                graphics2D.drawString(c.x + "",
                                        (int) (pixelx - envelope.getMinX()) - graphics2D.getFontMetrics().charsWidth((c.x + "").toCharArray(), 0, (c.x + "").length()),
                                        (int) (pixely - envelope.getMinY()));
                                graphics2D.drawString(c.y + "",
                                        (int) (pixelx - envelope.getMinX()) - graphics2D.getFontMetrics().charsWidth((c.x + "").toCharArray(), 0, (c.x + "").length()),
                                        (int) (pixely - envelope.getMinY()) + graphics2D.getFontMetrics().getHeight());
                            }

                        }
                        if (anchor.equals("top-left") || anchor.equals("bottom-left")) {
                            double xp1 = pixelx + graphics2D.getFontMetrics().charsWidth((c.x + "").toCharArray(), 0, (c.x + "").length());
                            double yp1 = pixely + graphics2D.getFontMetrics().getHeight() * 2;
                            Envelope e = new Envelope(c.x, MercatorProjection.pixelXToLongitude(xp1, zoom), c.y, MercatorProjection.pixelYToLatitude(yp1, zoom));
                            boolean isIn = false;
                            for (Envelope envelope1 : envelopes) {
                                if (envelope1.intersects(e)) {
                                    isIn = true;
                                    break;
                                }
                            }
                            if (!isIn) {
                                envelopes.add(e);

                                graphics2D.drawString(c.x + "",
                                        (int) (pixelx - envelope.getMinX()),
                                        (int) (pixely - envelope.getMinY()));
                                graphics2D.drawString(c.y + "",
                                        (int) (pixelx - envelope.getMinX()),
                                        (int) (pixely - envelope.getMinY()) + graphics2D.getFontMetrics().getHeight());
                            }
                        }
                    }
                    //绘制图形
                    graphics2D.setColor(new Color(247, 22, 3));
                    graphics2D.drawPolygon(xPoints, yPoints, nPoints);
                }

            }
            graphics2D.dispose();
            finalImg.flush();

            //把图片裁剪成最佳状态 即：地块加坐标点的外接矩形为截图的边界

            BufferedImage bufferedImageCut = null;
            //写入图像内容
            bufferedImageCut = new BufferedImage(new Double(pmaxX - pminx).intValue(), new Double(pmaxY - pminY).intValue(), finalImg.getType());
            Graphics2D gr = bufferedImageCut.createGraphics();
            gr.setFont(font);
            gr.drawImage(finalImg, 0, 0,
                    new Double(pmaxX - pminx).intValue(), new Double(pmaxY - pminY).intValue(),
                    new Double(pminx).intValue(),
                    new Double(pminY).intValue(),
                    new Double(pmaxX).intValue(), new Double(pmaxY).intValue(),
                    null);
            gr.setStroke(new BasicStroke(2));
            //绘制水印默认左下角由下到上绘制
            if (waters != null) {
                gr.setColor(Color.white);
                gr.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                gr.setStroke(new BasicStroke(100.0f));
                for (int i = 0; i < waters.size(); i++) {
                    gr.drawString(waters.get(i), 5, (bufferedImageCut.getHeight() - 5) - gr.getFontMetrics().getHeight() * i);
                }
            }
            gr.dispose();
            bufferedImageCut.flush();
            return bufferedImageCut;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } finally {
        }
    }
    public static List<Object> createMapScreenshot(List<String> wkts, ScreenshotsSetting screenshotsSetting) {
        List<Feature> features = new ArrayList<>();
        for (String wkt : wkts) {
            features.add(Feature.fromGeometry(Transformation.wkt2BoxGeometry(wkt)));
        }
        FeatureCollection featureCollection = FeatureCollection.fromFeatures(features);
        return createMapScreenshots(featureCollection, null, screenshotsSetting);
    }

    public static List<Object> createMapScreenshots(String featureCollectionJson, List<String> waters, String imgCustomUrl, ScreenshotsSetting screenshotsSetting) {
        FeatureCollection featureCollection = FeatureCollection.fromJson(featureCollectionJson);
        List<ScreenshotWaterSetting> waterSettings = new ArrayList<>();
        for (String water : waters) {
            ScreenshotWaterSetting screenshotWaterSetting = new ScreenshotWaterSetting();
            screenshotWaterSetting.setWaterMsg(water);
            screenshotWaterSetting.setFont(screenshotsSetting.getFont());
            waterSettings.add(screenshotWaterSetting);
        }
        screenshotsSetting.setLeftBottomWaters(waterSettings);
        return createMapScreenshots(featureCollection, imgCustomUrl, screenshotsSetting);
    }

    /**
     * 多地块生成坐落图
     *
     * @param featureCollection
     * @param waters
     * @param imgCustomUrl
     * @return
     */
    public static List<Object> createMapScreenshots(FeatureCollection featureCollection, final String imgCustomUrl, ScreenshotsSetting screenshotsSetting) {
        try {
            Geometry[] geometries = new Geometry[featureCollection.features().size()];
            List<Feature> features = featureCollection.features();
            for (int i = 0; i < features.size(); ++i) {
                geometries[i] = features.get(i).geometry();
            }
            BoundingBox boundingBox = Transformation.mergeBoxFeatureBound(1.02D, geometries);
            int[] arr = new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17};
            //List<long[]> tiles = TileUtil.getBoundTileV2(boundingBox.west(), boundingBox.south(), boundingBox.east(), boundingBox.north(), 15);
            //计算瓦片
         //   List<long[]> tiles = searchZoomTilesByArea(arr, boundingBox, screenshotsSetting.getTileDensity());
            List<Double> pixelList = new ArrayList<>();
            //List<long[]> tiles = TileUtil.getBoundTileV2(boundingBox.west(), boundingBox.south(), boundingBox.east(), boundingBox.north(), 15);
            //计算瓦片
            List<long[]> tiles = searchZoomTilesByAreaV2(screenshotsSetting,arr, boundingBox,pixelList);
            Envelope envelope = TileUtil.xyzs2Envelope2Pixel(tiles);
            int w = (int) (envelope.getMaxX() - envelope.getMinX());
            int h = (int) (envelope.getMaxY() - envelope.getMinY());
            BufferedImage finalImg = new BufferedImage(w, h, 1);
            Graphics2D graphics2D = finalImg.createGraphics();
            graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            if (screenshotsSetting.getBackgroundColor() != null) {
                graphics2D.setBackground(screenshotsSetting.getBackgroundColor());
                graphics2D.clearRect(0, 0, finalImg.getWidth(), finalImg.getHeight());
            }

            if (screenshotsSetting.isDrawImageMap()) {
                //绘制影像
                List<FutureTask<String>> futureTasks = drawTileImageAsync(tiles, imgCustomUrl, envelope, graphics2D, screenshotsSetting);
                for (FutureTask<String> futureTask : futureTasks) {
                    futureTask.get();
                }
            }
            List<String> otherImageUrls = screenshotsSetting.getOtherImageUrls();
            for (String otherImageUrl : otherImageUrls) {
                //绘制影像
                List<FutureTask<String>> futureTasksOther = drawTileImageAsync(tiles, otherImageUrl, envelope, graphics2D, screenshotsSetting);
                for (FutureTask<String> futureTask : futureTasksOther) {
                    futureTask.get();
                }
            }
            //绘制图形
            byte zoom = (byte) tiles.get(0)[0];
            int padding = 0;
            drawPolygon(geometries, screenshotsSetting, graphics2D, envelope, zoom, screenshotsSetting.isDrawPolygonMark(), null);
            if (screenshotsSetting.isDrawRect()) {
                //绘制外框以及四至坐标点
                padding = drawRectAndSzzb(boundingBox, graphics2D, zoom, envelope);
            }
            drawWatersByScreenshotWaterSetting(graphics2D, screenshotsSetting.getEnvBottomWaters(), ScreenshotWaterSetting.WaterAnchor.ENV_BOTTOM, envelope, boundingBox, zoom, finalImg);
            drawWatersByScreenshotWaterSetting(graphics2D, screenshotsSetting.getEnvTopWaters(), ScreenshotWaterSetting.WaterAnchor.ENV_TOP, envelope, boundingBox, zoom, finalImg);
            drawWatersByScreenshotWaterSetting(graphics2D, screenshotsSetting.getEnvLeftWaters(), ScreenshotWaterSetting.WaterAnchor.ENV_LEFT, envelope, boundingBox, zoom, finalImg);
            drawWatersByScreenshotWaterSetting(graphics2D, screenshotsSetting.getEnvRightWaters(), ScreenshotWaterSetting.WaterAnchor.ENV_RIGHT, envelope, boundingBox, zoom, finalImg);
            //对完成的图片进行裁剪，保证图形的位置在图片的中心
            List<Object> objects = null;
            if(screenshotsSetting.getWidth() == 0 || pixelList.size() == 0){
                //对完成的图片进行裁剪，保证图形的位置在图片的中心
                objects = cutImageWithPolygonCenter(boundingBox, zoom, envelope, finalImg, padding + screenshotsSetting.getMargin());
            }else {
                objects = cutImageWithPolygonCenterCustom(boundingBox, zoom, envelope, finalImg, screenshotsSetting,pixelList);
            }
            BufferedImage bufferedImageCut = (BufferedImage) objects.get(0);
            Graphics2D gr = (Graphics2D) objects.get(1);
            //绘制水印
            // drawWaters(gr, screenshotsSetting, waters, bufferedImageCut);
            drawWatersByScreenshotWaterSetting(gr, screenshotsSetting.getLeftTopWaters(), ScreenshotWaterSetting.WaterAnchor.LEFT_TOP, envelope, boundingBox, zoom, bufferedImageCut);
            drawWatersByScreenshotWaterSetting(gr, screenshotsSetting.getLeftBottomWaters(), ScreenshotWaterSetting.WaterAnchor.LEFT_BOTTOM, envelope, boundingBox, zoom, bufferedImageCut);
            drawWatersByScreenshotWaterSetting(gr, screenshotsSetting.getRightTopWaters(), ScreenshotWaterSetting.WaterAnchor.RIGHT_TOP, envelope, boundingBox, zoom, bufferedImageCut);
            drawWatersByScreenshotWaterSetting(gr, screenshotsSetting.getRightBottomWaters(), ScreenshotWaterSetting.WaterAnchor.RIGHT_BOTTOM, envelope, boundingBox, zoom, bufferedImageCut);
            //压缩图片
            BufferedImage bufferedImage = Thumbnails.of(bufferedImageCut).scale(screenshotsSetting.getImageScale())
                    .outputQuality(screenshotsSetting.getImageQuality()).asBufferedImage();
            //回收
            graphics2D.dispose();
            gr.dispose();
            finalImg.flush();
            bufferedImageCut.flush();
            bufferedImage.flush();
            List<Object> r = new ArrayList<>();
            r.add(bufferedImage);
            r.add(objects.get(2));
            return r;
        } catch (Exception var42) {
            var42.printStackTrace();
            throw new RuntimeException(var42);
        } finally {
            ;
        }
    }

    private static List<long[]>searchZoomTilesByAreaV2(ScreenshotsSetting screenshotsSetting,int[] arr,BoundingBox boundingBox,List<Double> pixelList){
        List<long[]> lastTiles = new ArrayList<>();
        int width = screenshotsSetting.getWidth();
        int height = screenshotsSetting.getHeight();
        int margin = screenshotsSetting.getMargin();

        if(width == 0){
            return searchZoomTilesByArea(arr,boundingBox,screenshotsSetting.getTileDensity());
        }
        byte b = -1 ;
        for (int i = arr.length - 1; i >= 0; i--) {
            long zoom = arr[i];
            b = (byte) zoom;
            double pminx = MercatorProjection.longitudeToPixelX(boundingBox.west(), b)-margin;
            double pmaxX = MercatorProjection.longitudeToPixelX(boundingBox.east(), b)+margin;
            double pminY = MercatorProjection.latitudeToPixelY(boundingBox.north(), b)-margin;
            double pmaxY = MercatorProjection.latitudeToPixelY(boundingBox.south(), b)+margin;
            double xwidth = pmaxX - pminx ;
            double yheight = pmaxY - pminY ;
            if(xwidth < width && yheight < height ){
                double buffx = (width - xwidth)/2;
                double buffy = (height - yheight)/2;
                lastTiles = TileUtil.getBoundTileV3(pminx-buffx, pmaxX+buffx, pminY-buffy, pmaxY+buffy, zoom);
                pixelList.add(pminx-buffx);
                pixelList.add(pmaxX+buffx);
                pixelList.add(pminY-buffy);
                pixelList.add(pmaxY+buffy);
                break;
            }else {
                b = -1;
            }
        }
        if(-1 == b){
            return searchZoomTilesByArea(arr,boundingBox,screenshotsSetting.getTileDensity());
        }
        return lastTiles;
    }

    /**
     * 多地块生成坐落图
     *
     * @param wktInfos
     * @param waters
     * @param imgCustomUrl
     * @return
     */
    public static BufferedImage createMapScreenshotsByWktInfo(List<WktInfo> wktInfos, List<String> waters, final String imgCustomUrl, Font font) {
        try {

            ScreenshotsSetting screenshotsSetting = new ScreenshotsSetting();
            screenshotsSetting.setImageScale(1);
            screenshotsSetting.setFont(font);
            Geometry[] geometries = new Geometry[wktInfos.size()];

            for (int i = 0; i < wktInfos.size(); ++i) {
                String wkt = wktInfos.get(i).getWkt();
                Geometry geometry = Transformation.wkt2BoxGeometry(wkt);
                geometries[i] = geometry;
            }
            BoundingBox boundingBox = Transformation.mergeBoxFeatureBound(1.02D, geometries);

            int[] arr = new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17};
            //List<long[]> tiles = TileUtil.getBoundTileV2(boundingBox.west(), boundingBox.south(), boundingBox.east(), boundingBox.north(), 15);
            //计算瓦片
            List<long[]> tiles = searchZoomTilesByArea(arr, boundingBox, screenshotsSetting.getTileDensity());
            Envelope envelope = TileUtil.xyzs2Envelope2Pixel(tiles);
            int w = (int) (envelope.getMaxX() - envelope.getMinX());
            int h = (int) (envelope.getMaxY() - envelope.getMinY());
            BufferedImage finalImg = new BufferedImage(w, h, 1);
            Graphics2D graphics2D = finalImg.createGraphics();
            graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            //绘制影像
            List<FutureTask<String>> futureTasks = drawTileImageAsync(tiles, imgCustomUrl, envelope, graphics2D, screenshotsSetting);
            for (FutureTask<String> futureTask : futureTasks) {
                futureTask.get();
            }
            //绘制图形
            byte zoom = (byte) tiles.get(0)[0];
            for (int i = 0; i < wktInfos.size(); ++i) {
                Geometry[] geometrieStr = new Geometry[1];
                geometrieStr[0] = geometries[i];
                screenshotsSetting.setColor(wktInfos.get(i).getColor());
                screenshotsSetting.setFillColor(null);
                drawPolygon(geometrieStr, screenshotsSetting, graphics2D, envelope, zoom, wktInfos.get(i).isMark(), wktInfos.get(i).getCenterSymbol());
            }
            //绘制外框以及四至坐标点
            // drawRectAndSzzb(boundingBox,graphics2D,zoom,envelope);
            //对完成的图片进行裁剪，保证图形的位置在图片的中心
            List<Object> objects = cutImageWithPolygonCenter(boundingBox, zoom, envelope, finalImg, 0);
            BufferedImage bufferedImageCut = (BufferedImage) objects.get(0);
            Graphics2D gr = (Graphics2D) objects.get(1);
            //绘制水印
            drawWaters(gr, screenshotsSetting, waters, bufferedImageCut);
            //压缩图片
            BufferedImage bufferedImage = Thumbnails.of(bufferedImageCut).scale(screenshotsSetting.getImageScale())
                    .outputQuality(screenshotsSetting.getImageQuality()).asBufferedImage();
            //回收
            graphics2D.dispose();
            gr.dispose();
            finalImg.flush();
            bufferedImageCut.flush();
            bufferedImage.flush();
            return bufferedImage;
        } catch (Exception var42) {
            var42.printStackTrace();
            throw new RuntimeException(var42);
        } finally {
            ;
        }
    }

    public static BufferedImage createMapScreenshot(String wkt, List<String> waters, boolean showZb, String tileUrl, String token) {
        try {
            Geometry[] geometries = new Geometry[1];
            geometries[0] = Transformation.wkt2BoxGeometry(wkt);
            BoundingBox boundingBox = Transformation.mergeBoxFeatureBound(1.1, geometries);
            //Envelope envelope =TileUtil.bound2PixelEnvelope(boundingBox,16);
            //确定截图的级别，预览图截图接近1024*768 坐落图接近256*256
            int[] arr = new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17};
            List<long[]> tiles = searchZoomTiles(arr, boundingBox, 4);
            Envelope envelope = TileUtil.xyzs2Envelope2Pixel(tiles);

            int w = (int) (envelope.getMaxX() - envelope.getMinX());
            int h = (int) ((envelope.getMaxY() - envelope.getMinY()));
            BufferedImage finalImg = new BufferedImage(w, h, 1);
            Graphics2D graphics2D = finalImg.createGraphics();
            graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            for (long[] tile : tiles) {
                String imgUrl = MAPTYPE.DAY_MAP.getUrl().replace("{x}", String.valueOf(tile[1]))
                        .replace("{y}", String.valueOf(tile[2]))
                        .replace("{z}", String.valueOf(tile[0]));
                InputStream in = toRequestImgTile(imgUrl, 3000);
              /*  if (in == null) {
                    imgUrl = MAPTYPE.MAPBOX_MAP.getUrl().replace("{x}", String.valueOf(tile[1]))
                            .replace("{y}", String.valueOf(tile[2]))
                            .replace("{z}", String.valueOf(tile[0]));
                    in = toRequestImgTile(imgUrl, 3000);
                }*/
                if (in == null) continue;
                BufferedImage image = ImageIO.read(in);
                Envelope envelopet = TileUtil.xyz2Envelope2Pixel(tile);
                int wt = (int) (envelopet.getMinX() - envelope.getMinX());
                int ht = (int) ((envelopet.getMinY() - envelope.getMinY()));
                graphics2D.drawImage(image, wt, ht, null);

                imgUrl = tileUrl.replace("{x}", String.valueOf(tile[1]))
                        .replace("{y}", String.valueOf(tile[2]))
                        .replace("{z}", String.valueOf(tile[0]));
                in = getImgTileByAuthorization(imgUrl, token);
                if (in == null) continue;
                image = ImageIO.read(in);
                envelopet = TileUtil.xyz2Envelope2Pixel(tile);
                wt = (int) (envelopet.getMinX() - envelope.getMinX());
                ht = (int) ((envelopet.getMinY() - envelope.getMinY()));
                graphics2D.drawImage(image, wt, ht, null);
            }


            graphics2D.setStroke(new BasicStroke(2));
            for (int i = 0; i < geometries.length; i++) {
                com.vividsolutions.jts.geom.Geometry geom = Transformation.wkt2JtsGeometry(Transformation.boxGeometry2Wkt(geometries[i]));
                int nPoints = geom.getNumPoints();
                int[] xPoints = new int[nPoints];
                int[] yPoints = new int[nPoints];
                Coordinate[] cs = geom.getCoordinates();
                byte zoom = (byte) tiles.get(0)[0];
                Coordinate c;
                for (int j = 0, len = cs.length; j < len; j++) {
                    c = cs[j];
                    xPoints[j] = (int) ((MercatorProjection.longitudeToPixelX(c.x, zoom)) - envelope.getMinX());
                    yPoints[j] = (int) ((MercatorProjection.latitudeToPixelY(c.y, zoom)) - envelope.getMinY());
                }

                //绘制标注需要做避让
                graphics2D.setColor(new Color(255, 255, 255));

                if (showZb) {
                    //得到多边形的最小凸壳多边形
                    com.vividsolutions.jts.geom.Geometry _convexHull = geom.convexHull();
                    cs = _convexHull.getCoordinates();
                    List<Envelope> envelopes = new ArrayList<Envelope>();
                    for (int j = 0, len = cs.length; j < len; j++) {
                        c = cs[j];
                        com.mapbox.geojson.Point point = com.mapbox.geojson.Point.fromLngLat(c.x, c.y);
                        com.mapbox.geojson.Point pointCenter = com.mapbox.geojson.Point.fromLngLat(_convexHull.getCentroid().getX(), _convexHull.getCentroid().getY());
                        String anchor = Transformation.calTextAnchor(point, pointCenter);
                        double pixelx = MercatorProjection.longitudeToPixelX(c.x, zoom);
                        double pixely = MercatorProjection.latitudeToPixelY(c.y, zoom);
                        if (anchor.equals("top-right") || anchor.equals("bottom-right")) {
                            // GeometryFactory.toLinearRingArray()
                            double xp1 = pixelx - graphics2D.getFontMetrics().charsWidth((c.x + "").toCharArray(), 0, (c.x + "").length());
                            double yp1 = pixely - graphics2D.getFontMetrics().getHeight();
                            if (envelopes.size() == 0) {
                                Envelope e = new Envelope(MercatorProjection.pixelXToLongitude(xp1, zoom), c.x, MercatorProjection.pixelYToLatitude(yp1, zoom), c.y);
                                envelopes.add(e);
                                graphics2D.drawString(c.x + "",
                                        (int) (pixelx - envelope.getMinX()) - graphics2D.getFontMetrics().charsWidth((c.x + "").toCharArray(), 0, (c.x + "").length()),
                                        (int) (pixely - envelope.getMinY()));
                                graphics2D.drawString(c.y + "",
                                        (int) (pixelx - envelope.getMinX()) - graphics2D.getFontMetrics().charsWidth((c.x + "").toCharArray(), 0, (c.x + "").length()),
                                        (int) (pixely - envelope.getMinY()) + graphics2D.getFontMetrics().getHeight());
                            } else {
                                Envelope e = new Envelope(MercatorProjection.pixelXToLongitude(xp1, zoom), c.x, MercatorProjection.pixelYToLatitude(yp1, zoom), c.y);
                                boolean isIn = false;
                                for (Envelope envelope1 : envelopes) {
                                    if (envelope1.intersects(e)) {
                                        isIn = true;
                                        break;
                                    }

                                }
                                if (!isIn) {
                                    envelopes.add(e);
                                    graphics2D.drawString(c.x + "",
                                            (int) (pixelx - envelope.getMinX()) - graphics2D.getFontMetrics().charsWidth((c.x + "").toCharArray(), 0, (c.x + "").length()),
                                            (int) (pixely - envelope.getMinY()));
                                    graphics2D.drawString(c.y + "",
                                            (int) (pixelx - envelope.getMinX()) - graphics2D.getFontMetrics().charsWidth((c.x + "").toCharArray(), 0, (c.x + "").length()),
                                            (int) (pixely - envelope.getMinY()) + graphics2D.getFontMetrics().getHeight());
                                }
                            }
                        }
                        if (anchor.equals("top-left") || anchor.equals("bottom-left")) {
                            double xp1 = pixelx + graphics2D.getFontMetrics().charsWidth((c.x + "").toCharArray(), 0, (c.x + "").length());
                            double yp1 = pixely + graphics2D.getFontMetrics().getHeight();
                            if (envelopes.size() == 0) {
                                Envelope e = new Envelope(c.x, MercatorProjection.pixelXToLongitude(xp1, zoom), c.y, MercatorProjection.pixelYToLatitude(yp1, zoom));
                                envelopes.add(e);
                                graphics2D.drawString(c.x + "",
                                        (int) (pixelx - envelope.getMinX()),
                                        (int) (pixely - envelope.getMinY()));
                                graphics2D.drawString(c.y + "",
                                        (int) (pixelx - envelope.getMinX()),
                                        (int) (pixely - envelope.getMinY()) + graphics2D.getFontMetrics().getHeight());
                            } else {
                                Envelope e = new Envelope(c.x, MercatorProjection.pixelXToLongitude(xp1, zoom), c.y, MercatorProjection.pixelYToLatitude(yp1, zoom));
                                boolean isIn = false;
                                for (Envelope envelope1 : envelopes) {
                                    if (envelope1.intersects(e)) {
                                        isIn = true;
                                        break;
                                    }

                                }
                                if (!isIn) {
                                    envelopes.add(e);
                                    graphics2D.drawString(c.x + "",
                                            (int) (pixelx - envelope.getMinX()),
                                            (int) (pixely - envelope.getMinY()));
                                    graphics2D.drawString(c.y + "",
                                            (int) (pixelx - envelope.getMinX()),
                                            (int) (pixely - envelope.getMinY()) + graphics2D.getFontMetrics().getHeight());
                                }
                            }

                        }

                    }
                }
                //绘制图形
                graphics2D.setColor(new Color(28, 247, 78));
                if (geometries[0] instanceof com.mapbox.geojson.Polygon) {
                    graphics2D.drawPolygon(xPoints, yPoints, nPoints);
                }
                if (geometries[0] instanceof LineString) {
                    graphics2D.drawPolyline(xPoints, yPoints, nPoints);
                }

            }

            //绘制水印默认左下角由下到上绘制
            if (waters != null) {
                graphics2D.setColor(Color.white);
                graphics2D.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                graphics2D.setStroke(new BasicStroke(100.0f));
                for (int i = 0; i < waters.size(); i++) {
                    graphics2D.drawString(waters.get(i), 5, (h - 5) - graphics2D.getFontMetrics().getHeight() * i);
                }
            }

            finalImg.flush();
            return finalImg;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {

        }
    }

    public static List<long[]> searchZoomTiles(int[] arr, BoundingBox boundingBox, int key) {
        List<long[]> lastTiles = new ArrayList<>();
        for (int i = arr.length - 1; i >= 0; i--) {
            int zoom = arr[i];
            List<long[]> tiles = TileUtil.getBoundTileV2(boundingBox.west(), boundingBox.south(), boundingBox.east(), boundingBox.north(), zoom);
            if (lastTiles.size() == 0) lastTiles = tiles;
            if (tiles.size() == 1 || tiles.size() == key) {
                return tiles;
            }
            if (Math.abs(tiles.size() - key) <= Math.abs(lastTiles.size() - key)) {
                lastTiles = tiles;
            }
        }
        return lastTiles;
    }

    /**
     * 根据面积计算合适的瓦片
     *
     * @param arr
     * @param boundingBox
     * @param key
     * @return
     */
    public static List<long[]> searchZoomTilesByArea(int[] arr, BoundingBox boundingBox, double key) {
        List<long[]> lastTiles = new ArrayList<>();
        double ratiolast = 0;
        for (int i = arr.length - 1; i >= 0; i--) {
            int zoom = arr[i];
            List<long[]> tiles = TileUtil.getBoundTileV2(boundingBox.west(), boundingBox.south(), boundingBox.east(), boundingBox.north(), zoom);
            Envelope envelope = TileUtil.xyzs2Envelope2Pixel(tiles);
            byte b = (byte) zoom;
            double pminx = MercatorProjection.longitudeToPixelX(boundingBox.west(), b);
            double pmaxX = MercatorProjection.longitudeToPixelX(boundingBox.east(), b);
            double pminY = MercatorProjection.latitudeToPixelY(boundingBox.north(), b);
            double pmaxY = MercatorProjection.latitudeToPixelY(boundingBox.south(), b);
            double pAreaLength = (pmaxX - pminx) > (pmaxY - pminY) ? pmaxX - pminx : pmaxY - pminY;
            double pTileLength = (pmaxX - pminx) > (pmaxY - pminY) ? (envelope.getMaxX() - envelope.getMinX()) : (envelope.getMaxY() - envelope.getMinY());
            System.out.println("级别：" + zoom + "占比：" + (pAreaLength / pTileLength));
            if (lastTiles.size() == 0) {
                ratiolast = pAreaLength / pTileLength;
                lastTiles = tiles;
            }
            if (Math.abs(pAreaLength / pTileLength - key) <= Math.abs(ratiolast - key)) {
                lastTiles = tiles;
                ratiolast = pAreaLength / pTileLength;
            }
        }
        return lastTiles;
    }


    public enum MAPTYPE {
        GOOGLE("img_gle", "https://mt1.google.cn/vt/lyrs=s@113&hl=nl&x={x}&y={y}&z={z}&s=", "谷歌影像"),
        GOOGLE_LABEL("cia_gle", "https://mt1.google.cn/vt/lyrs=s@113&hl=nl&x={x}&y={y}&z={z}&s=", "谷歌标注"),
        DAY_MAP("img_tdt", "https://t0.tianditu.gov.cn/DataServer?T=img_w&x={x}&y={y}&l={z}&tk=40da7abcd3265b23d7b867b4eaf479da", "天地图影像"),
        DAY_MAP_LABEL("cia_tdt", "https://t0.tianditu.gov.cn/DataServer?T=cia_w&x={x}&y={y}&l={z}&tk=40da7abcd3265b23d7b867b4eaf479da", "天地图标注"),
        MAPBOX_MAP("mapbox_map", "https://t0.tianditu.gov.cn/DataServer?T=img_w&x={x}&y={y}&l={z}&tk=26bbd602c5d4476bf6b3400f32b8f841", "mapbox影像");
        private String tableName;
        private String desc;
        private String url;

        MAPTYPE(String tableName, String url, String desc) {
            this.tableName = tableName;
            this.url = url;
            this.desc = desc;
        }

        public String getTableName() {
            return tableName;
        }

        public void setTableName(String tableName) {
            this.tableName = tableName;
        }

        public String getDesc() {
            return desc;
        }

        public void setDesc(String desc) {
            this.desc = desc;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }

    /**
     * 多瓦片并行请求，每个瓦片先请求天地图 6-0，每个2秒，还是不行的话就请求mapbox的影像地图
     *
     * @param imgUrl
     * @return
     */
    public static InputStream toRequestImgTile(String imgUrl, int socketTimeout) {
        InputStream imgTile = null;
       // String imgUrl1 = imgUrl.replace("https://t0.", "https://t" + i + ".");
        imgTile = getImgTile(imgUrl, socketTimeout);
        if (imgTile != null) return imgTile;
       /* for (int i = 6; i >= 0 && i <= 6; i--) {
            String imgUrl1 = imgUrl.replace("https://t0.", "https://t" + i + ".");
            imgTile = getImgTile(imgUrl1, socketTimeout);
            if (imgTile != null) return imgTile;
        }*/
        return null;
    }

    public static InputStream getImgTile(String imgUrl, int socketTimeout) {
        // 发送get请求
        HttpGet request = new HttpGet(imgUrl);

        // 设置请求和传输超时时间
        RequestConfig requestConfig = RequestConfig.custom()
                .setSocketTimeout(socketTimeout).setConnectTimeout(socketTimeout).build();

        //设置请求头
        request.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.1 (KHTML, like Gecko) Chrome/21.0.1180.79 Safari/537.1");
        request.setConfig(requestConfig);


        SSLContext sslContext = null;
        try {
            sslContext = SSLContexts.custom().loadTrustMaterial(null, new TrustStrategy() {
                @Override
                public boolean isTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
                    return true;
                }
            }).build();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        }

        //  CloseableHttpClient client = HttpClients.createDefault();
        HttpResponse response = null;
        try {
            HttpClient client = HttpClients.custom()
                    .setSSLContext(sslContext).
                            setSSLHostnameVerifier(new NoopHostnameVerifier()).build();
            response = client.execute(request);
            if (HttpStatus.SC_OK == response.getStatusLine().getStatusCode()) {
                HttpEntity entity = response.getEntity();
                InputStream in = null;
                try {
                    in = entity.getContent();
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
                System.out.println("请求成功：" + imgUrl);
                return in;

            }
        } catch (Exception e) {
            //e.printStackTrace();

            return null;
        }


        return null;
    }

    public static InputStream getImgTileByAuthorization(String imgUrl, String authorization) throws Exception {
        // 发送get请求
        HttpGet request = new HttpGet(imgUrl);
        // 设置请求和传输超时时间
        RequestConfig requestConfig = RequestConfig.custom()
                .setSocketTimeout(50000).setConnectTimeout(50000).build();

        //设置请求头
        request.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.1 (KHTML, like Gecko) Chrome/21.0.1180.79 Safari/537.1");
        if (authorization != null)
            request.setHeader("Authorization", authorization);
        request.setConfig(requestConfig);
        CloseableHttpClient client = HttpClients.createDefault();
        CloseableHttpResponse response = client.execute(request);

        if (HttpStatus.SC_OK == response.getStatusLine().getStatusCode()) {
            HttpEntity entity = response.getEntity();
            InputStream in = entity.getContent();
            return in;

        }
        return null;
    }

    public static void main(String a[]) throws Exception {

        /*List<String> l = new ArrayList<>();
        l.add("大萨达eqew");
        l.add("大萨达eqew");
        l.add("大萨达eqew");
        l.add("大萨达eqew");
       // BufferedImage finalImg = MapScreenshotTool.createMapScreenshot(
         //       "MULTIPOLYGON(((109.9971562 40.4338734,109.9961748 40.4338339,109.9962363 40.4337055,109.9971588 40.4337352,109.9971562 40.4338734)))", l);
        BufferedImage finalImg = MapScreenshotTool.createMapScreenshot("POLYGON((115.2765027 35.9441251,115.2779709 35.9438517,115.27794 35.9437076,115.277833 35.943024,115.27722 35.9431269,115.2762974 35.9432786,115.2763086 35.9433371,115.2763426 35.943449,115.2764407 35.9438908,115.2765027 35.9441251),(115.2770601 35.9434363,115.277227 35.943193,115.2776419 35.9432934,115.2775084 35.9435406,115.2770601 35.9434363))",l);
        String jarpath = System.getProperty("user.dir");
        String imgpath = jarpath + "\\finalImg1.jpg";
        String imgpath2 = jarpath + "\\finalImg2.jpg";
        ImageIO.write(finalImg, "png", new File(imgpath));
        //zoomImage(imgpath,imgpath2,512,512);
        System.out.println("完成拼接！");*/
        List<String> l = new ArrayList<>();


        l.add("地块编码:2021021507821080012448");
        l.add("投保面积:17.6亩");
        l.add("农户姓名:童金玲");
        // BufferedImage finalImg = MapScreenshotTool.createMapScreenshot(
        //       "MULTIPOLYGON(((109.9971562 40.4338734,109.9961748 40.4338339,109.9962363 40.4337055,109.9971588 40.4337352,109.9971562 40.4338734)))", l);
        List<String> list = new ArrayList<>();
        list.add("POLYGON((115.2765027 35.9441251,115.2779709 35.9438517,115.27794 35.9437076,115.277833 35.943024,115.27722 35.9431269,115.2762974 35.9432786,115.2763086 35.9433371,115.2763426 35.943449,115.2764407 35.9438908,115.2765027 35.9441251),(115.2770601 35.9434363,115.277227 35.943193,115.2776419 35.9432934,115.2775084 35.9435406,115.2770601 35.9434363))");
        //  list.add("MULTIPOLYGON(((109.9971562 40.4338734,109.9961748 40.4338339,109.9962363 40.4337055,109.9971588 40.4337352,109.9971562 40.4338734)))");
        //  BufferedImage finalImg = MapScreenshotTool.createMapScreenshot(list,l,null);
//       BufferedImage finalImg = MapScreenshotTool.createMapScreenshot("POLYGON ((115.3021241 35.9554462, 115.3025061 35.9553684, 115.3025086 35.9553678, 115.3034777 35.9582167, 115.3031039 35.9583201, 115.3021241 35.9554462), (115.3026701 35.9565879, 115.3027016 35.9562056, 115.3030794 35.9573015, 115.3027226 35.9569872, 115.3026701 35.9565879))",l);
        String url = "https://a.tiles.mapbox.com/v4/mapbox.satellite/{z}/{x}/{y}.png?access_token=pk.eyJ1IjoiZ3lraiIsImEiOiJjbDA5YjBjYzIwYmc4M2pwOXd4dHh3Mzc5In0.54AifzzOuqzO5M3f75zt1A";
        BufferedImage finalImg = MapScreenshotTool.createMapScreenshot("LINESTRING (115.276550748 35.942440416, 115.270654552 35.944256564, 115.262360667 35.945977648, 115.254671658 35.947414144, 115.247469329 35.947932924, 115.242156976 35.947307306, 115.236822294 35.945190943, 115.232107166 35.941219461, 115.229294139 35.934153436, 115.228007078 35.926015449, 115.228533785 35.915678081, 115.230473559 35.905470955, 115.234676814 35.894908184, 115.239222927 35.887628444, 115.2455573 35.880804078, 115.251655907 35.876922496, 115.258566274 35.874254304, 115.265720728 35.873264553, 115.273909876 35.874042815, 115.292758028 35.881682167, 115.301033941 35.890343427, 115.306539208 35.89807743, 115.308831572 35.909183445, 115.307586774 35.913980428, 115.303398824 35.921557771, 115.298544504 35.928378465)", null, url);
        String jarpath = System.getProperty("user.dir");
        String imgpath = jarpath + "\\finalImg1.jpg";
        String imgpath2 = jarpath + "\\finalImg2.jpg";
        ImageIO.write(finalImg, "png", new File(imgpath));
        //zoomImage(imgpath,imgpath2,512,512);
        System.out.println("完成拼接！");
      /*  Geometry g = Transformation.wkt2BoxGeometry("MULTIPOLYGON(((114.934402554 36.1468067220001,114.934163197 36.146233889,114.934697973 36.146097105,114.934662278 36.1459971670001,114.934924974 36.145929762,114.935178952 36.1466401000001,114.934402554 36.1468067220001)))");
        MapboxStaticMap staticImage = MapboxStaticMap.builder()
                .accessToken("pk.eyJ1IjoiZnMxOTk1MDMwMSIsImEiOiJjazBiejFlYTcwdDI5M2NveDg5bTJqNzNpIn0.hPoTgotjD-WQr2SLS34Ktw")
                .styleId(StaticMapCriteria.LIGHT_STYLE)
                .
                .cameraPoint(com.mapbox.geojson.Point.fromLngLat(114.934402554, 36.1468067220001)) // Image's centerpoint on map
                .cameraZoom(16).geoJson(g)
                .logo(false)
                .width(180) // Image width
                .height(180) // Image height
                .retina(true) // Retina 2x image will be returned
                .build();
        String imageUrl = staticImage.url().toString();
        System.out.println(imageUrl);*/

      /*  List<StaticMarkerAnnotation> markers = new ArrayList<>();
        List<StaticPolylineAnnotation> polylines = new ArrayList<>();


        markers.add(StaticMarkerAnnotation.builder().name(StaticMapCriteria.LARGE_PIN)
                .lnglat(com.mapbox.geojson.Point.fromLngLat(-122.46589, 37.77343))
                .color(ColorUtils.toHexString(
                        Color.MAGENTA.getRed(),
                        Color.MAGENTA.getGreen(),
                        Color.MAGENTA.getBlue()))
                .label("a").build());

        markers.add(StaticMarkerAnnotation.builder().name(StaticMapCriteria.LARGE_PIN)
                .lnglat(com.mapbox.geojson.Point.fromLngLat(-122.42816,37.75965))
                .color(Color.GREEN.getRed(),
                        Color.GREEN.getGreen(),
                        Color.GREEN.getBlue())
                .label("b")
                .build());

        polylines.add(StaticPolylineAnnotation.builder().polyline("abcdef").build());

        MapboxStaticMap mapboxStaticMap = MapboxStaticMap.builder()
                .accessToken("pk.eyJ1IjoiZnMxOTk1MDMwMSIsImEiOiJjazBiejFlYTcwdDI5M2NveDg5bTJqNzNpIn0.hPoTgotjD-WQr2SLS34Ktw")
                .width(500)
                .height(300)
                .retina(true)
                .cameraAuto(true)
                .staticMarkerAnnotations(markers)
                .staticPolylineAnnotations(polylines)
                .build();

        System.out.println(mapboxStaticMap.url());*/
    }

    /**
     * 改变图片尺寸
     *
     * @param srcFileName 源图片路径
     * @param path        图片修改后路径
     * @param width       修改后的宽度
     * @param height      修改后的高度
     */
    public static void zoomImage(String srcFileName, String path, int width, int height) {
        try {
            BufferedImage bi = ImageIO.read(new File(srcFileName));
            BufferedImage tag = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            tag.getGraphics().drawImage(bi, 0, 0, width, height, null);
            ImageIO.write(tag, "jpg", new File(path));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private static List<FutureTask<String>> drawTileImageAsync(List<long[]> tiles, final String imgCustomUrl, final Envelope envelope, final Graphics2D graphics2D, final ScreenshotsSetting screenshotsSetting) {
        List<FutureTask<String>> futureTasks = new ArrayList<>();
        Iterator<long[]> var12 = tiles.iterator();
        while (var12.hasNext()) {
            final long[] tile = (long[]) var12.next();
            FutureTask<String> futureTask = new FutureTask<String>(new Callable<String>() {
                @Override
                public String call() throws Exception {
                    String imgUrl = null;
                    if (imgCustomUrl != null && !"".equals(imgCustomUrl)) {
                        imgUrl = imgCustomUrl.replace("{x}", String.valueOf(tile[1])).replace("{y}", String.valueOf(tile[2])).replace("{z}", String.valueOf(tile[0]));
                    } else {
                        imgUrl = MapScreenshotTool.MAPTYPE.DAY_MAP.getUrl().replace("{x}", String.valueOf(tile[1])).replace("{y}", String.valueOf(tile[2])).replace("{z}", String.valueOf(tile[0]));
                    }

                    InputStream in = toRequestImgTile(imgUrl, screenshotsSetting.getSocketTimeout());
                    /*if (in == null) {
                        imgUrl = MAPTYPE.MAPBOX_MAP.getUrl().replace("{x}", String.valueOf(tile[1]))
                                .replace("{y}", String.valueOf(tile[2]))
                                .replace("{z}", String.valueOf(tile[0]));
                        in = toRequestImgTile(imgUrl, screenshotsSetting.getSocketTimeout());
                    }*/
                    if (in != null) {
                        try {
                            BufferedImage image = ImageIO.read(in);
                            Envelope envelopet = TileUtil.xyz2Envelope2Pixel(tile);
                            int wt = (int) (envelopet.getMinX() - envelope.getMinX());
                            int ht = (int) (envelopet.getMinY() - envelope.getMinY());
                            graphics2D.drawImage(image, wt, ht, (ImageObserver) null);
                        } catch (Exception e) {
                            System.out.println("ImageIO错误");
                            e.printStackTrace();
                        }
                    }
                    System.out.println("瓦片地址：" + imgUrl);
                    return "133";
                }
            });
            futureTasks.add(futureTask);
            ThreadPoolUtil.execute(futureTask);
        }
        return futureTasks;
    }

    private static void drawMark(Graphics2D graphics2D, Polygon polygon, byte zoom, Envelope envelope) throws Exception {
        List<Envelope> envelopes = new ArrayList<Envelope>();
        //绘制标注需要做避让
        graphics2D.setColor(new Color(255, 255, 255));
        //得到多边形的最小凸壳多边形
        com.vividsolutions.jts.geom.Geometry _convexHull = Transformation.wkt2JtsGeometry(Transformation.boxGeometry2Wkt(polygon)).convexHull();
        Coordinate[] _convexHullcs = _convexHull.getCoordinates();
        for (int j = 0, len = _convexHullcs.length; j < len; j++) {
            Coordinate c = _convexHullcs[j];
            com.mapbox.geojson.Point point = com.mapbox.geojson.Point.fromLngLat(c.x, c.y);
            com.mapbox.geojson.Point pointCenter = com.mapbox.geojson.Point.fromLngLat(_convexHull.getCentroid().getX(), _convexHull.getCentroid().getY());
            String anchor = Transformation.calTextAnchor(point, pointCenter);
            double pixelx = MercatorProjection.longitudeToPixelX(c.x, zoom);
            double pixely = MercatorProjection.latitudeToPixelY(c.y, zoom);
            if (anchor.equals("top-right") || anchor.equals("bottom-right")) {
                double xp1 = pixelx - graphics2D.getFontMetrics().charsWidth((c.x + "").toCharArray(), 0, (c.x + "").length());
                double yp1 = pixely - graphics2D.getFontMetrics().getHeight() * 2;
                Envelope e = new Envelope(MercatorProjection.pixelXToLongitude(xp1, zoom), c.x, MercatorProjection.pixelYToLatitude(yp1, zoom), c.y);
                boolean isIn = false;
                for (Envelope envelope1 : envelopes) {
                    if (envelope1.intersects(e)) {
                        isIn = true;
                        break;
                    }
                }
                if (!isIn) {
                    envelopes.add(e);
                    graphics2D.drawString(c.x + "",
                            (int) (pixelx - envelope.getMinX()) - graphics2D.getFontMetrics().charsWidth((c.x + "").toCharArray(), 0, (c.x + "").length()),
                            (int) (pixely - envelope.getMinY()));
                    graphics2D.drawString(c.y + "",
                            (int) (pixelx - envelope.getMinX()) - graphics2D.getFontMetrics().charsWidth((c.x + "").toCharArray(), 0, (c.x + "").length()),
                            (int) (pixely - envelope.getMinY()) + graphics2D.getFontMetrics().getHeight());
                }

            }
            if (anchor.equals("top-left") || anchor.equals("bottom-left")) {
                double xp1 = pixelx + graphics2D.getFontMetrics().charsWidth((c.x + "").toCharArray(), 0, (c.x + "").length());
                double yp1 = pixely + graphics2D.getFontMetrics().getHeight() * 2;
                Envelope e = new Envelope(c.x, MercatorProjection.pixelXToLongitude(xp1, zoom), c.y, MercatorProjection.pixelYToLatitude(yp1, zoom));
                boolean isIn = false;
                for (Envelope envelope1 : envelopes) {
                    if (envelope1.intersects(e)) {
                        isIn = true;
                        break;
                    }
                }
                if (!isIn) {
                    envelopes.add(e);

                    graphics2D.drawString(c.x + "",
                            (int) (pixelx - envelope.getMinX()),
                            (int) (pixely - envelope.getMinY()));
                    graphics2D.drawString(c.y + "",
                            (int) (pixelx - envelope.getMinX()),
                            (int) (pixely - envelope.getMinY()) + graphics2D.getFontMetrics().getHeight());
                }
            }
        }
    }

    /**
     * 绘制斜线
     *
     * @param g2
     * @param frameColor
     * @param x
     * @param y
     * @param numPoints
     */
    private static void drawSell(Graphics2D g2, Color frameColor, int[] x, int[] y, int numPoints) {
        g2.setColor(frameColor);
        //初始化多边形
        java.awt.Polygon p = new java.awt.Polygon(x, y, numPoints);
        //取得多边形外接矩形
        Rectangle r = p.getBounds();
        //裁切
        g2.setClip(p);
        //绘制填充线
        for (int j = r.y; j - r.width < r.y + r.height; j = j + 6) {
            Line2D line = new Line2D.Float(r.x, j, (r.x + r.width), j - r.width);
            g2.draw(line);
        }
        g2.setClip(null);
        //绘制多边形
        // g2.drawPolygon(p);
    }

    private static void drawPolygon(Geometry[] geometries, ScreenshotsSetting screenshotsSetting, Graphics2D graphics2D, Envelope envelope, byte zoom, boolean drawMark, String centerSymbol) throws Exception {
        graphics2D.setStroke(new BasicStroke(screenshotsSetting.getStrokeSize()));
        if (screenshotsSetting.getFont() != null) {
            graphics2D.setFont(screenshotsSetting.getFont());
        }
        for (int i = 0; i < geometries.length; ++i) {
            List<List<Point>> cs = new ArrayList<>();
            Geometry geometries1 = geometries[i];
            if (geometries1 instanceof com.mapbox.geojson.Polygon) {
                cs = ((com.mapbox.geojson.Polygon) geometries1).coordinates();
            }
            if (geometries1 instanceof MultiPolygon) {
                List<List<List<Point>>> csss = ((com.mapbox.geojson.MultiPolygon) geometries1).coordinates();
                for (List<List<Point>> css : csss) {
                    cs.addAll(css);
                }
            }
            for (List<Point> points : cs) {
                int nPoints = points.size();
                int[] xPoints = new int[nPoints];
                int[] yPoints = new int[nPoints];
                for (int j = 0, len = points.size(); j < len; j++) {
                    Point c = points.get(j);
                    xPoints[j] = (int) ((MercatorProjection.longitudeToPixelX(c.longitude(), zoom)) - envelope.getMinX());
                    yPoints[j] = (int) ((MercatorProjection.latitudeToPixelY(c.latitude(), zoom)) - envelope.getMinY());
                }
                Color fillColor = screenshotsSetting.getFillColor();
                if (fillColor != null) {
                    if (FILL.equals(screenshotsSetting.getFillType())) {
                        //绘制图形
                        graphics2D.setColor(fillColor);
                        graphics2D.fillPolygon(xPoints, yPoints, nPoints);
                    } else {
                        drawSell(graphics2D, fillColor, xPoints, yPoints, nPoints);
                    }
                }
                //绘制图形
                graphics2D.setColor(screenshotsSetting.getColor());
                graphics2D.drawPolygon(xPoints, yPoints, nPoints);
                if (drawMark) {
                    List<List<Point>> polyg = new ArrayList<>();
                    polyg.add(points);
                    Polygon polygon = Polygon.fromLngLats(polyg);
                    drawMark(graphics2D, polygon, zoom, envelope);
                }
                //绘制中心点符号
                if (centerSymbol != null && !centerSymbol.equals("")) {
                    GeoJson geoJson = Transformation.geoJson2CenterPoint(geometries1);
                    if (geoJson instanceof Point) {
                        Point point = (Point) geoJson;
                        int width = graphics2D.getFontMetrics().charsWidth(centerSymbol.toCharArray(), 0, centerSymbol.length());
                        int height = graphics2D.getFontMetrics().getHeight();
                        int x = (int) ((MercatorProjection.longitudeToPixelX(point.longitude(), zoom)) - envelope.getMinX()) - width / 2;
                        int y = (int) ((MercatorProjection.latitudeToPixelY(point.latitude(), zoom)) - envelope.getMinY()) + height / 2;
                        graphics2D.drawString(centerSymbol, x, y);
                    }
                }
            }
        }
    }

    private static int drawRectAndSzzb(BoundingBox boundingBox, Graphics2D graphics2D, byte zoom, Envelope envelope) {
        //获取1个字母的像素宽度
        int whith2letter = (int) graphics2D.getFont().getStringBounds("N", graphics2D.getFontRenderContext()).getWidth();
        int rectX1 = (int) ((MercatorProjection.longitudeToPixelX(boundingBox.west(), zoom)) - envelope.getMinX());
        int rectX2 = (int) ((MercatorProjection.longitudeToPixelX(boundingBox.east(), zoom)) - envelope.getMinX());
        int rectY1 = (int) ((MercatorProjection.latitudeToPixelY(boundingBox.north(), zoom)) - envelope.getMinY());
        int rectY2 = (int) ((MercatorProjection.latitudeToPixelY(boundingBox.south(), zoom)) - envelope.getMinY());
        graphics2D.setColor(new Color(255, 255, 255));
        //绘制左上
        String leftTopE = String.format("E%s", Transformation.changeToDFM(boundingBox.west()));
        String leftTopN = String.format("N%s", Transformation.changeToDFM(boundingBox.north()));
        int xpy = (int) graphics2D.getFont().getStringBounds(leftTopE.length() > leftTopN.length() ? leftTopE : leftTopN, graphics2D.getFontRenderContext()).getWidth();
        graphics2D.drawString(leftTopE, rectX1 - xpy, rectY1 - graphics2D.getFontMetrics().getHeight());
        graphics2D.drawString(leftTopN, rectX1 - xpy, rectY1);
        //绘制右上
        leftTopE = String.format("E%s", Transformation.changeToDFM(boundingBox.east()));
        leftTopN = String.format("N%s", Transformation.changeToDFM(boundingBox.north()));
        graphics2D.drawString(leftTopE, rectX2 + whith2letter, rectY1 - graphics2D.getFontMetrics().getHeight());
        graphics2D.drawString(leftTopN, rectX2 + whith2letter, rectY1);
        //绘制左下
        leftTopE = String.format("E%s", Transformation.changeToDFM(boundingBox.west()));
        leftTopN = String.format("N%s", Transformation.changeToDFM(boundingBox.south()));
        xpy = (int) graphics2D.getFont().getStringBounds(leftTopE.length() > leftTopN.length() ? leftTopE : leftTopN, graphics2D.getFontRenderContext()).getWidth();
        graphics2D.drawString(leftTopE, rectX1 - xpy, rectY2 + graphics2D.getFontMetrics().getHeight());
        graphics2D.drawString(leftTopN, rectX1 - xpy, rectY2);
        //绘制右下
        leftTopE = String.format("E%s", Transformation.changeToDFM(boundingBox.east()));
        leftTopN = String.format("N%s", Transformation.changeToDFM(boundingBox.south()));
        graphics2D.drawString(leftTopE, rectX2 + whith2letter, rectY2 + graphics2D.getFontMetrics().getHeight());
        graphics2D.drawString(leftTopN, rectX2 + whith2letter, rectY2);
        //绘制边框
        // graphics2D.setColor(new Color(0, 0, 0));
        graphics2D.setStroke(new BasicStroke(2));
        graphics2D.drawRect(rectX1, rectY1, rectX2 - rectX1, rectY2 - rectY1);
        return xpy;
    }

    /**
     * 自定义裁剪
     * @param padding
     * @param boundingBox
     * @param zoom
     * @param envelope
     * @param finalImg
     * @param pixelList
     * @return
     */
    private static List<Object> cutImageWithPolygonCenterCustom(
            BoundingBox boundingBox, byte zoom, Envelope envelope, BufferedImage finalImg, ScreenshotsSetting screenshotsSetting,
            List<Double> pixelList) {
        int margin = screenshotsSetting.getMargin();

        double pminx = pixelList.get(0) - envelope.getMinX();
        if (pminx < 0) {
            pminx = 0;
        }
        double pmaxX = pixelList.get(1) - envelope.getMinX();

        double pminY = pixelList.get(2) - envelope.getMinY();
        if (pminY < 0) {
            pminY = 0;
        }
        double pmaxY = pixelList.get(3) - envelope.getMinY();


        //把图片裁剪成最佳状态 即：地块加坐标点的外接矩形为截图的边界
        int hight = screenshotsSetting.getHeight();
        int width = screenshotsSetting.getWidth();
        //写入图像内容
        BufferedImage bufferedImageCut = new BufferedImage(width, hight, finalImg.getType());
        Graphics2D gr = bufferedImageCut.createGraphics();

        gr.drawImage(finalImg, 0, 0,
                new Double(screenshotsSetting.getWidth()).intValue(),
                new Double(screenshotsSetting.getHeight()).intValue(),
                new Double(pminx).intValue(),
                new Double(pminY).intValue(),
                new Double(pmaxX).intValue(), new Double(pmaxY).intValue(),
                null);
        BoundingBox boundingBox1 = BoundingBox.fromLngLats(MercatorProjection.pixelXToLongitude(pminx+envelope.getMinX(), zoom),
                MercatorProjection.pixelYToLatitude(pmaxY+envelope.getMinY(), zoom)
                , MercatorProjection.pixelXToLongitude(pmaxX+envelope.getMinX(), zoom)
                , MercatorProjection.pixelYToLatitude(pminY+envelope.getMinY(), zoom));
        List<Object> list = new ArrayList<>();
        list.add(bufferedImageCut);
        list.add(gr);
        list.add(boundingBox1);
        return list;
    }

    private static List<Object> cutImageWithPolygonCenter(BoundingBox boundingBox, byte zoom, Envelope envelope, BufferedImage finalImg, int padding) {
        double pminx = MercatorProjection.longitudeToPixelX(boundingBox.west(), zoom) - (padding < 100 ? 100 : padding) - envelope.getMinX();
        if (pminx < 0) {
            pminx = 0;
        }
        double pmaxX = MercatorProjection.longitudeToPixelX(boundingBox.east(), zoom) + (padding < 100 ? 100 : padding) - envelope.getMinX();
        if (pmaxX > finalImg.getWidth()) {
            pmaxX = finalImg.getWidth();
        }
        double pminY = MercatorProjection.latitudeToPixelY(boundingBox.north(), zoom) - (padding < 100 ? 100 : padding) - envelope.getMinY();
        if (pminY < 0) {
            pminY = 0;
        }
        double pmaxY = MercatorProjection.latitudeToPixelY(boundingBox.south(), zoom) + (padding < 100 ? 100 : padding) - envelope.getMinY();
        if (pmaxY > finalImg.getHeight()) {
            pmaxY = finalImg.getHeight();
        }
        if (pmaxX - pminx > pmaxY - pminY) {
            double cz = (pmaxX - pminx) - (pmaxY - pminY);
            pmaxY = (pmaxY + cz / 2) > finalImg.getHeight() ? finalImg.getHeight() : (pmaxY + cz / 2);
            pminY = (pminY - cz / 2) < 0 ? 0 : (pminY - cz / 2);
            if ((pmaxY - pminY) > finalImg.getHeight()) {
                cz = (pmaxY - pminY) - finalImg.getHeight();
                pmaxY = pmaxY - cz / 2;
                pminY = pminY + cz / 2;
            }
        } else {
            double cz = (pmaxY - pminY) - (pmaxX - pminx);
            pmaxX = (pmaxX + cz / 2) > finalImg.getWidth() ? finalImg.getWidth() : (pmaxX + cz / 2);
            pminx = (pminx - cz / 2) < 0 ? 0 : (pminx - cz / 2);
            if ((pmaxX - pminx) > finalImg.getWidth()) {
                cz = (pmaxX - pminx) - finalImg.getWidth();
                pmaxX = pmaxX - cz / 2;
                pminx = pminx + cz / 2;
            }
        }
        //把图片裁剪成最佳状态 即：地块加坐标点的外接矩形为截图的边界
        int hight = new Double(pmaxY - pminY).intValue();
        int width = new Double(pmaxX - pminx).intValue();
        //写入图像内容
        BufferedImage bufferedImageCut = new BufferedImage(width, hight, finalImg.getType());
        Graphics2D gr = bufferedImageCut.createGraphics();

        gr.drawImage(finalImg, 0, 0,
                new Double(pmaxX - pminx).intValue(), new Double(pmaxY - pminY).intValue(),
                new Double(pminx).intValue(),
                new Double(pminY).intValue(),
                new Double(pmaxX).intValue(), new Double(pmaxY).intValue(),
                null);
        BoundingBox boundingBox1 = BoundingBox.fromLngLats(MercatorProjection.pixelXToLongitude(pminx+envelope.getMinX(), zoom),
                MercatorProjection.pixelYToLatitude(pmaxY+envelope.getMinY(), zoom)
                , MercatorProjection.pixelXToLongitude(pmaxX+envelope.getMinX(), zoom)
                , MercatorProjection.pixelYToLatitude(pminY+envelope.getMinY(), zoom));
        List<Object> list = new ArrayList<>();
        list.add(bufferedImageCut);
        list.add(gr);
        list.add(boundingBox1);
        return list;
    }

    private static void drawWaters(Graphics2D gr, ScreenshotsSetting screenshotsSetting, List<String> waters, BufferedImage bufferedImageCut) {
        gr.setStroke(new BasicStroke(2));
        if (screenshotsSetting.getFont() != null) {
            gr.setFont(screenshotsSetting.getFont());
        }
        //绘制水印默认左下角由下到上绘制
        if (waters != null) {
            gr.setColor(Color.white);
            gr.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            gr.setStroke(new BasicStroke(100.0f));
            for (int i1 = 0; i1 < waters.size(); i1++) {
                gr.drawString(waters.get(i1), 5, (bufferedImageCut.getHeight() - 5) - gr.getFontMetrics().getHeight() * i1);
            }
        }
    }

    private static void drawWatersByScreenshotWaterSetting(
            Graphics2D gr,
            List<ScreenshotWaterSetting> screenshotsWaterSettings,
            ScreenshotWaterSetting.WaterAnchor waterAnchor,
            Envelope envelope,
            BoundingBox boundingBox,
            byte zoom,
            BufferedImage bufferedImageCut) {
        if (screenshotsWaterSettings == null || screenshotsWaterSettings.size() == 0) {
            return;
        }
        gr.setStroke(new BasicStroke(2));
        gr.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        gr.setStroke(new BasicStroke(100.0f));
        int rectX1 = (int) ((MercatorProjection.longitudeToPixelX(boundingBox.west(), zoom)) - envelope.getMinX());
        int rectX2 = (int) ((MercatorProjection.longitudeToPixelX(boundingBox.east(), zoom)) - envelope.getMinX());
        int rectY1 = (int) ((MercatorProjection.latitudeToPixelY(boundingBox.north(), zoom)) - envelope.getMinY());
        int rectY2 = (int) ((MercatorProjection.latitudeToPixelY(boundingBox.south(), zoom)) - envelope.getMinY());
        for (int i = 0; i < screenshotsWaterSettings.size(); i++) {
            ScreenshotWaterSetting screenshotWaterSetting = screenshotsWaterSettings.get(i);
            if (screenshotWaterSetting.getFont() != null) {
                gr.setFont(screenshotWaterSetting.getFont());
            }
            gr.setColor(screenshotWaterSetting.getWaterColor());
            if (waterAnchor == ScreenshotWaterSetting.WaterAnchor.ENV_TOP) {
                String waterMsg = screenshotWaterSetting.getWaterMsg();
                if (waterMsg == null || waterMsg.length() == 0) continue;
                int whith2letter = (int) gr.getFont().getStringBounds(waterMsg, gr.getFontRenderContext()).getWidth();
                gr.drawString(waterMsg, (int) ((rectX2 - rectX1) / 2 + rectX1 - whith2letter / 2), (rectY1 - 3) - gr.getFontMetrics().getHeight() * i);
            }
            if (waterAnchor == ScreenshotWaterSetting.WaterAnchor.ENV_BOTTOM) {
                String waterMsg = screenshotWaterSetting.getWaterMsg();
                if (waterMsg == null || waterMsg.length() == 0) continue;
                int whith2letter = (int) gr.getFont().getStringBounds(waterMsg, gr.getFontRenderContext()).getWidth();
                gr.drawString(waterMsg, (int) ((rectX2 - rectX1) / 2 + rectX1 - whith2letter / 2), (rectY2 + 3) + gr.getFontMetrics().getHeight() * (i + 1));
            }
            if (waterAnchor == ScreenshotWaterSetting.WaterAnchor.ENV_LEFT) {
                String waterMsg = screenshotWaterSetting.getWaterMsg();
                if (waterMsg == null || waterMsg.length() == 0) continue;
                int whith2letter = (int) gr.getFont().getStringBounds(waterMsg, gr.getFontRenderContext()).getWidth();
                gr.drawString(waterMsg, (int) (rectX1 - whith2letter - 3), (((rectY2 - rectY1) / 2 + rectY1 - gr.getFontMetrics().getHeight() * screenshotsWaterSettings.size() / 2) + gr.getFontMetrics().getHeight() * i));
            }
            if (waterAnchor == ScreenshotWaterSetting.WaterAnchor.ENV_RIGHT) {
                String waterMsg = screenshotWaterSetting.getWaterMsg();
                if (waterMsg == null || waterMsg.length() == 0) continue;
                gr.drawString(waterMsg, (int) (rectX2 + 3), (((rectY2 - rectY1) / 2 + rectY1 - gr.getFontMetrics().getHeight() * screenshotsWaterSettings.size() / 2) + gr.getFontMetrics().getHeight() * i));
            }
            if (waterAnchor == ScreenshotWaterSetting.WaterAnchor.LEFT_TOP) {
                String waterMsg = screenshotWaterSetting.getWaterMsg();
                if (waterMsg == null || waterMsg.length() == 0) continue;
                gr.drawString(waterMsg, 5, gr.getFontMetrics().getHeight() * (i + 1) + 5);
            }
            if (waterAnchor == ScreenshotWaterSetting.WaterAnchor.RIGHT_TOP) {
                String waterMsg = screenshotWaterSetting.getWaterMsg();
                if (waterMsg == null || waterMsg.length() == 0) continue;
                int whith2letter = (int) gr.getFont().getStringBounds(waterMsg, gr.getFontRenderContext()).getWidth();
                gr.drawString(waterMsg, bufferedImageCut.getWidth() - whith2letter - 5, gr.getFontMetrics().getHeight() * (i + 1) + 5);
            }
            if (waterAnchor == ScreenshotWaterSetting.WaterAnchor.LEFT_BOTTOM) {
                String waterMsg = screenshotWaterSetting.getWaterMsg();
                if (waterMsg == null || waterMsg.length() == 0) continue;
                gr.drawString(waterMsg, 5, bufferedImageCut.getHeight() - gr.getFontMetrics().getHeight() * (i) - 5);
            }
            if (waterAnchor == ScreenshotWaterSetting.WaterAnchor.RIGHT_BOTTOM) {
                String waterMsg = screenshotWaterSetting.getWaterMsg();
                if (waterMsg == null || waterMsg.length() == 0) continue;
                int whith2letter = (int) gr.getFont().getStringBounds(waterMsg, gr.getFontRenderContext()).getWidth();
                gr.drawString(waterMsg, bufferedImageCut.getWidth() - whith2letter - 5, bufferedImageCut.getHeight() - gr.getFontMetrics().getHeight() * (i) - 5);
            }
        }
    }
}
