package com.espressif.blemesh.api;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanRecord;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.os.ParcelUuid;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.espressif.blemesh.ble.EspGattClient;
import com.espressif.blemesh.constants.KeyConst;
import com.espressif.blemesh.constants.StatusConst;
import com.espressif.blemesh.constants.TypeConst;
import com.espressif.blemesh.model.EspFastNode;
import com.espressif.blemesh.model.EspMeshGroup;
import com.espressif.blemesh.model.EspMeshNode;
import com.espressif.blemesh.model.MeshScanResult;
import com.espressif.blemesh.model.RxObserver;
import com.espressif.blemesh.ota.EspOtaBin;
import com.espressif.blemesh.ota.EspOtaClient;
import com.espressif.blemesh.ota.IEspOtaClient;
import com.espressif.blemesh.utils.EspLog;
import com.espressif.blemesh.utils.EspUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.schedulers.Schedulers;
import no.nordicsemi.android.mesh.ApplicationKey;
import no.nordicsemi.android.mesh.Group;
import no.nordicsemi.android.mesh.MeshManagerApi;
import no.nordicsemi.android.mesh.MeshManagerCallbacks;
import no.nordicsemi.android.mesh.MeshNetwork;
import no.nordicsemi.android.mesh.MeshProvisioningStatusCallbacks;
import no.nordicsemi.android.mesh.MeshStatusCallbacks;
import no.nordicsemi.android.mesh.NetworkKey;
import no.nordicsemi.android.mesh.NodeKey;
import no.nordicsemi.android.mesh.Provisioner;
import no.nordicsemi.android.mesh.UnprovisionedBeacon;
import no.nordicsemi.android.mesh.espressif.EspMeshConstants;
import no.nordicsemi.android.mesh.espressif.EspMeshModelIds;
import no.nordicsemi.android.mesh.espressif.EspMeshUtils;
import no.nordicsemi.android.mesh.espressif.EspOtaUtils;
import no.nordicsemi.android.mesh.models.SigModel;
import no.nordicsemi.android.mesh.models.SigModelParser;
import no.nordicsemi.android.mesh.models.VendorModel;
import no.nordicsemi.android.mesh.opcodes.ConfigMessageOpCodes;
import no.nordicsemi.android.mesh.provisionerstates.ProvisioningCapabilities;
import no.nordicsemi.android.mesh.provisionerstates.ProvisioningState;
import no.nordicsemi.android.mesh.provisionerstates.UnprovisionedMeshNode;
import no.nordicsemi.android.mesh.transport.ConfigAppKeyAdd;
import no.nordicsemi.android.mesh.transport.ConfigAppKeyStatus;
import no.nordicsemi.android.mesh.transport.ConfigCompositionDataGet;
import no.nordicsemi.android.mesh.transport.ConfigCompositionDataStatus;
import no.nordicsemi.android.mesh.transport.ConfigDefaultTtlGet;
import no.nordicsemi.android.mesh.transport.ConfigDefaultTtlStatus;
import no.nordicsemi.android.mesh.transport.ConfigModelAppBind;
import no.nordicsemi.android.mesh.transport.ConfigModelAppStatus;
import no.nordicsemi.android.mesh.transport.ConfigModelSubscriptionAdd;
import no.nordicsemi.android.mesh.transport.ConfigModelSubscriptionDelete;
import no.nordicsemi.android.mesh.transport.ConfigModelSubscriptionDeleteAll;
import no.nordicsemi.android.mesh.transport.ConfigModelSubscriptionOverwrite;
import no.nordicsemi.android.mesh.transport.ConfigModelSubscriptionStatus;
import no.nordicsemi.android.mesh.transport.ConfigNetworkTransmitSet;
import no.nordicsemi.android.mesh.transport.ConfigNetworkTransmitStatus;
import no.nordicsemi.android.mesh.transport.ConfigNodeReset;
import no.nordicsemi.android.mesh.transport.ConfigNodeResetStatus;
import no.nordicsemi.android.mesh.transport.ControlMessage;
import no.nordicsemi.android.mesh.transport.Element;
import no.nordicsemi.android.mesh.transport.EspCurrentVersionGet;
import no.nordicsemi.android.mesh.transport.EspCurrentVersionStatus;
import no.nordicsemi.android.mesh.transport.EspFastProvInfoSet;
import no.nordicsemi.android.mesh.transport.EspFastProvInfoStatus;
import no.nordicsemi.android.mesh.transport.EspFastProvNodeAddrGet;
import no.nordicsemi.android.mesh.transport.EspFastProvNodeAddrStatus;
import no.nordicsemi.android.mesh.transport.EspOtaNeedUpdateNotification;
import no.nordicsemi.android.mesh.transport.EspOtaNewBinVersionNotification;
import no.nordicsemi.android.mesh.transport.EspOtaStart;
import no.nordicsemi.android.mesh.transport.EspOtaStartResponse;
import no.nordicsemi.android.mesh.transport.GenericOnOffGet;
import no.nordicsemi.android.mesh.transport.GenericOnOffSet;
import no.nordicsemi.android.mesh.transport.GenericOnOffSetUnacknowledged;
import no.nordicsemi.android.mesh.transport.GenericOnOffStatus;
import no.nordicsemi.android.mesh.transport.LightCtlGet;
import no.nordicsemi.android.mesh.transport.LightCtlSet;
import no.nordicsemi.android.mesh.transport.LightCtlSetUnacknowledged;
import no.nordicsemi.android.mesh.transport.LightCtlStatus;
import no.nordicsemi.android.mesh.transport.LightHslGet;
import no.nordicsemi.android.mesh.transport.LightHslSet;
import no.nordicsemi.android.mesh.transport.LightHslSetUnacknowledged;
import no.nordicsemi.android.mesh.transport.LightHslStatus;
import no.nordicsemi.android.mesh.transport.LightLightnessGet;
import no.nordicsemi.android.mesh.transport.LightLightnessSet;
import no.nordicsemi.android.mesh.transport.LightLightnessSetUnacknowledged;
import no.nordicsemi.android.mesh.transport.LightLightnessStatus;
import no.nordicsemi.android.mesh.transport.MeshMessage;
import no.nordicsemi.android.mesh.transport.MeshModel;
import no.nordicsemi.android.mesh.transport.ProvisionedMeshNode;
import no.nordicsemi.android.mesh.utils.AuthenticationOOBMethods;

