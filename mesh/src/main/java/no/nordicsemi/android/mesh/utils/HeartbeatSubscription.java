package no.nordicsemi.android.mesh.utils;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import androidx.annotation.NonNull;

import static no.nordicsemi.android.mesh.utils.MeshAddress.UNASSIGNED_ADDRESS;

/**
 * Class containing the Heartbeat subscription configuration.
 */
@SuppressWarnings("FieldMayBeFinal")
public class HeartbeatSubscription extends Heartbeat implements Parcelable {


    @Expose
    @SerializedName("source")
    private final int src;
    @Expose
    @SerializedName("minHops")
    private int minHops;
    @Expose
    @SerializedName("maxHops")
    private int maxHops;

    /**
     * Heartbeat subscription.
     *
     * @param src       Source address for Heartbeat messages.
     * @param dst       Destination address for Heartbeat messages.
     * @param periodLog Remaining period for processing Heartbeat messages.
     * @param countLog  Number of Heartbeat messages received.
     * @param minHops   Minimum hops when receiving Heartbeat messages.
     * @param maxHops   Maximum hops when receiving Heartbeat messages.
     */
    public HeartbeatSubscription(final int src, final int dst, final byte periodLog, final byte countLog, final int minHops, final int maxHops) {
        super(dst, periodLog, countLog);
        this.src = src;
        this.minHops = minHops;
        this.maxHops = maxHops;
    }

    public static final Creator<HeartbeatSubscription> CREATOR = new Creator<HeartbeatSubscription>() {
        @Override
        public HeartbeatSubscription createFromParcel(Parcel in) {
            return new HeartbeatSubscription(in.readInt(),
                    in.readInt(),
                    in.readByte(),
                    in.readByte(),
                    in.readInt(),
                    in.readInt());
        }

        @Override
        public HeartbeatSubscription[] newArray(int size) {
            return new HeartbeatSubscription[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(final Parcel dest, final int flags) {
        dest.writeInt(src);
        dest.writeInt(dst);
        dest.writeByte(periodLog);
        dest.writeByte(countLog);
        dest.writeInt(minHops);
        dest.writeInt(maxHops);
    }

    @NonNull
    @Override
    public String toString() {
        return "Source address: " + Integer.toHexString(src) +
                "\nDestination address: " + Integer.toHexString(dst) +
                "\nPeriod Log: " + Integer.toHexString(periodLog) +
                "\nCount Log: " + Integer.toHexString(countLog) +
                "\nMin Hops: " + minHops +
                "\n Max Hops: " + maxHops;
    }

    /**
     * Returns the source address.
     */
    public int getSrc() {
        return src;
    }

    /**
     * Returns the minimum number of hopes when receiving heartbeat messages.
     */
    public int getMinHops() {
        return minHops;
    }

    /**
     * Returns the maximum number of hopes when receiving heartbeat messages.
     */
    public int getMaxHops() {
        return maxHops;
    }

    /**
     * Returns true if the heartbeat subscriptions are enabled.
     */
    public boolean isEnabled() {
        return src != UNASSIGNED_ADDRESS && dst != UNASSIGNED_ADDRESS && periodLog != DO_NOT_SEND_PERIODICALLY;
    }
}
