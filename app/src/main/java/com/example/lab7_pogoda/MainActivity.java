package com.example.lab7_pogoda;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.lab7_pogoda.ui.WeatherDetailsActivity;

public class MainActivity extends AppCompatActivity {
    private Button btnCheckWeather;
    private EditText cityNameInput;
    private TextView errorMsg;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnCheckWeather = findViewById(R.id.btn_checkWeather);
        cityNameInput = findViewById(R.id.CityName);
        errorMsg = findViewById(R.id.MMM);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);

        btnCheckWeather.setOnClickListener(view -> {
            String city = cityNameInput.getText().toString().trim();
            if (!city.isEmpty()) {
                Intent intent = new Intent(MainActivity.this, WeatherDetailsActivity.class);
                intent.putExtra("CITY_NAME", city);
                startActivity(intent);
            } else {
                errorMsg.setVisibility(View.VISIBLE);
                errorMsg.setText("Wprowadź nazwę miasta!");
            }
        });
    }
}
