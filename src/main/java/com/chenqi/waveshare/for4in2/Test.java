package com.chenqi.waveshare.for4in2;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.io.IOException;
import java.util.Random;

public class Test {
    public static void main(String[] args) throws IOException {
        int a = 0x90;
        System.out.println((byte) a);

        String b =  Test.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        System.out.println(b);
        System.out.println(File.separator);

        //getBinImg();

        getFontImage();
    }

    public static void getBinImg() throws IOException {
        int width = 400;
        int height = 300 ;

        //定义一个BufferedImage对象，用于保存缩小后的位图
        BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics graphics = bufferedImage.getGraphics();

        //读取原始位图
        Image srcImage = ImageIO.read(new File("D:\\test.jpg"));

        //将原始位图按墨水屏幕大小缩小后绘制到bufferedImage对象中
        graphics.drawImage(srcImage, 0, 0, width, height, null);
        //将bufferedImage对象输出到磁盘上
        ImageIO.write(bufferedImage, "jpg", new File("D:\\test2.jpg"));

        BufferedImage binImage = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_BINARY);
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                int rgb = bufferedImage.getRGB(i, j);
                int oneGate = rgb & 0xffffff;
                int randomNum = new Random().nextInt(0xffffff);
                int binValue = 0;

                //0是黑  1是白 ，或者说数值小就靠近黑色，数值大就靠近白色
                if(oneGate > 0xf2ffff)//大于一定数值，直接用白点，这个值自己调
                {
                    binValue = 0xffffff;
                }
                else if(oneGate < 0x900000)//小于一定数值直接用黑点，这个值自己调
                {
                    binValue = 0;
                }
                else if(oneGate > randomNum)//关键if,模拟灰阶使用随机数画白点
                {
                    binValue = 0xffffff;
                }
                else
                {
                    binValue = 0;
                }
                binImage.setRGB(i,j,binValue);
            }
            ImageIO.write(binImage, "jpg", new File("D:\\test3.jpg"));
        }
    }


    public static void getFontImage() throws IOException {
        int width = 400;
        int height = 300;
        BufferedImage image = new BufferedImage(400, 300,
                BufferedImage.TYPE_BYTE_BINARY);
        for(int i=0;i<width;i++){
            for(int j=0;j<height;j++){
                //0黑1白，将画布置为白底色
                image.setRGB(i,j,0xffffff);
            }
        }
        Graphics2D g = image.createGraphics();
        g.setFont(new java.awt.Font("微软雅黑", Font.PLAIN, 30));
        g.setColor(new Color(0)); //0黑1白，设置字体为黑色
        g.drawString("WYY,你好哇:", 10, 50);
        g.drawString("待 得 花 信 年 ，", 80,100);
        g.drawString("意 欲 离 阁 否 ？", 80,150);
        g.drawString("明 媚 鲜 妍 时 ，", 80,200);
        g.drawString("粉 黛 嫁  C Q  ！", 80,250);

        File newFile = new File("D:/verse.jpg");
        ImageIO.write(image, "jpg", newFile);
    }

    public static void getImg(){
        BufferedImage tmpImage = new BufferedImage(8, 2, BufferedImage.TYPE_BYTE_BINARY);
        final byte[] pixels = ((DataBufferByte) tmpImage.getRaster().getDataBuffer()).getData();
        for(byte b : pixels){
            System.out.println(b&0xff);
        }
    }
}
