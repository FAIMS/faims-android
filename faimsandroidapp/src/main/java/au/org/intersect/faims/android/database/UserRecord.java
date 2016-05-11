package au.org.intersect.faims.android.database;

import android.util.Log;

import java.io.File;
import java.security.MessageDigest;

import au.org.intersect.faims.android.data.User;
import au.org.intersect.faims.android.log.FLog;
import jsqlite.Stmt;

/**
 * Created by Wes Cilldhaire on 11/05/16.
 */
public class UserRecord extends Database {

    public UserRecord(File dbFile) {
        super(dbFile);
    }

    public boolean saveUser(User user) throws Exception {
//        FLog.d("firstName:" + user.getFirstName());
//        FLog.d("lastName:" + user.getLastName());
//        FLog.d("email:" + user.getEmail());
//        FLog.d("password:" + user.getPassword());

        String query = DatabaseQueries.INSERT_INTO_USERS;

        jsqlite.Database db = null;
        Stmt st = null;
        try {
            db = openDB(jsqlite.Constants.SQLITE_OPEN_READWRITE);
            beginTransaction(db);
            st = db.prepare(query);
            st.bind(1, user.getFirstName());
            st.bind(2, user.getLastName());
            st.bind(3, user.getEmail());
            st.bind(4, new String(MessageDigest.getInstance("SHA1").digest(user.getPassword().getBytes())));
            st.step();
            st.close();
            st = null;
            commitTransaction(db);
            notifyListeners();
            return true;
        } finally {
            closeStmt(st);
            closeDB(db);
        }
    }

    public boolean verifyUser(String userId, String password) throws Exception {
        String userPassword = databaseManager.fetchRecord().fetchOne("SELECT password from user where userid =" + userId +";").get(0);
        if ((new String(MessageDigest.getInstance("SHA1").digest(password.getBytes()))).equals(userPassword)) {
            return true;
        } else {
            return false;
        }
    }
}
