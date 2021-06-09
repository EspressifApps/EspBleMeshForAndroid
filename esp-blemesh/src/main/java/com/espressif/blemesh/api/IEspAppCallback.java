package com.espressif.blemesh.api;

import com.espressif.blemesh.constants.StatusConst;

import org.json.JSONArray;
import org.json.JSONObject;

import no.nordicsemi.android.mesh.utils.AuthenticationOOBMethods;

public interface IEspAppCallback {
    /**
     * Callback of {@link IEspAppApi#getMeshNetwork()}
     *
     * @param info:
     *            [
     *            {
     *                "name":"network name",
     *                "id":1,
     *                "keyIndex":0,
     *                "key":"3965637302ad194c2459c32299cc5034"
     *            }
     *            ]
     */
    void getMeshNetworkCallback(JSONObject info);

    /**
     * Callback of {@link IEspAppApi#getMeshInfo(String)}
     *
     * @param info:
     *            {
     *                "type":"node",
     *                "nodes":[]
     *            }
     *
     *               type: "node", result key is "nodes"
     *                     "fastNode", result key is "fastNodes"
     *                     "group", result key is "models"
     */
    void getMeshInfoCallback(JSONObject info);

    /**
     * Callback of {@link IEspAppApi#getNodes()}
     *
     * @param info:
     *            [
     *            {
     *                "name":"node name",
     *            }
     *            ]
     */
    void getNodesCallback(JSONArray info);

    /**
     * Callback of {@link IEspAppApi#getGroups()}
     *
     * @param info:
     *            [
     *            {
     *                "name":"group name",
     *                "address":49152,
     *                "meshUuid":"Network UUID",
     *                "description":"Description"
     *            }
     *            ]
     */
    void getGroupsCallback(JSONArray info);

    /**
     * Callback of {@link IEspAppApi#startScan(String)}
     *
     * @param info:
     *            [
     *            {
     *                "name":"device name",
     *                "id":"device id",
     *                "rssi":-45
     *            }
     *            ]
     *
     *            id: device mac in Android, device identify in iOS
     */
    void scanCallback(JSONArray info);

    /**
     * Callback of {@link IEspAppApi#connect(String)}
     *
     * @param info:
     *            {
     *                "status":5000,
     *
     *                "connected":true,
     *
     *                "name":"node name",
     *                "oobMethods":[0, 1, 2, 3],
     *            }
     *
     *            status: @see {@link StatusConst}
     *            connected: nullable
     *            oobMethods: @see {@link AuthenticationOOBMethods}
     *
     */
    void connectCallback(JSONObject info);

    /**
     * Callback of {@link IEspAppApi#connect(String)} for provisioning
     *
     * @param info:
     *            {
     *                "status":5099
     *            }
     *
     *            status: @see {@link StatusConst}
     */
    void provisioningCallback(JSONObject info);

    /**
     * Callback when receive mesh message
     *
     * @param info: @see {@link EspApiOpCodes}
     */
    void meshMessageCallback(JSONObject info);

    /**
     * Callback of {@link IEspAppApi#createGroup(String)}
     *
     * @param info:
     *            {
     *                "name":"group name",
     *                "address":10,
     *                "meshUuid":"Network UUID",
     *                "description":"Group description"
     *            }
     *
     *            emtpy json if create group failed
     */
    void createGroupCallback(JSONObject info);

    /**
     * Callback of {@link IEspAppApi#getOtaBins()}
     *
     * @param info:
     *            [
     *            {
     *                "name":"Light_01_5.2.4.bin",
     *                "binId":0x0001,
     *                "versionName":"5.2.4",
     *                "versionCode":0x2405
     *            }
     *            ]
     */
    void getOtaBinsCallback(JSONArray info);

    /**
     * Callback of {@link IEspAppApi#getOtaBinVersionCode(String)}
     *
     * @param info:
     *            {
     *                "versionName":"2.0.1",
     *                "versionCode":4098,
     *                "status":2000
     *            }
     *
     *            status: {@link StatusConst#STATUS_SUCCESS} or {@link StatusConst#STATUS_FAILED}
     */
    void getOtaBinVersionCodeCallback(JSONObject info);

    /**
     * Callback of {@link IEspAppApi#startOta(String)}
     *
     * @param info:
     *            {
     *                "status":8900,
     *
     *                // if status == 8900
     *                "ssid":"SoftAP SSID",
     *                "password":"SoftAP password",
     *
     *                // if status == 8200
     *                "progress":0.32
     *            }
     *
     *            status: see OTA_STATUS_XXX in {@link IEspAppApi}
     */
    void otaCallback(JSONObject info);
}
