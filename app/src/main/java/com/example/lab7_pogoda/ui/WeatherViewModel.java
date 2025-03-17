package com.example.lab7_pogoda.ui;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.lab7_pogoda.response.ForecastResponse;
import com.example.lab7_pogoda.response.WeatherResponse;
import com.example.lab7_pogoda.repository.WeatherRepository;

public class WeatherViewModel extends ViewModel {
    private final WeatherRepository weatherRepository;
    private LiveData<WeatherResponse> weatherData;
    private LiveData<ForecastResponse> forecastData;

    public WeatherViewModel() {
        weatherRepository = new WeatherRepository();
    }

    public void loadWeather(String city) {
        weatherData = weatherRepository.fetchWeatherData(city);
    }
    public void load5DayForecast(String city) {
        forecastData = weatherRepository.fetch5DayForecast(city);
    }

    public LiveData<WeatherResponse> getWeatherData() {
        return weatherData;
    }
    public LiveData<ForecastResponse> getForecastData() {
        return forecastData;
    }
}
