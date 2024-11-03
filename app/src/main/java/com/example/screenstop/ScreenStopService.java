package com.example.screenstop;

import android.app.Service;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;

// ScreenStopService.java
public class ScreenStopService extends Service {
    private View overlayView;
    private WindowManager windowManager;
    private float currentPercentage = 20.0f;

    @Override
    public void onCreate() {
        super.onCreate();
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        createOverlay();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && intent.hasExtra("percentage")) {
            updateOverlayHeight(intent.getFloatExtra("percentage", 20.0f));
        }
        return START_STICKY;
    }

    private void createOverlay() {
        overlayView = new View(this);
        overlayView.setBackgroundColor(Color.BLACK);

        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE |
                        WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
                PixelFormat.TRANSLUCENT);

        params.gravity = Gravity.BOTTOM;

        windowManager.addView(overlayView, params);
        updateOverlayHeight(currentPercentage);
    }

    private void updateOverlayHeight(float percentage) {
        currentPercentage = percentage;
        int screenHeight = windowManager.getDefaultDisplay().getHeight();
        int overlayHeight = (int) (screenHeight * (percentage / 100.0f));

        WindowManager.LayoutParams params = (WindowManager.LayoutParams) overlayView.getLayoutParams();
        params.height = overlayHeight;
        windowManager.updateViewLayout(overlayView, params);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (overlayView != null) {
            windowManager.removeView(overlayView);
        }
    }
}
