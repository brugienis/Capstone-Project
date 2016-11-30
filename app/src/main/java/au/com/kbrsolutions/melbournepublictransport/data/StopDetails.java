package au.com.kbrsolutions.melbournepublictransport.data;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * This class keeps 'stops' details.
 */
public class StopDetails implements Parcelable {
    public final int id;
    public final int routeType;
    public final String stopId;
    public final String locationName;
    public final double latitude;
    public final double longitude;
    public final String favorite;

    public StopDetails(int id, int routeType, String stopId, String locationName, double latitude, double longitude, String favorite) {
        this.id = id;
        this.routeType = routeType;
        this.stopId = stopId;
        this.locationName = locationName;
        this.latitude = latitude;
        this.longitude = longitude;
        this.favorite = favorite;
    }

    public StopDetails(Parcel input) {
        id = input.readInt();
        routeType = input.readInt();
        stopId = input.readString();
        locationName = input.readString();
        latitude = input.readDouble();
        longitude = input.readDouble();
        favorite = input.readString();
    }

    @Override
    public String toString() {
        return "id: " + id +
                "; routeType: " + routeType +
                "; stopId: " + stopId +
                "; locationName: " + locationName +
                "; latitude: " + latitude +
                "; longitude: " + longitude +
                "; favorite: " + favorite;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeInt(routeType);
        dest.writeString(stopId);
        dest.writeString(locationName);
        dest.writeDouble(latitude);
        dest.writeDouble(longitude);
        dest.writeString(favorite);
    }

    public static final Parcelable.Creator<StopDetails> CREATOR =
            new Parcelable.Creator<StopDetails>() {
                public StopDetails createFromParcel(Parcel in) {
                    return new StopDetails(in);
                }

                public StopDetails[] newArray(int size) {
                    return new StopDetails[size];
                }
            };
}
