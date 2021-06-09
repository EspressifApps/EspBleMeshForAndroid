package no.nordicsemi.android.mesh.transport;

import androidx.annotation.NonNull;

abstract class EspGenericStatusMessage extends GenericStatusMessage {
    EspGenericStatusMessage(@NonNull AccessMessage message) {
        super(message);

        mParameters = message.getParameters();
    }
}
