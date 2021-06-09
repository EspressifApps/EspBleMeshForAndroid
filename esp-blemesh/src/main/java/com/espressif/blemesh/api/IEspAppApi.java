package com.espressif.blemesh.api;

import com.espressif.blemesh.ota.IEspOtaClient;

import no.nordicsemi.android.mesh.utils.AuthenticationOOBMethods;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Closeable;

public interface IEspAppApi extends Closeable {
    // OTA BLE status
    int OTA_STATUS_MESSAGE_NOT_NEED = 7001;
    int OTA_STATUS_MESSAGE_TIMEOUT = 7100;
    int OTA_STATUS_MESSAGE_READY = 8900;
    // OTA TCP normal status
    int OTA_STATUS_COMPLETE = 8000;
    int OTA_STATUS_CONNECTED = 8100;
    int OTA_STATUS_PROGRESS = 8200;
    // OTA TCP error status
    int OTA_STATUS_INVALID_STATE = IEspOtaClient.STATUS_INVALID_STATE;
    int OTA_STATUS_BIN_NOT_FOUND = IEspOtaClient.STATUS_BIN_NOT_FOUND;
    int OTA_STATUS_CONNECT_FAILED = IEspOtaClient.STATUS_CONNECT_FAILED;
    int OTA_STATUS_AES_IV_ERROR = IEspOtaClient.STATUS_AES_IV_ERROR;
    int OTA_STATUS_AES_KEY_ERROR = IEspOtaClient.STATUS_AES_KEY_ERROR;
    int OTA_STATUS_BIN_INFO_ERROR = IEspOtaClient.STATUS_BIN_INFO_ERROR;
    int OTA_STATUS_BIN_ACK_ERROR = IEspOtaClient.STATUS_BIN_ACK_ERROR;
    int OTA_STATUS_BIN_ACK_INVALID = IEspOtaClient.STATUS_BIN_ACK_INVALID;
    int OTA_STATUS_BIN_INCOMPLETE = IEspOtaClient.STATUS_BIN_INCOMPLETE;
    int OTA_STATUS_EXCEPTION = IEspOtaClient.STATUS_EXCEPTION;

    /**
     * Get network keys.
     * Callback: {@link IEspAppCallback#getMeshNetworkCallback(JSONObject)}
     */
    void getMeshNetwork();


    /**
     * Get some info
     * Callback: {@link IEspAppCallback#getMeshInfoCallback(JSONObject)}
     *
     * @param request:
     *               {
     *                   "type":"group",
     *                   "address":12345
     *               }
     *
     *               type: "node", "fastNode" or "group"
     */
    void getMeshInfo(String request);

    /**
     * Get nodes.
     * Callback: {@link IEspAppCallback#getNodesCallback(JSONArray)}
     */
    void getNodes();

    /**
     * Delete node.
     *
     * @param request:
     *               {
     *                   "address":2
     *               }
     */
    void deleteNode(String request);

    /**
     * Update node name.
     *
     * @param request:
     *               {
     *                   "address":2,
     *                   "name":"New Name"
     *               }
     */
    void updateNodeName(String request);

    /**
     * Get groups.
     * Callback: {@link IEspAppCallback#getGroupsCallback(JSONArray)}
     */
    void getGroups();

    /**
     * Start scan ble.
     * Callback: {@link IEspAppCallback#scanCallback(JSONArray)}
     *
     * @param request:
     *               {
     *                   "type":"provisioning"
     *               }
     *
     *               type: "provisioning", "proxy"
     */
    void startScan(String request);

    /**
     * Stop scan ble.
     */
    void stopScan();

    /**
     * Connect ble.
     * Callback: {@link IEspAppCallback#connectCallback(JSONObject)}
     *
     * @param request:
     *               {
     *                   "id":"device id",
     *                   "type":"provisioning",
     *
     *                   "address":1
     *               }
     *
     *               type: "provisioning", "proxy"
     *               address: nullable
     */
    void connect(String request);

    /**
     * Disconnect ble.
     */
    void disconnect();

    /**
     * Start provisioning
     * Callback of {@link IEspAppCallback#provisioningCallback(JSONObject)}
     *
     * @param request:
     *               {
     *                   "name":"node name",
     *                   "oobMethod":0,
     *               }
     *
     *               oobMethod: @see {@link AuthenticationOOBMethods}
     */
    void startProvisioning(String request);

    /**
     * Send Mesh PDU.
     * Callback: {@link IEspAppCallback#meshMessageCallback(JSONObject)}
     *
     * @param request: @see {@link EspApiOpCodes}
     */
    void sendMeshMessage(String request);

    /**
     * Create group.
     * Callback: {@link IEspAppCallback#createGroupCallback(JSONObject)}
     *
     * @param request:
     *               {
     *                   "name":"group name",
     *                   "description":"Group description"
     *               }
     */
    void createGroup(String request);

    /**
     * Update group.
     *
     * @param request:
     *               {
     *                   "address":1,
     *                   "name":"new name",
     *                   "description":"Group description"
     *               }
     */
    void updateGroup(String request);

    /**
     * Remove group.
     *
     * @param request:
     *               {
     *                   "address":1
     *               }
     */
    void removeGroup(String request);

    /**
     * Get OTA bin files
     * Callback: {@link IEspAppCallback#getOtaBinsCallback(JSONArray)}
     */
    void getOtaBins();

    /**
     * Get OTA bin version code for version name
     *
     * @param version: format like 1.2.5
     */
    void getOtaBinVersionCode(String version);

    /**
     * Request to OTA
     *
     * @param request:
     *               {
     *                   "dstAddress":1,
     *                   "companyId":0x02E5,
     *                   "binId":0x0001,
     *                   "versionCode":0x0524,
     *                   "clearFlash":false
     *               }
     */
    void sendOtaMeshMessage(String request);

    /**
     * Start OTA
     * Callback: {@link IEspAppCallback#otaCallback(JSONObject)}
     *
     * @param request:
     *               {
     *                   "name":"bin name",
     *                   "companyId":0x02E5,
     *                   "binId":0x0001,
     *                   "versionCode":0x0524,
     *               }
     */
    void startOta(String request);

    /**
     * Stop OTA
     */
    void stopOta();
}
