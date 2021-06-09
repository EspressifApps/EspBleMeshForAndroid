package no.nordicsemi.android.mesh.transport;

import androidx.annotation.NonNull;

import java.util.Arrays;

import no.nordicsemi.android.mesh.opcodes.EspMessageOpCodes;

public class EspOtaStartResponse extends EspGenericStatusMessage {
    private final int mOpCode;
    private int mCompanyId;

    EspOtaStartResponse(@NonNull AccessMessage message, int opCode) {
        super(message);

        mOpCode = opCode;
        parseStatusParameters();
    }

    @Override
    void parseStatusParameters() {
        mCompanyId = ((getOpCode() & 0xff) << 8) | (getOpCode() >> 8 & 0xff);
    }

    @Override
    int getOpCode() {
        return mOpCode;
    }

    public int getCompanyId() {
        return mCompanyId;
    }
}
