package com.grandtech.tools;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @program: MapScreenshot
 * @description: 总坐落图生成的一些参数配置
 * @author: 冯帅
 * @create: 2022-03-18 08:25
 **/

public class ScreenshotsSetting {
    //填充斜线
    public static final String CELL = "CELL";
    //填充铺满
    public static final String FILL = "FILL";


    //默认0.6 截图级别的因数
    private double tileDensity = 0.6;
    //图形轮廓宽度默认1
    private float strokeSize = 1f;
    //图片宽高压缩率 1为原宽高默认给0.8，可根据情况调整大小，可以减少图片的大小，同时降低质量
    private float imageScale=0.8f;
    //图片质量 1为原质量默认给1
    private float imageQuality=1.0f;
    //可以不传，字体
    private Font font;
    //地块边线颜色
    private Color color = new Color(20,205,180);
    //地块填充颜色 如果为null 则不填充
    private Color fillColor = Color.red;
    //地块填充样式 斜线、铺满
    private String fillType=CELL;
    ///是否绘制外框以及四至坐标点
    private boolean drawRect = true;
    //是否绘制图形的坐标
    private boolean drawPolygonMark =false;
    //下载影像源超时时间
    private int socketTimeout = 2000;
    //背景色，默认给白色
    private Color backgroundColor = Color.white;
    //是否绘制影像底图,默认true
    private boolean isDrawImageMap = true;
    //截图宽度
    private int width ;
    //截图高度
    private int height;
    //图形和图片边缘边距
    private int margin;

    public int getMargin() {
        return margin;
    }

    public void setMargin(int margin) {
        this.margin = margin;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    //其他需要填充的影像源
    private java.util.List<String> otherImageUrls = new ArrayList<>();

    //水印信息 边框左
    private List<ScreenshotWaterSetting> envLeftWaters;

    //水印信息 边框右
    private List<ScreenshotWaterSetting> envRightWaters;

    //水印信息 边框上
    private List<ScreenshotWaterSetting> envTopWaters;

    //水印信息 边框下
    private List<ScreenshotWaterSetting> envBottomWaters;

    //水印信息
    private List<ScreenshotWaterSetting> LeftTopWaters;

    //水印信息
    private List<ScreenshotWaterSetting> leftBottomWaters;

    //水印信息
    private List<ScreenshotWaterSetting> rightTopWaters;

    //水印信息
    private List<ScreenshotWaterSetting> rightBottomWaters;


    public List<ScreenshotWaterSetting> getEnvLeftWaters() {
        return envLeftWaters;
    }

    public void setEnvLeftWaters(List<ScreenshotWaterSetting> envLeftWaters) {
        this.envLeftWaters = envLeftWaters;
    }

    public List<ScreenshotWaterSetting> getEnvRightWaters() {
        return envRightWaters;
    }

    public void setEnvRightWaters(List<ScreenshotWaterSetting> envRightWaters) {
        this.envRightWaters = envRightWaters;
    }

    public List<ScreenshotWaterSetting> getEnvTopWaters() {
        return envTopWaters;
    }

    public void setEnvTopWaters(List<ScreenshotWaterSetting> envTopWaters) {
        this.envTopWaters = envTopWaters;
    }

    public List<ScreenshotWaterSetting> getEnvBottomWaters() {
        return envBottomWaters;
    }

    public void setEnvBottomWaters(List<ScreenshotWaterSetting> envBottomWaters) {
        this.envBottomWaters = envBottomWaters;
    }

    public List<ScreenshotWaterSetting> getLeftTopWaters() {
        return LeftTopWaters;
    }

    public void setLeftTopWaters(List<ScreenshotWaterSetting> leftTopWaters) {
        LeftTopWaters = leftTopWaters;
    }

    public List<ScreenshotWaterSetting> getLeftBottomWaters() {
        return leftBottomWaters;
    }

    public void setLeftBottomWaters(List<ScreenshotWaterSetting> leftBottomWaters) {
        this.leftBottomWaters = leftBottomWaters;
    }

    public List<ScreenshotWaterSetting> getRightTopWaters() {
        return rightTopWaters;
    }

    public void setRightTopWaters(List<ScreenshotWaterSetting> rightTopWaters) {
        this.rightTopWaters = rightTopWaters;
    }

    public List<ScreenshotWaterSetting> getRightBottomWaters() {
        return rightBottomWaters;
    }

    public void setRightBottomWaters(List<ScreenshotWaterSetting> rightBottomWaters) {
        this.rightBottomWaters = rightBottomWaters;
    }

    public int getSocketTimeout() {
        return socketTimeout;
    }

    public void setSocketTimeout(int socketTimeout) {
        this.socketTimeout = socketTimeout;
    }

    public String getFillType() {
        return fillType;
    }

    public void setFillType(String fillType) {
        this.fillType = fillType;
    }

    public List<String> getOtherImageUrls() {
        return otherImageUrls;
    }

    public void setOtherImageUrls(List<String> otherImageUrls) {
        this.otherImageUrls = otherImageUrls;
    }

    public boolean isDrawPolygonMark() {
        return drawPolygonMark;
    }

    public void setDrawPolygonMark(boolean drawPolygonMark) {
        this.drawPolygonMark = drawPolygonMark;
    }

    public boolean isDrawRect() {
        return drawRect;
    }

    public void setDrawRect(boolean drawRect) {
        this.drawRect = drawRect;
    }

    public Color getFillColor() {
        return fillColor;
    }

    public void setFillColor(Color fillColor) {
        this.fillColor = fillColor;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public Font getFont() {
        return font;
    }

    public void setFont(Font font) {
        this.font = font;
    }

    public float getImageScale() {
        return imageScale;
    }

    public void setImageScale(float imageScale) {
        this.imageScale = imageScale;
    }

    public float getImageQuality() {
        return imageQuality;
    }

    public void setImageQuality(float imageQuality) {
        this.imageQuality = imageQuality;
    }

    public double getTileDensity() {
        return tileDensity;
    }

    public void setTileDensity(double tileDensity) {
        this.tileDensity = tileDensity;
    }

    public float getStrokeSize() {
        return strokeSize;
    }

    public void setStrokeSize(float strokeSize) {
        this.strokeSize = strokeSize;
    }

    public Color getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(Color backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public boolean isDrawImageMap() {
        return isDrawImageMap;
    }

    public void setDrawImageMap(boolean drawImageMap) {
        isDrawImageMap = drawImageMap;
    }
}
