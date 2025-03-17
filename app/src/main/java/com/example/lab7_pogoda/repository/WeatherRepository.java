package com.example.lab7_pogoda.repository;

import android.util.Log;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.lab7_pogoda.api.ApiService;
import com.example.lab7_pogoda.api.RetrofitClient;
import com.example.lab7_pogoda.response.ForecastResponse;
import com.example.lab7_pogoda.response.WeatherResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WeatherRepository {
    private static final String UNITS = "metric";

    private final ApiService weatherService;

    public WeatherRepository() {
        weatherService = RetrofitClient.getRetrofit().create(ApiService.class);
    }

    public LiveData<WeatherResponse> fetchWeatherData(String city) {
        MutableLiveData<WeatherResponse> weatherData = new MutableLiveData<>();
        Log.v("WeatherRepository", "fetchWeatherData");
        weatherService.getWeather(city, RetrofitClient.getApiKey() , UNITS).enqueue(new Callback<WeatherResponse>() {
            @Override
            public void onResponse(Call<WeatherResponse> call, Response<WeatherResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    weatherData.setValue(response.body()); // Przekazanie danych do LiveData
                    Log.d("WeatherRepository", weatherData.toString());
                }else{
                    weatherData.setValue(null);
                    Log.v("WeatherRepository", "brak danych");
                }
            }

            @Override
            public void onFailure(Call<WeatherResponse> call, Throwable t) {
                weatherData.setValue(null); // Obsługa błędu
            }
        });

        return weatherData;
    }

    public LiveData<ForecastResponse> fetch5DayForecast(String city) {

        MutableLiveData<ForecastResponse> forecastData = new MutableLiveData<>();
        weatherService.get5DayForecast(city, RetrofitClient.getApiKey(), UNITS).enqueue(new Callback<ForecastResponse>() {
            @Override
            public void onResponse(Call<ForecastResponse> call, Response<ForecastResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    forecastData.setValue(response.body());

                } else {
                    forecastData.setValue(null);
                }
            }

            @Override
            public void onFailure(Call<ForecastResponse> call, Throwable t) {
                Log.d( "🚨 Błąd sieci: ",t.getMessage().toString());
            }
        });

        return forecastData;
    }
}
