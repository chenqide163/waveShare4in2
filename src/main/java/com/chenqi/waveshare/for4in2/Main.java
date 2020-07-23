package com.chenqi.waveshare.for4in2;

import java.io.IOException;

public class Main {

    public static void main(String[] args) {
        System.out.println("start to run spi");

        display16GrayLadderOptimization();
    }

    /**
     * 打印16阶灰度阶梯(波形未经过优化)
     */
    public static void display16GrayLadder() {
        System.out.println("start to run spi");
        try {

            For4in2Demo.getInstance().init(); //初始化
            For4in2Demo.getInstance().clear(); //清空屏幕

            System.out.println("start to run 16 gray labber");
            for (int i = 0; i < 16; i++) {
                Epaper4in2_16GrayScale.getInstance().display16GrayLadder(i);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 打印16阶灰度阶梯(波形经过优化)
     */
    public static void display16GrayLadderOptimization() {
        System.out.println("start to run spi");
        try {

            For4in2Demo.getInstance().init(); //初始化
            For4in2Demo.getInstance().clear(); //清空屏幕

            System.out.println("start to run 16 gray labber");
            for (int i = 0; i < 16; i++) {
                Epaper4in2_16GrayScale.getInstance().display16GrayLadderOptimization(i);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 打印4灰阶图片
     */
    public static void display4GrayImg() {
        try {

            For4in2Demo.getInstance().init(); //初始化
            For4in2Demo.getInstance().clear(); //清空屏幕
            //打印4灰阶图片
            Epaper4in2_4GrayScale.getInstance().displayImgWith4GrayScale(GetEpaperImg.getGrayImg());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 打印4灰阶颜色梯度
     */
    public static void display4GrayLabber() {
        try {

            For4in2Demo.getInstance().init(); //初始化
            For4in2Demo.getInstance().clear(); //清空屏幕
            //打印4灰阶图片
            Epaper4in2_4GrayScale.getInstance().display4GrayScaleLabber();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 打印黑白图片
     */
    public static void displayOnlyBlackAndWhiteImg() {
        try {
            For4in2Demo.getInstance().init(); //初始化
            For4in2Demo.getInstance().clear(); //清空屏幕
            For4in2Demo.getInstance().display(GetEpaperImg.getFontImg());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
