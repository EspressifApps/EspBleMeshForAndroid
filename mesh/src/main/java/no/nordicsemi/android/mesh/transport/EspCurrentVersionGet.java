package no.nordicsemi.android.mesh.transport;

import no.nordicsemi.android.mesh.ApplicationKey;
import no.nordicsemi.android.mesh.opcodes.EspMessageOpCodes;

public class EspCurrentVersionGet extends EspGenericMessage {
    private static final int OP_CODE = EspMessageOpCodes.CURRENT_VERSION_GET;

    public EspCurrentVersionGet(ApplicationKey appKey) {
        super(appKey);

        assembleMessageParameters();
    }

    @Override
    void assembleMessageParameters() {
    }

    @Override
    int getOpCode() {
        return OP_CODE;
    }
}
