package no.nordicsemi.android.mesh.transport;

import androidx.annotation.NonNull;

import no.nordicsemi.android.mesh.ApplicationKey;
import no.nordicsemi.android.mesh.opcodes.EspMessageOpCodes;

public class EspFastProvNodeAddrGet extends EspGenericMessage {
    private static final int OP_CODE = EspMessageOpCodes.FAST_PROV_NODE_ADDR_GET;

    /**
     * Constructs a generic message
     *
     * @param appKey application key
     */
    public EspFastProvNodeAddrGet(@NonNull ApplicationKey appKey) {
        super(appKey);

        assembleMessageParameters();
    }

    @Override
    void assembleMessageParameters() {
        mParameters = new byte[0];
    }

    @Override
    int getOpCode() {
        return OP_CODE;
    }
}
