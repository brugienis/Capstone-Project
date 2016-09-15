package au.com.kbrsolutions.melbournepublictransport.data;

/**
 * Created by business on 12/09/2016.
 */
public class NextDepartureDetails {
    public final int directionId;
    public final int runId;
    public final int numSkipped;
    public final int destinationId;
    public final String utcDepartureTime;

    public NextDepartureDetails(
            int directionId,
            int runId,
            int numSkipped,
            int destinationId,
            String utcDepartureTime) {
        this.directionId = directionId;
        this.runId = runId;
        this.numSkipped = numSkipped;
        this.destinationId = destinationId;
        this.utcDepartureTime = utcDepartureTime;
    }

    @Override
    public String toString() {return
            "directionId: " + directionId +
            "; runId: " + runId +
            "; numSkipped: " + numSkipped +
            "; destinationId: " + destinationId +
            "; utcDepartureTime: " + utcDepartureTime;
    }
}
