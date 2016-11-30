package au.com.kbrsolutions.melbournepublictransport.data;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * This class keeps 'nearby trains' details.
 */
public class NearbyTrainsDetails implements Parcelable {

    public final String stopId;
    public final String stopName;
    public final double latitude;
    public final double longitude;
    public final double distanceMeters;

    public NearbyTrainsDetails(String stopId,
                        String stopName,
                        double latitude,
                        double longitude,
                        double distanceMeters) {

        this.stopId = stopId;
        this.stopName = stopName;
        this.latitude = latitude;
        this.longitude = longitude;
        this.distanceMeters = distanceMeters;
    }

    public NearbyTrainsDetails(Parcel input) {
        stopId = input.readString();
        stopName = input.readString();
        latitude = input.readDouble();
        longitude = input.readDouble();
        distanceMeters = input.readDouble();
    }

    @Override
    public String toString() {
        return "stopId: " + stopId +
                "; stopName: " + stopName +
                "; latitude: " + latitude +
                "; longitude: " + longitude;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(stopId);
        dest.writeString(stopName);
        dest.writeDouble(latitude);
        dest.writeDouble(longitude);
        dest.writeDouble(distanceMeters);
    }

    public static final Parcelable.Creator<NearbyTrainsDetails> CREATOR =
            new Parcelable.Creator<NearbyTrainsDetails>() {
                public NearbyTrainsDetails createFromParcel(Parcel in) {
                    return new NearbyTrainsDetails(in);
                }

                public NearbyTrainsDetails[] newArray(int size) {
                    return new NearbyTrainsDetails[size];
                }
            };
}
