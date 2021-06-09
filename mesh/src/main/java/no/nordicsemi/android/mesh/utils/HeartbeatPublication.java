package no.nordicsemi.android.mesh.utils;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import androidx.annotation.NonNull;
import no.nordicsemi.android.mesh.Features;

import static no.nordicsemi.android.mesh.utils.MeshAddress.UNASSIGNED_ADDRESS;

/**
 * Class containing the Heartbeat subscription configuration.
 */
public class HeartbeatPublication extends Heartbeat implements Parcelable {

    @Expose
    @SerializedName("ttl")
    private final int ttl;
    @Expose
    @SerializedName("features")
    private final Features features;
    @Expose
    @SerializedName("index")
    private final int netKeyIndex;

    /**
     * Heartbeat subscription.
     *
     * @param dst         Destination address identifies the Heartbeat Publication
     *                    destination where the address can only be an unassigned address,
     *                    unicast address or a group address. All other values are prohibited.
     * @param countLog    Number of Heartbeat messages to be sent.
     * @param periodLog   Period for sending Heartbeat messages.
     * @param ttl         TTL to be used when sending Heartbeat messages.
     * @param features    Bit field indicating features that trigger Heartbeat messages when changed.
     * @param netKeyIndex Net key index.
     */
    public HeartbeatPublication(final int dst,
                                final byte countLog,
                                final byte periodLog,
                                final int ttl,
                                final Features features,
                                final int netKeyIndex) {
        super(dst, periodLog, countLog);
        this.ttl = ttl;
        this.features = features;
        this.netKeyIndex = netKeyIndex;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(dst);
        dest.writeByte(countLog);
        dest.writeByte(periodLog);
        dest.writeInt(ttl);
        dest.writeParcelable(features, flags);
        dest.writeInt(netKeyIndex);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<HeartbeatPublication> CREATOR = new Creator<HeartbeatPublication>() {
        @Override
        public HeartbeatPublication createFromParcel(Parcel in) {
            return new HeartbeatPublication(in.readInt(),
                    in.readByte(),
                    in.readByte(),
                    in.readInt(),
                    in.readParcelable(Features.class.getClassLoader()),
                    in.readInt());
        }

        @Override
        public HeartbeatPublication[] newArray(int size) {
            return new HeartbeatPublication[size];
        }
    };

    @NonNull
    @Override
    public String toString() {
        return "Destination address: " + Integer.toHexString(dst) +
                "\nCount Log: " + Integer.toHexString(countLog) +
                "\nPeriod Log: " + Integer.toHexString(periodLog) +
                "\nTTL: " + ttl +
                "\nFeatures: " + features.toString() +
                "\nNet key index: " + Integer.toHexString(netKeyIndex);
    }

    /**
     * Returns the destination address of the Heartbeat publications.
     */
    public int getDstAddress() {
        return dst;
    }

    /**
     * Returns the publication ttl.
     */
    public int getTtl() {
        return ttl;
    }

    /**
     * Returns the features.
     */
    public Features getFeatures() {
        return features;
    }

    /**
     * Returns the Net key index.
     */
    public int getNetKeyIndex() {
        return netKeyIndex;
    }

    /**
     * Returns true if the heartbeat subscriptions are enabled.
     */
    public boolean isEnabled() {
        return dst != UNASSIGNED_ADDRESS;
    }
}
