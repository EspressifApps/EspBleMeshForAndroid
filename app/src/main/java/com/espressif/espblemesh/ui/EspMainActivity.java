package com.espressif.espblemesh.ui;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.location.LocationManagerCompat;

import com.espressif.blemesh.constants.KeyConst;
import com.espressif.blemesh.utils.EspLog;
import com.espressif.espblemesh.app.BaseActivity;
import com.espressif.espblemesh.databinding.EspMainActivityBinding;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

public class EspMainActivity extends BaseActivity {
    private static final String APP_API_NAME = "EspBleMesh";
    private static final String WEB_URL = "file:///android_asset/index.html";

    private static final int REQUEST_PERMISSION = 0xf000;
    private static final int REQUEST_AUDIO_RECORD_PERMISSION = 0xf001;

    public static final int REQUEST_WIFI = 0x10;
    public static final int REQUEST_BLUETOOTH = 0x11;
    public static final int REQUEST_LOCATION = 0x12;

    private final EspLog mLog = new EspLog(getClass());

    private EspMainActivityBinding mBinding;

    private EspJSApiForApp mJSApi;
    private EspAppApiForJS mAppApi;

    private final PhoneStateReceiver mReceiver = new PhoneStateReceiver() {
        @Override
        public void onPhoneStateChange() {
            boolean isBluetoothEnabled = BluetoothAdapter.getDefaultAdapter().isEnabled();
            LocationManager lm = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
            boolean isLocationEnabled = lm != null && LocationManagerCompat.isLocationEnabled(lm);
            try {
                JSONObject jsonObject = new JSONObject()
                        .put(KeyConst.KEY_BLUETOOTH_ENABLED, isBluetoothEnabled)
                        .put(KeyConst.KEY_LOCATION_ENABLED, isLocationEnabled);
                mJSApi.phoneStateChangedCallback(jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = EspMainActivityBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        WebSettings webSettings = mBinding.webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setTextZoom(100);

        mJSApi = new EspJSApiForApp(this);
        mBinding.webView.loadUrl(WEB_URL);
        mAppApi = new EspAppApiForJS(this);
        mBinding.webView.addJavascriptInterface(mAppApi, APP_API_NAME);

        mReceiver.register(this);

        mBinding.testForm.setVisibility(View.GONE);
        mBinding.testBtn1.setOnClickListener(v -> {
        });
        mBinding.testBtn2.setOnClickListener(v -> {
        });
        mBinding.testBtn3.setOnClickListener(v -> {
        });
        mBinding.testBtn4.setOnClickListener(v -> {
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        mJSApi.onActivityResume();
    }

    @Override
    protected void onPause() {
        super.onPause();

        mJSApi.onActivityPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        mReceiver.unregister(this);
        mBinding.webView.removeJavascriptInterface(APP_API_NAME);
        mBinding.webView.destroy();
        mAppApi.close();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_PERMISSION) {
            JSONObject json = new JSONObject();
            for (int i = 0; i < permissions.length; ++i) {
                String permission = permissions[i];
                boolean granted = grantResults[i] == PackageManager.PERMISSION_GRANTED;
                if (Objects.equals(permission, Manifest.permission.ACCESS_FINE_LOCATION)) {
                    try {
                        json.put(KeyConst.KEY_LOCATION_GRANTED, granted);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
            mJSApi.requestPermissionsCallback(json);
        }
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        mJSApi.onBackPressed();
    }

    void evaluateJavascript(String script) {
        mBinding.webView.evaluateJavascript(script, null);
    }

    void requestRequiredPermissions() {
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION};
        ActivityCompat.requestPermissions(this, permissions, REQUEST_PERMISSION);
    }

    boolean requestAudioRecordPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) ==
                PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO},
                    REQUEST_AUDIO_RECORD_PERMISSION);
            return false;
        }
    }
}
