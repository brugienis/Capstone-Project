package au.com.kbrsolutions.melbournepublictransport.data;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * This class keeps 'stops nearby' details.
 */
@SuppressWarnings("CanBeFinal")
public class StopsNearbyDetails implements Parcelable {

    public String stopName;
    public final String stopAddress;
    public final String suburb;
    public final int routeType;
    public final String stopId;
    public final double latitude;
    public final double longitude;
    public final double distance;

    public final static int TRAIN_ROUTE_TYPE = 0;
    public final static int TRAM_ROUTE_TYPE = 1;
    public final static int BUS_ROUTE_TYPE = 2;

    public StopsNearbyDetails(
            String stopName,
            String stopAddress,
            String suburb,
            int route_type,
            String stopId,
            double stopLat,
            double stopLon,
            double distance) {
        this.stopName = stopName;
        this.stopAddress = stopAddress;
        this.suburb = suburb;
        this.routeType = route_type;
        this.stopId = stopId;
        this.latitude = stopLat;
        this.longitude = stopLon;
        this.distance = distance;
    }

    private StopsNearbyDetails(Parcel input) {
        stopName = input.readString();
        stopAddress = input.readString();
        suburb = input.readString();
        routeType = input.readInt();
        stopId = input.readString();
        latitude = input.readDouble();
        longitude = input.readDouble();
        distance = input.readDouble();
    }

    @Override
    public String toString() {
        return "stopId: " + stopId +
                "; stopName: " + stopName +
                "; stopAddress: " + stopAddress +
                "; suburb: " + suburb +
                "; routeType: " + routeType +
                "; latitude: " + latitude +
                "; longitude: " + longitude;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(stopName);
        dest.writeString(stopAddress);
        dest.writeString(suburb);
        dest.writeInt(routeType);
        dest.writeString(stopId);
        dest.writeDouble(latitude);
        dest.writeDouble(longitude);
        dest.writeDouble(distance);
    }

    public static final Parcelable.Creator<StopsNearbyDetails> CREATOR =
            new Parcelable.Creator<StopsNearbyDetails>() {
                public StopsNearbyDetails createFromParcel(Parcel in) {
                    return new StopsNearbyDetails(in);
                }

                public StopsNearbyDetails[] newArray(int size) {
                    return new StopsNearbyDetails[size];
                }
            };
}
