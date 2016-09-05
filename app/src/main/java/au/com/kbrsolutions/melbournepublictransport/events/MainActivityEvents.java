package au.com.kbrsolutions.melbournepublictransport.events;

/**
 * Created by business on 5/09/2016.
 */
public class MainActivityEvents {

    public enum MainEvents {
        NETWORK_STATUS
    }

    public final MainEvents event;
    public final String msg;

    private MainActivityEvents(Builder builder) {
        this.event = builder.event;
        this.msg = builder.msg;
    }

    public static class Builder {

        public Builder(MainActivityEvents.MainEvents event) {
            this.event = event;
        }

        private MainEvents event;
        private String msg;

        public Builder setMsg(String msg) {
            this.msg = msg;
            return this;
        }

        public MainActivityEvents build() {
            return new MainActivityEvents(this);
        }
    }
}