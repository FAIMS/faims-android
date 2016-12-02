package au.org.intersect.faims.android.tasks;

import android.os.AsyncTask;
import android.util.Log;

import com.google.inject.Inject;

import org.json.JSONObject;

import au.org.intersect.faims.android.data.Module;
import au.org.intersect.faims.android.database.DatabaseManager;
import au.org.intersect.faims.android.net.FAIMSClient;
import au.org.intersect.faims.android.net.FAIMSClientResultCode;
import au.org.intersect.faims.android.net.Request;
import au.org.intersect.faims.android.net.Result;

/**
 * Created by Wes Cilldhaire on 24/05/16.
 */

public class DatabaseRecordCountTask extends AsyncTask<Void, Void, Void> {
    @Inject
    DatabaseManager databaseManager;

    private FAIMSClient faimsClient;
    private ITaskListener listener;
    private Module module;
    private au.org.intersect.faims.android.database.Database db;
    private Result result;

    public DatabaseRecordCountTask(FAIMSClient faimsClient, ITaskListener listener, Module module) {
        this.faimsClient = faimsClient;
        this.listener = listener;
        this.module = module;
        this.databaseManager = new DatabaseManager();
        databaseManager.init(this.module.getDirectoryPath("db.sqlite"));
    }

    @Override
    protected Void doInBackground(Void... params) {
        result = faimsClient.fetchRequestObject(Request.DATABASE_RECORD_COUNT(module));
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        JSONObject json;
        if (result.resultCode == FAIMSClientResultCode.FAILURE) {
            faimsClient.invalidate();
        } else {
            try {
                json = (JSONObject) result.data;
                json.put("localRelationships", databaseManager.relationshipRecord().totalNonDeletedRelationships());
                json.put("localEntities", databaseManager.entityRecord().totalEntities());
                result.data = json;
            } catch (Exception e) {
                Log.d("JSON", e.getMessage());
            }
        }
        databaseManager.destroy();
        listener.handleTaskCompleted(result);
    }
}
