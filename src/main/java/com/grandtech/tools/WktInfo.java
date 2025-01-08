package com.grandtech.tools;

import java.awt.*;

/**
 * @program: MapScreenshot
 * @description: a
 * @author: 冯帅
 * @create: 2022-04-01 14:29
 **/

public class WktInfo {
    private String wkt;
    private Color color ;
    private boolean mark;
    //中心点的符号标记
    private String centerSymbol;

    public String getCenterSymbol() {
        return centerSymbol;
    }

    public void setCenterSymbol(String centerSymbol) {
        this.centerSymbol = centerSymbol;
    }

    public String getWkt() {
        return wkt;
    }

    public void setWkt(String wkt) {
        this.wkt = wkt;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public boolean isMark() {
        return mark;
    }

    public void setMark(boolean mark) {
        this.mark = mark;
    }
}
