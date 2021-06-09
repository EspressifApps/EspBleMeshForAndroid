package no.nordicsemi.android.mesh.transport;

import androidx.annotation.NonNull;

import no.nordicsemi.android.mesh.ApplicationKey;

public abstract class EspOtaMessage extends EspGenericMessage {

    EspOtaMessage(@NonNull ApplicationKey appKey) {
        super(appKey);
    }
}
