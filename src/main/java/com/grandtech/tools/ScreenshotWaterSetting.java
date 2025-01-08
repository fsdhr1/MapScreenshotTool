package com.grandtech.tools;

import java.awt.*;

/**
 * @program: MapScreenshot
 * @description: 水印样式
 * @author: 冯帅
 * @create: 2022-09-06 13:16
 **/

public class ScreenshotWaterSetting {

    public enum WaterAnchor{
        LEFT_TOP,LEFT_BOTTOM,RIGHT_TOP,RIGHT_BOTTOM,ENV_TOP,ENV_LEFT,ENV_RIGHT,ENV_BOTTOM
    }


    private Color waterColor=Color.WHITE;

    private Font font =  new Font("方正黑体", Font.BOLD, 12);
    //水印信息
    private String waterMsg;

    public String getWaterMsg() {
        return waterMsg;
    }

    public void setWaterMsg(String waterMsg) {
        this.waterMsg = waterMsg;
    }

    public Color getWaterColor() {
        return waterColor==null?Color.white:waterColor;
    }

    public void setWaterColor(Color waterColor) {
        this.waterColor = waterColor;
    }

    public Font getFont() {
        return font;
    }

    public void setFont(Font font) {
        this.font = font;
    }
}
