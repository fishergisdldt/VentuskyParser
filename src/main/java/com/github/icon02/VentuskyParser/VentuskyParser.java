package com.github.icon02.VentuskyParser;

import com.github.icon02.VentuskyParser.utils.Forecast;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


import java.io.IOException;

public class VentuskyParser {

    public static final int ERROR = -1;

    public static final int METRIC_CELSIUS = 1;
    public static final int METRIC_FAHRENHEIT = 2;

    public static final int METRIC_KMH = 3;
    public static final int METRIC_MPH = 4;

    public static final int METRIC_MM = 5;
    public static final int METRIC_INCHES = 6;
    public static final int METRIC_CM = 7;

    private static final String baseURL = "https://www.ventusky.com/";

    private static final String aside_element_id = "aside_inner";

    private double lat;
    private double lan;

    private Document document;

    private Element aside;

    /**
     * (0 °C × 9/5) + 32 = 32 °F
     *
     * @param cel
     * @return
     */
    public static float celsius2Fahrenheit(float cel) {
        return (cel * (9f / 5f)) + 32;
    }

    /**
     * (32 °F − 32) × 5/9 = 0 °C
     *
     * @param fahrenheit
     * @return
     */
    public static float fahrenheit2Celsius(float fahrenheit) {
        return (fahrenheit - 32) * (5f / 9f);
    }

    public VentuskyParser(double lat, double lan) throws IOException {
        this.lat = lat;
        this.lan = lan;

        refresh();
    }

    /**
     * Reconnects to ventusky and gets current data.
     * Data will remain the same until this method is called.
     * @throws IOException
     */
    public void refresh() throws IOException {
        aside = Jsoup.connect(getFullUrl()).get().body().getElementById(aside_element_id);
    }

    public String getCurrentTemperature() {
        Element actual = aside.getElementById("actual");
        Elements temperature = actual.getElementsByClass("temperature");
        String data = temperature.get(0).childNode(0).toString(); // will be something link "4 °C"

        return data;
    }

    private String getFullUrl() {
        return baseURL + lat + ";" + lan;
    }

    private int toIntValue(String val) {
        String croppedStart = val;
        while (croppedStart.charAt(0) < '0' && croppedStart.charAt(0) > '9') croppedStart = croppedStart.substring(1);

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < croppedStart.length() && croppedStart.charAt(i) >= '0' && croppedStart.charAt(i) <= '9'; i++) {
            sb.append(croppedStart.charAt(i));
        }

        return Integer.parseInt(sb.toString());
    }


    public Forecast getForecast() {
        Elements forecastElements = aside.getElementsByTag("forecast");
        Element forecast = forecastElements.get(0);
        String forecastData = forecast.attr("data-forecast");

        JSONObject json = new JSONObject(forecastData);

        return new Forecast(json);
    }
}
