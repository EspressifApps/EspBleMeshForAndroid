package no.nordicsemi.android.mesh.transport;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import no.nordicsemi.android.mesh.opcodes.EspMessageOpCodes;

public class EspOtaNeedUpdateNotification extends EspGenericStatusMessage implements Parcelable {
    private int mCompanyId;
    private int mBinId;
    private int mVersion;

    EspOtaNeedUpdateNotification(@NonNull AccessMessage message) {
        super(message);

        parseStatusParameters();
    }

    public static final Creator<EspOtaNeedUpdateNotification> CREATOR = new Creator<EspOtaNeedUpdateNotification>() {
        @Override
        public EspOtaNeedUpdateNotification createFromParcel(Parcel in) {
            final AccessMessage message = in.readParcelable(AccessMessage.class.getClassLoader());
            return new EspOtaNeedUpdateNotification(message);
        }

        @Override
        public EspOtaNeedUpdateNotification[] newArray(int size) {
            return new EspOtaNeedUpdateNotification[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        final AccessMessage message = (AccessMessage) mMessage;
        dest.writeParcelable(message, flags);
    }

    @Override
    void parseStatusParameters() {
        mCompanyId = ((getOpCode() & 0xff) << 8) | (getOpCode() >> 8 & 0xff);
        mBinId = (mParameters[0] & 0xff) & ((mParameters[1] & 0xff) << 8);
        mVersion = ((mParameters[2] & 0xff) << 8) | (mParameters[3] & 0xff);
    }

    @Override
    int getOpCode() {
        return ((EspMessageOpCodes.OTA_NOUN_FB << 16) |
                (mParameters[1] & 0xff) |
                ((mParameters[2] & 0xff) << 8));
    }

    public int getCompanyId() {
        return mCompanyId;
    }

    public int getBinId() {
        return mBinId;
    }

    public int getVersion() {
        return mVersion;
    }
}
