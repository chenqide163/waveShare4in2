package com.chenqi.waveshare.for4in2;

import java.io.IOException;

public class Main {

    public static void main(String[] args) {
        System.out.println("start to rum spi");
        try {

            For4in2Demo.getInstance().init(); //初始化
            For4in2Demo.getInstance().clear(); //清空屏幕
            //display(GetEpaperImg.getFontImg());
            //Thread.sleep(1000);
            //Epaper4in2_4GrayScale.getInstance().display4GrayScale();
            //Thread.sleep(1000);

            Epaper4in2_4GrayScale.getInstance().displayImgWith4GrayScale(GetEpaperImg.getGrayImg());
         /*   for (int i = 0; i < 16; i++) {
                For4in2Demo.getInstance().partialDisplay(i);
            }
*/
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
