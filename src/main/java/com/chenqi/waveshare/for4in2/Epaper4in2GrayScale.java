package com.chenqi.waveshare.for4in2;

import java.io.IOException;

public class Epaper4in2GrayScale {

    final static int[] EPD_4IN2_PARTIAL_LUT_VCOM1 = {
            0x00	,0x19	,0x01	,0x00	,0x00	,0x01,
            0x00	,0x00	,0x00	,0x00	,0x00	,0x00,
            0x00	,0x00	,0x00	,0x00	,0x00	,0x00,
            0x00	,0x00	,0x00	,0x00	,0x00	,0x00,
            0x00	,0x00	,0x00	,0x00	,0x00	,0x00,
            0x00	,0x00	,0x00	,0x00	,0x00	,0x00,
            0x00	,0x00	,0x00	,0x00	,0x00	,0x00,
            0x00	,0x00
    };

    final static int[] EPD_4IN2_PARTIAL_LUT_WW1 = {
            0x00	,0x19	,0x01	,0x00	,0x00	,0x01,
            0x00	,0x00	,0x00	,0x00	,0x00	,0x00,
            0x00	,0x00	,0x00	,0x00	,0x00	,0x00,
            0x00	,0x00	,0x00	,0x00	,0x00	,0x00,
            0x00	,0x00	,0x00	,0x00	,0x00	,0x00,
            0x00	,0x00	,0x00	,0x00	,0x00	,0x00,
            0x00	,0x00	,0x00	,0x00	,0x00	,0x00
    };

    final static int[] EPD_4IN2_PARTIAL_LUT_BW1 = {
            0x80	,0x09	,0x02	,0x00	,0x00	,0x01,
            0x00	,0x00	,0x00	,0x00	,0x00	,0x00,
            0x00	,0x00	,0x00	,0x00	,0x00	,0x00,
            0x00	,0x00	,0x00	,0x00	,0x00	,0x00,
            0x00	,0x00	,0x00	,0x00	,0x00	,0x00,
            0x00	,0x00	,0x00	,0x00	,0x00	,0x00,
            0x00	,0x00	,0x00	,0x00	,0x00	,0x00
    };

    final static int[] EPD_4IN2_PARTIAL_LUT_WB1 = {
            0x40	,0x02	,0x05	,0x00	,0x00	,0x01,
            0x00	,0x00	,0x00	,0x00	,0x00	,0x00,
            0x00	,0x00	,0x00	,0x00	,0x00	,0x00,
            0x00	,0x00	,0x00	,0x00	,0x00	,0x00,
            0x00	,0x00	,0x00	,0x00	,0x00	,0x00,
            0x00	,0x00	,0x00	,0x00	,0x00	,0x00,
            0x00	,0x00	,0x00	,0x00	,0x00	,0x00
    };

    final static int[] EPD_4IN2_PARTIAL_LUT_BB1 = {
            0x00	,0x19	,0x01	,0x00	,0x00	,0x01,
            0x00	,0x00	,0x00	,0x00	,0x00	,0x00,
            0x00	,0x00	,0x00	,0x00	,0x00	,0x00,
            0x00	,0x00	,0x00	,0x00	,0x00	,0x00,
            0x00	,0x00	,0x00	,0x00	,0x00	,0x00,
            0x00	,0x00	,0x00	,0x00	,0x00	,0x00,
            0x00	,0x00	,0x00	,0x00	,0x00	,0x00
    };

}
