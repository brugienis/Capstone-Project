package au.com.kbrsolutions.melbournepublictransport.utilities;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;

/**
 * Created by business on 5/09/2016.
 */
public class JodaDateTimeUtility {

    // FIXME: 6/09/2016 - make sure all date/time are in Melbourne time zone
    public static org.joda.time.DateTime getUtcTime(org.joda.time.DateTime time) {
        return time.toDateTime( org.joda.time.DateTimeZone.UTC );
    }

    private static org.joda.time.DateTime getLocalTime(org.joda.time.DateTime utcTime) {
        DateTimeZone dateTimeZone = DateTimeZone.getDefault();
        return utcTime.withZone(dateTimeZone);
    }

    public static org.joda.time.DateTime getLocalTimeFromUtcString(String utcTimeStr) {
        DateTime date = DateTime.parse(utcTimeStr,
                DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ssZ"));
        return JodaDateTimeUtility.getLocalTime(date);
    }
}
