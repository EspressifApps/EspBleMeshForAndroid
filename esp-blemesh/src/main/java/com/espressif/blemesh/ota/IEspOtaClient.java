package com.espressif.blemesh.ota;

import java.io.Closeable;

public interface IEspOtaClient extends Closeable {
    int STATUS_INVALID_STATE = 9000;
    int STATUS_BIN_NOT_FOUND = 9001;
    int STATUS_CONNECT_FAILED = 9002;
    int STATUS_AES_IV_ERROR = 9100;
    int STATUS_AES_KEY_ERROR = 9101;
    int STATUS_BIN_INFO_ERROR = 9200;
    int STATUS_BIN_ACK_ERROR = 9300;
    int STATUS_BIN_ACK_INVALID = 9301;
    int STATUS_BIN_INCOMPLETE = 9400;
    int STATUS_EXCEPTION = 9900;

    boolean ota();
}
