package au.com.kbrsolutions.melbournepublictransport.data;

/**
 * Created by business on 5/09/2016.
 */
public class LineDetails {
    public final int routeType;
    public final String lineId;
    public final String lineName;
    public final String lineNameShort;

    public LineDetails(int routeType, String lineId, String lineName, String lineNameShort) {
        this.routeType = routeType;
        this.lineId = lineId;
        this.lineName = lineName;
        this.lineNameShort = lineNameShort;
    }

    @Override
    public String toString() {
        return "routeType: " + routeType + "lineId: " + lineId + "lineName: " + lineName + "lineNameShort: " + lineNameShort;
    }
}
