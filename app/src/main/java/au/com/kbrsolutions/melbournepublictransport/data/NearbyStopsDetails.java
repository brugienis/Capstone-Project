package au.com.kbrsolutions.melbournepublictransport.data;

import android.os.Parcel;
import android.os.Parcelable;

public class NearbyStopsDetails implements Parcelable {

    public String stopName;
    public final String stopAddress;
    public final String suburb;
    public final int route_type;
    public final String stopId;
    public final double stopLat;
    public final double stopLon;
    public final double distance;

    public final static int TRAIN_ROUTE_TYPE = 0;
    public final static int TRAM_ROUTE_TYPE = 1;
    public final static int BUS_ROUTE_TYPE = 2;

    public NearbyStopsDetails(
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
        this.route_type = route_type;
        this.stopId = stopId;
        this.stopLat = stopLat;
        this.stopLon = stopLon;
        this.distance = distance;
    }

    public NearbyStopsDetails(Parcel input) {
        stopName = input.readString();
        stopAddress = input.readString();
        suburb = input.readString();
        route_type = input.readInt();
        stopId = input.readString();
        stopLat = input.readDouble();
        stopLon = input.readDouble();
        distance = input.readDouble();
    }

    @Override
    public String toString() {
        return "stopId: " + stopId +
                "; stopName: " + stopName +
                "; stopAddress: " + stopAddress +
                "; suburb: " + suburb +
                "; route_type: " + route_type +
                "; stopLat: " + stopLat +
                "; stopLon: " + stopLon;
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
        dest.writeInt(route_type);
        dest.writeString(stopId);
        dest.writeDouble(stopLat);
        dest.writeDouble(stopLon);
        dest.writeDouble(distance);
    }

    public static final Parcelable.Creator<NearbyStopsDetails> CREATOR =
            new Parcelable.Creator<NearbyStopsDetails>() {
                public NearbyStopsDetails createFromParcel(Parcel in) {
                    return new NearbyStopsDetails(in);
                }

                public NearbyStopsDetails[] newArray(int size) {
                    return new NearbyStopsDetails[size];
                }
            };
}
