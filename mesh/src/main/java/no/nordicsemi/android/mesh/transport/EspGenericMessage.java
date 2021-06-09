package no.nordicsemi.android.mesh.transport;

import androidx.annotation.NonNull;

import no.nordicsemi.android.mesh.ApplicationKey;
import no.nordicsemi.android.mesh.utils.SecureUtils;

abstract class EspGenericMessage extends GenericMessage {
    /**
     * Constructs a generic message
     *
     * @param appKey application key
     */
    EspGenericMessage(@NonNull ApplicationKey appKey) {
        super(appKey);

        mAid = SecureUtils.calculateK4(appKey.getKey());
    }
}
