package au.org.intersect.faims.android.tasks;

import android.os.AsyncTask;

import au.org.intersect.faims.android.data.Module;
import au.org.intersect.faims.android.data.User;
import au.org.intersect.faims.android.net.FAIMSClient;
import au.org.intersect.faims.android.net.FAIMSClientResultCode;
import au.org.intersect.faims.android.net.Request;
import au.org.intersect.faims.android.net.Result;

/**
 * Created by Wes Cilldhaire on 24/05/16.
 */
public class DatabaseRecordCountTask extends AsyncTask<Void, Void, Void> {
    private FAIMSClient faimsClient;
    private ITaskListener listener;
    private Module module;

    private Result result;

    public DatabaseRecordCountTask(FAIMSClient faimsClient, ITaskListener listener, Module module) {
        this.faimsClient = faimsClient;
        this.listener = listener;
        this.module = module;
    }

    @Override
    protected Void doInBackground(Void... params) {
        result = faimsClient.fetchRequestObject(Request.DATABASE_RECORD_COUNT(module));
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        if (result.resultCode == FAIMSClientResultCode.FAILURE) {
            faimsClient.invalidate();
        }

        listener.handleTaskCompleted(result);
    }
}
