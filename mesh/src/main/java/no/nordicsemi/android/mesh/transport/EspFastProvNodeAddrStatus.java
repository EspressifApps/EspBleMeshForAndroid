package no.nordicsemi.android.mesh.transport;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import no.nordicsemi.android.mesh.opcodes.EspMessageOpCodes;

public class EspFastProvNodeAddrStatus extends EspGenericStatusMessage implements Parcelable {
    private static final int OP_CODE = EspMessageOpCodes.FAST_PROV_NODE_ADDR_STATUS;

    private long[] mAddrArray;

    EspFastProvNodeAddrStatus(@NonNull AccessMessage message) {
        super(message);

        parseStatusParameters();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        final AccessMessage message = (AccessMessage) mMessage;
        dest.writeParcelable(message, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<EspFastProvNodeAddrStatus> CREATOR = new Creator<EspFastProvNodeAddrStatus>() {
        @Override
        public EspFastProvNodeAddrStatus createFromParcel(Parcel in) {
            final AccessMessage message = in.readParcelable(AccessMessage.class.getClassLoader());
            return new EspFastProvNodeAddrStatus(message);
        }

        @Override
        public EspFastProvNodeAddrStatus[] newArray(int size) {
            return new EspFastProvNodeAddrStatus[size];
        }
    };

    @Override
    void parseStatusParameters() {
        final int addrCount = mParameters.length / 2;
        mAddrArray = new long[addrCount];
        for (int i = 0; i < addrCount; ++i) {
            int offset = i * 2;
            mAddrArray[i] = (mParameters[offset] & 0xff) | ((mParameters[offset + 1] & 0xff) << 8);
        }
    }

    @Override
    int getOpCode() {
        return OP_CODE;
    }

    @NonNull
    public long[] getAddrArray() {
        return mAddrArray;
    }
}
