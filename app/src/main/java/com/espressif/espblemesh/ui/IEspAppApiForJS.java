package com.espressif.espblemesh.ui;

import com.espressif.blemesh.api.IEspAppApi;
import org.json.JSONObject;

public interface IEspAppApiForJS extends IEspAppApi {
    /**
     * Print out.
     *
     * @param message:
     */
    void webLog(String message);

    /**
     * Finish the app
     */
    void finish();

    /**
     * Get app info.
     * Callback: {@link IEspJSApiForApp#getAppInfoCallback(JSONObject)}
     */
    void getAppInfo();

    /**
     * Request requirement permissions.
     * Callback: {@link IEspJSApiForApp#requestPermissionsCallback(JSONObject)}
     */
    void requestPermissions();

    /**
     * Get current Wi-Fi connection info
     * Callback: {@link IEspJSApiForApp#getWiFiInfoCallback(JSONObject)}
     */
    void getWiFiInfo();

    /**
     * Go to system settings page
     *
     * @param setting: "wifi", "bluetooth", "location"
     */
    void gotoSystemSettings(String setting);

    /**
     *
     * @param request:
     *               {
     *                   "interval":3000,
     *                   "dstAddress":1,
     *                   "nodeAddress":1,
     *                   "callbackInterval":500
     *               }
     *
     *               interval: interval to send message to device
     *               dstAddress: node element address or group address
     *               nodeAddress: null if dstAddress is group address
     *               callbackInterval: callback interval to js
     */
    void startRecordAudio(String request);

    /**
     * Stop record audio
     */
    void stopRecordAudio();

    /**
     *
     * @param request:
     *               {
     *                   "key":"key string",
     *                   "value":"value string"
     *               }
     */
    void saveData(String request);

    void loadData(String key);

    void removeData(String key);
}
