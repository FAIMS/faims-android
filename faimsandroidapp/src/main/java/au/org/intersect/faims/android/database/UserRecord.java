package au.org.intersect.faims.android.database;

import android.util.Base64;
import android.util.Log;

import java.io.File;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import au.org.intersect.faims.android.data.User;
import au.org.intersect.faims.android.log.FLog;
import jsqlite.Exception;
import jsqlite.Stmt;

/**
 * Created by Wes Cilldhaire on 11/05/16.
 */
public class UserRecord extends Database {

    public UserRecord(File dbFile) {
        super(dbFile);
    }

//    public boolean saveUser(User user) throws java.lang.Exception {
//
//        try {
//            String userLookup = "";
//            userLookup = databaseManager.fetchRecord().fetchOne("SELECT userId from user where email = '" + user.getEmail() + "' ;").get(0);
//            if (null != userLookup || !userLookup.isEmpty()) {
//                return false;
//            }
//        } catch (java.lang.Exception e) {
////            return false;
//        }
//
//    }

    public boolean userExists(User user) throws Exception {
        try {
            String userId = databaseManager.fetchRecord().fetchOne("SELECT count(*) from user where email ='" + user.getEmail() + "';").get(0);
            if (userId.equals("1")) {
                return true;
            } else {
                return false;
            }
        } catch (java.lang.Exception e) {
            Exception ex = new Exception("Unable to check password");
            throw ex;
        }
    }

    public boolean verifyUser(String userId, String password) throws Exception {
        try {
            String userPassword = databaseManager.fetchRecord().fetchOne("SELECT password from user where userid =" + userId + ";").get(0);
            String verifyPassword = new String(Base64.encode(MessageDigest.getInstance("SHA1").digest(password.getBytes()), Base64.NO_WRAP));
            if (verifyPassword.equals(userPassword)) {
                return true;
            } else {
                return false;
            }
        } catch (java.lang.Exception e) {
            Exception ex = new Exception("Unable to check password");
            throw ex;
        }
    }
}