public class EspAppApi implements IEspAppApi, MeshManagerCallbacks,
        MeshProvisioningStatusCallbacks, MeshStatusCallbacks {
    private static final boolean DEBUG = true;

    private static final int MESSAGE_SCAN_NOTIFY = 100;

    private static final long SCAN_NOTIFY_INTERVAL = 1000;

    private static final long GROUP_ACTION_INTERVAL = 800;

    private final EspLog mLog = new EspLog(getClass());

    private final Context mContext;
    private final Handler mHandler;

    private final IEspAppCallback mCallback;

    private final MeshManagerApi mMeshApi;
    private MeshNetwork mMeshNetwork;
    private volatile UnprovisionedMeshNode mUnprovisionedNode;
    private int mUnprovisionedElementCount = 0;

    private boolean mMessageRequestForConnecting = false;
    private boolean mMessageRequestForProxy = false;

    private String mNetworkId;
    private ParcelUuid mFilterUUID;

    private final BluetoothAdapter mBluetoothAdapter;

    private final Map<String, MeshScanResult> mScanResults;
    private volatile EspGattClient mGattClient;

    private final List<EspMeshGroup> mEspMeshGroups;
    private final Map<String, EspMeshNode> mEspMeshNodes;

    private volatile String mOtaSSID;
    private volatile String mOtaPassword;
    private EspOtaClient mOtaClient;
    private volatile LinkedBlockingQueue<Boolean> mOtaNounQueue;
    private volatile LinkedBlockingQueue<Boolean> mOtaStartRespQueue;

    private final LinkedList<Runnable> mMessageTaskQueue;

    public EspAppApi(@NonNull Context context, @NonNull IEspAppCallback callback) {
        mLog.setLevel(DEBUG ? EspLog.Level.V : EspLog.Level.W);
        mContext = context.getApplicationContext();
        mHandler = new ApiHandler(this);
        mCallback = callback;

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        mScanResults = new HashMap<>();

        mMeshApi = new MeshManagerApi(context.getApplicationContext());
        mMeshApi.setMeshManagerCallbacks(this);
        mMeshApi.setProvisioningStatusCallbacks(this);
        mMeshApi.setMeshStatusCallbacks(this);
        mMeshApi.loadMeshNetwork();

        mEspMeshGroups = new ArrayList<>();
        mEspMeshNodes = new HashMap<>();

        mMessageTaskQueue = new LinkedList<>();
    }

    @Override
    public void close() {
        stopOta();
        stopScan();
        if (mGattClient != null) {
            mGattClient.close();
        }
    }

    private EspMeshGroup getEspMeshGroupForAddress(int address) {
        for (EspMeshGroup group : mEspMeshGroups) {
            if (group.address == address) {
                return group;
            }
        }
        return null;
    }

    private EspMeshNode getEspMeshNodeForAddress(int address) {
        for (EspMeshNode node : mEspMeshNodes.values()) {
            if (node.address == address) {
                return node;
            }
        }
        return null;
    }

    // region MeshManagerCallbacks
    private void loadNetwork(MeshNetwork meshNetwork) {
        mEspMeshGroups.clear();
        mMeshNetwork = meshNetwork;
        if (meshNetwork != null) {
            if (!meshNetwork.isProvisionerSelected()) {
                Provisioner provisioner = meshNetwork.getProvisioners().get(0);
                provisioner.setLastSelected(true);
                meshNetwork.selectProvisioner(provisioner);
            }

            if (meshNetwork.getAppKeys().isEmpty()) {
                meshNetwork.addAppKey(meshNetwork.createAppKey());
            }

            Group fastProvGroupMin = meshNetwork.getGroup(EspMeshConstants.ADDRESS_FAST_PROV_GROUP_MIN);
            if (fastProvGroupMin == null) {
                fastProvGroupMin = new Group(EspMeshConstants.ADDRESS_FAST_PROV_GROUP_MIN, meshNetwork.getMeshUUID());
                meshNetwork.addGroup(fastProvGroupMin);
            }

            MeshObjectBox objectBox = MeshObjectBox.getInstance();
            List<EspMeshGroup> espMeshGroups = objectBox.getMeshGroups(meshNetwork.getMeshUUID());
            mEspMeshGroups.addAll(espMeshGroups);
            List<EspMeshNode> espMeshNodes = objectBox.getMeshNodes(meshNetwork.getMeshUUID());
            for (EspMeshNode node : espMeshNodes) {
                mEspMeshNodes.put(node.bleId, node);
            }
        }
    }

    @Override
    public void onNetworkLoaded(MeshNetwork meshNetwork) {
        mLog.d("onNetworkLoaded()");
        loadNetwork(meshNetwork);
    }

    @Override
    public void onNetworkUpdated(MeshNetwork meshNetwork) {
        mLog.d("onNetworkUpdated()");
        loadNetwork(meshNetwork);
    }

    @Override
    public void onNetworkLoadFailed(String error) {
        mLog.w("onNetworkLoadFailed() : " + error);
    }

    @Override
    public void onNetworkImported(MeshNetwork meshNetwork) {
        mLog.d("onNetworkImported()");
    }

    @Override
    public void onNetworkImportFailed(String error) {
        mLog.w("onNetworkImportFailed() : " + error);
    }

    @Override
    public void sendProvisioningPdu(UnprovisionedMeshNode meshNode, byte[] pdu) {
        mLog.d("sendProvisioningPdu()");
        if (mGattClient != null) {
            mGattClient.write(pdu);
        }
    }

    @Override
    public void onMeshPduCreated(byte[] pdu) {
        mLog.d("onMeshPduCreated()");
        if (mGattClient != null) {
            mGattClient.write(pdu);
        }
    }

    @Override
    public int getMtu() {
        return mGattClient == null ? EspGattClient.MTU_MIN : mGattClient.getAvailableMtu();
    }
    // endregion

    // region ProvisioningStateCallbacks
    private int getAvailableUnicastAddress(int elementCount, int startAddress) {
        List<EspMeshNode> nodes = new ArrayList<>(mEspMeshNodes.values());
        Collections.sort(nodes, (n1, n2) -> {
            int addr1 = n1.address;
            int addr2 = n2.address;
            return Integer.compare(addr1, addr2);
        });

        Provisioner provisioner = mMeshNetwork.getSelectedProvisioner();
        if (provisioner == null || provisioner.getProvisionerAddress() == null) {
            return -1;
        }
        int prevAddr = startAddress;
        for (EspMeshNode node : nodes) {
            int nodeAddr = node.address;
            int lastAddr = node.address + (node.elementCount > 1 ? node.elementCount - 1 : 0);
            if (prevAddr > provisioner.getProvisionerAddress()) {
                if (nodeAddr - prevAddr > elementCount) {
                    return prevAddr + 1;
                }
            }
            prevAddr = lastAddr;
        }
        return prevAddr + 1;
    }

    @Override
    public void onProvisioningStateChanged(UnprovisionedMeshNode meshNode, ProvisioningState.States state, byte[] data) {
        mLog.d("onProvisioningStateChanged() " + state);
        mUnprovisionedNode = meshNode;
        String nodeBleId = mGattClient.getDeviceAddress();
        switch (state) {
            case PROVISIONING_CAPABILITIES:
                if (meshNode == null) {
                    break;
                }
                ProvisioningCapabilities capabilities = meshNode.getProvisioningCapabilities();
                String name = meshNode.getNodeName();
                List<AuthenticationOOBMethods> oobMethods = capabilities.getAvailableOOBTypes();

                final int elementCount = capabilities.getNumberOfElements();
                final Provisioner provisioner = mMeshNetwork.getSelectedProvisioner();

                // Generate  unicast address
                int unicast = -1;
                try {
                    unicast = mMeshNetwork.nextAvailableUnicastAddress(elementCount, provisioner);
                    mLog.i("nrf nextAvailableUnicastAddress is " + unicast);
                    mMeshNetwork.assignUnicastAddress(unicast);
                } catch (IllegalArgumentException e) {
                    mLog.w("assignUnicastAddress IllegalArgumentException: " + e.getMessage());
                    if (provisioner.getProvisionerAddress() == null) {
                        mLog.w("ProvisionerAddress is null");
                        return;
                    }
                    int startAddress = provisioner.getProvisionerAddress();
                    int retryCount = 0;
                    while (true) {
                        try {
                            unicast = getAvailableUnicastAddress(elementCount, startAddress);
                            mLog.i("esp getAvailableUnicastAddress is " + unicast);
                            mMeshNetwork.assignUnicastAddress(unicast);
                            mUnprovisionedElementCount = elementCount;
                            break;
                        } catch (IllegalArgumentException e2) {
                            mLog.w("assignUnicastAddress IllegalArgumentException2: " + e2.getMessage());
                            startAddress = unicast + 1;
                        }
                        if (++retryCount >= 100) {
                            mLog.w("Retry getAvailableUnicastAddress " + retryCount);
                            return;
                        }
                    }
                }

                try {
                    JSONArray oobArray = new JSONArray();
                    for (AuthenticationOOBMethods oob : oobMethods) {
                        oobArray.put(oob.getAuthenticationMethod());
                    }
                    JSONObject jsonObject = new JSONObject()
                            .put(KeyConst.KEY_STATUS, StatusConst.STATUS_PROVISIONING_READY)
                            .put(KeyConst.KEY_ID, nodeBleId)
                            .put(KeyConst.KEY_NAME, name)
                            .put(KeyConst.KEY_OOB_METHODS, oobArray);

                    mCallback.connectCallback(jsonObject);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onProvisioningFailed(UnprovisionedMeshNode meshNode, ProvisioningState.States state, byte[] data) {
        mLog.d("onProvisioningFailed() " + state);
        String nodeBleId = mGattClient.getDeviceAddress();
        disconnect();
        try {
            JSONObject jsonObject = new JSONObject()
                    .put(KeyConst.KEY_STATUS, StatusConst.STATUS_FAILED)
                    .put(KeyConst.KEY_ID, nodeBleId);
            mCallback.provisioningCallback(jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onProvisioningCompleted(ProvisionedMeshNode meshNode, ProvisioningState.States state, byte[] data) {
        mLog.d("onProvisioningCompleted() " + state);
        String nodeBleId = mGattClient.getDeviceAddress();
        disconnect();
        EspMeshNode espMeshNode = mEspMeshNodes.get(nodeBleId);
        if (espMeshNode == null) {
            espMeshNode = new EspMeshNode();
            mEspMeshNodes.put(nodeBleId, espMeshNode);
        }
        espMeshNode.bleId = nodeBleId;
        espMeshNode.meshUUID = mMeshNetwork.getMeshUUID();
        espMeshNode.address = meshNode.getUnicastAddress();
        espMeshNode.elementCount = mUnprovisionedElementCount;
        MeshObjectBox.getInstance().updateMeshNode(espMeshNode);
        try {
            JSONObject jsonObject = new JSONObject()
                    .put(KeyConst.KEY_STATUS, StatusConst.STATUS_PROVISIONING_COMPLETE)
                    .put(KeyConst.KEY_ID, nodeBleId)
                    .put(KeyConst.KEY_NODE, toProvisionedMeshNodeJSON(meshNode));
            mCallback.provisioningCallback(jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
    // endregion

    // region MeshStatusCallbacks
    @Override
    public void onTransactionFailed(int dst, boolean hasIncompleteTimerExpired) {
        mLog.w("onTransactionFailed() dst: " + Integer.toHexString(dst));
    }

    @Override
    public void onUnknownPduReceived(int src, byte[] accessPayload) {
        mLog.d("onUnknownPduReceived() src: " + Integer.toHexString(src));
    }

    @Override
    public void onBlockAcknowledgementProcessed(int dst, @NonNull ControlMessage message) {
        mLog.d("onBlockAcknowledgementProcessed() dst: " + Integer.toHexString(dst));
    }

    @Override
    public void onBlockAcknowledgementReceived(int src, @NonNull ControlMessage message) {
        mLog.d("onBlockAcknowledgementReceived() src: " + Integer.toHexString(src));
    }

    @Override
    public void onMeshMessageProcessed(int dst, @NonNull MeshMessage meshMessage) {
        mLog.d("onMeshMessageProcessed() dst: " + Integer.toHexString(dst));
    }

    @Override
    public void onMeshMessageReceived(int src, @NonNull MeshMessage meshMessage) {
        mLog.d("onMeshMessageReceived() src: " + Integer.toHexString(src) + " , opCode: " + Integer.toHexString(meshMessage.getMessageOpCode()));
        final ProvisionedMeshNode node = mMeshNetwork.getNode(src);

        if (meshMessage instanceof ConfigCompositionDataStatus) {
            mLog.d("ConfigCompositionDataStatus");
            meshCheckNode(node);
        } else if (meshMessage instanceof ConfigDefaultTtlStatus) {
            mLog.d("ConfigDefaultTtlStatus");
            meshCheckNode(node);
        } else if (meshMessage instanceof ConfigAppKeyStatus) {
            ConfigAppKeyStatus status = (ConfigAppKeyStatus) meshMessage;
            if (status.isSuccessful()) {
                mLog.d("ConfigAppKeyStatus successful");
                meshCheckNode(node);
            } else {
                mLog.w("ConfigAppKeyStatus failed");
            }
        } else if (meshMessage instanceof ConfigNetworkTransmitStatus) {
            mLog.d("ConfigNetworkTransmitStatus");
            meshCheckNode(node);
        } else {
            try {
                JSONObject jsonObject = parseMeshMessage(src, node, meshMessage);
                if (jsonObject != null) {
                    mLog.w("FBY mCallback.meshMessageCallback" + jsonObject);
                    mCallback.meshMessageCallback(jsonObject);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        synchronized (mMessageTaskQueue) {
            Runnable runnable = mMessageTaskQueue.poll();
            if (runnable != null) {
                runnable.run();
            }
        }
    }

    private JSONObject parseMeshMessage(int src, ProvisionedMeshNode node, MeshMessage meshMessage)
            throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(KeyConst.KEY_OP_CODE, meshMessage.getMessageOpCode());
        jsonObject.put(KeyConst.KEY_SRC_ADDRESS, src);
        if (node != null) {
            jsonObject.put(KeyConst.KEY_NODE_ADDRESS, node.getUnicastAddress());
        }
        if (meshMessage instanceof ConfigNodeResetStatus) {
            final ConfigNodeResetStatus status = (ConfigNodeResetStatus) meshMessage;
            deleteProvisionMeshNode(src);
            deleteEspMeshNode(src);
        } else if (meshMessage instanceof EspFastProvInfoStatus) {
            final EspFastProvInfoStatus status = (EspFastProvInfoStatus) meshMessage;
            mLog.d("Receive EspFastProvInfoStatus " + Integer.toHexString(status.getSrc()));
        } else if (meshMessage instanceof EspFastProvNodeAddrStatus) {
            final EspFastProvNodeAddrStatus status = (EspFastProvNodeAddrStatus) meshMessage;
            List<EspFastNode> fastNodes = new ArrayList<>();
            EspFastNode srcFastNode = new EspFastNode();
            srcFastNode.meshUUID = mMeshNetwork.getMeshUUID();
            srcFastNode.address = src;
            EspMeshNode espMeshNode = getEspMeshNodeForAddress(src);
            if (espMeshNode != null) {
                srcFastNode.bleId = espMeshNode.bleId;
            }
            fastNodes.add(srcFastNode);
            for (long addr : status.getAddrArray()) {
                EspFastNode fastNode = new EspFastNode();
                fastNode.meshUUID = mMeshNetwork.getMeshUUID();
                fastNode.address = addr;
                fastNodes.add(fastNode);
            }
            MeshObjectBox.getInstance().updateFastNodes(fastNodes);

            JSONArray fastNodeArray = new JSONArray();
            for (EspFastNode fastNode : fastNodes) {
                JSONObject fastNodeJSON = toFastNodeJson(fastNode);
                fastNodeArray.put(fastNodeJSON);
            }
            jsonObject.put(KeyConst.KEY_FAST_NODES, fastNodeArray);
        } else if (meshMessage instanceof ConfigModelAppStatus) {
            if (node == null) {
                mLog.w("Receive ConfigModelAppStatus, Node not found");
                return null;
            }
            final ConfigModelAppStatus status = (ConfigModelAppStatus) meshMessage;
            if (node.getElements().containsKey(status.getElementAddress())) {
                final Element element = node.getElements().get(status.getElementAddress());
                assert element != null;
                final MeshModel model = element.getMeshModels().get(status.getModelIdentifier());
                assert model != null;
                jsonObject.put(KeyConst.KEY_NODE_ADDRESS, node.getUnicastAddress());
                jsonObject.put(KeyConst.KEY_ELEMENT_ADDRESS, element.getElementAddress());
                jsonObject.put(KeyConst.KEY_MODEL_ID, model.getModelId());
                jsonObject.put(KeyConst.KEY_APP_KEYS, model.getBoundAppKeyIndexes());
            }
        } else if (meshMessage instanceof ConfigModelSubscriptionStatus) {
            if (node == null) {
                mLog.w("Receive ConfigModelSubscriptionStatus, Node not found");
                return null;
            }
            final ConfigModelSubscriptionStatus status = (ConfigModelSubscriptionStatus) meshMessage;
            if (node.getElements().containsKey(status.getElementAddress())) {
                final Element element = node.getElements().get(status.getElementAddress());
                assert element != null;
                final MeshModel model = element.getMeshModels().get(status.getModelIdentifier());
                assert model != null;
                jsonObject.put(KeyConst.KEY_NODE_ADDRESS, node.getUnicastAddress());
                jsonObject.put(KeyConst.KEY_ELEMENT_ADDRESS, element.getElementAddress());
                jsonObject.put(KeyConst.KEY_MODEL_ID, model.getModelId());
                jsonObject.put(KeyConst.KEY_GROUPS, new JSONArray(model.getSubscribedAddresses()));
            }
        } else if (meshMessage instanceof GenericOnOffStatus) {
            final GenericOnOffStatus status = (GenericOnOffStatus) meshMessage;
            jsonObject.put(KeyConst.KEY_STATE, status.getPresentState());
        } else if (meshMessage instanceof LightLightnessStatus) {
            final LightLightnessStatus status = (LightLightnessStatus) meshMessage;
            jsonObject.put(KeyConst.KEY_LIGHTNESS, status.getPresentLightness());
        } else if (meshMessage instanceof LightHslStatus) {
            final LightHslStatus status = (LightHslStatus) meshMessage;
            int[] meshHSL = {status.getPresentHue(), status.getPresentSaturation(), status.getPresentLightness()};
            double[] hsl = EspMeshUtils.meshHSL2HSL(meshHSL);
            JSONArray hslArray = new JSONArray(hsl);
            jsonObject.put(KeyConst.KEY_HSL, hslArray);
        } else if (meshMessage instanceof LightCtlStatus) {
            final LightCtlStatus status = (LightCtlStatus) meshMessage;
            double c = 0;
            double t = (status.getPresentTemperature() - 800) / 100.0;
            double l = EspMeshUtils.ffff2double(status.getPresentLightness());
            JSONArray ctlArray = new JSONArray(new double[]{c, t, l});
            jsonObject.put(KeyConst.KEY_CTL, ctlArray);
        } else if (meshMessage instanceof EspOtaNeedUpdateNotification) {
            final EspOtaNeedUpdateNotification notification = (EspOtaNeedUpdateNotification) meshMessage;
            jsonObject.put(KeyConst.KEY_COMPANY_ID, notification.getCompanyId());
            jsonObject.put(KeyConst.KEY_BIN_ID, notification.getBinId());
            String verStr = EspOtaUtils.binVersionInt2String(notification.getVersion());
            jsonObject.put(KeyConst.KEY_APP_VERSION_NAME, verStr);
            jsonObject.put(KeyConst.KEY_APP_VERSION_CODE, notification.getVersion());
            if (mOtaNounQueue != null) {
                mOtaNounQueue.add(Boolean.TRUE);
            }
        } else if (meshMessage instanceof EspOtaStartResponse) {
            final EspOtaStartResponse response = (EspOtaStartResponse) meshMessage;
            jsonObject.put(KeyConst.KEY_COMPANY_ID, response.getCompanyId());
            jsonObject.put(KeyConst.KEY_SSID, mOtaSSID);
            jsonObject.put(KeyConst.KEY_PASSWORD, mOtaPassword);
            if (mOtaStartRespQueue != null) {
                mOtaStartRespQueue.add(Boolean.TRUE);
            }
        } else if (meshMessage instanceof EspCurrentVersionStatus) {
            final EspCurrentVersionStatus status = (EspCurrentVersionStatus) meshMessage;
            int version = status.getVersion();
            jsonObject.put(KeyConst.KEY_BIN_VERSION_NAME, EspOtaUtils.binVersionInt2String(version));
            jsonObject.put(KeyConst.KEY_BIN_VERSION_CODE, version);
        }

        return jsonObject;
    }

    @Override
    public void onMessageDecryptionFailed(String meshLayer, String errorMessage) {
        mLog.w("onMessageDecryptionFailed() meshLayer: " + meshLayer + " , errorMessage: " + errorMessage);
    }
    // endregion

    // region IEspAppApi
    @Override
    public void getMeshNetwork() {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(KeyConst.KEY_NAME, mMeshNetwork.getMeshName());
            jsonObject.put(KeyConst.KEY_UUID, mMeshNetwork.getMeshUUID());

            List<ApplicationKey> applicationKeys = mMeshNetwork.getAppKeys();
            JSONArray appKeyArray = new JSONArray();
            for (ApplicationKey key : applicationKeys) {
                JSONObject keyJSON = new JSONObject();
                keyJSON.put(KeyConst.KEY_NAME, key.getName());
                keyJSON.put(KeyConst.KEY_KEY_INDEX, key.getKeyIndex());
            }
            jsonObject.put(KeyConst.KEY_APP_KEYS, appKeyArray);

            List<NetworkKey> networkKeys = mMeshNetwork.getNetKeys();
            JSONArray netKeyArray = new JSONArray();
            for (NetworkKey key : networkKeys) {
                JSONObject keyJSON = new JSONObject();
                keyJSON.put(KeyConst.KEY_NAME, key.getName());
                keyJSON.put(KeyConst.KEY_KEY_INDEX, key.getKeyIndex());
            }
            jsonObject.put(KeyConst.KEY_NET_KEYS, netKeyArray);

            jsonObject.put(KeyConst.KEY_NODES, getNodeArray());
            jsonObject.put(KeyConst.KEY_GROUPS, getGroupArray());

            mCallback.getMeshNetworkCallback(jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void getMeshInfo(String request) {
        String type;
        int groupAddr = 0;
        final String typeNode = "node";
        final String typeFastNode = "fastNode";
        final String typeGroup = "group";
        try {
            JSONObject requestJSON = new JSONObject(request);
            type = requestJSON.getString(KeyConst.KEY_TYPE);
            if (type.equals(typeGroup)) {
                groupAddr = requestJSON.getInt(KeyConst.KEY_ADDRESS);
            }

            JSONObject respJSON = new JSONObject();
            respJSON.put(KeyConst.KEY_TYPE, type);
            switch (type) {
                case typeNode:
                    JSONArray nodeArray = getNodeArray();
                    respJSON.put(KeyConst.KEY_NODES, nodeArray);
                    break;
                case typeFastNode:
                    JSONArray fastNodeArray = getFastNodeArray();
                    respJSON.put(KeyConst.KEY_FAST_NODES, fastNodeArray);
                    break;
                case typeGroup:
                    JSONArray groupNodeArray = getGroupNodeArray(groupAddr);
                    respJSON.put(KeyConst.KEY_ADDRESS, groupAddr);
                    respJSON.put(KeyConst.KEY_MESH_UUID, mMeshNetwork.getMeshUUID());
                    respJSON.put("isOnOff", false);
                    respJSON.put(KeyConst.KEY_MODELS, groupNodeArray);
                    break;
            }
            mCallback.getMeshInfoCallback(respJSON);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private List<ProvisionedMeshNode> getNodeList() {
        List<ProvisionedMeshNode> nodes = mMeshNetwork.getNodes();
        Provisioner provisioner = mMeshNetwork.getSelectedProvisioner();
        List<ProvisionedMeshNode> list = new ArrayList<>();
        for (ProvisionedMeshNode meshNode : nodes) {
            if (Objects.equals(meshNode.getUnicastAddress(), provisioner.getProvisionerAddress())) {
                continue;
            }
            list.add(meshNode);
        }
        return list;
    }

    private JSONArray getNodeArray() {
        List<ProvisionedMeshNode> nodes = getNodeList();
        JSONArray array = new JSONArray();
        for (ProvisionedMeshNode meshNode : nodes) {
            try {
                JSONObject nodeJSON = toProvisionedMeshNodeJSON(meshNode);
                array.put(nodeJSON);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return array;
    }

    private JSONArray getGroupNodeArray(int groupAddress) {
        JSONArray result = new JSONArray();
        Group group = mMeshNetwork.getGroup(groupAddress);
        if (group == null) {
            mLog.w("Group address: " + groupAddress + " is not found");
            return result;
        }

        for (ProvisionedMeshNode node : getNodeList()) {
            for (Element element : node.getElements().values()) {
                for (MeshModel model : element.getMeshModels().values()) {
                    if (!model.getSubscribedAddresses().contains(groupAddress)) {
                        continue;
                    }
                    try {
                        JSONObject modelJSON = new JSONObject();
                        modelJSON.put(KeyConst.KEY_NODE_ADDRESS, node.getUnicastAddress());
                        modelJSON.put(KeyConst.KEY_ELEMENT_ADDRESS, element.getElementAddress());
                        modelJSON.put(KeyConst.KEY_MODEL_ID, model.getModelId());
                        modelJSON.put(KeyConst.KEY_NAME, model.getModelName());
                        modelJSON.put(KeyConst.KEY_STATUS, false);
                        modelJSON.put(KeyConst.KEY_TID, 1);

                        result.put(modelJSON);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return result;
    }

    private JSONObject toGroupJSON(Group group, EspMeshGroup espGroup) throws JSONException {
        JSONObject groupJSON = new JSONObject();
        groupJSON.put(KeyConst.KEY_NAME, group.getName());
        groupJSON.put(KeyConst.KEY_ADDRESS, group.getAddress());
        groupJSON.put(KeyConst.KEY_MESH_UUID, group.getMeshUuid());
        if (espGroup != null) {
            groupJSON.put(KeyConst.KEY_DESCRIPTION, espGroup.description);
        }

        return groupJSON;
    }

    private JSONArray getGroupArray() {
        JSONArray groupArray = new JSONArray();
        List<Group> groups = mMeshNetwork.getGroups();
        List<EspMeshGroup> espMeshGroupList = MeshObjectBox.getInstance().getMeshGroups(mMeshNetwork.getMeshUUID());
        Map<Integer, EspMeshGroup> espMeshGroupMap = new HashMap<>();
        for (EspMeshGroup group : espMeshGroupList) {
            espMeshGroupMap.put(group.address, group);
        }
        espMeshGroupList.clear();
        for (Group group : groups) {
            if (group.getAddress() == EspMeshConstants.ADDRESS_FAST_PROV_GROUP_MIN) {
                continue;
            }
            try {
                EspMeshGroup espMeshGroup = espMeshGroupMap.get(group.getAddress());
                JSONObject groupJSON = toGroupJSON(group, espMeshGroup);
                groupArray.put(groupJSON);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return groupArray;
    }

    private JSONObject toFastNodeJson(EspFastNode fastNode) throws JSONException {
        return new JSONObject()
                .put(KeyConst.KEY_ID, fastNode.id)
                .put(KeyConst.KEY_ADDRESS, fastNode.address)
                .put(KeyConst.KEY_MESH_UUID, fastNode.meshUUID);
    }

    private JSONArray getFastNodeArray() {
        JSONArray result = new JSONArray();
        List<EspFastNode> fastNodes = MeshObjectBox.getInstance().getFastNodes(mMeshNetwork.getMeshUUID());
        for (EspFastNode node : fastNodes) {
            try {
                JSONObject json = toFastNodeJson(node);
                result.put(json);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    @Override
    public void getNodes() {
        JSONArray nodeArray = getNodeArray();
        mCallback.getNodesCallback(nodeArray);
    }

    private JSONObject toProvisionedMeshNodeJSON(ProvisionedMeshNode node) throws JSONException {
        JSONObject json = new JSONObject();
        json.put(KeyConst.KEY_NAME, node.getNodeName());
        json.put(KeyConst.KEY_UUID, node.getUuid());
        json.put(KeyConst.KEY_MESH_UUID, node.getMeshUuid());
        json.put(KeyConst.KEY_ADDRESS, node.getUnicastAddress());
        json.put(KeyConst.KEY_ELEMENTS, getElements(node));
        json.put(KeyConst.KEY_TID, 1);
        json.put(KeyConst.KEY_COLOR, new JSONArray(new int[]{0, 1, 1}));
        json.put(KeyConst.KEY_STATUS, false);
        JSONArray appKeyArray = new JSONArray();
        for (NodeKey key : node.getAddedAppKeys()) {
            appKeyArray.put(key.getIndex());
        }
        return json;
    }

    private JSONArray getElements(ProvisionedMeshNode node) throws JSONException {
        JSONArray elementArray = new JSONArray();
        Map<Integer, Element> elementMap = node.getElements();
        for (Element element : elementMap.values()) {
            JSONObject elementJSON = new JSONObject();
            elementJSON.put(KeyConst.KEY_NAME, element.getName());
            elementJSON.put(KeyConst.KEY_ADDRESS, element.getElementAddress());
            elementJSON.put(KeyConst.KEY_MODELS, getModels(element));

            elementArray.put(elementJSON);
        }

        return elementArray;
    }

    private JSONArray getModels(Element element) throws JSONException {
        List<Integer> groupAddrList = Observable.fromIterable(mMeshNetwork.getGroups())
                .map(Group::getAddress)
                .toList()
                .blockingGet();
        JSONArray modelArray = new JSONArray();
        Map<Integer, MeshModel> meshModelMap = element.getMeshModels();
        for (MeshModel model : meshModelMap.values()) {
            JSONObject modelJSON = new JSONObject();
            modelJSON.put(KeyConst.KEY_ID, model.getModelId());
            modelJSON.put(KeyConst.KEY_NAME, model.getModelName());
            if (model instanceof SigModel) {
                modelJSON.put(KeyConst.KEY_TYPE, TypeConst.MODEL_TYPE_SIG);
            } else if (model instanceof VendorModel) {
                modelJSON.put(KeyConst.KEY_TYPE, TypeConst.MODEL_TYPE_VENDOR);
            } else {
                // Should not come here
                modelJSON.put(KeyConst.KEY_TYPE, TypeConst.MODEL_TYPE_UNKNOWN);
            }
            modelJSON.put(KeyConst.KEY_APP_KEYS, new JSONArray(model.getBoundAppKeyIndexes()));
            JSONArray groupArray = new JSONArray();
            for (int subAddr : model.getSubscribedAddresses()) {
                if (groupAddrList.contains(subAddr)) {
                    groupArray.put(subAddr);
                }
            }
            modelJSON.put(KeyConst.KEY_GROUPS, groupArray);

            modelArray.put(modelJSON);
        }
        return modelArray;
    }

    @Override
    public void deleteNode(String request) {
        try {
            JSONObject requestJSON = new JSONObject(request);
            int address = requestJSON.getInt(KeyConst.KEY_ADDRESS);
            deleteProvisionMeshNode(address);
            deleteEspMeshNode(address);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void deleteProvisionMeshNode(int address) {
        ProvisionedMeshNode node = mMeshNetwork.getNode(address);
        if (node != null) {
            mMeshNetwork.deleteNode(node);
        }
    }

    private void deleteEspMeshNode(int address) {
        EspMeshNode espMeshNode = getEspMeshNodeForAddress(address);
        if (espMeshNode != null) {
            MeshObjectBox.getInstance().deleteMeshNode(espMeshNode);
            mEspMeshNodes.remove(espMeshNode.bleId);
            deleteFastNode(espMeshNode.bleId);
        }
    }

    private void deleteEspMeshNode(String bleId) {
        EspMeshNode espMeshNode = mEspMeshNodes.get(bleId);
        if (espMeshNode != null) {
            MeshObjectBox.getInstance().deleteMeshNode(espMeshNode);
            mEspMeshNodes.remove(espMeshNode.bleId);
        }
        deleteFastNode(bleId);
    }

    private void deleteFastNode(String bleId) {
        String meshUUID = mMeshNetwork.getMeshUUID();
        EspFastNode fastNode = MeshObjectBox.getInstance().getFastNodeForBleId(meshUUID, bleId);
        if (fastNode != null) {
            MeshObjectBox.getInstance().deleteFastNode(fastNode);
        }
    }

    @Override
    public void updateNodeName(String request) {
        try {
            JSONObject requestJSON = new JSONObject(request);
            int address = requestJSON.getInt(KeyConst.KEY_ADDRESS);
            ProvisionedMeshNode node = mMeshNetwork.getNode(address);
            if (node != null) {
                String name = requestJSON.getString(KeyConst.KEY_NAME);
                mMeshNetwork.updateNodeName(node, name);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void getGroups() {
        JSONArray groupArray = getGroupArray();
        mCallback.getGroupsCallback(groupArray);
    }

    private ScanCallback mScanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            onBatchScanResults(Collections.singletonList(result));
        }

        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            String nodeUuid = null;
            for (ScanResult result : results) {
                boolean meshValid = false;
                if (Objects.equals(mFilterUUID.getUuid(), EspMeshConstants.UUID_PROVISIONING_SERVICE)) {
                    meshValid = true;
                } else if (Objects.equals(mFilterUUID.getUuid(), EspMeshConstants.UUID_PROXY_SERVICE)) {
                    final ScanRecord scanRecord = result.getScanRecord();
                    if (scanRecord == null) {
                        continue;
                    }
                    final byte[] serviceData = scanRecord.getServiceData(mFilterUUID);
                    if (mMeshApi.isAdvertisingWithNetworkIdentity(serviceData)) {
                        if (mMeshApi.networkIdMatches(mNetworkId, serviceData)) {
                            meshValid = true;
                        }
                    } else if (mMeshApi.isAdvertisedWithNodeIdentity(serviceData)) {
                        if (serviceData == null) {
                            continue;
                        }
                        final MeshNetwork network = mMeshApi.getMeshNetwork();
                        if (network != null) {
                            for (ProvisionedMeshNode node : network.getNodes()) {
                                if (mMeshApi.nodeIdentityMatches(node, serviceData)) {
                                    meshValid = true;
                                    nodeUuid = node.getUuid();
                                    break;
                                }
                            }
                        }
                    }
                }

                if (meshValid) {
                    MeshScanResult meshScanResult = new MeshScanResult(result);
                    meshScanResult.updateMeshBeacon(mMeshApi);
                    meshScanResult.setNodeUUID(nodeUuid);
                    mScanResults.put(meshScanResult.getAddress(), meshScanResult);
                }
            }

            if (!mHandler.hasMessages(MESSAGE_SCAN_NOTIFY)) {
                mHandler.sendEmptyMessageDelayed(MESSAGE_SCAN_NOTIFY, SCAN_NOTIFY_INTERVAL);
            }
        }

        @Override
        public void onScanFailed(int errorCode) {
            mLog.w("onScanFailed() errorCode: " + errorCode);
        }
    };

    private void notifyScanResults() {
        JSONArray array = new JSONArray();
        for (MeshScanResult result : mScanResults.values()) {
            EspMeshNode espMeshNode = mEspMeshNodes.get(result.getAddress());
            try {
                JSONObject scanJSON = new JSONObject()
                        .put(KeyConst.KEY_NAME, result.getDevice().getName())
                        .put(KeyConst.KEY_ID, result.getDevice().getAddress())
                        .put(KeyConst.KEY_RSSI, result.getRssi());
                if (espMeshNode != null) {
                    scanJSON.put(KeyConst.KEY_ADDRESS, espMeshNode.address);
                }
                array.put(scanJSON);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        mLog.i("notifyScanResults" + array.toString());
        mCallback.scanCallback(array);
    }

    private static final class ApiHandler extends Handler {
        private WeakReference<EspAppApi> mmRef;

        ApiHandler(EspAppApi api) {
            mmRef = new WeakReference<>(api);
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            EspAppApi api = mmRef.get();
            if (api == null) {
                return;
            }
            if (msg.what == MESSAGE_SCAN_NOTIFY) {
                api.notifyScanResults();
            }
        }
    }

    @Override
    public void startScan(String request) {
        BluetoothLeScanner scanner = mBluetoothAdapter.getBluetoothLeScanner();
        if (scanner == null) {
            return;
        }

        String scanType;
        try {
            JSONObject requestJSON = new JSONObject(request);
            scanType = requestJSON.getString(KeyConst.KEY_TYPE);
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }
        UUID uuid;
        if (Objects.equals(scanType, TypeConst.MESH_TYPE_PROVISIONING)) {
            uuid = EspMeshConstants.UUID_PROVISIONING_SERVICE;
        } else if (Objects.equals(scanType, TypeConst.MESH_TYPE_PROXY)) {
            final MeshNetwork network = mMeshApi.getMeshNetwork();
            if (network != null) {
                mNetworkId = mMeshApi.generateNetworkId(network.getNetKeys().get(0).getKey());
            }

            uuid = EspMeshConstants.UUID_PROXY_SERVICE;
        } else {
            mLog.w("startScan() Invalid type: " + scanType);
            return;
        }
        ParcelUuid filterUUID = new ParcelUuid(uuid);
        mFilterUUID = filterUUID;

        ScanFilter filter = new ScanFilter.Builder()
                .setServiceUuid(filterUUID)
                .build();
        List<ScanFilter> filters = Collections.singletonList(filter);
        ScanSettings settings = new ScanSettings.Builder()
                .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                .setReportDelay(0)
                .build();
        mHandler.post(() -> {
            mScanResults.clear();
            scanner.startScan(filters, settings, mScanCallback);
        });
    }

    @Override
    public void stopScan() {
        BluetoothLeScanner scanner = mBluetoothAdapter.getBluetoothLeScanner();
        if (scanner != null) {
            scanner.stopScan(mScanCallback);
        }
        if (mHandler.hasMessages(MESSAGE_SCAN_NOTIFY)) {
            mHandler.removeMessages(MESSAGE_SCAN_NOTIFY);
        }
    }

    @Override
    public void connect(String request) {
        mLog.d("connect() " + request);
        if (mGattClient != null) {
            mLog.w("Gatt connection exists");
            try {
                JSONObject callbackJSON = new JSONObject()
                        .put(KeyConst.KEY_STATUS, StatusConst.STATUS_GATT_EXISTS);
                mCallback.connectCallback(callbackJSON);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return;
        }

        String id;
        String type;
        int address;
        try {
            JSONObject requestJSON = new JSONObject(request);
            id = requestJSON.getString(KeyConst.KEY_ID);
            type = requestJSON.getString(KeyConst.KEY_TYPE);
            address = requestJSON.optInt(KeyConst.KEY_ADDRESS, -1);
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }
        MeshScanResult scanResult = mScanResults.get(id);
        if (scanResult == null) {
            mLog.w("connect() Device not found: " + id);
            try {
                JSONObject callbackJSON = new JSONObject()
                        .put(KeyConst.KEY_STATUS, StatusConst.STATUS_DEVICE_NOT_FOUND);
                mCallback.connectCallback(callbackJSON);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return;
        }
        if (Objects.equals(type, TypeConst.MESH_TYPE_PROVISIONING)) {
            connectToProvisioning(scanResult);
        } else if (Objects.equals(type, TypeConst.MESH_TYPE_PROXY)) {
            if (address >= 0) {
                ProvisionedMeshNode node = mMeshNetwork.getNode(address);
                if (node == null) {
                    try {
                        JSONObject callbackJSON = new JSONObject()
                                .put(KeyConst.KEY_STATUS, StatusConst.STATUS_PROXY_ADDRESS_NOT_FOUNT);
                        mCallback.connectCallback(callbackJSON);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    return;
                }
            }
            connectToProxy(scanResult, address);
        } else {
            mLog.w("connect() Invalid type: " + type);
        }
    }

    @Override
    public void disconnect() {
        mLog.d("disconnect()");
        if (mGattClient != null) {
            mGattClient.close();
            mGattClient = null;
        }
        mUnprovisionedNode = null;
    }

    private abstract class ConnectDeviceCallback implements EspGattClient.DeviceCallback {
        @Override
        public void onDeviceConnected(MeshScanResult sr) {
            mLog.d("onDeviceConnected()");
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put(KeyConst.KEY_STATUS, StatusConst.STATUS_GATT_CONNECTED)
                        .put(KeyConst.KEY_CONNECTED, true);
                mCallback.connectCallback(jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onDeviceDisconnected(MeshScanResult sr) {
            mLog.d("onDeviceDisconnected()");
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put(KeyConst.KEY_STATUS, StatusConst.STATUS_GATT_DISCONNECTED)
                        .put(KeyConst.KEY_CONNECTED, false);
                mCallback.connectCallback(jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            disconnect();
        }

        @Override
        public void onDeviceFailed(MeshScanResult sr) {
            mLog.d("onDeviceFailed()");
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put(KeyConst.KEY_STATUS, StatusConst.STATUS_FAILED);
                mCallback.connectCallback(jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void connectToProvisioning(MeshScanResult scanResult) {
//        deleteEspMeshNode(scanResult.getAddress());
//        ProvisionedMeshNode cacheNode = mMeshNetwork.getNode(scanResult.getAddress());
//        if (cacheNode != null) {
//            mMeshNetwork.deleteNode(cacheNode);
//        }
        mGattClient = new EspGattClient(mContext, scanResult,
                EspMeshConstants.UUID_PROVISIONING_SERVICE, EspMeshConstants.UUID_PROVISIONING_CHAR_WRITE,
                EspMeshConstants.UUID_PROVISIONING_CHAR_NOTIFICATION, mMeshApi);
        mGattClient.setDeviceCallback(new ConnectDeviceCallback() {
            @Override
            public void onDeviceReady(MeshScanResult sr) {
                mLog.d("connectForProvisioning() onDeviceReady");
                if (sr.getMeshBeacon() == null) {
                    mLog.d("onDeviceReady() MeshBeacon is null");
                    ScanRecord scanRecord = sr.getScanRecord();
                    assert scanRecord != null;
                    byte[] serviceData = scanRecord.getServiceData(new ParcelUuid(EspMeshConstants.UUID_PROVISIONING_SERVICE));
                    if (serviceData != null) {
                        UUID uuid = mMeshApi.getDeviceUuid(serviceData);
                        mMeshApi.identifyNode(uuid);
                    } else {
                        mLog.w("onDeviceReady() Provisioning serviceData is null");
                    }
                } else {
                    UnprovisionedBeacon beacon = (UnprovisionedBeacon) sr.getMeshBeacon();
                    mMeshApi.identifyNode(beacon.getUuid());
                }
            }
        });
        mGattClient.connect();
    }

    private void connectToProxy(MeshScanResult scanResult, int address) {
        mLog.d("connectToProxy() dst=" + address);
        mGattClient = new EspGattClient(mContext, scanResult,
                EspMeshConstants.UUID_PROXY_SERVICE, EspMeshConstants.UUID_PROXY_CHAR_WRITE,
                EspMeshConstants.UUID_PROXY_CHAR_NOTIFICATION, mMeshApi);
        mGattClient.setDeviceCallback(new ConnectDeviceCallback() {
            @Override
            public void onDeviceReady(MeshScanResult sr) {
                mLog.d("connectToProxy() onDeviceReady, dst=" + address);
                if (address < 0) {
                    try {
                        JSONObject jsonObject = new JSONObject()
                                .put(KeyConst.KEY_STATUS, StatusConst.STATUS_PROXY_READY_NO_DST);
                        mCallback.connectCallback(jsonObject);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    ProvisionedMeshNode node = mMeshNetwork.getNode(address);
                    if (node != null) {
                        mMessageRequestForConnecting = true;
                        meshCheckNode(node);
                    } else {
                        // Should not come here
                        mLog.w("Proxy Node not found");
                    }
                }
            }
        });
        mGattClient.connect();
    }

    @Override
    public void startProvisioning(String request) {
        String nodeName;
        int oobValue;
        try {
            JSONObject requestJSON = new JSONObject(request);
            nodeName = requestJSON.getString(KeyConst.KEY_NAME);
            oobValue = requestJSON.getInt(KeyConst.KEY_OOB_METHOD);
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }

        AuthenticationOOBMethods oob = AuthenticationOOBMethods.fromValue(oobValue);
        UnprovisionedMeshNode node = mUnprovisionedNode;
        node.setNodeName(nodeName);

        assert oob != null;
        switch (oob) {
            case NO_OOB_AUTHENTICATION:
                mMeshApi.startProvisioning(node);
                break;
            case STATIC_OOB_AUTHENTICATION:
                mMeshApi.startProvisioningWithStaticOOB(node);
                break;
            default:
                break;
        }
    }

    private void addAppKey(ProvisionedMeshNode node) {
        ApplicationKey appKey = mMeshNetwork.getAppKeys().get(0);
        int netKeyIndex = node.getAddedNetKeys().get(0).getIndex();
        NetworkKey netKey = mMeshNetwork.getNetKey(netKeyIndex);
        ConfigAppKeyAdd appKeyAdd = new ConfigAppKeyAdd(netKey, appKey);
        mMeshApi.createMeshPdu(node.getUnicastAddress(), appKeyAdd);
    }

    private boolean bindAppKey(ProvisionedMeshNode node, int dst, int modelId) {
        if (node == null) {
            return false;
        }
        Element element = node.getElements().get(dst);
        if (element == null) {
            mLog.w("bindAppKey() not found Element");
            return false;
        }
        MeshModel model = element.getMeshModels().get(modelId);
        if (model == null) {
            mLog.w("bindAppKey not found MeshModel");
            return false;
        }
        if (!model.getBoundAppKeyIndexes().isEmpty()) {
            return false;
        }

        ApplicationKey key = mMeshNetwork.getAppKeys().get(0);
        ConfigModelAppBind modelAppBind = new ConfigModelAppBind(element.getElementAddress(),
                model.getModelId(), key.getKeyIndex());
        mMeshApi.createMeshPdu(element.getElementAddress(), modelAppBind);
        return true;
    }

    private int[] getWillSubscriptionModelIds(JSONObject requestJSON) throws JSONException {
        int dstAddress = requestJSON.getInt(KeyConst.KEY_DST_ADDRESS);
        int elementAddress = requestJSON.getInt(KeyConst.KEY_ELEMENT_ADDRESS);
        if (!requestJSON.isNull(KeyConst.KEY_MODEL_ID)) {
            int modelId = requestJSON.getInt(KeyConst.KEY_MODEL_ID);
            return new int[]{modelId};
        } else {
            final ProvisionedMeshNode elementNode = mMeshNetwork.getNode(dstAddress);
            if (elementNode == null) {
                mLog.w("getSubscriptionModelIds() ProvisionedMeshNode failed: " + dstAddress);
                return new int[0];
            }
            final Element element = elementNode.getElements().get(elementAddress);
            if (element == null) {
                mLog.w("getSubscriptionModelIds() Element failed: " + elementAddress);
                return new int[0];
            }
            Collection<MeshModel> models = element.getMeshModels().values();
            int[] result = new int[models.size()];
            int index = 0;
            for (MeshModel model : models) {
                result[index] = model.getModelId();
                ++index;
            }
            return result;
        }
    }

    private void groupBatchActions(final int[] modelIds, final Consumer<Integer> action) {
        Arrays.sort(modelIds);
        final List<Integer> modelIdList = new ArrayList<>(modelIds.length);
        for (int modelId : modelIds) {
            if (modelId == SigModelParser.GENERIC_ON_OFF_SERVER) {
                modelIdList.add(modelId);
                break; // just process on off module, device will process others
            }
        }
        if (modelIdList.isEmpty()) {
            mLog.w("groupBatchActions() not found on off model");
            return;
        }
        Observable.fromIterable(modelIdList)
                .subscribeOn(Schedulers.io())
                .doOnNext(modelId -> {
                    action.accept(modelId);
                    Thread.sleep(GROUP_ACTION_INTERVAL);
                })
                .subscribe(new RxObserver<Integer>() {
                    @Override
                    public void onError(Throwable e) {
                        if (e instanceof InterruptedException) {
                            mLog.w("InterruptedException groupBatchActions");
                        } else {
                            e.printStackTrace();
                        }
                    }
                });
    }

    @Override
    public void sendMeshMessage(String request) {
        if (mGattClient == null) {
            mLog.w("sendMeshMessage(): no Gatt connection");
            return;
        }

        synchronized (mMessageTaskQueue) {
            if (!mMessageTaskQueue.isEmpty()) {
                mMessageTaskQueue.clear();
            }
        }
        NetworkKey networkKey = mMeshNetwork.getPrimaryNetworkKey();
        ApplicationKey appKey = mMeshNetwork.getAppKeys().get(0);
        try {
            JSONObject requestJSON = new JSONObject(request);
            int opCode = requestJSON.getInt(KeyConst.KEY_OP_CODE);
            int dstAddress = requestJSON.getInt(KeyConst.KEY_DST_ADDRESS);

            // Start Config Message
            if (opCode == EspApiOpCodes.CONFIG_COMPOSITION_DATA_GET) {
                mMessageRequestForProxy = true;
                meshCompositionDataGet(dstAddress);
                return;
            } else if (opCode == EspApiOpCodes.CONFIG_DEFAULT_TTL_GET) {
                ConfigDefaultTtlGet message = new ConfigDefaultTtlGet();
                mMeshApi.createMeshPdu(dstAddress, message);
                return;
            } else if (opCode == EspApiOpCodes.CONFIG_APPKEY_ADD) {
                if (networkKey == null) {
                    mLog.w("CONFIG_APPKEY_ADD no networkKey");
                    return;
                }
                ConfigAppKeyAdd message = new ConfigAppKeyAdd(networkKey, appKey);
                mMeshApi.createMeshPdu(dstAddress, message);
                return;
            } else if (opCode == EspApiOpCodes.CONFIG_NETWORK_TRANSMIT_SET) {
                ConfigNetworkTransmitSet message = new ConfigNetworkTransmitSet(2, 1);
                mMeshApi.createMeshPdu(dstAddress, message);
                return;
            } else if (opCode == EspApiOpCodes.CONFIG_MODEL_SUBSCRIPTION_ADD) {
                int elementAddress = requestJSON.getInt(KeyConst.KEY_ELEMENT_ADDRESS);
                int groupAddress = requestJSON.getInt(KeyConst.KEY_GROUP_ADDRESS);
                int[] modelIds = getWillSubscriptionModelIds(requestJSON);
                Consumer<Integer> action = modelId -> {
                    ConfigModelSubscriptionAdd message = new ConfigModelSubscriptionAdd(elementAddress,
                            groupAddress, modelId);
                    mMeshApi.createMeshPdu(dstAddress, message);
                };
                mLog.d("Send CONFIG_MODEL_SUBSCRIPTION_ADD");
                groupBatchActions(modelIds, action);
                return;
            } else if (opCode == EspApiOpCodes.CONFIG_MODEL_SUBSCRIPTION_DELETE) {
                int elementAddress = requestJSON.getInt(KeyConst.KEY_ELEMENT_ADDRESS);
                int groupAddress = requestJSON.getInt(KeyConst.KEY_GROUP_ADDRESS);
                int[] modelIds = getWillSubscriptionModelIds(requestJSON);
                Consumer<Integer> action = modelId -> {
                    ConfigModelSubscriptionDelete message = new ConfigModelSubscriptionDelete(elementAddress,
                            groupAddress, modelId);
                    mMeshApi.createMeshPdu(dstAddress, message);
                };
                mLog.d("Send CONFIG_MODEL_SUBSCRIPTION_DELETE");
                groupBatchActions(modelIds, action);
                return;
            } else if (opCode == EspApiOpCodes.CONFIG_MODEL_SUBSCRIPTION_DELETE_ALL) {
                int elementAddress = requestJSON.getInt(KeyConst.KEY_ELEMENT_ADDRESS);
                int[] modelIds = getWillSubscriptionModelIds(requestJSON);
                Consumer<Integer> action = modelId -> {
                    ConfigModelSubscriptionDeleteAll message = new ConfigModelSubscriptionDeleteAll(elementAddress, modelId);
                    mMeshApi.createMeshPdu(dstAddress, message);
                };
                mLog.d("Send CONFIG_MODEL_SUBSCRIPTION_DELETE_ALL");
                groupBatchActions(modelIds, action);
                return;
            } else if (opCode == EspApiOpCodes.CONFIG_MODEL_SUBSCRIPTION_OVERWRITE) {
                int elementAddress = requestJSON.getInt(KeyConst.KEY_ELEMENT_ADDRESS);
                int groupAddress = requestJSON.getInt(KeyConst.KEY_GROUP_ADDRESS);
                int[] modelIds = getWillSubscriptionModelIds(requestJSON);
                Consumer<Integer> action = modelId -> {
                    ConfigModelSubscriptionOverwrite message = new ConfigModelSubscriptionOverwrite(elementAddress,
                            groupAddress, modelId);
                    mMeshApi.createMeshPdu(dstAddress, message);
                };
                mLog.d("Send CONFIG_MODEL_SUBSCRIPTION_OVERWRITE");
                groupBatchActions(modelIds, action);
                return;
            } else if (opCode == EspApiOpCodes.CONFIG_NODE_RESET) {
                ConfigNodeReset message = new ConfigNodeReset();
                mMeshApi.createMeshPdu(dstAddress, message);
                return;
            }
            // End Config Message
            // Start Espressif Custom Message
            else if (opCode == EspApiOpCodes.FAST_PROV_INFO_SET) {
                ProvisionedMeshNode node = mMeshNetwork.getNode(dstAddress);
                int provCount = requestJSON.optInt(KeyConst.KEY_EXPECT_PROVISIONING_COUNT, 100);
                int unicastAddressMin = 0x0400;
                int groupAddress = EspMeshConstants.ADDRESS_GROUP_MIN;
                Provisioner provisioner = mMeshNetwork.getSelectedProvisioner();
                Integer primaryProvisionerAddress = provisioner.getProvisionerAddress();
                byte[] matchValue = {
                        (byte) Integer.parseInt(node.getUuid().substring(2, 4), 16),
                        (byte) Integer.parseInt(node.getUuid().substring(0, 2), 16),
                };
                int action = 0b10000001;
                EspFastProvInfoSet message = new EspFastProvInfoSet(appKey, provCount, unicastAddressMin,
                        null, null, null, null, groupAddress, primaryProvisionerAddress, matchValue, action);
                Runnable runnable = () -> mMeshApi.createMeshPdu(dstAddress, message);

                synchronized (mMessageTaskQueue) {
                    if (node.getAddedAppKeys().isEmpty()) {
                        addAppKey(node);
                        mMessageTaskQueue.add(runnable);
                    } else {
                        runnable.run();
                    }
                }
                return;
            } else if (opCode == EspApiOpCodes.FAST_PROV_NODE_ADDR_GET) {
                ProvisionedMeshNode node = mMeshNetwork.getNode(dstAddress);
                EspFastProvNodeAddrGet message = new EspFastProvNodeAddrGet(appKey);
                Runnable runnable = () -> mMeshApi.createMeshPdu(dstAddress, message);
                synchronized (mMessageTaskQueue) {
                    if (node.getAddedAppKeys().isEmpty()) {
                        addAppKey(node);
                        mMessageTaskQueue.add(runnable);
                    } else {
                        runnable.run();
                    }
                }
                return;
            } else if (opCode == EspApiOpCodes.OTA_NBVN_FB ||
                    ((opCode >> 16 & 0xff) == EspApiOpCodes.OTA_NBVN_FB)) {
                mLog.i("Send OTA NBVN message");
                int companyId;
                if ((opCode >> 16 & 0xff) == EspApiOpCodes.OTA_NBVN_FB) {
                    companyId = EspOtaUtils.getCompanyId(opCode);
                } else {
                    companyId = requestJSON.getInt(KeyConst.KEY_COMPANY_ID);
                }
                int binId = requestJSON.getInt(KeyConst.KEY_BIN_ID);
                int version = requestJSON.getInt(KeyConst.KEY_BIN_VERSION_CODE);
                boolean clearFlash = requestJSON.optBoolean(KeyConst.KEY_CLEAR_FLASH, false);
                String url = requestJSON.optString(KeyConst.KEY_URL, "");
                boolean urlEnable = !TextUtils.isEmpty(url);
                ApplicationKey key = EspOtaUtils.getOtaAppKey();
                EspOtaNewBinVersionNotification notification = new EspOtaNewBinVersionNotification(key, companyId, binId,
                        version, clearFlash, urlEnable);
                mMeshApi.createMeshPdu(dstAddress, notification);
                return;
            } else if (opCode == EspApiOpCodes.OTA_START_FB ||
                    ((opCode >> 16 & 0xff) == EspApiOpCodes.OTA_START_FB)) {
                mLog.i("Send OTA Start message");
                int companyId;
                if ((opCode >> 16 & 0xff) == EspApiOpCodes.OTA_START_FB) {
                    companyId = EspOtaUtils.getCompanyId(opCode);
                } else {
                    companyId = requestJSON.getInt(KeyConst.KEY_COMPANY_ID);
                }
                int binId = requestJSON.optInt(KeyConst.KEY_BIN_ID, -1);
                Random random = new Random();
                byte[] ssid = {
                        (byte) ('a' + random.nextInt('z' - 'a')),
                        (byte) ('a' + random.nextInt('z' - 'a'))
                };
                byte[] password = {
                        (byte) ('a' + random.nextInt('z' - 'a')),
                        (byte) ('a' + random.nextInt('z' - 'a'))
                };
                String url = requestJSON.optString(KeyConst.KEY_URL, "");
                String urlSSID = requestJSON.optString(KeyConst.KEY_URL_SSID, "");
                String urlPassword = requestJSON.optString(KeyConst.KEY_URL_PASSWORD, "");
                byte[] urlBytes = Arrays.copyOf(url.getBytes(), 256);
                byte[] urlSSIDBytes = Arrays.copyOf(urlSSID.getBytes(), 32);
                byte[] urlPasswordBytes = Arrays.copyOf(urlPassword.getBytes(), 64);
                ApplicationKey key = EspOtaUtils.getOtaAppKey();

                EspOtaStart message = new EspOtaStart(key, companyId, ssid, password, urlBytes, urlSSIDBytes, urlPasswordBytes);
                mMeshApi.createMeshPdu(dstAddress, message);
                mOtaSSID = EspOtaUtils.getOtaSoftApSSID(ssid, dstAddress);
                mOtaPassword = EspOtaUtils.getOtaSoftApPassword(password, ssid, dstAddress);

                return;
            } else if (opCode == EspApiOpCodes.CURRENT_VERSION_GET) {
                ApplicationKey key = EspOtaUtils.getOtaAppKey(); // todo
                EspCurrentVersionGet message = new EspCurrentVersionGet(key);
                mMeshApi.createMeshPdu(dstAddress, message);

                return;
            }
            // End Espressif Custom Message

            // Check Model App bind status
            int dstModelId;
            switch (opCode) {
                case EspApiOpCodes.GENERIC_ON_OFF_GET:
                case EspApiOpCodes.GENERIC_ON_OFF_SET:
                case EspApiOpCodes.GENERIC_ON_OFF_SET_UNACKNOWLEDGED: {
                    dstModelId = EspMeshModelIds.GENERIC_ON_OFF_SERVER;
                    break;
                }
                case EspApiOpCodes.LIGHT_LIGHTNESS_GET:
                case EspApiOpCodes.LIGHT_LIGHTNESS_SET:
                case EspApiOpCodes.LIGHT_LIGHTNESS_SET_UNACKNOWLEDGED:
                    dstModelId = EspMeshModelIds.LIGHT_LIGHTNESS_SERVER;
                    break;
                case EspApiOpCodes.LIGHT_HSL_GET:
                case EspApiOpCodes.LIGHT_HSL_SET:
                case EspApiOpCodes.LIGHT_HSL_SET_UNACKNOWLEDGED: {
                    dstModelId = EspMeshModelIds.LIGHT_HSL_SERVER;
                    break;
                }
                case EspApiOpCodes.LIGHT_CTL_GET:
                case EspApiOpCodes.LIGHT_CTL_SET:
                case EspApiOpCodes.LIGHT_CTL_SET_UNACKNOWLEDGED:
                    dstModelId = EspMeshModelIds.LIGHT_CTL_SERVER;
                    break;
                default: {
                    mLog.w("Unsupported OP Code");
                    return;
                }
            }

            ProvisionedMeshNode node = null;
            if (!requestJSON.isNull(KeyConst.KEY_NODE_ADDRESS)) {
                int nodeAddress = requestJSON.getInt(KeyConst.KEY_NODE_ADDRESS);
                node = mMeshNetwork.getNode(nodeAddress);
            }
            Runnable runnable = null;
            switch (opCode) {
                case EspApiOpCodes.GENERIC_ON_OFF_GET: {
                    GenericOnOffGet message = new GenericOnOffGet(appKey);
                    runnable = () -> mMeshApi.createMeshPdu(dstAddress, message);
                    break;
                }
                case EspApiOpCodes.GENERIC_ON_OFF_SET: {
                    boolean state = requestJSON.getBoolean(KeyConst.KEY_STATE);
                    int tid = new Random().nextInt();
                    GenericOnOffSet message = new GenericOnOffSet(appKey, state, tid);
                    runnable = () -> mMeshApi.createMeshPdu(dstAddress, message);
                    break;
                }
                case EspApiOpCodes.GENERIC_ON_OFF_SET_UNACKNOWLEDGED: {
                    boolean state = requestJSON.getBoolean(KeyConst.KEY_STATE);
                    int tid = new Random().nextInt();
                    GenericOnOffSetUnacknowledged message = new GenericOnOffSetUnacknowledged(appKey, state, tid);
                    runnable = () -> mMeshApi.createMeshPdu(dstAddress, message);
                    break;
                }
                case EspApiOpCodes.LIGHT_LIGHTNESS_GET: {
                    LightLightnessGet message = new LightLightnessGet(appKey);
                    runnable = () -> mMeshApi.createMeshPdu(dstAddress, message);
                    break;
                }
                case EspApiOpCodes.LIGHT_LIGHTNESS_SET: {
                    int lightness = EspMeshUtils.double2FFFF(requestJSON.getDouble(KeyConst.KEY_LIGHTNESS));
                    int tid = new Random().nextInt();
                    LightLightnessSet message = new LightLightnessSet(appKey, lightness, tid);
                    runnable = () -> mMeshApi.createMeshPdu(dstAddress, message);
                    break;
                }
                case EspApiOpCodes.LIGHT_LIGHTNESS_SET_UNACKNOWLEDGED: {
                    int lightness = EspMeshUtils.double2FFFF(requestJSON.getDouble(KeyConst.KEY_LIGHTNESS));
                    int tid = new Random().nextInt();
                    LightLightnessSetUnacknowledged message = new LightLightnessSetUnacknowledged(appKey, lightness, tid);
                    runnable = () -> mMeshApi.createMeshPdu(dstAddress, message);
                    break;
                }
                case EspApiOpCodes.LIGHT_HSL_GET: {
                    LightHslGet message = new LightHslGet(appKey);
                    runnable = () -> mMeshApi.createMeshPdu(dstAddress, message);
                    break;
                }
                case EspApiOpCodes.LIGHT_HSL_SET: {
                    JSONArray hslArray = requestJSON.getJSONArray(KeyConst.KEY_HSL);
                    int[] meshHSL = EspMeshUtils.HSL2MeshHSL(new double[]{
                            hslArray.getDouble(0), hslArray.getDouble(1), hslArray.getDouble(2)
                    });
                    int tid = new Random().nextInt();
                    LightHslSet message = new LightHslSet(appKey, meshHSL[2], meshHSL[0], meshHSL[1], tid);
                    runnable = () -> mMeshApi.createMeshPdu(dstAddress, message);
                    break;
                }
                case EspApiOpCodes.LIGHT_HSL_SET_UNACKNOWLEDGED: {
                    JSONArray hslArray = requestJSON.getJSONArray(KeyConst.KEY_HSL);
                    int[] meshHSL = EspMeshUtils.HSL2MeshHSL(new double[]{
                            hslArray.getDouble(0), hslArray.getDouble(1), hslArray.getDouble(2)
                    });
                    int tid = new Random().nextInt();
                    LightHslSetUnacknowledged message = new LightHslSetUnacknowledged(appKey, meshHSL[2], meshHSL[0], meshHSL[1], tid);
                    runnable = () -> mMeshApi.createMeshPdu(dstAddress, message);
                    break;
                }
                case EspApiOpCodes.LIGHT_CTL_GET: {
                    LightCtlGet message = new LightCtlGet(appKey);
                    runnable = () -> mMeshApi.createMeshPdu(dstAddress, message);
                    break;
                }
                case EspApiOpCodes.LIGHT_CTL_SET: {
                    JSONArray ctlArray = requestJSON.getJSONArray(KeyConst.KEY_CTL);
                    int c = EspMeshUtils.double2FFFF(ctlArray.getDouble(0));
                    int t = 800 + (int) (100 * ctlArray.getDouble(1));
                    int l = EspMeshUtils.double2FFFF(ctlArray.getDouble(2));
                    int tid = new Random().nextInt();
                    LightCtlSet message = new LightCtlSet(appKey, l, t, c, tid);
                    runnable = () -> mMeshApi.createMeshPdu(dstAddress, message);
                    break;
                }
                case EspApiOpCodes.LIGHT_CTL_SET_UNACKNOWLEDGED: {
                    JSONArray ctlArray = requestJSON.getJSONArray(KeyConst.KEY_CTL);
                    int c = EspMeshUtils.double2FFFF(ctlArray.getDouble(0));
                    int t = 800 + (int) (100 * ctlArray.getDouble(1));
                    int l = EspMeshUtils.double2FFFF(ctlArray.getDouble(2));
                    int tid = new Random().nextInt();
                    LightCtlSetUnacknowledged message = new LightCtlSetUnacknowledged(appKey, l, t, c, tid);
                    runnable = () -> mMeshApi.createMeshPdu(dstAddress, message);
                    break;
                }
                default: {
                    mLog.w("Unsupported OP Code");
                    return;
                }
            }
            synchronized (mMessageTaskQueue) {
                if (node != null && node.getElements().isEmpty()) {
                    meshCheckNode(node);
                } else if (bindAppKey(node, dstAddress, dstModelId)) {
                    mMessageTaskQueue.add(runnable);
                } else {
                    runnable.run();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void meshCheckNode(ProvisionedMeshNode node) {
        if (node == null) {
            return;
        }
        int address = node.getUnicastAddress();
        if (node.getElements().isEmpty()) {
            mLog.d("meshCheckNode() ConfigCompositionDataGet");
            meshCompositionDataGet(address);
        } else if (node.getTtl() == null) {
            mLog.d("meshCheckNode() ConfigDefaultTtlGet");
            ConfigDefaultTtlGet ttlGet = new ConfigDefaultTtlGet();
            mMeshApi.createMeshPdu(node.getUnicastAddress(), ttlGet);
        } else if (node.getAddedAppKeys().isEmpty()) {
            mLog.d("meshCheckNode() ConfigAppKeyAdd");
            addAppKey(node);
        } else if (node.getNetworkTransmitSettings() == null) {
            mLog.d("meshCheckNode() ConfigNetworkTransmitSet");
            ConfigNetworkTransmitSet networkTransmitSet = new ConfigNetworkTransmitSet(2, 1);
            mMeshApi.createMeshPdu(node.getUnicastAddress(), networkTransmitSet);
        } else {
            mLog.d("meshCheckNode() Node is ready");
            try {
                JSONObject jsonObject = new JSONObject();
                if (mMessageRequestForConnecting) {
                    mMessageRequestForConnecting = false;
                    jsonObject.put(KeyConst.KEY_STATUS, StatusConst.STATUS_PROXY_READY);
                    jsonObject.put(KeyConst.KEY_NODE, toProvisionedMeshNodeJSON(node));
                    mCallback.connectCallback(jsonObject);
                }
                if (mMessageRequestForProxy) {
                    mMessageRequestForProxy = false;
                    jsonObject.put(KeyConst.KEY_OP_CODE, ConfigMessageOpCodes.CONFIG_COMPOSITION_DATA_STATUS);
                    jsonObject.put(KeyConst.KEY_NODE, toProvisionedMeshNodeJSON(node));
                    mCallback.meshMessageCallback(jsonObject);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void meshCompositionDataGet(int dst) {
        ConfigCompositionDataGet message = new ConfigCompositionDataGet();
        mMeshApi.createMeshPdu(dst, message);
    }

    @Override
    public void createGroup(String request) {
        String name;
        String description;
        try {
            JSONObject requestJSON = new JSONObject(request);
            name = requestJSON.getString(KeyConst.KEY_NAME);
            description = requestJSON.optString(KeyConst.KEY_DESCRIPTION, "");
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }

        Group group = mMeshNetwork.createGroup(mMeshNetwork.getSelectedProvisioner(), name);
        try {
            JSONObject groupJSON;
            if (group == null) {
                groupJSON = new JSONObject();
            } else {
                mMeshNetwork.addGroup(group);
                EspMeshGroup espMeshGroup = new EspMeshGroup();
                espMeshGroup.description = description;
                espMeshGroup.address = group.getAddress();
                espMeshGroup.meshUUID = mMeshNetwork.getMeshUUID();
                MeshObjectBox.getInstance().updateMeshGroups(Collections.singletonList(espMeshGroup));
                mEspMeshGroups.add(espMeshGroup);

                groupJSON = toGroupJSON(group, espMeshGroup);
                groupJSON.put(KeyConst.KEY_DESCRIPTION, description);
            }
            mCallback.createGroupCallback(groupJSON);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updateGroup(String request) {
        int address;
        String name;
        String description;
        try {
            JSONObject requestJSON = new JSONObject(request);
            address = requestJSON.getInt(KeyConst.KEY_ADDRESS);
            name = requestJSON.getString(KeyConst.KEY_NAME);
            description = requestJSON.optString(KeyConst.KEY_DESCRIPTION, "");
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }

        Group group = mMeshNetwork.getGroup(address);
        if (group != null) {
            group.setName(name);
            mMeshNetwork.updateGroup(group);
        }
        EspMeshGroup espMeshGroup = getEspMeshGroupForAddress(address);
        if (espMeshGroup != null) {
            espMeshGroup.description = description;
            MeshObjectBox.getInstance().updateMeshGroups(Collections.singletonList(espMeshGroup));
        }
    }

    @Override
    public void removeGroup(String request) {
        int address;
        try {
            JSONObject requestJSON = new JSONObject(request);
            address = requestJSON.getInt(KeyConst.KEY_ADDRESS);
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }
        Group group = mMeshNetwork.getGroup(address);
        if (group != null) {
            mMeshNetwork.removeGroup(group);
            mLog.d("removeGroup() remove from MeshDB " + address);
        }
        EspMeshGroup espMeshGroup = getEspMeshGroupForAddress(address);
        if (espMeshGroup != null) {
            MeshObjectBox.getInstance().deleteMeshGroups(Collections.singletonList(espMeshGroup));
            mEspMeshGroups.remove(espMeshGroup);
            mLog.d("removeGroup() remove from EspDB " + address);
        }
    }

    @Override
    public void getOtaBins() {
        List<EspOtaBin> bins = EspUtils.getOtaBins(mContext);
        JSONArray binArray = new JSONArray();
        for (EspOtaBin bin : bins) {
            try {
                JSONObject binJSON = new JSONObject()
                        .put(KeyConst.KEY_NAME, bin.getName())
                        .put(KeyConst.KEY_BIN_ID, bin.getBinId())
                        .put(KeyConst.KEY_BIN_VERSION_NAME, bin.getVersionName())
                        .put(KeyConst.KEY_BIN_VERSION_CODE, bin.getVersionCode());
                binArray.put(binJSON);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        mCallback.getOtaBinsCallback(binArray);
    }

    @Override
    public void getOtaBinVersionCode(String version) {
        try {
            JSONObject respJson = new JSONObject().put(KeyConst.KEY_BIN_VERSION_NAME, version);
            try {
                int versionCode = EspOtaUtils.binVersionString2Int(version);
                respJson.put(KeyConst.KEY_BIN_VERSION_CODE, versionCode);
                respJson.put(KeyConst.KEY_STATUS, StatusConst.STATUS_SUCCESS);
            } catch (Exception e) {
                mLog.w("An exception occurred: " + e.getMessage());
                respJson.put(KeyConst.KEY_STATUS, StatusConst.STATUS_FAILED);
            }
            mCallback.getOtaBinVersionCodeCallback(respJson);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void sendOtaMeshMessage(String request) {
        JSONObject requestJSON;
        try {
            requestJSON = new JSONObject(request);
            requestJSON.put(KeyConst.KEY_OP_CODE, EspApiOpCodes.OTA_NBVN_FB);

            sendMeshMessage(requestJSON.toString());
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }

        final LinkedBlockingQueue<Boolean> nounQueue = new LinkedBlockingQueue<>();
        final LinkedBlockingQueue<Boolean> startRespQueue = new LinkedBlockingQueue<>();
        mOtaNounQueue = nounQueue;
        mOtaStartRespQueue = startRespQueue;
        System.out.println("XXJ new Queue");
        new Thread(() -> {
            try {
                Boolean noun = nounQueue.poll(5, TimeUnit.SECONDS);
                if (EspUtils.isTrue(noun)) {
                    mLog.d("Receive NOUN response");
                } else {
                    JSONObject jsonObject = new JSONObject()
                            .put(KeyConst.KEY_STATUS, OTA_STATUS_MESSAGE_NOT_NEED);
                    mCallback.otaCallback(jsonObject);
                    return;
                }

                JSONObject startRequestJSON = new JSONObject(request);
                startRequestJSON.put(KeyConst.KEY_OP_CODE, EspApiOpCodes.OTA_START_FB);
                sendMeshMessage(startRequestJSON.toString());

                Boolean startResp = startRespQueue.poll(5, TimeUnit.SECONDS);
                mLog.d("Receive OTA Start Response " + startResp);
                JSONObject jsonObject = new JSONObject()
                        .put(KeyConst.KEY_STATUS, OTA_STATUS_MESSAGE_READY)
                        .put(KeyConst.KEY_SSID, mOtaSSID)
                        .put(KeyConst.KEY_PASSWORD, mOtaPassword);
                mCallback.otaCallback(jsonObject);
            } catch (InterruptedException | JSONException e) {
                e.printStackTrace();
            } finally {
                mOtaNounQueue = null;
                mOtaStartRespQueue = null;
            }
        }).start();
    }

    @Override
    public void startOta(String request) {
        if (mOtaClient != null) {
            try {
                JSONObject jsonObject = new JSONObject()
                        .put(KeyConst.KEY_STATUS, IEspOtaClient.STATUS_INVALID_STATE);
                mCallback.otaCallback(jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return;
        }

        try {
            JSONObject requestJSON = new JSONObject(request);
            String name = requestJSON.getString(KeyConst.KEY_NAME);
            int companyId = requestJSON.getInt(KeyConst.KEY_COMPANY_ID);
            int binId = requestJSON.getInt(KeyConst.KEY_BIN_ID);
            int version = requestJSON.getInt(KeyConst.KEY_BIN_VERSION_CODE);

            File dir = EspUtils.getOtaBinDir(mContext);
            File bin = new File(dir, name);
            byte[] netKey = Objects.requireNonNull(mMeshNetwork.getPrimaryNetworkKey()).getKey();
            mOtaClient = new EspOtaClient(bin, companyId, binId, version, netKey);
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }

        mOtaClient.setOtaListener(new EspOtaClient.OtaListener() {
            @Override
            public void onConnected() {
                try {
                    JSONObject jsonObject = new JSONObject()
                            .put(KeyConst.KEY_STATUS, OTA_STATUS_CONNECTED);
                    mCallback.otaCallback(jsonObject);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onOtaProgress(float progress) {
                try {
                    JSONObject jsonObject = new JSONObject()
                            .put(KeyConst.KEY_STATUS, OTA_STATUS_PROGRESS)
                            .put(KeyConst.KEY_PROGRESS, progress);
                    mCallback.otaCallback(jsonObject);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onOtaComplete() {
                try {
                    JSONObject jsonObject = new JSONObject()
                            .put(KeyConst.KEY_STATUS, OTA_STATUS_COMPLETE);
                    mCallback.otaCallback(jsonObject);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailed(int status) {
                try {
                    JSONObject jsonObject = new JSONObject()
                            .put(KeyConst.KEY_STATUS, status);
                    mCallback.otaCallback(jsonObject);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        Observable.just(mOtaClient)
                .subscribeOn(Schedulers.io())
                .doOnNext(EspOtaClient::ota)
                .subscribe(new RxObserver<EspOtaClient>() {
                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        stopOta();
                        try {
                            JSONObject jsonObject = new JSONObject()
                                    .put(KeyConst.KEY_STATUS, OTA_STATUS_EXCEPTION);
                            mCallback.otaCallback(jsonObject);
                        } catch (JSONException je) {
                            je.printStackTrace();
                        }
                    }

                    @Override
                    public void onComplete() {
                        stopOta();
                    }
                });
    }

    @Override
    public void stopOta() {
        if (mOtaClient != null) {
            try {
                mOtaClient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            mOtaClient = null;
        }
    }

    // endregion
}
