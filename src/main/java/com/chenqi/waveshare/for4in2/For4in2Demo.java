package com.chenqi.waveshare.for4in2;

import com.pi4j.io.gpio.*;
import com.pi4j.io.spi.SpiChannel;
import com.pi4j.io.spi.SpiDevice;
import com.pi4j.io.spi.SpiFactory;

import java.io.IOException;

public class For4in2Demo {

    public final static int WIDTH = 400;
    public final static int HEIGHT = 300;

    private For4in2Demo(){}

    private static For4in2Demo for4in2Demo = new For4in2Demo();

    public static For4in2Demo getInstance(){
        return for4in2Demo;
    }

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



    /**
     * reset墨水屏
     *
     * @throws InterruptedException
     */
    public void reset() throws InterruptedException {
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
    public void sendCommand(int date) throws IOException {
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
    public void sendData(int date) throws IOException {
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
    public void readBusy() throws IOException, InterruptedException {
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
    public void init() throws IOException, InterruptedException {
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
    public void setLut() throws IOException {
        sendCommand(0x20);
        for (int data : LUT_VCOM0) {
            sendData(data);
        }

        sendCommand(0x21);
        for (int data : LUT_WW) {
            sendData(data);
        }

        sendCommand(0x22);
        for (int data : LUT_BW) {
            sendData(data);
        }

        sendCommand(0x23);
        for (int data : LUT_BB) {
            sendData(data);
        }

        sendCommand(0x24);
        for (int data : LUT_WB) {
            sendData(data);
        }

    }


    /**
     * 显示
     *
     * @throws IOException
     * @throws InterruptedException
     */
    public void display(byte[] pixels) throws IOException, InterruptedException {
        System.out.println("just display.no gray.");
        //sendCommand(0x92); //Partial Out,python驱动中有设置这个，奇怪，设置局部刷新？这个指令不设置也是可以的
        setLut();
        sendCommand(0x10);
        for (int i = 0; i < HEIGHT * WIDTH / 8; i++) {
            sendData(0xFF);
        }
        sendCommand(0x13);
        for (int i = 0; i < HEIGHT * WIDTH / 8; i++) {
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
    public void clear() throws IOException, InterruptedException {
        //sendCommand(0x92);
        setLut();
        sendCommand(0x10);
        for (int i = 0; i < HEIGHT * WIDTH; i++) {
            sendData(0xFF);
        }
        sendCommand(0x13);
        for (int i = 0; i < HEIGHT * WIDTH; i++) {
            sendData(0xFF);
        }
        sendCommand(0x12);
        readBusy();
    }

    public void initGray() throws InterruptedException, IOException {
        System.out.println("init Gray");
        reset();

        sendCommand(0x01); //Power Setting
        sendData(0x03);
        sendData(0x00);
        sendData(0x2b);
        sendData(0x2b);
        sendData(0x13);//python驱动中未写入该值，但是数据手册中写入了该值

        sendCommand(0x06); //Booster Soft Start
        sendData(0x17);
        sendData(0x17);
        sendData(0x17);

        sendCommand(0x04); //Power ON
        readBusy();

        sendCommand(0x00); //Panel Setting
        sendData(0x3f);
        //sendData(0x0d); //python驱动中多写入了0d,不知道是何用意

        sendCommand(0x30); //PLL control
        sendData(0x3c); //3A 100HZ   29 150Hz 39 200HZ  31 171HZ

        sendCommand(0x61); //Resolution setting
        sendData(0x01);
        sendData(0x90);
        sendData(0x01);
        sendData(0x2c);

        sendCommand(0x82); //VCM_DC Setting
        sendData(0x12);

        sendCommand(0X50); //Vcom and data interval setting
        sendData(0x97);
    }

    final static int[] LUT_VCOM0 = {0x00, 0x17, 0x00, 0x00, 0x00, 0x02,
            0x00, 0x17, 0x17, 0x00, 0x00, 0x02,
            0x00, 0x0A, 0x01, 0x00, 0x00, 0x01,
            0x00, 0x0E, 0x0E, 0x00, 0x00, 0x02,
            0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
            0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
            0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00};
    final static int[] LUT_WW = {0x40, 0x17, 0x00, 0x00, 0x00, 0x02,
            0x90, 0x17, 0x17, 0x00, 0x00, 0x02,
            0x40, 0x0A, 0x01, 0x00, 0x00, 0x01,
            0xA0, 0x0E, 0x0E, 0x00, 0x00, 0x02,
            0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
            0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
            0x00, 0x00, 0x00, 0x00, 0x00, 0x00};
    final static int[] LUT_BW = {0x40, 0x17, 0x00, 0x00, 0x00, 0x02,
            0x90, 0x17, 0x17, 0x00, 0x00, 0x02,
            0x40, 0x0A, 0x01, 0x00, 0x00, 0x01,
            0xA0, 0x0E, 0x0E, 0x00, 0x00, 0x02,
            0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
            0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
            0x00, 0x00, 0x00, 0x00, 0x00, 0x00};
    final static int[] LUT_WB = {0x80, 0x17, 0x00, 0x00, 0x00, 0x02,
            0x90, 0x17, 0x17, 0x00, 0x00, 0x02,
            0x80, 0x0A, 0x01, 0x00, 0x00, 0x01,
            0x50, 0x0E, 0x0E, 0x00, 0x00, 0x02,
            0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
            0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
            0x00, 0x00, 0x00, 0x00, 0x00, 0x00};
    final static int[] LUT_BB = {0x80, 0x17, 0x00, 0x00, 0x00, 0x02,
            0x90, 0x17, 0x17, 0x00, 0x00, 0x02,
            0x80, 0x0A, 0x01, 0x00, 0x00, 0x01,
            0x50, 0x0E, 0x0E, 0x00, 0x00, 0x02,
            0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
            0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
            0x00, 0x00, 0x00, 0x00, 0x00, 0x00};





}
