package com.example.lab7_pogoda.response;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class WeatherResponse {
    @SerializedName("main")
    private MainInfo mainInfo;

    @SerializedName("timezone")
    private int timezone;

    @SerializedName("name")
    private String name;

    @SerializedName("weather")
    private List<WeatherInfo> weatherInfoList;

    @SerializedName("dt")
    private Long dt;

    public Long getDt() {
        return dt;
    }

    public String getName() {
        return name;
    }

    public MainInfo getMainInfo() {
        return mainInfo;
    }

    public int getTimezone() {
        return timezone;
    }

    public List<WeatherInfo> getWeatherInfoList() {
        return weatherInfoList;
    }
}
