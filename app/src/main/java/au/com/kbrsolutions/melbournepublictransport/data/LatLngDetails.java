package au.com.kbrsolutions.melbournepublictransport.data;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * This class keeps latitude and longitude values.
 */
public class LatLngDetails implements Parcelable {

    public final double latitude;
    public final double longitude;

    public LatLngDetails(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    private LatLngDetails(Parcel input) {
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

    public static final Parcelable.Creator<LatLngDetails> CREATOR =
            new Parcelable.Creator<LatLngDetails>() {
                public LatLngDetails createFromParcel(Parcel in) {
                    return new LatLngDetails(in);
                }

                public LatLngDetails[] newArray(int size) {
                    return new LatLngDetails[size];
                }
            };
}
