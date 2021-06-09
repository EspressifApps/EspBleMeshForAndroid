package no.nordicsemi.android.mesh.transport;

import android.util.Log;

import java.util.UUID;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import no.nordicsemi.android.mesh.ApplicationKey;

/**
 * State class for handling VendorModelMessageAckedState messages.
 */
@SuppressWarnings({"unused"})
class VendorModelMessageAckedState extends GenericMessageState {

    private static final String TAG = VendorModelMessageAckedState.class.getSimpleName();

    /**
     * Constructs {@link VendorModelMessageAckedState}
     *
     * @param src                     Source address
     * @param dst                     Destination address to which the message must be sent to
     * @param vendorModelMessageAcked Wrapper class {@link VendorModelMessageStatus} containing the
     *                                opcode and parameters for {@link VendorModelMessageStatus} message
     * @param callbacks               {@link InternalMeshMsgHandlerCallbacks} for internal callbacks
     * @throws IllegalArgumentException exception for invalid arguments
     */
    VendorModelMessageAckedState(final int src,
                                 final int dst,
                                 @NonNull final VendorModelMessageAcked vendorModelMessageAcked,
                                 @NonNull final MeshTransport meshTransport,
                                 @NonNull final InternalMeshMsgHandlerCallbacks callbacks) throws IllegalArgumentException {
        this(src, dst, null, vendorModelMessageAcked, meshTransport, callbacks);
    }

    /**
     * Constructs {@link VendorModelMessageAckedState}
     *
     * @param src                     Source address
     * @param dst                     Destination address to which the message must be sent to
     * @param label                   Label UUID of destination address
     * @param vendorModelMessageAcked Wrapper class {@link VendorModelMessageStatus} containing the
     *                                opcode and parameters for {@link VendorModelMessageStatus} message
     * @param callbacks               {@link InternalMeshMsgHandlerCallbacks} for internal callbacks
     * @throws IllegalArgumentException exception for invalid arguments
     */
    VendorModelMessageAckedState(final int src,
                                 final int dst,
                                 @Nullable UUID label,
                                 @NonNull final VendorModelMessageAcked vendorModelMessageAcked,
                                 @NonNull final MeshTransport meshTransport,
                                 @NonNull final InternalMeshMsgHandlerCallbacks callbacks) throws IllegalArgumentException {
        super(src, dst, label, vendorModelMessageAcked, meshTransport, callbacks);
    }

    @Override
    public MessageState getState() {
        return MessageState.VENDOR_MODEL_ACKNOWLEDGED_STATE;
    }

    @Override
    protected final void createAccessMessage() {
        final VendorModelMessageAcked message = (VendorModelMessageAcked) mMeshMessage;
        final ApplicationKey key = message.getAppKey();
        final int akf = message.getAkf();
        final int aid = message.getAid();
        final int aszmic = message.getAszmic();
        final int opCode = message.getOpCode();
        final byte[] parameters = message.getParameters();
        final int companyIdentifier = message.getCompanyIdentifier();
        this.message = mMeshTransport.createVendorMeshMessage(companyIdentifier, mSrc, mDst, mLabel,
                message.messageTtl, key, akf, aid, aszmic, opCode, parameters);
        message.setMessage(this.message);
    }

    @Override
    public void executeSend() {
        Log.v(TAG, "Sending acknowledged vendor model message");
        super.executeSend();
    }
}
