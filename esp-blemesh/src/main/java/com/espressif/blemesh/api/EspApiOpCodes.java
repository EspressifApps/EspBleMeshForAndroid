package com.espressif.blemesh.api;

import no.nordicsemi.android.mesh.opcodes.ApplicationMessageOpCodes;
import no.nordicsemi.android.mesh.opcodes.ConfigMessageOpCodes;
import no.nordicsemi.android.mesh.opcodes.EspMessageOpCodes;

public final class EspApiOpCodes {

    /**
     * App call this opcode auto
     */
    public static final int CONFIG_COMPOSITION_DATA_GET = ConfigMessageOpCodes.CONFIG_COMPOSITION_DATA_GET;

    /**
     * App call this opcode auto
     */
    public static final int CONFIG_DEFAULT_TTL_GET = ConfigMessageOpCodes.CONFIG_DEFAULT_TTL_GET;

    /**
     * App call this opcode auto
     */
    public static final int CONFIG_APPKEY_ADD = ConfigMessageOpCodes.CONFIG_APPKEY_ADD;

    public static final int CONFIG_MODEL_APP_STATUS = ConfigMessageOpCodes.CONFIG_MODEL_APP_STATUS;

    /**
     * App call this opcode auto
     */
    public static final int CONFIG_NETWORK_TRANSMIT_SET = ConfigMessageOpCodes.CONFIG_NETWORK_TRANSMIT_SET;

    /**
     * {
     *     "opCode":0x801B,
     *     "dstAddress":1,
     *     "elementAddress":2,
     *     "groupAddress":49152,
     *     "modelId":4096
     * }
     *
     * dstAddress: node address
     * elementAddress: element address
     * groupAddress: group address user want add subscription
     * modelId: mesh model id, nullable. If null, App will add subscription for all models in the dst element
     *
     * Response OpCode: {@link #CONFIG_MODEL_SUBSCRIPTION_STATUS}
     */
    public static final int CONFIG_MODEL_SUBSCRIPTION_ADD = ConfigMessageOpCodes.CONFIG_MODEL_SUBSCRIPTION_ADD;

    /**
     * {
     *     "opCode":0x801C,
     *     "dstAddress":1,
     *     "elementAddress":2,
     *     "groupAddress":49152,
     *     "modelId":4096
     * }
     *
     * dstAddress: node address
     * elementAddress: element address
     * groupAddress: group address user want delete subscription
     * modelId: mesh model id, nullable. If null, App will delete subscription for all models in the dst element
     *
     * Response OpCode: {@link #CONFIG_MODEL_SUBSCRIPTION_STATUS}
     */
    public static final int CONFIG_MODEL_SUBSCRIPTION_DELETE = ConfigMessageOpCodes.CONFIG_MODEL_SUBSCRIPTION_DELETE;

    /**
     * {
     *     "opCode":0x801D,
     *     "dstAddress":1,
     *     "elementAddress":2,
     *     "modelId":4096
     * }
     *
     * dstAddress: node address
     * elementAddress: element address
     * modelId: mesh model id, nullable. If null, App will delete subscription all models in the dst element
     *
     * Response OpCode: {@link #CONFIG_MODEL_SUBSCRIPTION_STATUS}
     */
    public static final int CONFIG_MODEL_SUBSCRIPTION_DELETE_ALL = ConfigMessageOpCodes.CONFIG_MODEL_SUBSCRIPTION_DELETE_ALL;

    /**
     * {
     *     "opCode":0x801E,
     *     "dstAddress":1,
     *     "elementAddress":2,
     *     "groupAddress":49152,
     *     "modelId":4096
     * }
     *
     * dstAddress: node address
     * elementAddress: element address
     * groupAddress: group address user want add subscription
     * modelId: mesh model id, nullable. If null, App will overwrite subscription for all models in the dst element
     *
     * Response OpCode: {@link #CONFIG_MODEL_SUBSCRIPTION_STATUS}
     */
    public static final int CONFIG_MODEL_SUBSCRIPTION_OVERWRITE = ConfigMessageOpCodes.CONFIG_MODEL_SUBSCRIPTION_OVERWRITE;

    /**
     * {
     *     "opCode":0x801F,
     *     "srcAddress":1,
     *     "nodeAddress":1,
     *     "elementAddress":1,
     *     "modelId":4096,
     *     "groups":[49152]
     * }
     *
     * srcAddress: element address
     * nodeAddress: node address
     * elementAddress: element address
     * modelId: subscription model id
     * groups: model current subscription group address array
     */
    public static final int CONFIG_MODEL_SUBSCRIPTION_STATUS = ConfigMessageOpCodes.CONFIG_MODEL_SUBSCRIPTION_STATUS;

