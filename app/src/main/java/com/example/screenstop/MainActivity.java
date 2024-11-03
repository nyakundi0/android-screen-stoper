package com.example.screenstop;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.view.ViewGroup;

public class MainActivity extends AppCompatActivity {
    public static final String PREFS_NAME = "ScreenStopPrefs";
    public static final String SCREEN_PERCENTAGE_KEY = "screenPercentage";
    private static final float MIN_PERCENTAGE = 20.0f;
    private static final float MAX_PERCENTAGE = 22.0f;
    private static final int SEEKBAR_STEPS = 20; // (22-20) * 10 steps for 0.1 increments

    private SeekBar heightSeekBar;
    private TextView percentageText;
    private View disabledAreaView;
    private Button saveButton;
    private float currentPercentage = MIN_PERCENTAGE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Ensure service starts on boot
        enableBootReceiver();

        // Initialize UI components
        initializeUI();

        // Load saved percentage and update UI
        loadSavedSettings();

        // Set up listeners
        setupListeners();

        // Start service if it's not running
        startScreenStopService(currentPercentage);
    }

    private void initializeUI() {
        heightSeekBar = findViewById(R.id.heightSeekBar);
        percentageText = findViewById(R.id.percentageText);
        disabledAreaView = findViewById(R.id.disabledAreaView);
        saveButton = findViewById(R.id.saveButton);

        heightSeekBar.setMax(SEEKBAR_STEPS);
    }

    private void loadSavedSettings() {
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        currentPercentage = settings.getFloat(SCREEN_PERCENTAGE_KEY, MIN_PERCENTAGE);

        int progress = (int)((currentPercentage - MIN_PERCENTAGE) * 10);
        heightSeekBar.setProgress(progress);

        updatePercentageDisplay(currentPercentage);
        updateDisabledAreaView(currentPercentage);
    }

    private void setupListeners() {
        heightSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                float percentage = MIN_PERCENTAGE + (progress / 10.0f);
                updatePercentageDisplay(percentage);
                updateDisabledAreaView(percentage);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        saveButton.setOnClickListener(v -> {
            saveSettings();
            finish();
        });
    }

    private void updatePercentageDisplay(float percentage) {
        percentageText.setText(String.format("%.1f%%", percentage));
    }

    private void updateDisabledAreaView(float percentage) {
        ViewGroup.LayoutParams params = disabledAreaView.getLayoutParams();
        int totalHeight = ((View) disabledAreaView.getParent()).getHeight();
        params.height = (int) (totalHeight * (percentage / 100.0f));
        disabledAreaView.setLayoutParams(params);
    }

    private void saveSettings() {
        float percentage = MIN_PERCENTAGE + (heightSeekBar.getProgress() / 10.0f);
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putFloat(SCREEN_PERCENTAGE_KEY, percentage);
        editor.apply();

        startScreenStopService(percentage);
    }

    private void startScreenStopService(float percentage) {
        Intent serviceIntent = new Intent(this, ScreenStopService.class);
        serviceIntent.putExtra("percentage", percentage);
        startService(serviceIntent);
    }

    private void enableBootReceiver() {
        ComponentName receiver = new ComponentName(this, BootReceiver.class);
        PackageManager pm = getPackageManager();
        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);
    }
}
