package au.com.kbrsolutions.melbournepublictransport.events;

import java.util.List;

import au.com.kbrsolutions.melbournepublictransport.data.DisruptionsDetails;
import au.com.kbrsolutions.melbournepublictransport.data.StopsNearbyDetails;
import au.com.kbrsolutions.melbournepublictransport.data.NextDepartureDetails;
import au.com.kbrsolutions.melbournepublictransport.data.LatLngDetails;
import au.com.kbrsolutions.melbournepublictransport.data.StopDetails;

/**
 * Created by business on 5/09/2016.
 */
public class MainActivityEvents {

    public enum MainEvents {
        REMOTE_ACCESS_PROBLEMS,
        NEXT_DEPARTURES_DETAILS,
        DISRUPTIONS_DETAILS,
        CURR_LOCATION_DETAILS,
        NEARBY_LOCATION_DETAILS,
        REFRESH_FAVORITE_STOPS_VIEW,
        DATABASE_LOAD_PROGRESS
    }

    public final MainEvents event;
    public final String msg;
    public final List<NextDepartureDetails> nextDepartureDetailsList;
    public final StopDetails stopDetails;
    public final List<DisruptionsDetails> disruptionsDetailsList;
    public final LatLngDetails latLonDetails;
    public final List<StopsNearbyDetails> nearbyStopsDetailsList;
    public final boolean forTrainsStopsNearby;
    public final int databaseLoadProgress;
    public final int databaseLoadTarget;

    private MainActivityEvents(Builder builder) {
        this.event = builder.event;
        this.msg = builder.msg;
        this.nextDepartureDetailsList = builder.nextDepartureDetailsList;
        this.stopDetails = builder.stopDetails;
        this.disruptionsDetailsList = builder.disruptionsDetailsList;
        this.latLonDetails = builder.latLonDetails;
        this.nearbyStopsDetailsList = builder.nearbyStopsDetailsList;
        this.forTrainsStopsNearby = builder.forTrainsStopsNearby;
        this.databaseLoadProgress = builder.databaseLoadProgress;
        this.databaseLoadTarget = builder.databaseLoadTarget;
    }

    public static class Builder {

        public Builder(MainActivityEvents.MainEvents event) {
            this.event = event;
        }

        private final MainEvents event;
        private String msg;
        private List<NextDepartureDetails> nextDepartureDetailsList;
        private StopDetails stopDetails;
        private List<DisruptionsDetails> disruptionsDetailsList;
        private LatLngDetails latLonDetails;
        private List<StopsNearbyDetails> nearbyStopsDetailsList;
        private boolean forTrainsStopsNearby;
        private int databaseLoadProgress;
        private int databaseLoadTarget;

        public Builder setMsg(String msg) {
            this.msg = msg;
            return this;
        }

        public Builder setNextDepartureDetailsList(List<NextDepartureDetails> nextDepartureDetailsList) {
            this.nextDepartureDetailsList = nextDepartureDetailsList;
            return this;
        }

        public Builder setStopDetails(StopDetails stopDetails) {
            this.stopDetails = stopDetails;
            return this;
        }

        public Builder setDisruptionsDetailsList(List<DisruptionsDetails> disruptionsDetailsList) {
            this.disruptionsDetailsList = disruptionsDetailsList;
            return this;
        }

        public Builder setLatLonDetails(LatLngDetails latLonDetails) {
            this.latLonDetails = latLonDetails;
            return this;
        }

        public Builder setNearbyStopsDetailsList(List<StopsNearbyDetails> nearbyStopsDetailsList) {
            this.nearbyStopsDetailsList = nearbyStopsDetailsList;
            return this;
        }

        public Builder setForTrainsStopsNearby(boolean forTrainsStopsNearby) {
            this.forTrainsStopsNearby = forTrainsStopsNearby;
            return this;
        }

        public Builder setDatabaseLoadProgress(int databaseLoadProgress) {
            this.databaseLoadProgress = databaseLoadProgress;
            return this;
        }

        public Builder setDatabaseLoadTarget(int databaseLoadTarget) {
            this.databaseLoadTarget = databaseLoadTarget;
            return this;
        }

        public MainActivityEvents build() {
            return new MainActivityEvents(this);
        }
    }
}