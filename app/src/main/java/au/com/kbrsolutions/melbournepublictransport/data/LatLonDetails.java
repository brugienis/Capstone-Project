package au.com.kbrsolutions.melbournepublictransport.data;

import android.os.Parcel;
import android.os.Parcelable;

/**
 */
public class LatLonDetails implements Parcelable {

    public final double latitude;
    public final double longitude;

    public LatLonDetails(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public LatLonDetails(Parcel input) {
        latitude = input.readDouble();
        longitude = input.readDouble();
    }

    @Override
    public String toString() {
        return "latitude: " + latitude +
                "; longitude: " + longitude;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(latitude);
        dest.writeDouble(longitude);
    }

    public static final Parcelable.Creator<LatLonDetails> CREATOR =
            new Parcelable.Creator<LatLonDetails>() {
                public LatLonDetails createFromParcel(Parcel in) {
                    return new LatLonDetails(in);
                }

                public LatLonDetails[] newArray(int size) {
                    return new LatLonDetails[size];
                }
            };
}
