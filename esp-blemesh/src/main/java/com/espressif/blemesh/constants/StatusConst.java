package com.espressif.blemesh.constants;

public class StatusConst {
    public static final int STATUS_SUCCESS = 2000;
    public static final int STATUS_FAILED = 3000;

    public static final int STATUS_GATT_CONNECTED = 4000;
    public static final int STATUS_GATT_DISCONNECTED = 4001;
    public static final int STATUS_GATT_EXISTS = 4002;
    public static final int STATUS_DEVICE_NOT_FOUND = 4003;

    public static final int STATUS_PROVISIONING_READY = 5000;
    public static final int STATUS_PROVISIONING_COMPLETE = 5099;

    public static final int STATUS_PROXY_READY = 6000;
    public static final int STATUS_PROXY_READY_NO_DST = 6001;
    public static final int STATUS_PROXY_ADDRESS_NOT_FOUNT = 6600;
}
