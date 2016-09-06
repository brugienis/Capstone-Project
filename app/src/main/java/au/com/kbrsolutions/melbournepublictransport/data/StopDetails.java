package au.com.kbrsolutions.melbournepublictransport.data;

/**
 * Created by business on 18/08/2016.
 */
public class StopDetails {
    public final int routeType;
    public final String stopId;
    public final String locationName;
    public final double latitude;
    public final double longitude;
    public final String favorite;

    public StopDetails(int routeType, String stopId, String locationName, double latitude, double longitude, String favorite) {
        this.routeType = routeType;
        this.stopId = stopId;
        this.locationName = locationName;
        this.latitude = latitude;
        this.longitude = longitude;
        this.favorite = favorite;
    }

    @Override
    public String toString() {
        return "routeType: " + routeType + "stopId: " + stopId + "locationName: " + locationName + "latitude: " + latitude + "longitude: " + longitude + "favorite: " + favorite;
    }
}
