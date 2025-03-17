package com.example.lab7_pogoda.response;

import com.google.gson.annotations.SerializedName;

public class WeatherInfo {
    @SerializedName("icon")
    private String icon;

    public String getIcon() {
        return icon;
    }
}
