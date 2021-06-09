package no.nordicsemi.android.mesh.transport;

import no.nordicsemi.android.mesh.opcodes.ProxyConfigMessageOpCodes;
import no.nordicsemi.android.mesh.utils.ProxyFilterType;

/**
 * To be used as a wrapper class to create the ProxyConfigSetFilterType message.
 */
@SuppressWarnings({"unused", "WeakerAccess"})
public class ProxyConfigSetFilterType extends ProxyConfigMessage {

    private final ProxyFilterType filterType;

    /**
     * Sets the proxy filter
     *
     * @param filterType Filter type set by the proxy configuration
     */
    public ProxyConfigSetFilterType(final ProxyFilterType filterType) {
        this.filterType = filterType;
        assembleMessageParameters();
    }

    @Override
    void assembleMessageParameters() {
        mParameters = new byte[]{(byte) filterType.getType()};
    }

    @Override
    int getOpCode() {
        return ProxyConfigMessageOpCodes.SET_FILTER_TYPE;
    }

    @Override
    byte[] getParameters() {
        return mParameters;
    }
}
