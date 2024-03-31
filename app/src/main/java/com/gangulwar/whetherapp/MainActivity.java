package com.gangulwar.whetherapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private TextView temperatureTextView;
    private TextView humidityTextView;
    private TextView windSpeedTextView;
    private Button searchButton;
    private TextView selectedCity;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);

        searchButton = findViewById(R.id.search_button);
        temperatureTextView = findViewById(R.id.temperature);
        humidityTextView = findViewById(R.id.humidityTextView);
        windSpeedTextView = findViewById(R.id.windSpeedTextView);

        selectedCity = findViewById(R.id.city);

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

                progressDialog.show();

                String city = selectedCity.getText().toString();
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                        .url("http://api.weatherapi.com/v1/current.json?key=fa4d2771445047d48fb100343243103&q=" + city)
                        .build();

                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        progressDialog.dismiss();
                        Toast.makeText(MainActivity.this, "Failure try again!", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        progressDialog.dismiss();
                        if (response.isSuccessful()) {
                            final String responseData = response.body().string();
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        JSONObject jsonObject = new JSONObject(responseData);
                                        updateWeatherUI(jsonObject);
                                    } catch (JSONException e) {
                                        Toast.makeText(MainActivity.this, "Failure try again!", Toast.LENGTH_SHORT).show();
                                        e.printStackTrace();
                                    }
                                }
                            });
                        } else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(MainActivity.this, "Failure try again!", Toast.LENGTH_SHORT).show();
                                }
                            });

                            Log.e("API Error", "Unsuccessful response: " + response);
                        }
                    }
                });
            }
        });

    }

    private void updateWeatherUI(JSONObject jsonObject) {
        if (jsonObject != null) {
            try {
                JSONObject current = jsonObject.getJSONObject("current");
                double temperature = current.getDouble("temp_c");
                int humidity = current.getInt("humidity");
                double windSpeed = current.getDouble("wind_kph");

                temperatureTextView.setText(String.valueOf(temperature));
                humidityTextView.setText(String.valueOf(humidity));
                windSpeedTextView.setText(String.valueOf(windSpeed));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }


}
