package com.espressif.espblemesh.ui;

import android.app.Activity;

import com.espressif.blemesh.api.IEspAppCallback;

import org.json.JSONObject;

public interface IEspJSApiForApp extends IEspAppCallback {
    /**
     * Callback when user pressed back button
     */
    void onBackPressed();

    /**
     * Callback when {@link Activity#onPause()}
     */
    void onActivityPause();

    /**
     * Callback when {@link Activity#onResume()}
     */
    void onActivityResume();

    /**
     * Callback of {@link IEspAppApiForJS#getAppInfo()}
     *
     * @param info:
     *            {
     *                "os":"Android",
     *                "osVersion":29,
     *                "appVersionName":"v1.0.0",
     *                "appVersionCode":3,
     *                "isLocationGranted":true,
     *                "isLocationEnabled":true,
     *                "isBluetoothEnabled":true,
     *                "isRecordEnabled":true,
     *                "language":"en",
     *                "country":"US"
     *            }
     *
     *            os: "Android", "iOS"
     *            isLocationGranted: Location permission is granted or denied
     *            isLocationEnabled: GPS is on or off
     *            isBluetoothEnabled: Bluetooth is on or off
     *            language: "en", "zh", etc...
     *            country: "US", "CN", etc...
     */
    void getAppInfoCallback(JSONObject info);

    /**
     * Callback of {@link IEspAppApiForJS#requestPermissions()}
     *
     * @param info:
     *            {
     *                "isLocationGranted":true
     *            }
     *
     *            isLocationGranted: Location permission is granted or denied
     */
    void requestPermissionsCallback(JSONObject info);

    /**
     * Callback when phone state has changed.
     *
     * @param info:
     *            {
     *                "isBluetoothEnabled":false,
     *                "isLocationEnabled":false
     *            }
     *
     *            isBluetoothEnabled: Bluetooth is on or off
     *            isLocationEnabled: GPS is on or off
     */
    void phoneStateChangedCallback(JSONObject info);

    /**
     * Callback of {@link IEspAppApiForJS#getWiFiInfo()}
     * @param info:
     *            {
     *                "ssid":"wifi ssid",
     *                "bssid":"wifi bssid"
     *            }
     *
     *            ssid: null if no Wi-Fi connection
     *            bssid: null if no Wi-Fi connection
     */
    void getWiFiInfoCallback(JSONObject info);

    /**
     * Callback of {@link IEspAppApiForJS#startRecordAudio(String)}
     * @param info:
     *            {
     *                "result":false,
     *                "reason":"permission"
     *            }
     *
     *            result: true if successful
     */
    void startRecordAudioCallback(JSONObject info);

    /**
     * Callback when receive audio record after call {@link IEspAppApiForJS#startRecordAudio(String)}
     *
     * @param info:
     *            {
     *                "volume":0.4
     *            }
     *
     *            volume: range is 0.0 ~ 1.0
     */
    void onRecordAudioCallback(JSONObject info);

    /**
     * Callback of {@link IEspAppApiForJS#loadData(String)}
     * @param info: base64 encoded string
     *            {
     *                "key":"key string",
     *                "value":"value string"
     *            }
     */
    void loadDataCallback(JSONObject info);
}
