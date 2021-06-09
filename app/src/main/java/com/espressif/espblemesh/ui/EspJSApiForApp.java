package com.espressif.espblemesh.ui;

import android.util.Base64;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.Locale;

public class EspJSApiForApp implements IEspJSApiForApp {
    private final WeakReference<EspMainActivity> mActivity;

    EspJSApiForApp(EspMainActivity activity) {
        mActivity = new WeakReference<>(activity);
    }

    private void evaluateJavascript(String method, String param) {
        EspMainActivity activity = mActivity.get();
        if (activity != null) {
            String script = param == null ?
                    String.format(Locale.ENGLISH, "%s()", method) :
                    String.format(Locale.ENGLISH, "%s('%s')", method, base64Encode(param));
            activity.runOnUiThread(() -> activity.evaluateJavascript(script));
        }
    }

    private String base64Encode(String string) {
        return Base64.encodeToString(string.getBytes(), Base64.NO_WRAP);
    }

    @Override
    public void onBackPressed() {
        evaluateJavascript("onBackPressed", null);
    }

    @Override
    public void onActivityPause() {
        evaluateJavascript("onActivityPause", null);
    }

    @Override
    public void onActivityResume() {
        evaluateJavascript("onActivityResume", null);
    }

    @Override
    public void getAppInfoCallback(JSONObject info) {
        evaluateJavascript("getAppInfoCallback", info.toString());
    }

    @Override
    public void requestPermissionsCallback(JSONObject info) {
        evaluateJavascript("requestPermissionsCallback", info.toString());
    }

    @Override
    public void phoneStateChangedCallback(JSONObject info) {
        evaluateJavascript("phoneStateChangedCallback", info.toString());
    }

    @Override
    public void getWiFiInfoCallback(JSONObject info) {
        evaluateJavascript("getWiFiInfoCallback", info.toString());
    }

    @Override
    public void getMeshNetworkCallback(JSONObject info) {
        evaluateJavascript("getMeshNetworkCallback", info.toString());
    }

    @Override
    public void getMeshInfoCallback(JSONObject info) {
        evaluateJavascript("getMeshInfoCallback", info.toString());
    }

    @Override
    public void getNodesCallback(JSONArray info) {
        evaluateJavascript("getNodesCallback", info.toString());
    }

    @Override
    public void getGroupsCallback(JSONArray info) {
        evaluateJavascript("getGroupsCallback", info.toString());
    }

    @Override
    public void scanCallback(JSONArray info) {
        evaluateJavascript("scanCallback", info.toString());
    }

    @Override
    public void connectCallback(JSONObject info) {
        evaluateJavascript("connectCallback", info.toString());
    }

    @Override
    public void provisioningCallback(JSONObject info) {
        evaluateJavascript("provisioningCallback", info.toString());
    }

    @Override
    public void meshMessageCallback(JSONObject info) {
        evaluateJavascript("meshMessageCallback", info.toString());
    }

    @Override
    public void createGroupCallback(JSONObject info) {
        evaluateJavascript("createGroupCallback", info.toString());
    }

    @Override
    public void getOtaBinsCallback(JSONArray info) {
        evaluateJavascript("getOtaBinsCallback", info.toString());
    }

    @Override
    public void getOtaBinVersionCodeCallback(JSONObject info) {
        evaluateJavascript("getOtaBinVersionCodeCallback", info.toString());
    }

    @Override
    public void otaCallback(JSONObject info) {
        evaluateJavascript("otaCallback", info.toString());
    }

    @Override
    public void startRecordAudioCallback(JSONObject info) {
        evaluateJavascript("startRecordAudioCallback", info.toString());
    }

    @Override
    public void onRecordAudioCallback(JSONObject info) {
        evaluateJavascript("onRecordAudioCallback", info.toString());
    }

    @Override
    public void loadDataCallback(JSONObject info) {
        evaluateJavascript("loadDataCallback", info.toString());
    }
}
