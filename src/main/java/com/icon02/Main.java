package com.icon02;

import com.icon02.VentuskyParser.VentuskyParser;
import com.icon02.VentuskyParser.utils.Forecast;

import java.io.IOException;

public class Main {

    public static void main(String... args) throws IOException {
        final long time = System.currentTimeMillis();
        VentuskyParser parser = new VentuskyParser(48.748429, 15.125223);
        Forecast forecast = parser.getForecast();

        System.out.println((System.currentTimeMillis() - time) + " ms");
    }
}
