package au.org.intersect.faims.android.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.GpsStatus;
import android.provider.Settings;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by Wes Cilldhaire on 22/04/16.
 */
public class ClockskewCheckUtil implements GpsStatus.NmeaListener {

    private static SimpleDateFormat gpsGmtTimeFormat = new SimpleDateFormat("HHmmss");
    private static SimpleDateFormat localTimeFormat = new SimpleDateFormat("HH:mm:ss");

    private static Boolean userActioned = false;

    public static Boolean isActioned() {
        return userActioned;
    }

    public static synchronized void setUserActioned(Boolean actioned) {
        userActioned = actioned;
    }

    @Override
    public void onNmeaReceived(long timestamp, String nmea) {
         checkTime(nmea);
    }

    public static void checkTime(String nmea) {
        if (nmea.startsWith("$GPGGA") || nmea.startsWith("$GPRMC")) {
            gpsGmtTimeFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

            String sysTime = gpsGmtTimeFormat.format(new Date());
            try {
                String gpsTime = nmea.split(",")[1].split("\\.")[0];
                if (!gpsTime.isEmpty()) {
                    Integer skew = Math.abs(Integer.parseInt(gpsTime) - Integer.parseInt(sysTime));
//                    Log.d("FAIMS", "GPS Time: " + gpsTime + " (" + nmea.substring(0, 6) + ")");
//                    Log.d("FAIMS", "SYS Time: " + sysTime);
//                    Log.d("FAIMS", "Skew: " + skew);
                    if (skew > 120 && !userActioned) {
                        final Activity currentActivity = TimezoneCheckUtil.getActivity();
                        if (currentActivity != null) {
                            ClockskewCheckUtil.setUserActioned(true); // do this early to prevent multiple dialogs being created, they are modal anyway
                            new AlertDialog.Builder(currentActivity)
                                    .setMessage("The time from GPS (" +
                                            localTimeFormat.format(gpsGmtTimeFormat.parse(gpsTime)) +
                                            ") does not match your system time (" +
                                            localTimeFormat.format(gpsGmtTimeFormat.parse(sysTime)) +
                                            ").  Do you wish to change your clock settings?")
                                    .setCancelable(false)
                                    .setPositiveButton(
                                            "Yes",
                                            new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int id) {
                                                    // Display the system Date and Time settings so the user can choose a timezone
                                                    currentActivity.startActivity(new Intent(Settings.ACTION_DATE_SETTINGS));
                                                    dialog.cancel();
                                                }
                                            }
                                    )
                                    .setNegativeButton(
                                            "No",
                                            new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int id) {
                                                    dialog.cancel();
                                                }
                                            }
                                    )
                                    .create()
                                    .show();
                        }

                    }
                }
            } catch (ArrayIndexOutOfBoundsException e) {
                Log.d("FAIMS","Skew: NMEA parse failed");
                Log.d("FAIMS", e.getMessage());
            } catch (ParseException e) {
                Log.d("FAIMS","Skew: Time parse failed");
                Log.d("FAIMS", e.getMessage());
            }
        }
    }
}