    /**
     * {
     *     "opCode":0x8049,
     *     "dstAddress":2
     * }
     *
     * dstAddress: node address
     */
    public static final int CONFIG_NODE_RESET = ConfigMessageOpCodes.CONFIG_NODE_RESET;

    /**
     * {
     *     "opCode":0x804A,
     *     "srcAddress":2
     * }
     *
     * srcAddress: node address
     */
    public static final int CONFIG_NODE_RESET_STATUS = ConfigMessageOpCodes.CONFIG_NODE_RESET_STATUS;

    /**
     * {
     *     "opCode":0x02E5C0,
     *     "dstAddress":1,
     *     "expectProvisioningCount":50,
     * }
     *
     * dstAddress: node address
     * expectProvisioningCount: nullable, default value is 100
     *
     * Response OpCode: {@link #FAST_PROV_INFO_STATUS}
     */
    public static final int FAST_PROV_INFO_SET = EspMessageOpCodes.FAST_PROV_INFO_SET;

    /**
     * {
     *     "opCode":0x02E5C1,
     *     "srcAddress":1,
     *     "nodeAddress":1,
     * }
     *
     * srcAddress: node address
     * nodeAddress: node address
     */
    public static final int FAST_PROV_INFO_STATUS = EspMessageOpCodes.FAST_PROV_INFO_STATUS;

    /**
     * {
     *     "opCode":0x02E5C6,
     *     "dstAddress":1,
     * }
     *
     * dstAddress: node address
     *
     * Response OpCode: {@link #FAST_PROV_NODE_ADDR_STATUS}
     */
    public static final int FAST_PROV_NODE_ADDR_GET = EspMessageOpCodes.FAST_PROV_NODE_ADDR_GET;

    /**
     * {
     *     "opCode":0x02E5C7,
     *     "srcAddress":1,
     *     "nodeAddress":1,
     *     "fastNodes":[
     *         {
     *             "id":1,
     *             "address":100,
     *             "meshUuid":"Network UUID"
     *         }
     *     ],
     * }
     *
     * srcAddress: node address
     * nodeAddress: node address
     * fastNodes: fast provisioned node array
     *     id: database id
     *     address: fast node address
     *     meshUuid: network UUID
     */
    public static final int FAST_PROV_NODE_ADDR_STATUS = EspMessageOpCodes.FAST_PROV_NODE_ADDR_STATUS;

    /**
     * {
     *     "opCode":0xCC,
     *     "dstAddress":1,
     *     "companyId":0x02E5,
     *     "binId":0x0001,
     *     "versionCode":0x0524,
     *     "clearFlash":false
     * }
     *
     * opCode: 0xCC or 0xCC****. **** is Company Id, for example, "companyId" is 0x02E5,
     *       than the "opCode" should be 0xCCE502
     * dstAddress: node address
     * companyId: company id, nullable if "opCode" is 0xCC****
     * binId: bin id
     * versionCode: bin version code
     * clearFlash: nullable, default value is false
     *
     * Response OpCode: {@link #OTA_NOUN_FB}. Device won't send response if it needn't upgrade
     */
    public static final int OTA_NBVN_FB = EspMessageOpCodes.OTA_NBVN_FB;

    /**
     * {
     *     "opCode":0xCD****,
     *     "srcAddress":1,
     *     "nodeAddress":1,
     *     "companyId":0x02E5,
     *     "binId":0x0001,
     *     "versionName":"5.2.4",
     *     "versionCode":0x0524
     * }
     *
     * opCode: **** is Company Id, for example, "companyId" is 0x02E5, than the "opCode" should be 0xCDE502
     * srcAddress: node address
     * nodeAddress: node address
     * companyId: company id
     * binId: device's self bin id
     * versionName: device's current bin version name
     * versionCode: device's current bin version code
     */
    public static final int OTA_NOUN_FB = EspMessageOpCodes.OTA_NOUN_FB;

    /**
     * {
     *     "opCode":0xCE,
     *     "dstAddress":1,
     *     "companyId":0x02E5,
     *     "binId":0x0001
     * }
     *
     * opCode: 0xCE or 0xCE****. **** is Company Id, for example, "companyId" is 0x02E5,
     *       than the "opCode" should be 0xCEE502
     * dstAddress: node address
     * companyId: company id, nullable if "opCode" is 0xCE****
     * binId: bin id
     *
     * Response OpCode: {@link #OTA_START_RESPONSE_FB}
     */
    public static final int OTA_START_FB = EspMessageOpCodes.OTA_START_FB;

