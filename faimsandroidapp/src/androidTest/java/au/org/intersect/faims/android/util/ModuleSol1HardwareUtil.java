package au.org.intersect.faims.android.util;

import android.view.View;
import android.widget.EditText;

import com.robotium.solo.Solo;

/**
 * Created by Matthew on 5/05/2016.
 */
public class ModuleSol1HardwareUtil {

    // Drop Downs
    /*
        File: ui_schema.xml
        Type: Drop Down
        ref: User/User/Select_User
     */
    public static View get_UserUserSelect_User(Solo solo) {
        return solo.getView((Object) "User/User/Select_User");
    }

    /*
        File: ui_schema.xml
        Type: Drop Down
        ref: User/User/Select_User
     */
    public static View get_AssetCustomerCustomer(Solo solo) {
        return solo.getView((Object) "Asset/Customer/Customer");
    }

    // Drop down item
    /*
        File: data_schema.xml
        Type: Drop down item
        ref:
     */
    public static void clickText_Sol1(Solo solo) {
        /*
            TODO: Not sure what this will end up with due to "Create users from module" feature, probably a constant added for testing
         */

        solo.clickOnText("Sol1");
    }

    // Buttons
    /*
        File: ui_schema.xml
        Type: Button
        ref: User/User/Login
     */
    public static void clickButton_Login(Solo solo) {
        // TODO: Would we want to do anything other than click on a button?
        solo.clickOnButton("Login");
    }

    /*
        File: ui_schema.xml
        Type: Button
        ref: Control/Main/Record_Asset
     */
    public static void clickButton_Record_Asset(Solo solo) {
        // TODO: Would we want to do anything other than click on a button?
        /*
            TODO: This one gets the label from the language properties file
                can I read this from the view? it exists in mText but I can't read it
                can I address this using solo.getView((Object) "Control/Main/Record_Asset")? no, though the ref tags exist
         */
        solo.clickOnButton("Record Asset");
    }

    /*
        File: ui_schema.xml
        Type: Button
        ref: Control/Main/Record_Asset
     */
    public static void clickButton_Serial_Trigger(Solo solo) {
        // TODO: Would we want to do anything other than click on a button?
        /*
            TODO: This one gets the label from the language properties file
                can I read this from the view? it exists in mText but I can't read it
                can I address this using solo.getView((Object) "Control/Main/Record_Asset")? no, though the ref tags exist
         */
        solo.clickOnButton("Serial Trigger");
    }

    /*
        File: ui_schema.xml
        Type: Button
        ref: Control/Main/Record_Asset
     */
    public static void clickButton_Search(Solo solo) {
        // TODO: Would we want to do anything other than click on a button?
        /*
            TODO: This one gets the label from the language properties file
                can I read this from the view? it exists in mText but I can't read it
                can I address this using solo.getView((Object) "Control/Main/Record_Asset")? no, though the ref tags exist
         */
        solo.clickOnButton("Search");
    }

    // Tab
    /*
        File: ui_schema.xml
        Type: Tab
        ref: Asset/Customer
     */
    public static void clickTab_Customer(Solo solo) {
        /*
            TODO: This one doesn't have an ref value, need to be added
            TODO: Click on view would be better
         */

        solo.clickOnText("Customer");
    }

    /*
        File: ui_schema.xml
        Type: Tab
        ref: Asset/Hardware
     */
    public static void clickTab_Hardware(Solo solo) {
        /*
            TODO: This one doesn't have an ref value, need to be added
            TODO: Click on view would be better
         */

        solo.clickOnText("Hardware");
    }

    /*
        File: ui_schema.xml
        Type: Tab
        ref: Control/Main
     */
    public static void clickTab_Main(Solo solo) {
        /*
            TODO: This one doesn't have an ref value, need to be added
            TODO: Click on view would be better
         */

        solo.clickOnText("Main");
    }

    /*
        File: ui_schema.xml
        Type: Tab
        ref: Control/Records
     */
    public static void clickTab_Records(Solo solo) {
        /*
            TODO: This one doesn't have an ref value, need to be added
            TODO: Click on view would be better
         */

        solo.clickOnText("Records");
    }

    // Text field
    /*
        File: ui_schema.xml
        Type: Text field
        ref: Asset/Hardware/Manufacture_Date
     */
    public static EditText getEditText_AssetHardwareManufacture_Date(Solo solo) {
        return (android.widget.EditText) solo.getView((Object)  "Asset/Hardware/Manufacture_Date");
    }

    /*
        File: ui_schema.xml
        Type: Text field
        ref: Asset/Hardware/Owner
     */
    public static EditText getEditText_AssetHardwareOwner(Solo solo) {
        return (android.widget.EditText) solo.getView((Object)  "Asset/Hardware/Owner");
    }

    /*
        File: ui_schema.xml
        Type: Text field
        ref: Asset/Hardware/Make
     */
    public static EditText getEditText_AssetHardwareMake(Solo solo) {
        return (android.widget.EditText) solo.getView((Object)  "Asset/Hardware/Make");
    }

    /*
        File: ui_schema.xml
        Type: Text field
        ref: Asset/Hardware/Model
     */
    public static EditText getEditText_AssetHardwareModel(Solo solo) {
        return (android.widget.EditText) solo.getView((Object)  "Asset/Hardware/Model");
    }

    /*
        File: ui_schema.xml
        Type: Text field
        ref: Asset/Hardware/Serial
     */
    public static EditText getEditText_AssetHardwareSerial(Solo solo) {
        return (android.widget.EditText) solo.getView((Object)  "Asset/Hardware/Serial");
    }

    /*
        File: ui_schema.xml
        Type: Text field
        ref: Control/Search/Search_Term
     */
    public static EditText getEditText_ControlSearchSearch_Term(Solo solo) {
        return (android.widget.EditText) solo.getView((Object) "Control/Search/Search_Term");
    }


}
