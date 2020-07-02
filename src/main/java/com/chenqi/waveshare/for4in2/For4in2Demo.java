package com.chenqi.waveshare.for4in2;

import com.pi4j.io.gpio.*;
import com.pi4j.io.spi.SpiChannel;
import com.pi4j.io.spi.SpiDevice;
import com.pi4j.io.spi.SpiFactory;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.io.IOException;
import java.util.Random;

public class For4in2Demo {

    final static GpioPinDigitalOutput CS;
    final static GpioPinDigitalOutput DC;
    final static GpioPinDigitalOutput RST;
    final static GpioPinDigitalInput BUSY;
    // SPI device
    public static SpiDevice spi;

    static {
        // in order to use the Broadcom GPIO pin numbering scheme, we need to configure the
        // GPIO factory to use a custom configured Raspberry Pi GPIO provider
        RaspiGpioProvider raspiGpioProvider = new RaspiGpioProvider(RaspiPinNumberingScheme.BROADCOM_PIN_NUMBERING);
        GpioFactory.setDefaultProvider(raspiGpioProvider);

        // create gpio controller
        final GpioController gpio = GpioFactory.getInstance();

        CS = gpio.provisionDigitalOutputPin(RaspiBcmPin.GPIO_08, "CS", PinState.HIGH);
        DC = gpio.provisionDigitalOutputPin(RaspiBcmPin.GPIO_25, "DC", PinState.HIGH);
        RST = gpio.provisionDigitalOutputPin(RaspiBcmPin.GPIO_17, "RST", PinState.HIGH);
        BUSY = gpio.provisionDigitalInputPin(raspiGpioProvider, RaspiBcmPin.GPIO_24, "BUSY");

        try {
            spi = SpiFactory.getInstance(SpiChannel.CS1, //这里我试了，使用CS0/CS1都行，不明白这个到底是什么用处
                    SpiDevice.DEFAULT_SPI_SPEED, // default spi speed 1 MHz
                    SpiDevice.DEFAULT_SPI_MODE); // default spi mode 0
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        System.out.println("start to rum spi");
        try {

            init(); //初始化
            clear(); //清空屏幕
            display(getBinImg());

        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * reset墨水屏
     *
     * @throws InterruptedException
     */
    public static void reset() throws InterruptedException {
        System.out.println("reset spi");
        RST.high();
        Thread.sleep(200);
        RST.low();
        Thread.sleep(10);
        RST.high();
        Thread.sleep(200);
    }

    /**
     * 写入指令
     *
     * @param date
     * @throws IOException
     */
    public static void sendCommand(int date) throws IOException {
        DC.low();
        CS.low();
        spi.write((byte) date);
        CS.high();
    }

    /**
     * 写入数据
     *
     * @param date
     * @throws IOException
     */
    public static void sendData(int date) throws IOException {
        DC.high();
        CS.low();
        spi.write((byte) date);
        CS.high();
    }

    /**
     * 判断屏幕是否忙
     *
     * @throws IOException
     * @throws InterruptedException
     */
    public static void readBusy() throws IOException, InterruptedException {
        System.out.println("readBusy spi");
        sendCommand((byte) 0x71);
        while (BUSY.isLow()) {
            sendCommand((byte) 0x71);
            Thread.sleep(1000);
            System.out.println("BUSY!!!");
        }
        System.out.println("not busy!");
    }

    /**
     * 初始化屏幕
     *
     * @throws IOException
     * @throws InterruptedException
     */
    public static void init() throws IOException, InterruptedException {
        System.out.println("init spi");
        reset();

        sendCommand(0x01); //Power Setting
        sendData(0x03);
        sendData(0x00);
        sendData(0x2b);
        sendData(0x2b);
        sendData(0x09);//python驱动中未写入该值，但是数据手册中写入了该值

        sendCommand(0x06); //Booster Soft Start
        sendData(0x17);
        sendData(0x17);
        sendData(0x17);

        sendCommand(0x04); //Power ON
        readBusy();

        sendCommand(0x00); //Panel Setting
        sendData(0xbf);
        //sendData(0x0d); //python驱动中多写入了0d,不知道是何用意

        sendCommand(0x30); //PLL control
        sendData(0x3c); //3A 100HZ   29 150Hz 39 200HZ  31 171HZ

        sendCommand(0x61); //Resolution setting
        sendData(0x01);
        sendData(0x90);
        sendData(0x01);
        sendData(0x2c);

        sendCommand(0x82); //VCM_DC Setting
        sendData(0x28);

        sendCommand(0X50); //Vcom and data interval setting
        sendData(0x97);

        setLut(); //设置LUT
    }

    /**
     * 初始化LUT
     *
     * @throws IOException
     */
    public static void setLut() throws IOException {
        sendCommand(0x20);
        for (int data : lut_vcom0) {
            sendData(data);
        }

        sendCommand(0x21);
        for (int data : lut_ww) {
            sendData(data);
        }

        sendCommand(0x22);
        for (int data : lut_bw) {
            sendData(data);
        }

        sendCommand(0x23);
        for (int data : lut_bb) {
            sendData(data);
        }

        sendCommand(0x24);
        for (int data : lut_wb) {
            sendData(data);
        }

    }

    /**
     * 获取二进制图片的字节数组
     *
     * @return
     * @throws IOException
     */
    public static byte[] getBinImg() throws IOException {
        int width = 400;
        int height = 300;

        //定义一个BufferedImage对象，用于保存缩小后的位图
        BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics graphics = bufferedImage.getGraphics();

        String path = For4in2Demo.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        String imgPath = path.substring(0, path.lastIndexOf(File.separator) + 1) + File.separator + "test.jpg";
        System.out.println("imgPath = " + imgPath);
        //读取原始位图
        Image srcImage = ImageIO.read(new File(imgPath));

        //将原始位图缩小后绘制到bufferedImage对象中
        graphics.drawImage(srcImage, 0, 0, width, height, null);
        //将bufferedImage对象输出到磁盘上

        BufferedImage binImage = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_BINARY);
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                int rgb = bufferedImage.getRGB(i, j);
                int oneGate = rgb & 0xffffff;
                int randomNum = new Random().nextInt(0xffffff);
                int binValue = 0;

                //0是黑  1是白
                if (oneGate > 0xf2ffff) {
                    binValue = 0xffffff;
                } else if (oneGate < 0x900000) {
                    binValue = 0;
                } else if (oneGate > randomNum) {
                    binValue = 0xffffff;
                } else {
                    binValue = 0;
                }

                binImage.setRGB(i, j, binValue);
            }
        }
        final byte[] pixels = ((DataBufferByte) binImage.getRaster().getDataBuffer()).getData();
        System.out.println(" pixels size = " + pixels.length);
        return pixels;
    }

