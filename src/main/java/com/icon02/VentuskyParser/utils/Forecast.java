package com.icon02.VentuskyParser.utils;

import org.json.JSONObject;

public class Forecast {

    private static final String day_keys[] = {
            "d_1", "d_2", "d_3", "d_4", "d_5", "d_6", "d_7"
    };
    private static final String hour_keys[] = {
            "h_0", "h_3", "h_6", "h_9", "h_12", "h_15", "h_18", "h_21"
    };
    private static final String key_temp = "td";
    private static final String key_wind_speed = "vsd";
    private static final String key_wind_direction = "vdId";
    private static final String key_precipitation = "srd";

    private static int hourFromKey(String key) {
        return Integer.parseInt(key.substring(2)) + 1;
    }

    private static int getIntFromValue(String val) {
        StringBuilder sb = new StringBuilder();
        for(int i = 0; val.charAt(i) >= '0' && val.charAt(i) <= '9' || val.charAt(i) == '-' || val.charAt(i) == '+'; i++) sb.append(val.charAt(i));

        return Integer.parseInt(sb.toString());
    }

    private static String getUnitFromValue(String val) {
        StringBuilder sb = new StringBuilder();
        val = val.trim();

        for(int i = val.length() - 1; isLetter(val.charAt(i)); i--) {
            sb.append(val.charAt(i));
        }

        return sb.toString();
    }

    private static boolean isLetter(char c) {
        return (c >= 'a' && c <= 'z')
                || (c >= 'A' && c <= 'Z')
                || c == 'ü' || c == 'Ü'
                || c == 'ä' || c == 'Ä'
                || c == 'ö' || c == 'Ö';
    }

    /*================OBJECT================*/
    private ForecastDay day1;

    public Forecast(JSONObject json) {

        ForecastDay prevDay = null;

        for(String dayKey : day_keys) {
            final JSONObject dayJson = json.getJSONObject(dayKey);
            String maxTemp = null;
            String minTemp = null;
            String maxWindSpeed = null;
            String sumPrecipitation = null;

            ForecastHour hour1 = null;
            ForecastHour prevHour = null;

            for(String hourKey : hour_keys) {
                final JSONObject hourJson = dayJson.getJSONObject(hourKey);

                int hour = hourFromKey(hourKey);
                String temp = hourJson.getString(key_temp);
                String windSpeed = hourJson.getString(key_wind_speed);
                String windDirection = hourJson.getString(key_wind_direction);
                String precipitation = hourJson.getString(key_precipitation);

                ForecastHour forecastHour = new ForecastHour(hour, temp, precipitation, windDirection, windSpeed, null);

                if(prevHour == null) {
                    prevHour = forecastHour;
                    hour1 = forecastHour;
                } else {
                    prevHour.setNext(forecastHour);
                    prevHour = forecastHour;
                }

                // daily stuff
                if(maxTemp == null || maxTemp.length() == 0) maxTemp = temp;
                else {
                    if(getIntFromValue(maxTemp) < getIntFromValue(temp)) maxTemp = temp;
                }
                if(minTemp == null || minTemp.length() == 0) minTemp = temp;
                else {
                    if(getIntFromValue(minTemp) > getIntFromValue(temp)) minTemp = temp;
                }
                if(maxWindSpeed == null || maxWindSpeed.length() == 0) maxWindSpeed = windSpeed;
                else {
                    if(getIntFromValue(maxWindSpeed) < getIntFromValue(windSpeed)) maxWindSpeed = windSpeed;
                }
                if(sumPrecipitation == null || sumPrecipitation.length() == 0) sumPrecipitation = precipitation;
                else {
                    int cVal = getIntFromValue(sumPrecipitation);
                    int plusVal = getIntFromValue(precipitation);

                    sumPrecipitation = (cVal + plusVal) + " " + getUnitFromValue(precipitation);
                }
            }

            // new ForecastDay
            ForecastDay temp = new ForecastDay(maxTemp, minTemp, maxWindSpeed, sumPrecipitation, hour1);
            if(day1 == null) day1 = temp;
            if(prevDay != null) prevDay.setNext(temp);
            prevDay = temp;
        }
    }



    public static class ForecastDay {

        private String maxTemp;
        private String minTemp;
        private String maxWindSpeed;
        private String precipitation;
        private ForecastHour first;

        private ForecastDay next;

        public ForecastDay(String maxTemp, String minTemp, String maxWindSpeed, String precipitation, ForecastHour first) {
            this.maxTemp = maxTemp;
            this.minTemp = minTemp;
            this.maxWindSpeed = maxWindSpeed;
            this.precipitation = precipitation;
            this.first = first;
        }

        public String getMaxTemp() {
            return maxTemp;
        }

        public void setMaxTemp(String maxTemp) {
            this.maxTemp = maxTemp;
        }

        public String getMinTemp() {
            return minTemp;
        }

        public void setMinTemp(String minTemp) {
            this.minTemp = minTemp;
        }

        public String getMaxWindSpeed() {
            return maxWindSpeed;
        }

        public void setMaxWindSpeed(String maxWindSpeed) {
            this.maxWindSpeed = maxWindSpeed;
        }

        public String getPrecipitation() {
            return precipitation;
        }

        public void setPrecipitation(String precipitation) {
            this.precipitation = precipitation;
        }

        public ForecastDay getNext() {
            return next;
        }

        public void setNext(ForecastDay next) {
            this.next = next;
        }
    }

    public static class ForecastHour {

        private int hour; // in 24h format
        private String temp;
        private String precipitation;
        private String windDirection;
        private String windSpeed;

        private ForecastHour next;

        public ForecastHour(int hour, String temp, String precipitation, String windDirection, String windSpeed, ForecastHour next) {
            this.hour = hour;
            this.temp = temp;
            this.precipitation = precipitation;
            this.windDirection = windDirection;
            this.windSpeed = windSpeed;
            this.next = next;
        }

        public int getHour() {
            return hour;
        }

        public void setHour(int hour) {
            this.hour = hour;
        }

        public String getTemp() {
            return temp;
        }

        public void setTemp(String temp) {
            this.temp = temp;
        }

        public String getPrecipitation() {
            return precipitation;
        }

        public void setPrecipitation(String precipitation) {
            this.precipitation = precipitation;
        }

        public String getWindDirection() {
            return windDirection;
        }

        public void setWindDirection(String windDirection) {
            this.windDirection = windDirection;
        }

        public String getWindSpeed() {
            return windSpeed;
        }

        public void setWindSpeed(String windSpeed) {
            this.windSpeed = windSpeed;
        }

        public ForecastHour getNext() {
            return next;
        }

        public void setNext(ForecastHour next) {
            this.next = next;
        }
    }
}
