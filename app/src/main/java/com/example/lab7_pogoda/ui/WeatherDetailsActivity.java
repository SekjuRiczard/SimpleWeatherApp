package com.example.lab7_pogoda.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.lab7_pogoda.R;

import com.example.lab7_pogoda.response.ForecastResponse;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class WeatherDetailsActivity extends AppCompatActivity {
    private TextView cityName, temp, pressure, humidity, updateTime, min,max;
    private ImageView weatherIcon;
    private LineChart lineChart;
    private WeatherViewModel weatherViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.weather_details);

        cityName = findViewById(R.id.cityName);
        temp = findViewById(R.id.Temp);
        pressure = findViewById(R.id.Pressure);
        humidity = findViewById(R.id.Humidity);
        weatherIcon = findViewById(R.id.img);
        updateTime = findViewById(R.id.updateTime);
        min = findViewById(R.id.temp_min);
        max = findViewById(R.id.temp_max);
        lineChart = findViewById(R.id.lineChart);


        weatherViewModel = new ViewModelProvider(this).get(WeatherViewModel.class);

        String city = getIntent().getStringExtra("CITY_NAME");
        Log.d("CITY NAME:",city.toString());

        weatherViewModel.loadWeather(city);


        weatherViewModel.getWeatherData().observe(this, weather -> {
            if (weather != null) {
                cityName.setText(weather.getName());
                temp.setText(weather.getMainInfo().getTemperature() + " ℃");
                pressure.setText(weather.getMainInfo().getPressure() + " hPa");
                humidity.setText(weather.getMainInfo().getHumidity() + " %");
                min.setText(weather.getMainInfo().getTempMin() + " ℃");
                max.setText(weather.getMainInfo().getTempMax() + " ℃");
                String iconCode = weather.getWeatherInfoList().get(0).getIcon();
                String iconURL = "http://openweathermap.org/img/w/" + iconCode + ".png";
                String updateTimeStr = formatUnixTime(weather.getDt(), weather.getTimezone());
                updateTime.setText("Ostatnia aktualizacja: " + updateTimeStr);
                Picasso.get().load(iconURL).into(weatherIcon);
            } else {
                cityName.setText("Błąd pobierania danych.");
            }
        });

        weatherViewModel.load5DayForecast("Krakow");

        weatherViewModel.getForecastData().observe(this, forecast -> {
            if (forecast != null) {
                updateChart(forecast);
            } else {
                Log.d("WeatherDetailsActivity", "❌ Brak danych do wyświetlenia.");
            }
        });
    }

    private void updateChart(ForecastResponse forecast) {
        List<Entry> entries = new ArrayList<>();
        int day = 1;

        for (ForecastResponse.WeatherData daily : forecast.getForecastList()) {
            entries.add(new Entry(day, daily.getTemperature().getTemp()));
            day++;
        }

        LineDataSet dataSet = new LineDataSet(entries, "Temperatura (°C)");
        dataSet.setColor(Color.BLUE);
        dataSet.setLineWidth(3f);
        dataSet.setCircleRadius(6f);
        dataSet.setCircleColor(Color.RED);
        dataSet.setDrawCircleHole(false);
        dataSet.setValueTextSize(12f);
        dataSet.setValueTextColor(Color.BLACK);
        dataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        dataSet.setDrawValues(true);
        dataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        dataSet.setCubicIntensity(0.2f);

        LineData lineData = new LineData(dataSet);
        lineChart.setData(lineData);


        lineChart.setDrawGridBackground(false);
        lineChart.getDescription().setText("Prognoza temperatury na 5 dni");
        lineChart.getDescription().setTextSize(14f);
        lineChart.getDescription().setTextColor(Color.GRAY);
        lineChart.getLegend().setTextSize(14f);
        lineChart.getLegend().setTextColor(Color.DKGRAY);
        lineChart.getLegend().setForm(Legend.LegendForm.LINE);


        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextSize(12f);
        xAxis.setTextColor(Color.BLACK);
        xAxis.setDrawGridLines(true);
        xAxis.setGranularity(1f);
        xAxis.setLabelCount(entries.size());
        xAxis.setValueFormatter(new IndexAxisValueFormatter(getDaysLabels(forecast)));


        YAxis leftAxis = lineChart.getAxisLeft();
        leftAxis.setTextSize(12f);
        leftAxis.setTextColor(Color.BLACK);
        leftAxis.setDrawGridLines(true);
        leftAxis.setGranularity(1f);
        lineChart.getAxisRight().setEnabled(false);

        lineChart.invalidate();
    }




    private List<String> getDaysLabels(ForecastResponse forecast) {
        List<String> labels = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("EEE", Locale.getDefault());

        for (ForecastResponse.WeatherData daily : forecast.getForecastList()) {
            labels.add(sdf.format(new Date(daily.getTimestamp() * 1000L)));
        }
        return labels;
    }
    public String formatUnixTime(Long dt, int timezoneOffset) {
        if (dt == null) return "Brak danych";

        long localTimeMillis = (dt + timezoneOffset) * 1000L;

        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));

        return sdf.format(new Date(localTimeMillis));
    }
}
