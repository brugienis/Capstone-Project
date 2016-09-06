package au.com.kbrsolutions.melbournepublictransport.utilities;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by business on 5/09/2016.
 */
public class JodaDateTimeUtility {

    // FIXME: 6/09/2016 - make sure all date/time are in Melbourne time zone
    public static org.joda.time.DateTime getUtcTime(org.joda.time.DateTime time) {
        return time.toDateTime( org.joda.time.DateTimeZone.UTC );
    }

    public static org.joda.time.DateTime getLocalTime(org.joda.time.DateTime utcTime) {
        DateTimeZone dateTimeZone = DateTimeZone.getDefault();
        return utcTime.withZone(dateTimeZone);
    }

    public static org.joda.time.DateTime getLocalTimeFromUtcString(String utcTimeStr) {
        DateTime date = DateTime.parse(utcTimeStr,
                DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ssZ"));
        return JodaDateTimeUtility.getLocalTime(date);
    }

    private void DelLater() {

        org.joda.time.DateTime now = new org.joda.time.DateTime(); // Default time zone.
        org.joda.time.DateTime zulu = now.toDateTime( org.joda.time.DateTimeZone.UTC );
        DateTimeZone dateTimeZone = DateTimeZone.getDefault();
        org.joda.time.DateTime localTime = zulu.withZone(dateTimeZone);

        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        Date d = new Date();
        String dateLineStr = df.format(d);
        System.out.println( "Local time in ISO 8601 format: " + now );
        System.out.println( "Same moment in UTC (Zulu): " + zulu );
        System.out.println( "dateLineStr: " + dateLineStr );
        System.out.println( "localTime: " + localTime );
    }
}
