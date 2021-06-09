package no.nordicsemi.android.mesh.transport;

import androidx.annotation.NonNull;

import no.nordicsemi.android.mesh.ApplicationKey;
import no.nordicsemi.android.mesh.espressif.EspMeshConstants;
import no.nordicsemi.android.mesh.opcodes.EspMessageOpCodes;

public class EspOtaNewBinVersionNotification extends EspOtaMessage {
    private int mCompanyId;
    private int mBinId;
    private int mVersion;
    private boolean mClearFlash;
    private boolean mUrlEnable;

    public EspOtaNewBinVersionNotification(@NonNull ApplicationKey appKey, int companyId, int binId, int version, boolean clearFlash, boolean urlEnable) {
        super(appKey);

        mCompanyId = companyId;
        mBinId = binId;
        mVersion = version;
        mClearFlash = clearFlash;
        mUrlEnable = urlEnable;

        assembleMessageParameters();
    }

    private byte getFlag() {
        return (byte) (
                (mClearFlash ? 1 : 0) |
                        (mUrlEnable ? 0b10 : 0)
        );
    }

    @Override
    void assembleMessageParameters() {
        mParameters = new byte[]{
                (byte) (mBinId & 0xff),
                (byte) (mBinId >> 8 & 0xff),
                (byte) (mVersion & 0xff),
                (byte) (mVersion >> 8 & 0xff),
                getFlag(),
                (byte) (EspMeshConstants.ADDRESS_OTA_GROUP & 0xff),
                (byte) ((EspMeshConstants.ADDRESS_OTA_GROUP >> 8) & 0xff)
        };
    }

    @Override
    int getOpCode() {
        return (EspMessageOpCodes.OTA_NBVN_FB << 16) |
                ((mCompanyId & 0xff) << 8) |
                ((mCompanyId >> 8) & 0xff);
    }
}
