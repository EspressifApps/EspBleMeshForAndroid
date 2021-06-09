package com.espressif.blemesh.ota;

import no.nordicsemi.android.mesh.espressif.EspOtaUtils;

class EspOtaPacket {
    int packetId;
    int flag;
    int companyId;
    byte[] data;
    int checksum;

    boolean isChecksumValid(int checksum) {
        if (EspOtaUtils.isChecksum(flag)) {
            return true;
        } else {
            return this.checksum == checksum;
        }
    }
}