package com.example.lab7_pogoda.response;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class ForecastResponse {

    @SerializedName("city")
    private City city;

    @SerializedName("list")
    private List<WeatherData> forecastList;

    public String getCityName() {
        return city.getName();
    }

    public List<WeatherData> getForecastList() {
        return forecastList;
    }

    public static class City {
        @SerializedName("name")
        private String name;

        public String getName() {
            return name;
        }
    }

    public static class WeatherData {
        @SerializedName("dt")
        private long timestamp;

        @SerializedName("main")
        private Temperature temperature;

        public long getTimestamp() {
            return timestamp;
        }

        public Temperature getTemperature() {
            return temperature;
        }
    }

    public static class Temperature {
        @SerializedName("temp")
        private float temp;

        public float getTemp() {
            return temp;
        }
    }
}
