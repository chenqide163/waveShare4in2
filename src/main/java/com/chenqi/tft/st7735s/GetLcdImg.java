package com.chenqi.tft.st7735s;

import com.chenqi.waveshare.for4in2.For4in2Demo;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.io.IOException;
import java.util.Random;

public class GetLcdImg {

    /**
     * 获取文字图片的字节数组
     *
     * @return
     */
    public static byte[] getFontImg() {
        int width = 128;
        int height = 160;
        BufferedImage image = new BufferedImage(400, 300,
                BufferedImage.TYPE_BYTE_BINARY);
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                image.setRGB(i, j, 0xffffff);
            }
        }
        Graphics2D g = image.createGraphics();
        //g.setFont(new java.awt.Font("叶根友毛笔行书2.0版", Font.PLAIN, 25));
        g.setFont(new Font("微软雅黑", Font.PLAIN, 30));
        g.setColor(new Color(0));
        g.drawString("WYY,你好哇:", 10, 50);
        g.drawString("待 得 花 信 年 ，", 80, 100);
        g.drawString("意 欲 离 阁 否 ？", 80, 150);
        g.drawString("明 媚 鲜 妍 时 ，", 80, 200);
        g.drawString("粉 黛 嫁  C Q  ！", 80, 250);

        final byte[] pixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
        System.out.println(" pixels size = " + pixels.length);
        return pixels;
    }


    /**
     * 获取二进制图片的字节数组
     *
     * @return
     * @throws IOException
     */
    public static BufferedImage getColorImg(String imgPath) throws IOException {
        int width = 128;
        int height = 160;

        //定义一个BufferedImage对象，用于保存缩小后的位图
        BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics graphics = bufferedImage.getGraphics();

        //读取原始位图
        Image srcImage = ImageIO.read(new File(imgPath));

        //将原始位图缩小后绘制到bufferedImage对象中
        graphics.drawImage(srcImage, 0, 0, width, height, null);
        //将bufferedImage对象输出到磁盘上

        return bufferedImage;
    }

    /**
     * 获取灰阶图片的字节数组
     *
     * @return
     * @throws IOException
     */
    public static BufferedImage getGrayImg() throws IOException {
        int width = 400;
        int height = 300;

        String path = For4in2Demo.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        String imgPath = path.substring(0, path.lastIndexOf(File.separator) + 1) + File.separator + "test.jpg";
        System.out.println("imgPath = " + imgPath);

        //读取原始位图
        Image srcImage = ImageIO.read(new File(imgPath));
        //定义一个BufferedImage对象，用于保存缩小后的位图
        BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics graphics = bufferedImage.getGraphics();
        //将原始位图缩小后绘制到bufferedImage对象中
        graphics.drawImage(srcImage, 0, 0, width, height, null);
        //将bufferedImage对象输出到磁盘上

        BufferedImage grayImage = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                int rgb = bufferedImage.getRGB(i, j);
                grayImage.setRGB(i, j, rgb);
            }
        }
        return grayImage;
    }

}
