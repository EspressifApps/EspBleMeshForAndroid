package com.espressif.espblemesh.ui;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.LocationManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.SystemClock;
import android.provider.Settings;
import android.util.Log;
import android.webkit.JavascriptInterface;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.location.LocationManagerCompat;
import androidx.core.os.LocaleListCompat;

import com.espressif.blemesh.api.EspApiOpCodes;
import com.espressif.blemesh.api.EspAppApi;
import com.espressif.blemesh.api.MeshObjectBox;
import com.espressif.blemesh.constants.KeyConst;
import com.espressif.blemesh.model.JSBox;
import com.espressif.blemesh.model.JSBox_;
import com.espressif.blemesh.utils.EspLog;
import com.espressif.espblemesh.utils.Wisper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

import io.objectbox.Box;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableOnSubscribe;
import io.reactivex.rxjava3.schedulers.Schedulers;
import no.nordicsemi.android.mesh.espressif.EspColorUtils;

public class EspAppApiForJS implements IEspAppApiForJS {
    private static final boolean DEBUG = true;

    private final EspLog mLog = new EspLog(getClass());

    private final EspAppApi mApiDelegate;
    private final WeakReference<EspMainActivity> mActivity;

    private final EspJSApiForApp mJSApi;

    private final BluetoothAdapter mBluetoothAdapter;
    private final LocationManager mLocationManager;

    EspAppApiForJS(@NonNull EspMainActivity activity) {
        mLog.setLevel(DEBUG ? EspLog.Level.V : EspLog.Level.W);
        mActivity = new WeakReference<>(activity);
        mJSApi = new EspJSApiForApp(activity);
        mApiDelegate = new EspAppApi(activity, mJSApi);

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        mLocationManager = (LocationManager) activity.getApplicationContext()
                .getSystemService(Context.LOCATION_SERVICE);
    }

    @Override
    public void close() {
        mApiDelegate.close();
    }

    // region IEspAppApiForJS
    @Override
    @JavascriptInterface
    public void webLog(String message) {
        if (DEBUG) {
            Log.i("webLog", message);
        }
    }

    @Override
    @JavascriptInterface
    public void finish() {
        mApiDelegate.close();
        EspMainActivity activity = mActivity.get();
        if (activity != null) {
            activity.finish();
        }
    }

    @Override
    @JavascriptInterface
    public void getAppInfo() {
        EspMainActivity activity = mActivity.get();
        if (activity == null) {
            return;
        }

        String versionName;
        long versionCode;
        try {
            PackageInfo pi = activity.getPackageManager().getPackageInfo(activity.getPackageName(), 0);
            versionName = pi.versionName;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                versionCode = pi.getLongVersionCode();
            } else {
                versionCode = pi.versionCode;
            }
        } catch (PackageManager.NameNotFoundException e) {
            mLog.w("getAppInfo() PackageManager.NameNotFoundException");
            versionName = "unknown";
            versionCode = -1L;
        }

        Locale locale = LocaleListCompat.getDefault().get(0);
        String language = locale.getLanguage();
        String country = locale.getCountry();

