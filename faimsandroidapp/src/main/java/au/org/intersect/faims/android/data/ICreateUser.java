package au.org.intersect.faims.android.data;

/**
 * Created by Wes Cilldhaire on 11/05/16.
 */
public interface ICreateUser {
    public void createUser(String fname, String lname, String email, String password, String callback);
}
