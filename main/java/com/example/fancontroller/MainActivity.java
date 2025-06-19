package com.example.fancontroller;

import androidx.appcompat.app.AppCompatActivity;

import android.app.TimePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    private String currentNodemcuIp;
    private String baseUrl;

    private static final String TAG = "SmartFanControlApp";
    private static final String PREFS_NAME = "FanControllerPrefs";
    private static final String KEY_NODEMCU_IP = "nodemcu_ip";        // Key for storing IP in SharedPreferences
    private static final String DEFAULT_NODEMCU_IP = "192.168.212.110";


    // UI Components
    private TextView statusTextView;
    private TextView currentScheduleTextView;
    private EditText ipAddressEditText;
    private Button saveIpButton;

    // ExecutorService for background tasks
    private ExecutorService executorService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        executorService = Executors.newFixedThreadPool(2);


        statusTextView = findViewById(R.id.statusTextView);
        Button onButton = findViewById(R.id.onButton);
        Button offButton = findViewById(R.id.offButton);
        Button setScheduleButton = findViewById(R.id.setScheduleButton);
        Button cancelScheduleButton = findViewById(R.id.cancelScheduleButton);
        currentScheduleTextView = findViewById(R.id.currentScheduleTextView);
        ipAddressEditText = findViewById(R.id.ipAddressEditText);
        saveIpButton = findViewById(R.id.saveIpButton);


        loadNodemcuIp();


        ipAddressEditText.setText(currentNodemcuIp);


        onButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                executeHttpRequest(baseUrl + "/on", "ON", null);
            }
        });

        offButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                executeHttpRequest(baseUrl + "/off", "OFF", null);
            }
        });

        setScheduleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePickerDialog();
            }
        });

        cancelScheduleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                executeHttpRequest(baseUrl + "/cancel", "CANCEL_SCHEDULE", null);
            }
        });

        saveIpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveNodemcuIp();
            }
        });

        updateScheduleDisplay("None");
    }

    private void loadNodemcuIp() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        currentNodemcuIp = prefs.getString(KEY_NODEMCU_IP, DEFAULT_NODEMCU_IP);
        baseUrl = "http://" + currentNodemcuIp;
        Log.d(TAG, "Loaded NodeMCU IP: " + currentNodemcuIp);
        Toast.makeText(this, "Using IP: " + currentNodemcuIp, Toast.LENGTH_SHORT).show();
    }

    private void saveNodemcuIp() {
        String newIp = ipAddressEditText.getText().toString().trim();
        if (newIp.isEmpty() || !isValidIpAddress(newIp)) {
            Toast.makeText(this, "Please enter a valid IP address.", Toast.LENGTH_LONG).show();
            return;
        }

        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(KEY_NODEMCU_IP, newIp);
        editor.apply();

        currentNodemcuIp = newIp;
        baseUrl = "http://" + currentNodemcuIp;
        Toast.makeText(this, "IP Address saved: " + currentNodemcuIp, Toast.LENGTH_SHORT).show();
        Log.d(TAG, "Saved new NodeMCU IP: " + currentNodemcuIp);
    }
    private boolean isValidIpAddress(String ip) {
        // Regex for a valid IPv4 address (0-255 for each octet)
        String IP_PATTERN = "^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
                "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
                "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
                "([01]?\\d\\d?|2[0-4]\\d|25[0-5])$";
        return ip.matches(IP_PATTERN);
    }

    private void executeHttpRequest(String urlString, String commandType, String scheduleDisplayText) {
        executorService.submit(() -> {
            String requestResult; // var for req op or errors

            try {
                URL url = new URL(urlString);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(5000);
                connection.setReadTimeout(5000);

                int responseCode = connection.getResponseCode();
                Log.d(TAG, "Sending GET request to URL: " + urlString);
                Log.d(TAG, "Response Code: " + responseCode);

                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                Log.d(TAG, "NodeMCU Response: " + response.toString());
                requestResult = response.toString(); // Assign success result
            } catch (Exception e) {
                Log.e(TAG, "Error sending HTTP request: " + e.getMessage());
                requestResult = "Error: " + e.getMessage(); // Assign error result
            }

            final String finalResultForUi = requestResult;
            final String finalScheduleDisplayText = scheduleDisplayText;

            runOnUiThread(() -> {
                if (finalResultForUi.startsWith("Error:")) {
                    Toast.makeText(MainActivity.this, finalResultForUi, Toast.LENGTH_LONG).show();
                    statusTextView.setText("Fan Status: Error");
                } else {
                    Toast.makeText(MainActivity.this, "NodeMCU says: " + finalResultForUi, Toast.LENGTH_SHORT).show();

                    switch (commandType) {
                        case "ON":
                            statusTextView.setText("Fan Status: ON");
                            break;
                        case "OFF":
                            statusTextView.setText("Fan Status: OFF");
                            updateScheduleDisplay("None");
                            break;
                        case "SET_SCHEDULE":
                            statusTextView.setText("Fan Status: Scheduled");
                            updateScheduleDisplay(finalScheduleDisplayText);
                            break;
                        case "CANCEL_SCHEDULE":
                            statusTextView.setText("Fan Status: Manual");
                            updateScheduleDisplay("None");
                            break;
                    }
                }
            });
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdownNow();
            Log.d(TAG, "ExecutorService shut down.");
        }
    }

    private void updateScheduleDisplay(String scheduleText) {
        runOnUiThread(() -> currentScheduleTextView.setText("Current Schedule: " + scheduleText));
    }
    private void showTimePickerDialog() {
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int selectedHour, int selectedMinute) {
                        sendFanOffSchedule(selectedHour, selectedMinute);
                    }
                },
                hour,
                minute,
                false); // true for 24-hour format, false for 12-hour format
        timePickerDialog.show();
    }

    private void sendFanOffSchedule(int hour24, int minute) {

        String url = baseUrl + "/setofftime?hour=" + hour24 + "&minute=" + minute;

        String displayTime;
        if (hour24 == 0) {
            displayTime = String.format("12:%02d AM", minute);
        } else if (hour24 == 12) {
            displayTime = String.format("12:%02d PM", minute);
        } else if (hour24 < 12) {
            displayTime = String.format("%d:%02d AM", hour24, minute);
        } else {
            displayTime = String.format("%d:%02d PM", hour24 - 12, minute);
        }

        // Execute the HTTP request to set the schedule
        executeHttpRequest(url, "SET_SCHEDULE", displayTime);
    }
}