    /**
     * {
     *     "opCode":0xCF****,
     *     "srcAddress":1,
     *     "nodeAddress":1,
     *     "companyId":0x02E5,
     *     "ssid":"softAP ssid",
     *     "password":"softAP password"
     * }
     *
     * opCode: **** is Company Id, for example, "companyId" is 0x02E5, than the "opCode" should be 0xCFE502
     * srcAddress: node address
     * nodeAddress: node address
     * companyId: company id
     * ssid: SSID of the SoftAP
     * password: Password of the SoftAP
     */
    public static final int OTA_START_RESPONSE_FB = EspMessageOpCodes.OTA_START_RESPONSE_FB;

    /**
     * {
     *     "opCode":0xD0E502,
     *     "dstAddress":1,
     *     "nodeAddress":1
     * }
     *
     * dstAddress: element address
     * nodeAddress: node address
     */
    public static final int CURRENT_VERSION_GET = EspMessageOpCodes.CURRENT_VERSION_GET;

    /**
     * {
     *     "opCode":0xD1E502,
     *     "srcAddress":1,
     *     "nodeAddress":1,
     *     "versionName":"5.2.4",
     *     "versionCode":0x0524
     * }
     *
     * srcAddress: element address
     * nodeAddress: node address
     */
    public static final int CURRENT_VERSION_STATUS = EspMessageOpCodes.CURRENT_VERSION_STATUS;

    /**
     * {
     *     "opCode":0x8201,
     *     "dstAddress":1,
     *     "nodeAddress":1,
     * }
     *
     * dstAddress: element address
     * nodeAddress: node address
     *
     * Response OpCode: {@link #GENERIC_ON_OFF_STATUS}
     */
    public static final int GENERIC_ON_OFF_GET = ApplicationMessageOpCodes.GENERIC_ON_OFF_GET;

    /**
     * {
     *     "opCode":0x8202,
     *     "dstAddress":1,
     *     "nodeAddress":1,
     *     "state":true
     * }
     *
     * dstAddress: element address or group address
     * nodeAddress: node address, nullable if dstAddress is group address
     * state: true is on, false if off
     *
     * Response OpCode: {@link #GENERIC_ON_OFF_STATUS}
     */
    public static final int GENERIC_ON_OFF_SET = ApplicationMessageOpCodes.GENERIC_ON_OFF_SET;

    /**
     * {
     *     "opCode":0x8203,
     *     "dstAddress":1,
     *     "nodeAddress":1,
     *     "state":true
     * }
     *
     * dstAddress: element address or group address
     * nodeAddress: node address, nullable if dstAddress is group address
     * state: true is on, false if off
     *
     * No Response
     */
    public static final int GENERIC_ON_OFF_SET_UNACKNOWLEDGED = ApplicationMessageOpCodes.GENERIC_ON_OFF_SET_UNACKNOWLEDGED;

    /**
     * {
     *     "opCode":0x8204,
     *     "srcAddress":1,
     *     "nodeAddress":1,
     *     "state":true
     * }
     *
     * srcAddress: element address
     * nodeAddress: node address
     * state: true is on, false if off
     */
    public static final int GENERIC_ON_OFF_STATUS = ApplicationMessageOpCodes.GENERIC_ON_OFF_STATUS;

    /**
     * {
     *     "opCode":0x826D,
     *     "dstAddress":1,
     *     "nodeAddress":1
     * }
     *
     * dstAddress: element address
     * nodeAddress: node address
     *
     * Response OpCode: {@link #LIGHT_HSL_STATUS}
     */
    public static final int LIGHT_HSL_GET = ApplicationMessageOpCodes.LIGHT_HSL_GET;

    /**
     * {
     *     "opCode":0x8276,
     *     "dstAddress":1,
     *     "nodeAddress":1,
     *     "hsl":[0.3, 0.4, 0.5]
     * }
     *
     * dstAddress: element address or group address
     * nodeAddress: node address, nullable if dstAddress is group address
     * hsl: the length must be three, hsl[0] is hue, hsl[1] is saturation, hsl[2] is lightness
     *
     * Response OpCode: {@link #LIGHT_HSL_STATUS}
     */
    public static final int LIGHT_HSL_SET = ApplicationMessageOpCodes.LIGHT_HSL_SET;

    /**
     * {
     *     "opCode":0x8277,
     *     "dstAddress":1,
     *     "nodeAddress":1,
     *     "hsl":[0.3, 0.4, 0.5]
     * }
     *
     * dstAddress: element address or group address
     * nodeAddress: node address, nullable if dstAddress is group address
     * hsl: the length must be three, hsl[0] is hue, hsl[1] is saturation, hsl[2] is lightness
     *
     * No Response
     */
    public static final int LIGHT_HSL_SET_UNACKNOWLEDGED = ApplicationMessageOpCodes.LIGHT_HSL_SET_UNACKNOWLEDGED;

