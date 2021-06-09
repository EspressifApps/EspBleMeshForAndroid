package no.nordicsemi.android.mesh.transport;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import no.nordicsemi.android.mesh.opcodes.EspMessageOpCodes;

public class EspCurrentVersionStatus extends EspGenericStatusMessage implements Parcelable {
    private int mVersion;
    private int mBinId;

    EspCurrentVersionStatus(@NonNull AccessMessage message) {
        super(message);

        parseStatusParameters();
    }

    public static final Creator<EspCurrentVersionStatus> CREATOR = new Creator<EspCurrentVersionStatus>() {
        @Override
        public EspCurrentVersionStatus createFromParcel(Parcel in) {
            final AccessMessage message = in.readParcelable(AccessMessage.class.getClassLoader());
            return new EspCurrentVersionStatus(message);
        }

        @Override
        public EspCurrentVersionStatus[] newArray(int size) {
            return new EspCurrentVersionStatus[size];
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
        mBinId = (mParameters[0] & 0xff) | ((mParameters[1] & 0xff) << 8);
        mVersion = (mParameters[2] & 0xff) | ((mParameters[3] & 0xff) << 8);
    }

    @Override
    int getOpCode() {
        return EspMessageOpCodes.CURRENT_VERSION_STATUS;
    }

    public int getVersion() {
        return mVersion;
    }

    public int getBinId() {
        return mBinId;
    }
}
