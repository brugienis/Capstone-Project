package au.com.kbrsolutions.melbournepublictransport.data;

import android.os.Parcel;
import android.os.Parcelable;

public class NearbyStopsDetails implements Parcelable {

    public String stopName;
    public final String stopAddress;
    public final String transportType;
    public final String stopId;
    public final double stopLat;
    public final double stopLon;

    public NearbyStopsDetails(
            String stopName,
            String stopAddress,
            String transportType,
            String stopId,
            double stopLat,
            double stopLon) {
        this.stopName = stopName;
        this.stopAddress = stopAddress;
        this.transportType = transportType;
        this.stopId = stopId;
        this.stopLat = stopLat;
        this.stopLon = stopLon;
    }

    public NearbyStopsDetails(Parcel input) {
        stopName = input.readString();
        stopAddress = input.readString();
        transportType = input.readString();
        stopId = input.readString();
        stopLat = input.readDouble();
        stopLon = input.readDouble();
    }

    @Override
    public String toString() {
        return "stopId: " + stopId +
                "stopName: " + stopName +
                "; stopAddress: " + stopAddress +
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
        dest.writeString(transportType);
        dest.writeString(stopId);
        dest.writeDouble(stopLat);
        dest.writeDouble(stopLon);
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
