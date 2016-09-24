package au.com.kbrsolutions.melbournepublictransport.events;

import java.util.List;

import au.com.kbrsolutions.melbournepublictransport.data.DisruptionsDetails;
import au.com.kbrsolutions.melbournepublictransport.data.NextDepartureDetails;
import au.com.kbrsolutions.melbournepublictransport.data.StopDetails;

/**
 * Created by business on 5/09/2016.
 */
public class MainActivityEvents {

    public enum MainEvents {
        NETWORK_STATUS,
        NEXT_DEPARTURES_DETAILS,
        DISRUPTIONS_DETAILS,
        CURR_LOCATION_DETAILS
    }

    public final MainEvents event;
    public final String msg;
    public final List<NextDepartureDetails> nextDepartureDetailsList;
    public final List<DisruptionsDetails> disruptionsDetailsList;
    public final StopDetails stopDetails;

    private MainActivityEvents(Builder builder) {
        this.event = builder.event;
        this.msg = builder.msg;
        this.nextDepartureDetailsList = builder.nextDepartureDetailsList;
        this.disruptionsDetailsList = builder.disruptionsDetailsList;
        this.stopDetails = builder.stopDetails;
    }

    public static class Builder {

        public Builder(MainActivityEvents.MainEvents event) {
            this.event = event;
        }

        private MainEvents event;
        private String msg;
        private List<NextDepartureDetails> nextDepartureDetailsList;
        private List<DisruptionsDetails> disruptionsDetailsList;
        private StopDetails stopDetails;

        public Builder setMsg(String msg) {
            this.msg = msg;
            return this;
        }

        public Builder setNextDepartureDetailsList(List<NextDepartureDetails> nextDepartureDetailsList) {
            this.nextDepartureDetailsList = nextDepartureDetailsList;
            return this;
        }

        public Builder setDisruptionsDetailsList(List<DisruptionsDetails> disruptionsDetailsList) {
            this.disruptionsDetailsList = disruptionsDetailsList;
            return this;
        }

        public Builder setStopDetails(StopDetails stopDetails) {
            this.stopDetails = stopDetails;
            return this;
        }

        public MainActivityEvents build() {
            return new MainActivityEvents(this);
        }
    }
}