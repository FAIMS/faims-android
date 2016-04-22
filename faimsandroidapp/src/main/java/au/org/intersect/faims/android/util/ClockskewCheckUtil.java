package au.org.intersect.faims.android.util;

import android.location.GpsStatus;
import android.util.Log;

/**
 * Created by anomaly on 22/04/16.
 */
public class ClockskewCheckUtil implements GpsStatus.NmeaListener {
    @Override
    public void onNmeaReceived(long timestamp, String nmea) {
        Log.d("FAIMS", nmea);
    }
}
