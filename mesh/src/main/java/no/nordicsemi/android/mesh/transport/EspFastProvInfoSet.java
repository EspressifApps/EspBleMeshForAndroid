package no.nordicsemi.android.mesh.transport;

import androidx.annotation.NonNull;

import java.util.Arrays;

import no.nordicsemi.android.mesh.ApplicationKey;
import no.nordicsemi.android.mesh.espressif.EspDataUtils;
import no.nordicsemi.android.mesh.espressif.EspMeshUtils;
import no.nordicsemi.android.mesh.opcodes.EspMessageOpCodes;

public class EspFastProvInfoSet extends EspGenericMessage {
    private static final int OP_CODE = EspMessageOpCodes.FAST_PROV_INFO_SET;

    private Integer mProvCount;
    private Integer mUnicastAddressMin;
    private Integer mUnicastAddressMax;
    private Integer mFlags;
    private Integer mIvIndex;
    private Integer mNetKeyIndex;
    private Integer mGroupAddress;
    private Integer mPrimaryProvisionerAddress;
    private byte[] mMatchValue;
    private Integer mAction;

    /**
     * Constructs a generic message
     *
     * @param appKey application key
     */
    public EspFastProvInfoSet(@NonNull ApplicationKey appKey, Integer provCount, Integer unicastAddressMin, Integer unicastAddressMax,
                              Integer flags, Integer ivIndex, Integer netKeyIndex, Integer groupAddress, Integer primaryProvisionerAddress,
                              byte[] matchValue, Integer action) {
        super(appKey);

        mProvCount = provCount;
        mUnicastAddressMin = unicastAddressMin;
        mUnicastAddressMax = unicastAddressMax;
        mFlags = flags;
        mIvIndex = ivIndex;
        mNetKeyIndex = netKeyIndex;
        mGroupAddress = groupAddress;
        mPrimaryProvisionerAddress = primaryProvisionerAddress;
        mMatchValue = matchValue;
        mAction = action;
        assembleMessageParameters();
    }

    @Override
    void assembleMessageParameters() {
        int context0 = 0;
        int context1 = 0;
        byte[] parameters = new byte[0];
        if (mProvCount != null && mProvCount >= 0) {
            context0 = 1;
            parameters = new byte[]{(byte) (mProvCount & 0xff), (byte) ((mProvCount >> 8) & 0xff)};
        }
        if (mUnicastAddressMin != null) {
            context0 = context0 | (1 << 1);
            byte[] unicastAddrMinBytes = EspMeshUtils.long2LittleEndianBytes(mUnicastAddressMin, 2);
            parameters = EspDataUtils.mergeBytes(parameters, unicastAddrMinBytes);
        }
        if (mUnicastAddressMax != null) {
            context0 = context0 | (1 << 2);
            byte[] unicastAddrMaxBytes = EspMeshUtils.long2LittleEndianBytes(mUnicastAddressMax, 2);
            parameters = EspDataUtils.mergeBytes(parameters, unicastAddrMaxBytes);
        }
        if (mFlags != null) {
            context0 = context0 | (1 << 3);
            byte[] flagBytes = {(byte) (mFlags & 0xff)};
            parameters = EspDataUtils.mergeBytes(parameters, flagBytes);
        }
        if (mIvIndex != null) {
            context0 = context0 | (1 << 4);
            byte[] ivIndexBytes = EspMeshUtils.long2LittleEndianBytes(mIvIndex, 4);
            parameters = EspDataUtils.mergeBytes(parameters, ivIndexBytes);
        }
        if (mNetKeyIndex != null) {
            context0 = context0 | (1 << 5);
            byte[] netKeyIndexBytes = EspMeshUtils.long2LittleEndianBytes(mNetKeyIndex, 2);
            parameters = EspDataUtils.mergeBytes(parameters, netKeyIndexBytes);
        }
        if (mGroupAddress != null) {
            context0 = context0 | (1 << 6);
            byte[] groupAddrBytes = EspMeshUtils.long2LittleEndianBytes(mGroupAddress, 2);
            parameters = EspDataUtils.mergeBytes(parameters, groupAddrBytes);
        }
        if (mPrimaryProvisionerAddress != null) {
            context0 = context0 | (1 << 7);
            byte[] primaryProvisionerAddrBytes = EspMeshUtils.long2LittleEndianBytes(mPrimaryProvisionerAddress, 2);
            parameters = EspDataUtils.mergeBytes(parameters, primaryProvisionerAddrBytes);
        }
        if (mMatchValue != null && mMatchValue.length > 0) {
            context1 = 1;
            byte[] matchValueBytes = Arrays.copyOf(mMatchValue, mMatchValue.length); // DataUtil.reverseBytes(mMatchValue);
            System.out.println("DEVICE UUID = " + EspDataUtils.bigEndianBytesToHexString(matchValueBytes));
            parameters = EspDataUtils.mergeBytes(parameters, matchValueBytes);
        }
        if (mAction != null) {
            context1 = context1 | (1 << 1);
            byte[] actionBytes = {(byte) (int) mAction};
            parameters = EspDataUtils.mergeBytes(parameters, actionBytes);
        }

        byte[] context = {(byte) context0, (byte) context1};

        mParameters = EspDataUtils.mergeBytes(context, parameters);
    }

    @Override
    int getOpCode() {
        return OP_CODE;
    }
}
