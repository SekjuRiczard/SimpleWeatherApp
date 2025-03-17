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
import com.github.mikephil.charting.components.Description;
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


        // Obserwowanie zmian w danych
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
            entries.add(new Entry(day, daily.getTemperature().getTemp())); // Dzień jako X, Temperatura jako Y
            day++;
        }

        LineDataSet dataSet = new LineDataSet(entries, "Temperatura (°C)");
        dataSet.setColor(Color.BLUE); // Kolor linii
        dataSet.setLineWidth(3f); // Grubość linii
        dataSet.setCircleRadius(6f); // Rozmiar punktów
        dataSet.setCircleColor(Color.RED); // Kolor punktów
        dataSet.setDrawCircleHole(false); // Wypełnione punkty
        dataSet.setValueTextSize(12f); // Rozmiar tekstu wartości
        dataSet.setValueTextColor(Color.BLACK); // Kolor wartości
        dataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER); // Gładkie linie
        dataSet.setDrawValues(true); // Pokazuj wartości na wykresie
        dataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER); // Płynne przejścia
        dataSet.setCubicIntensity(0.2f); // Intensywność wygładzenia (0.1 - 1.0)

        LineData lineData = new LineData(dataSet);
        lineChart.setData(lineData);

        // Ustawienia wykresu
        lineChart.setDrawGridBackground(false);
        lineChart.getDescription().setText("Prognoza temperatury na 5 dni");
        lineChart.getDescription().setTextSize(14f);
        lineChart.getDescription().setTextColor(Color.GRAY);
        lineChart.getLegend().setTextSize(14f);
        lineChart.getLegend().setTextColor(Color.DKGRAY);
        lineChart.getLegend().setForm(Legend.LegendForm.LINE);

        // Ustawienia osi X (dni)
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextSize(12f);
        xAxis.setTextColor(Color.BLACK);
        xAxis.setDrawGridLines(true);
        xAxis.setGranularity(1f); // Co 1 dzień
        xAxis.setLabelCount(entries.size()); // Ustaw liczbę etykiet
        xAxis.setValueFormatter(new IndexAxisValueFormatter(getDaysLabels(forecast))); // Ustaw nazwy dni

        // Ustawienia osi Y (temperatura)
        YAxis leftAxis = lineChart.getAxisLeft();
        leftAxis.setTextSize(12f);
        leftAxis.setTextColor(Color.BLACK);
        leftAxis.setDrawGridLines(true);
        leftAxis.setGranularity(1f); // Co 1 stopień
        lineChart.getAxisRight().setEnabled(false); // Wyłącz prawą oś

        lineChart.invalidate(); // Odświeżenie wykresu
    }

    /**
     * Generuje etykiety dni na osi X
     */
    private List<String> getDaysLabels(ForecastResponse forecast) {
        List<String> labels = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("EEE", Locale.getDefault()); // Skróty dni np. "Pon", "Wt"

        for (ForecastResponse.WeatherData daily : forecast.getForecastList()) {
            labels.add(sdf.format(new Date(daily.getTimestamp() * 1000L))); // Konwersja czasu Unix na datę
        }
        return labels;
    }

    public String formatUnixTime(Long dt, int timezoneOffset) {
        if (dt == null) return "Brak danych";

        // Obliczamy czas lokalny
        long localTimeMillis = (dt + timezoneOffset) * 1000L;

        // Formatowanie do HH:mm
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
        sdf.setTimeZone(TimeZone.getTimeZone("UTC")); // Ustawienie UTC jako bazowy czas

        return sdf.format(new Date(localTimeMillis));
    }
}