    /**
     * {
     *     "opCode":0x8278,
     *     "srcAddress":1,
     *     "nodeAddress":1,
     *     "hsl":[0.3, 0.4, 0.5]
     * }
     *
     * srcAddress: element address
     * nodeAddress: node address
     * hsl: the length must be three, hsl[0] is hue, hsl[1] is saturation, hsl[2] is lightness
     */
    public static final int LIGHT_HSL_STATUS = ApplicationMessageOpCodes.LIGHT_HSL_STATUS;

    /**
     * {
     *     "opCode":0x824B,
     *     "dstAddress":1,
     *     "nodeAddress":1,
     * }
     *
     * dstAddress: element address or group address
     * nodeAddress: node address, nullable if dstAddress is group address
     *
     * Response OpCode: {@link #LIGHT_LIGHTNESS_STATUS}
     */
    public static final int LIGHT_LIGHTNESS_GET = ApplicationMessageOpCodes.LIGHT_LIGHTNESS_GET;

    /**
     * {
     *     "opCode":0x824C,
     *     "dstAddress":1,
     *     "nodeAddress":1,
     *     "lightness":0.5
     * }
     *
     * dstAddress: element address or group address
     * nodeAddress: node address, nullable if dstAddress is group address
     *
     * Response OpCode: {@link #LIGHT_LIGHTNESS_STATUS}
     */
    public static final int LIGHT_LIGHTNESS_SET = ApplicationMessageOpCodes.LIGHT_LIGHTNESS_SET;

    /**
     * {
     *     "opCode":0x824D,
     *     "dstAddress":1,
     *     "nodeAddress":1,
     *     "lightness":0.5
     * }
     *
     * dstAddress: element address or group address
     * nodeAddress: node address, nullable if dstAddress is group address
     *
     * No Response
     */
    public static final int LIGHT_LIGHTNESS_SET_UNACKNOWLEDGED = ApplicationMessageOpCodes.LIGHT_LIGHTNESS_SET_UNACKNOWLEDGED;

    /**
     * {
     *     "opCode":0x824C,
     *     "dstAddress":1,
     *     "nodeAddress":1,
     *     "lightness":0.5
     * }
     *
     * dstAddress: element address or group address
     * nodeAddress: node address, nullable if dstAddress is group address
     */
    public static final int LIGHT_LIGHTNESS_STATUS = ApplicationMessageOpCodes.LIGHT_LIGHTNESS_STATUS;

    /**
     * {
     *     "opCode":0x825D,
     *     "dstAddress":1,
     *     "nodeAddress":1
     * }
     *
     * dstAddress: element address
     * nodeAddress: node address
     *
     * Response OpCode: {@link #LIGHT_CTL_STATUS}
     */
    public static final int LIGHT_CTL_GET = ApplicationMessageOpCodes.LIGHT_CTL_GET;

    /**
     * {
     *     "opCode":0x825E,
     *     "dstAddress":1,
     *     "nodeAddress":1,
     *     "ctl":[0, 2000, 0.5]
     * }
     *
     * dstAddress: element address or group address
     * nodeAddress: node address, nullable if dstAddress is group address
     * ctl: the length must be three, hsl[0] is deltaUV, hsl[1] is temperature, hsl[2] is lightness
     *
     * Response OpCode: {@link #LIGHT_CTL_STATUS}
     */
    public static final int LIGHT_CTL_SET = ApplicationMessageOpCodes.LIGHT_CTL_SET;

    /**
     * {
     *     "opCode":0x825E,
     *     "dstAddress":1,
     *     "nodeAddress":1,
     *     "ctl":[0, 2000, 0.5]
     * }
     *
     * dstAddress: element address or group address
     * nodeAddress: node address, nullable if dstAddress is group address
     * ctl: the length must be three, hsl[0] is deltaUV, hsl[1] is temperature, hsl[2] is lightness
     *
     * No Response
     */
    public static final int LIGHT_CTL_SET_UNACKNOWLEDGED = ApplicationMessageOpCodes.LIGHT_CTL_SET_UNACKNOWLEDGED;

    /**
     * {
     *     "opCode":0x8278,
     *     "srcAddress":1,
     *     "nodeAddress":1,
     *     "ctl":[0.3, 0.4, 0.5]
     * }
     *
     * srcAddress: element address
     * nodeAddress: node address
     * ctl: the length must be three, hsl[0] is deltaUV, hsl[1] is temperature, hsl[2] is lightness
     */
    public static final int LIGHT_CTL_STATUS = ApplicationMessageOpCodes.LIGHT_CTL_STATUS;
}