        boolean isLocationGranted = ActivityCompat.checkSelfPermission(activity,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
        boolean isLocationEnabled = LocationManagerCompat.isLocationEnabled(mLocationManager);
        boolean isBluetoothEnabled = mBluetoothAdapter.isEnabled();
        boolean isRecordEnabled = ActivityCompat.checkSelfPermission(activity,
                Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED;

        JSONObject json = new JSONObject();
        try {
            json.put(KeyConst.KEY_OS, "Android");
            json.put(KeyConst.KEY_OS_Version, Build.VERSION.SDK_INT);
            json.put(KeyConst.KEY_APP_VERSION_NAME, versionName);
            json.put(KeyConst.KEY_APP_VERSION_CODE, versionCode);
            json.put(KeyConst.KEY_LOCATION_GRANTED, isLocationGranted);
            json.put(KeyConst.KEY_LOCATION_ENABLED, isLocationEnabled);
            json.put(KeyConst.KEY_BLUETOOTH_ENABLED, isBluetoothEnabled);
            json.put(KeyConst.KEY_RECORD_ENABLED, isRecordEnabled);
            json.put(KeyConst.KEY_LANGUAGE, language);
            json.put(KeyConst.KEY_COUNTRY, country);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        mJSApi.getAppInfoCallback(json);
    }

    @Override
    @JavascriptInterface
    public void requestPermissions() {
        EspMainActivity activity = mActivity.get();
        if (activity != null) {
            activity.requestRequiredPermissions();
        }
    }

    @Override
    @JavascriptInterface
    public void getWiFiInfo() {
        EspMainActivity activity = mActivity.get();
        if (activity == null) {
            return;
        }
        WifiManager wifiManager = (WifiManager) activity.getApplicationContext()
                .getSystemService(Context.WIFI_SERVICE);
        if (wifiManager == null) {
            return;
        }
        WifiInfo info = wifiManager.getConnectionInfo();
        try {
            JSONObject wifiJSON = new JSONObject();
            if (info.getNetworkId() == -1) {
                wifiJSON.put(KeyConst.KEY_SSID, JSONObject.NULL)
                        .put(KeyConst.KEY_BSSID, JSONObject.NULL);
            } else {
                String ssid = info.getSSID();
                if (ssid.startsWith("\"") && ssid.endsWith("\"")) {
                    ssid = ssid.substring(1, ssid.length() - 1);
                }
                String bssid = info.getBSSID();
                wifiJSON.put(KeyConst.KEY_SSID, ssid)
                        .put(KeyConst.KEY_BSSID, bssid);
            }
            mJSApi.getWiFiInfoCallback(wifiJSON);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    @JavascriptInterface
    public void gotoSystemSettings(String setting) {
        EspMainActivity activity = mActivity.get();
        if (activity == null) {
            return;
        }
        String action = null;
        int request = -1;
        switch (setting) {
            case "wifi":
                action = Settings.ACTION_WIFI_SETTINGS;
                request = EspMainActivity.REQUEST_WIFI;
                break;
            case "bluetooth":
                action = Settings.ACTION_BLUETOOTH_SETTINGS;
                request = EspMainActivity.REQUEST_BLUETOOTH;
                break;
            case "location":
                action = Settings.ACTION_LOCATION_SOURCE_SETTINGS;
                request = EspMainActivity.REQUEST_LOCATION;
                break;
        }
        if (action != null) {
            Intent intent = new Intent(action);
            activity.startActivityForResult(intent, request);
        }
    }

    @Override
    @JavascriptInterface
    public void startRecordAudio(String request) {
        mLog.d("startRecordAudio()");
        EspMainActivity activity = mActivity.get();
        if (activity == null) {
            return;
        }

        final String keyResult = "result";
        final String keyReason = "reason";
        if (!activity.requestAudioRecordPermission()) {
            try {
                JSONObject respJson = new JSONObject()
                        .put(keyResult, false)
                        .put(keyReason, "permission");
                mJSApi.startRecordAudioCallback(respJson);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return;
        }

        final int dstAddress;
        final Integer nodeAddress;
        final long interval;
        final long cbInterval;
        try {
            JSONObject requestJSON = new JSONObject(request);
            interval = requestJSON.optLong(KeyConst.KEY_INTERVAL, 300);
            dstAddress = requestJSON.getInt(KeyConst.KEY_DST_ADDRESS);
            nodeAddress = requestJSON.isNull(KeyConst.KEY_NODE_ADDRESS) ?
                    null : requestJSON.getInt(KeyConst.KEY_NODE_ADDRESS);
            cbInterval = requestJSON.optLong(KeyConst.KEY_CALLBACK_INTERVAL, -1);
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }

        final int[] colors = new int[]{
                Color.RED,
                Color.CYAN,
                Color.GREEN,
                Color.MAGENTA,
                Color.BLUE,
                Color.YELLOW
        };
        final AtomicBoolean colorChange = new AtomicBoolean(false);
        final AtomicInteger colorIndex = new AtomicInteger(0);
        final AtomicReference<Double> prevVolume = new AtomicReference<>(0.0);
        final AtomicLong lastUpdate = new AtomicLong(SystemClock.elapsedRealtime());
        final AtomicLong lastCallback = new AtomicLong(SystemClock.elapsedRealtime());
        Observable.create((ObservableOnSubscribe<Double>) emitter -> {
            Wisper.getInstance().startListenVolume(volume -> {
                double cbVolume = volumeToLightness(volume);
                double prevVolumeValue = prevVolume.getAndSet(cbVolume);
                final double change = Math.abs(prevVolumeValue - cbVolume);
                if (change > 0.12) {
                    colorChange.set(true);
                }

                if (cbInterval == 0 ||
                        (cbInterval > 0 && SystemClock.elapsedRealtime() - lastCallback.get() > cbInterval)
                ) {
                    lastCallback.set(SystemClock.elapsedRealtime());
                    try {
                        JSONObject cbJSON = new JSONObject()
                                .put(KeyConst.KEY_VOLUME, cbVolume);
                        mJSApi.onRecordAudioCallback(cbJSON);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                if (SystemClock.elapsedRealtime() - lastUpdate.get() > interval) {
                    emitter.onNext(volume);
                    lastUpdate.set(SystemClock.elapsedRealtime());
                }
            });
            emitter.onComplete();
        }).subscribeOn(Schedulers.io())
                .doOnNext(volume -> {
                    JSONObject messageJSON = new JSONObject()
                            .put(KeyConst.KEY_DST_ADDRESS, dstAddress);
                    if (nodeAddress != null) {
                        messageJSON.put(KeyConst.KEY_NODE_ADDRESS, nodeAddress);
                    }

                    final double cbVolume = volumeToLightness(volume);
                    if (colorChange.getAndSet(false)) {
                        mLog.d("Record: Change color: " + colorIndex + ", " + cbVolume);
                        int color = colors[colorIndex.getAndIncrement()];
                        if (colorIndex.get() >= colors.length) {
                            colorIndex.set(0);
                        }
                        double[] hsl = EspColorUtils.RGBToHSL(
                                Color.red(color),
                                Color.green(color),
                                Color.blue(color)
                        );
                        hsl[2] = cbVolume;
                        JSONArray hslArray = new JSONArray();
                        for (double d : hsl) {
                            hslArray.put(d);
                        }
                        messageJSON.put(KeyConst.KEY_OP_CODE, EspApiOpCodes.LIGHT_HSL_SET_UNACKNOWLEDGED)
                                .put(KeyConst.KEY_HSL, hslArray);
                    } else {
                        mLog.d("Record: Change lightness: " + cbVolume);
                        messageJSON.put(KeyConst.KEY_OP_CODE, EspApiOpCodes.LIGHT_LIGHTNESS_SET_UNACKNOWLEDGED)
                                .put(KeyConst.KEY_LIGHTNESS, cbVolume);
                    }

                    sendMeshMessage(messageJSON.toString());
                })
                .subscribe();
        try {
            JSONObject respJSON = new JSONObject()
                    .put(keyResult, true);
            mJSApi.startRecordAudioCallback(respJSON);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private double volumeToLightness(double volume) {
        final double max = 50.0;
        final double min = 16.0;
        if (volume <= min) {
            return 0.0;
        } else if (volume >= max) {
            return 1.0;
        } else {
            return (volume - min) / (max - min);
        }
    }

    @Override
    @JavascriptInterface
    public void stopRecordAudio() {
        mLog.d("stopRecordAudio()");
        Wisper.getInstance().stop();
    }

    @Override
    @JavascriptInterface
    public void saveData(String request) {
        String key;
        String value;
        try {
            JSONObject requestJSON = new JSONObject(request);
            key = requestJSON.getString("key");
            value = requestJSON.getString("value");
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }

        Box<JSBox> box = MeshObjectBox.getInstance().boxStore().boxFor(JSBox.class);
        JSBox jsBox = box.query().equal(JSBox_.key, key).build().findUnique();
        if (jsBox == null) {
            jsBox = new JSBox();
            jsBox.id = 0L;
            jsBox.key = key;
        }
        jsBox.value = value;
        box.put(jsBox);
    }

    @Override
    @JavascriptInterface
    public void loadData(String key) {
        Box<JSBox> box = MeshObjectBox.getInstance().boxStore().boxFor(JSBox.class);
        JSBox jsBox = box.query().equal(JSBox_.key, key).build().findUnique();
        try {
            JSONObject json = new JSONObject();
            json.put("key", key);
            json.put("value", jsBox != null ? jsBox.value : JSONObject.NULL);
            mJSApi.loadDataCallback(json);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    @JavascriptInterface
    public void removeData(String key) {
        Box<JSBox> box = MeshObjectBox.getInstance().boxStore().boxFor(JSBox.class);
        box.query().equal(JSBox_.key, key).build().remove();
    }

    // endregion

    @Override
    @JavascriptInterface
    public void getMeshNetwork() {
        mApiDelegate.getMeshNetwork();
    }

    @Override
    @JavascriptInterface
    public void getMeshInfo(String request) {
        mApiDelegate.getMeshInfo(request);
    }

    @Override
    @JavascriptInterface
    public void getNodes() {
        mApiDelegate.getNodes();
    }

    @Override
    @JavascriptInterface
    public void deleteNode(String request) {
        mApiDelegate.deleteNode(request);
    }

    @Override
    @JavascriptInterface
    public void updateNodeName(String request) {
        mApiDelegate.updateNodeName(request);
    }

    @Override
    @JavascriptInterface
    public void getGroups() {
        mApiDelegate.getGroups();
    }

    @Override
    @JavascriptInterface
    public void startScan(String request) {
        EspMainActivity activity = mActivity.get();
        if (activity == null) {
            return;
        }
        mApiDelegate.startScan(request);
    }

    @Override
    @JavascriptInterface
    public void stopScan() {
        mApiDelegate.stopScan();
    }

    @Override
    @JavascriptInterface
    public void connect(String request) {
        mLog.d("connect() " + request);
        EspMainActivity activity = mActivity.get();
        if (activity == null) {
            return;
        }
        mApiDelegate.connect(request);
    }

    @Override
    @JavascriptInterface
    public void disconnect() {
        mLog.d("disconnect()");
        mApiDelegate.disconnect();
    }

    @Override
    @JavascriptInterface
    public void startProvisioning(String request) {
        mApiDelegate.startProvisioning(request);
    }

    @Override
    @JavascriptInterface
    public void sendMeshMessage(String request) {
        mApiDelegate.sendMeshMessage(request);
    }

    @Override
    @JavascriptInterface
    public void createGroup(String request) {
        mApiDelegate.createGroup(request);
    }

    @Override
    @JavascriptInterface
    public void updateGroup(String request) {
        mApiDelegate.updateGroup(request);
    }

    @Override
    @JavascriptInterface
    public void removeGroup(String request) {
        mApiDelegate.removeGroup(request);
    }

    @Override
    @JavascriptInterface
    public void getOtaBins() {
        mApiDelegate.getOtaBins();
    }

    @Override
    @JavascriptInterface
    public void getOtaBinVersionCode(String version) {
        mApiDelegate.getOtaBinVersionCode(version);
    }

    @Override
    @JavascriptInterface
    public void sendOtaMeshMessage(String request) {
        mApiDelegate.sendOtaMeshMessage(request);
    }

    @Override
    @JavascriptInterface
    public void startOta(String request) {
        mApiDelegate.startOta(request);
    }

    @Override
    @JavascriptInterface
    public void stopOta() {
        mApiDelegate.stopOta();
    }
}
