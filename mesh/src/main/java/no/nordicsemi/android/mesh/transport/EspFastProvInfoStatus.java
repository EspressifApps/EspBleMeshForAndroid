package no.nordicsemi.android.mesh.transport;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import no.nordicsemi.android.mesh.opcodes.EspMessageOpCodes;

public class EspFastProvInfoStatus extends EspGenericStatusMessage implements Parcelable {

    EspFastProvInfoStatus(@NonNull AccessMessage message) {
        super(message);

        parseStatusParameters();
    }

    public static final Creator<EspFastProvInfoStatus> CREATOR = new Creator<EspFastProvInfoStatus>() {
        @Override
        public EspFastProvInfoStatus createFromParcel(Parcel in) {
            final AccessMessage message = in.readParcelable(AccessMessage.class.getClassLoader());
            //noinspection ConstantConditions
            return new EspFastProvInfoStatus(message);
        }

        @Override
        public EspFastProvInfoStatus[] newArray(int size) {
            return new EspFastProvInfoStatus[size];
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
    }

    @Override
    int getOpCode() {
        return EspMessageOpCodes.FAST_PROV_INFO_STATUS;
    }
}
