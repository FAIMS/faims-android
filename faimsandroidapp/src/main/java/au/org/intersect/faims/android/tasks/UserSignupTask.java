package au.org.intersect.faims.android.tasks;

import android.os.AsyncTask;

import au.org.intersect.faims.android.data.Module;
import au.org.intersect.faims.android.data.User;
import au.org.intersect.faims.android.net.FAIMSClient;
import au.org.intersect.faims.android.net.FAIMSClientResultCode;
import au.org.intersect.faims.android.net.Request;
import au.org.intersect.faims.android.net.Result;

/**
 * Created by Wes Cilldhaire on 17/05/16.
 */
public class UserSignupTask extends AsyncTask<Void, Void, Void> {

    private FAIMSClient faimsClient;
    private ITaskListener listener;
    private User user;
    private Module module;

    private Result result;

    public UserSignupTask(FAIMSClient faimsClient, ITaskListener listener, User user, Module module) {
        this.faimsClient = faimsClient;
        this.listener = listener;
        this.user = user;
        this.module = module;
    }

    @Override
    protected Void doInBackground(Void... params) {
        result = faimsClient.createUser(Request.USER_SIGNUP_REQUEST(module), user);
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