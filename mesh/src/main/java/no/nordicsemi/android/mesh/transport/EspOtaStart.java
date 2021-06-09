package no.nordicsemi.android.mesh.transport;

import androidx.annotation.NonNull;

import no.nordicsemi.android.mesh.ApplicationKey;
import no.nordicsemi.android.mesh.espressif.EspDataUtils;
import no.nordicsemi.android.mesh.opcodes.EspMessageOpCodes;

public class EspOtaStart extends EspOtaMessage {
    private int mCompanyId;
    private byte[] mSoftApSSID;
    private byte[] mSoftApPassword;
    private byte[] mUrl;
    private byte[] mUrlSSID;
    private byte[] mUrlPassword;

    public EspOtaStart(@NonNull ApplicationKey appKey, int companyId, byte[] softApSSID, byte[] softApPassword,
                byte[] url, byte[] urlSSID, byte[] urlPassword) {
        super(appKey);

        mCompanyId = companyId;
        mSoftApSSID = softApSSID;
        mSoftApPassword = softApPassword;
        mUrl = url;
        mUrlSSID = urlSSID;
        mUrlPassword = urlPassword;

        assembleMessageParameters();
    }

    @Override
    void assembleMessageParameters() {
        mParameters = EspDataUtils.mergeBytes(
                new byte[]{0x00}, // App is station. SoftAP is 0x01
                mSoftApSSID,
                mSoftApPassword,
                mUrl,
                mUrlSSID,
                mUrlPassword
        );
    }

    @Override
    int getOpCode() {
        return (EspMessageOpCodes.OTA_START_FB << 16) |
                ((mCompanyId & 0xff) << 8) |
                ((mCompanyId >> 8) & 0xff);
    }
}
