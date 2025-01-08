package com.grandtech.tools;

import java.awt.image.BufferedImage;

/**
 * @program: MapScreenshot
 * @description: 图片一些处理方法
 * @author: 冯帅
 * @create: 2022-09-07 09:19
 **/

public class ImageUtil {
    /**
     * 返回黑色占图片的像素比例 0-1
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