    /**
     * 获取文字图片的字节数组
     *
     * @return
     */
    public static byte[] getFontImg() {
        int width = 400;
        int height = 300;
        BufferedImage image = new BufferedImage(400, 300,
                BufferedImage.TYPE_BYTE_BINARY);
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                image.setRGB(i, j, 0xffffff);
            }
        }
        Graphics2D g = image.createGraphics();
        //g.setFont(new java.awt.Font("叶根友毛笔行书2.0版", Font.PLAIN, 25));
        g.setFont(new java.awt.Font("微软雅黑", Font.PLAIN, 30));
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
     * 显示
     *
     * @throws IOException
     * @throws InterruptedException
     */
    public static void display(byte[] pixels) throws IOException, InterruptedException {
        //sendCommand(0x92); //Partial Out,python驱动中有设置这个，奇怪，设置局部刷新？这个指令不设置也是可以的
        setLut();
        sendCommand(0x10);
        for (int i = 0; i < 300 * 400 / 8; i++) {
            sendData(0xFF);
        }
        sendCommand(0x13);
        for (int i = 0; i < 300 * 400 / 8; i++) {
            sendData(pixels[i]);
        }
        sendCommand(0x12);
        readBusy();
    }

    /**
     * 清屏
     *
     * @throws IOException
     * @throws InterruptedException
     */
    public static void clear() throws IOException, InterruptedException {
        //sendCommand(0x92);
        setLut();
        sendCommand(0x10);
        for (int i = 0; i < 300 * 400; i++) {
            sendData(0xFF);
        }
        sendCommand(0x13);
        for (int i = 0; i < 300 * 400; i++) {
            sendData(0xFF);
        }
        sendCommand(0x12);
        readBusy();
    }


    final static int[] lut_vcom0 = {0x00, 0x17, 0x00, 0x00, 0x00, 0x02,
            0x00, 0x17, 0x17, 0x00, 0x00, 0x02,
            0x00, 0x0A, 0x01, 0x00, 0x00, 0x01,
            0x00, 0x0E, 0x0E, 0x00, 0x00, 0x02,
            0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
            0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
            0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00};
    final static int[] lut_ww = {0x40, 0x17, 0x00, 0x00, 0x00, 0x02,
            0x90, 0x17, 0x17, 0x00, 0x00, 0x02,
            0x40, 0x0A, 0x01, 0x00, 0x00, 0x01,
            0xA0, 0x0E, 0x0E, 0x00, 0x00, 0x02,
            0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
            0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
            0x00, 0x00, 0x00, 0x00, 0x00, 0x00};
    final static int[] lut_bw = {0x40, 0x17, 0x00, 0x00, 0x00, 0x02,
            0x90, 0x17, 0x17, 0x00, 0x00, 0x02,
            0x40, 0x0A, 0x01, 0x00, 0x00, 0x01,
            0xA0, 0x0E, 0x0E, 0x00, 0x00, 0x02,
            0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
            0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
            0x00, 0x00, 0x00, 0x00, 0x00, 0x00};
    final static int[] lut_wb = {0x80, 0x17, 0x00, 0x00, 0x00, 0x02,
            0x90, 0x17, 0x17, 0x00, 0x00, 0x02,
            0x80, 0x0A, 0x01, 0x00, 0x00, 0x01,
            0x50, 0x0E, 0x0E, 0x00, 0x00, 0x02,
            0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
            0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
            0x00, 0x00, 0x00, 0x00, 0x00, 0x00};
    final static int[] lut_bb = {0x80, 0x17, 0x00, 0x00, 0x00, 0x02,
            0x90, 0x17, 0x17, 0x00, 0x00, 0x02,
            0x80, 0x0A, 0x01, 0x00, 0x00, 0x01,
            0x50, 0x0E, 0x0E, 0x00, 0x00, 0x02,
            0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
            0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
            0x00, 0x00, 0x00, 0x00, 0x00, 0x00};
}
