package au.com.kbrsolutions.melbournepublictransport.events;

import au.com.kbrsolutions.melbournepublictransport.data.NextDepartureDetails;

/**
 * Created by business on 5/09/2016.
 */
public class MainActivityEvents {

    public enum MainEvents {
        NETWORK_STATUS,
        NEXT_DEPARTURES_DETAILS
    }

    public final MainEvents event;
    public final String msg;
    public final NextDepartureDetails mNextDepartureDetails;

    private MainActivityEvents(Builder builder) {
        this.event = builder.event;
        this.msg = builder.msg;
        this.mNextDepartureDetails = builder.nextDepartureDetails;
    }

    public static class Builder {

        public Builder(MainActivityEvents.MainEvents event) {
            this.event = event;
        }

        private MainEvents event;
        private String msg;
        private NextDepartureDetails nextDepartureDetails;

        public Builder setMsg(String msg) {
            this.msg = msg;
            return this;
        }

        public Builder setNextDepartureDetails(NextDepartureDetails nextDepartureDetails) {
            this.nextDepartureDetails = nextDepartureDetails;
            return this;
        }

        public MainActivityEvents build() {
            return new MainActivityEvents(this);
        }
    }
}