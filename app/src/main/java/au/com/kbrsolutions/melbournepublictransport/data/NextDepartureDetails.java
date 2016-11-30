package au.com.kbrsolutions.melbournepublictransport.data;

import android.os.Parcel;
import android.os.Parcelable;
/**
 * This class keeps 'next departures' details.
 */
public class NextDepartureDetails implements Parcelable {
    public final int directionId;
    public final int routeType;
    public final String directionName;
    public final int runId;
    public final int numSkipped;
    public final String runType;
    public final int destinationId;
    public final String utcDepartureTime;

    public NextDepartureDetails(Parcel input) {
        directionId = input.readInt();
        routeType = input.readInt();
        directionName = input.readString();
        runId = input.readInt();
        numSkipped = input.readInt();
        runType = input.readString();
        destinationId = input.readInt();
        utcDepartureTime = input.readString();
    }

    public NextDepartureDetails(
            int directionId,
            int routeType,
            String directionName,
            int runId,
            int numSkipped,
            String runType,
            int destinationId,
            String utcDepartureTime) {
        this.directionId = directionId;
        this.routeType = routeType;
        this.directionName = directionName;
        this.runId = runId;
        this.numSkipped = numSkipped;
        this.runType = runType;
        this.destinationId = destinationId;
        this.utcDepartureTime = utcDepartureTime;
    }

    @Override
    public String toString() {
        return
            "directionId: " + directionId +
            ": routeType: " + routeType +
            "; directionName: " + directionName +
            "; runId: " + runId +
            "; numSkipped: " + numSkipped +
            "; directionName: " + destinationId +
            "; utcDepartureTime: " + utcDepartureTime;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(directionId);
        dest.writeInt(routeType);
        dest.writeString(directionName);
        dest.writeInt(runId);
        dest.writeInt(numSkipped);
        dest.writeString(runType);
        dest.writeInt(destinationId);
        dest.writeString(utcDepartureTime);
    }

    public static final Parcelable.Creator<NextDepartureDetails> CREATOR =
            new Parcelable.Creator<NextDepartureDetails>() {
                public NextDepartureDetails createFromParcel(Parcel in) {
                    return new NextDepartureDetails(in);
                }

                public NextDepartureDetails[] newArray(int size) {
                    return new NextDepartureDetails[size];
                }
            };
}
