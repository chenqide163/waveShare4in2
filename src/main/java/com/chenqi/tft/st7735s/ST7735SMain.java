package com.chenqi.tft.st7735s;

import com.chenqi.waveshare.for4in2.For4in2Demo;

import java.io.File;
import java.io.IOException;

public class ST7735SMain {
    public static void main(String[] args) throws IOException, InterruptedException {
        for (; ; ) {

            String path = ST7735SMain.class.getProtectionDomain().getCodeSource().getLocation().getPath();
            path = path.substring(0, path.lastIndexOf(File.separator) + 1);
            System.out.println(path);
            File file = new File(path);
            File[] files = file.listFiles();
            for(File eachFile : files){
                if(eachFile.getName().endsWith("jpg"))
                {
                    String imgPath = eachFile.getCanonicalPath();
                    System.out.println("imgPath = " + imgPath);
                    ST7735sDriver.getInstance().drawImg16BitColor(GetLcdImg.getColorImg(imgPath));
                    Thread.sleep(2000);
                }
            }


        }

    }
}
