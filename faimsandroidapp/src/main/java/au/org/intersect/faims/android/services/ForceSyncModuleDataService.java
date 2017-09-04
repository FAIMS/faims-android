package au.org.intersect.faims.android.services;

/**
 * Created by Wes Cilldhaire on 1/09/17.
 */

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.StringBody;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import au.org.intersect.faims.android.constants.FaimsSettings;
import au.org.intersect.faims.android.data.FileInfo;
import au.org.intersect.faims.android.data.Module;
import au.org.intersect.faims.android.database.DatabaseManager;
import au.org.intersect.faims.android.database.FileRecord;
import au.org.intersect.faims.android.log.FLog;
import au.org.intersect.faims.android.net.Request;
import au.org.intersect.faims.android.net.Result;
import au.org.intersect.faims.android.util.FileUtil;
import au.org.intersect.faims.android.util.ModuleUtil;


public class ForceSyncModuleDataService extends DownloadUploadService {

    protected File tempDB;

    public ForceSyncModuleDataService() {
        super("ForceSyncModuleDataService");
    }

    public ForceSyncModuleDataService(String name) {
        super(name);
    }

    @Override
    public void onDestroy() {
        if (tempDB != null) {
            FileUtil.delete(tempDB);
        }
        super.onDestroy();
    }

    protected void initService(Intent intent) {
        super.initService(intent);
    }

    @Override
    protected void performService() throws Exception {
        uploadDatabase();
    }

    private void uploadDatabase() throws Exception {

        Module module = ModuleUtil.getModule(serviceModule.key);

        File tempDB = File.createTempFile("temp_", ".sqlite", module.getDirectoryPath());
        final DatabaseManager databaseManager = new DatabaseManager();
        databaseManager.init(new File(Environment.getExternalStorageDirectory() + FaimsSettings.modulesDir + module.key + "/db.sqlite"));

        databaseManager.mergeRecord().dumpDatabaseTo(tempDB);

        // check if database is empty
        if (databaseManager.mergeRecord().isEmpty(tempDB)) {
            FLog.d("database is empty");
            return;
        }

        final HashMap<String, ContentBody> extraParts = new HashMap<String, ContentBody>();
        extraParts.put("user", new StringBody("0"));

        uploadFile(
                null,
                "/android/module/" + module.key+ "/db_upload",
                tempDB,
                new File(Environment.getExternalStorageDirectory() + FaimsSettings.modulesDir + module.key),
                extraParts
        );
        uploadAllFiles(
                FileRecord.APP,
                "/android/module/" + module.key + "/app_file_upload",
                new File(Environment.getExternalStorageDirectory() + FaimsSettings.modulesDir + module.key)
        );
    }
}
