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

    protected static ArrayList<TimezoneCheckUtil> instances = new ArrayList<TimezoneCheckUtil>();

    public TimezoneCheckUtil(Context c) {
        this.context = c;
        instances.add(this);
    }

    public static Boolean isActioned() {
        return userActioned;
    }

    private Activity getActivity() {
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
        String gpsTZ = TimezoneMapper.latLngToTimezoneString(location.getLatitude(), location.getLongitude());
        String sysTZ = TimeZone.getDefault().getID();

        Log.d("FAIMS","LocationListener:onLocationChanged");
        Log.d("FAIMS", location.getProvider() + ": " + gpsTZ);
        Log.d("FAIMS", "ACC: " + location.getAccuracy());
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
        }
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
