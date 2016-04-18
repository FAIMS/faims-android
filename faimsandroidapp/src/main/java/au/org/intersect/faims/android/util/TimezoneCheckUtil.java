package au.org.intersect.faims.android.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Map;
import java.util.TimeZone;
import au.com.edval.drtimcooper.TimezoneMapper;

/**
 * Created by Wes Cilldhaire on 18/04/16.
 */

public class TimezoneCheckUtil implements LocationListener {

    Context context;

    private static Boolean userActioned = false;

    // In the splash screen we start LocationListeners on both the Passive and the GPS providers.
    // This lets us track both so we can remove them when the user has responded to one or the other
    protected static ArrayList<TimezoneCheckUtil> instances = new ArrayList<TimezoneCheckUtil>();

    public TimezoneCheckUtil(Context c) {
        this.context = c;
        instances.add(this);
    }

    public static Boolean isActioned() {
        return userActioned;
    }

    public static void resetUserActioned() {
        userActioned = false;
    }

    // Compare location-determined TZ to system TZ and display a dialog asking the user to set the
    // system TZ correctly
    public static void checkTimezone(Location location) {
        String gpsTZ = TimezoneMapper.latLngToTimezoneString(location.getLatitude(), location.getLongitude());
        String sysTZ = TimeZone.getDefault().getID();

        Log.d("FAIMS", location.getProvider() + ": " + gpsTZ + " (accuracy: " + location.getAccuracy() + ")");
        Log.d("FAIMS", "SYS: " + sysTZ);

        if (!gpsTZ.equals(sysTZ) && !userActioned) {
            final Activity currentActivity = getActivity();
            if (currentActivity != null) {
                new AlertDialog.Builder(currentActivity)
                        .setMessage("Timezone from " + location.getProvider() + " ("
                                + gpsTZ
                                + ") does not match your system timezone ("
                                + sysTZ
                                + ").  Do you wish to change your system timezone?")
                        .setCancelable(false)
                        .setPositiveButton(
                                "Yes",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        TimezoneCheckUtil.userActioned = true;
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
                                        TimezoneCheckUtil.userActioned = true;
                                        dialog.cancel();
                                    }
                                }
                        )
                        .create()
                        .show();
            }
        } else if (!gpsTZ.equals(sysTZ) && userActioned) {
            Log.d("FAIMS","User has already addressed the TZ mismatch");
        } else if (gpsTZ.equals(sysTZ)) {
            Log.d("FAIMS","TZ matches");
        }
    }

    // Borrowed from http://stackoverflow.com/a/28423385
    // Returns the currently displayed Activity, required for providing the right context
    // for displaying the AlertDialog when a TZ mismatch is detected
    private static Activity getActivity() {
        try {
            Class activityThreadClass = Class.forName("android.app.ActivityThread");
            Object activityThread = activityThreadClass.getMethod("currentActivityThread").invoke(null);
            Field activitiesField = activityThreadClass.getDeclaredField("mActivities");
            activitiesField.setAccessible(true);
            Map activities = (Map) activitiesField.get(activityThread);
            for (Object activityRecord : activities.values()) {
                Class activityRecordClass = activityRecord.getClass();
                Field pausedField = activityRecordClass.getDeclaredField("paused");
                pausedField.setAccessible(true);
                if (!pausedField.getBoolean(activityRecord)) {
                    Field activityField = activityRecordClass.getDeclaredField("activity");
                    activityField.setAccessible(true);
                    Activity activity = (Activity) activityField.get(activityRecord);
                    return activity;
                }
            }
        } catch (ClassNotFoundException e) {

        } catch (NoSuchMethodException e) {

        } catch (InvocationTargetException e) {

        } catch (IllegalAccessException e) {

        } catch (NoSuchFieldException e) {

        }
        return null;
    }

    @Override
    public void onLocationChanged(final Location location) {
        Log.d("FAIMS","LocationListener:onLocationChanged");
        checkTimezone(location);
        Log.d("FAIMS", "Removing location listeners");
        for (TimezoneCheckUtil tzcheck : instances) {
            ((LocationManager) context.getSystemService(Context.LOCATION_SERVICE)).removeUpdates(tzcheck);
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Log.d("FAIMS","LocationListener:onStatusChanged");
    }

    @Override
    public void onProviderEnabled(String provider) {
        Log.d("FAIMS","LocationListener:onProviderEnabled");
    }

    @Override
    public void onProviderDisabled(String provider) {
        Log.d("FAIMS","LocationListener:onProviderDisabled");
    }
}
