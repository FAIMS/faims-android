package au.org.intersect.faims.android.util;

import com.robotium.solo.Solo;

/**
 * Created by Matthew on 16/05/2016.
 */
public abstract class ModuleHelper {
    /*
        Generic button clicking method
     */
    public static void clickOpenDialogButton(Solo solo, String name) {
        // TODO: Would we want to do anything other than click on a button?
        /*
            TODO: This one gets the label from the language properties file
                can I read this from the view? it exists in mText but I can't read it
                can I address this using solo.getView((Object) "Control/Main/Record_Asset")? no, though the ref tags exist
         */

        clickOpenDialogButton(solo, name, 8000);
    }

    public static void clickOpenDialogButton(Solo solo, String name, long timeout) {
        // TODO: Would we want to do anything other than click on a button?
        /*
            TODO: This one gets the label from the language properties file
                can I read this from the view? it exists in mText but I can't read it
                can I address this using solo.getView((Object) "Control/Main/Record_Asset")? no, though the ref tags exist
         */

        solo.clickOnButton(name);
        solo.waitForDialogToOpen(timeout);
    }

    public static void clickCloseDialogButton(Solo solo, String name) {
        // TODO: Would we want to do anything other than click on a button?
        /*
            TODO: This one gets the label from the language properties file
                can I read this from the view? it exists in mText but I can't read it
                can I address this using solo.getView((Object) "Control/Main/Record_Asset")? no, though the ref tags exist
         */

        clickCloseDialogButton(solo, name, 8000);
    }

    public static void clickCloseDialogButton(Solo solo, String name, long timeout) {
        // TODO: Would we want to do anything other than click on a button?
        /*
            TODO: This one gets the label from the language properties file
                can I read this from the view? it exists in mText but I can't read it
                can I address this using solo.getView((Object) "Control/Main/Record_Asset")? no, though the ref tags exist
         */

        solo.clickOnButton(name);
        solo.waitForDialogToClose(8000);
    }

    public static void clickButton(Solo solo, String name) {
        // TODO: Would we want to do anything other than click on a button?
        /*
            TODO: This one gets the label from the language properties file
                can I read this from the view? it exists in mText but I can't read it
                can I address this using solo.getView((Object) "Control/Main/Record_Asset")? no, though the ref tags exist
         */

        solo.clickOnButton(name);
        solo.waitForActivity(au.org.intersect.faims.android.ui.activity.ShowModuleActivity.class);
    }

    /*
        Generic button clicking method
     */
    public static void clickTab(Solo solo, String name) {
        /*
            TODO: This one doesn't have an ref value, need to be added
            TODO: Click on view would be better
         */
        solo.clickOnText(name);
        solo.waitForActivity(au.org.intersect.faims.android.ui.activity.ShowModuleActivity.class);
    }




}
