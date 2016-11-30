package au.com.kbrsolutions.melbournepublictransport.data;

import android.os.Parcel;
import android.os.Parcelable;

import static au.com.kbrsolutions.melbournepublictransport.R.id.directionName;
/**
 * This class keeps 'disruptions' details.
 */

public class DisruptionsDetails implements Parcelable {
    public final String title;
    public final String description;

    public DisruptionsDetails(String title, String description) {
        this.title = title;
        this.description = description;
    }

    public DisruptionsDetails(Parcel input) {
        title = input.readString();
        description = input.readString();
    }

    @Override
    public String toString() {
        return "title: " + title +
                "; directionName: " + directionName +
                "; description: " + description;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(description);
    }

    public static final Parcelable.Creator<DisruptionsDetails> CREATOR =
            new Parcelable.Creator<DisruptionsDetails>() {
                public DisruptionsDetails createFromParcel(Parcel in) {
                    return new DisruptionsDetails(in);
                }

                public DisruptionsDetails[] newArray(int size) {
                    return new DisruptionsDetails[size];
                }
            };
}
