package au.org.intersect.faims.android.util;

import android.widget.EditText;

import com.robotium.solo.Solo;

import au.org.intersect.faims.android.R;

/**
 * Created by Matthew on 5/05/2016.
 */
public class AppSignupUtil extends ModuleHelper {

    // Buttons
    /*
        Sign up
     */
    public static void clickButton_Signup(Solo solo) {
        clickOpenDialogButton(solo, "Sign up");
    }

    /*
        OK
     */
    public static void clickButton_SignupOk(Solo solo) {
        clickCloseDialogButton(solo, "Ok");
    }

    /*
        Cancel
     */
    public static void clickButton_SignupCancel(Solo solo) {
        clickCloseDialogButton(solo, "Cancel");
    }

    /*
        OK
     */
    public static void clickButton_ErrorOk(Solo solo) {
        clickCloseDialogButton(solo, "OK");
    }

    /*
        OK
     */
    public static void clickButton_LoginOk(Solo solo) {
        clickCloseDialogButton(solo, "Ok");
    }

    /*
        Cancel
     */
    public static void clickButton_LoginCancel(Solo solo) {
        clickCloseDialogButton(solo, "Cancel");
    }


    // Text fields for the sign up page (part of the app but used in modules)
	/*
		First Name field
 	 */
    public static EditText getEditText_FirstName(Solo solo) {
        return (android.widget.EditText) solo.getView(R.id.create_user_edittext_fname);
    }

    /*
        Last Name field
      */
    public static EditText getEditText_LastName(Solo solo) {
        return (android.widget.EditText) solo.getView(R.id.create_user_edittext_lname);
    }

    /*
        Email field
      */
    public static EditText getEditText_Email(Solo solo) {
        return (android.widget.EditText) solo.getView(R.id.create_user_edittext_email);
    }

    /*
        Password field
      */
    public static EditText getEditText_Password(Solo solo) {
        return (android.widget.EditText) solo.getView(R.id.create_user_edittext_password);
    }

    /*
        Password Confitmation field
      */
    public static EditText getEditText_PasswordConfirmation(Solo solo) {
        return (android.widget.EditText) solo.getView(R.id.create_user_edittext_password_confirmation);
    }

    /*
        Login Password field
      */
    public static EditText getEditText_LoginPassword(Solo solo) {
        return (android.widget.EditText) solo.getView(R.id.verify_user_edittext_password);
    }


}
