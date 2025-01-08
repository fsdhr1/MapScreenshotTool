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

public class GeometrySetting {
    //地块边线颜色
    private Color color = new Color(20,205,180);
    //地块填充颜色 如果为null 则不填充
    private Color fillColor = Color.red;
    //是否绘制图形的坐标
    private boolean drawPolygonMark =false;
    private  String a;



    public boolean isDrawPolygonMark() {
        return drawPolygonMark;
    }

    public void setDrawPolygonMark(boolean drawPolygonMark) {
        this.drawPolygonMark = drawPolygonMark;
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


}
