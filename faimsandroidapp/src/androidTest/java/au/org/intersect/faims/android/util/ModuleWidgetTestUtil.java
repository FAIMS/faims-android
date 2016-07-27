package au.org.intersect.faims.android.util;

import com.robotium.solo.Solo;

import static junit.framework.Assert.assertTrue;

/**
 * Created by Matthew on 5/05/2016.
 */
public class ModuleWidgetTestUtil extends ModuleHelper {

//    public static final String MODULE_SIGNUP = "Widget Test";

    public static final String BACK_TO_TOP = "Back to top";

    public static final String BONE_ONLY = "Bone Only";

    public static final String INSITBO = "insitbo";
    public static final String SURFBO = "surfbo";

    public static final String INSIT_SURF = "insit&surf";

    // Drop Downs

    // Drop down item

    // Buttons
    /*
        File: ui_schema.xml
        Type: Button
        ref:
     */
    public static void clickButton_Record_Feature(Solo solo) {
        clickButton(solo, "Record Feature");
    }

    // Tab

    // Text field


    /*

     */
    public static void clickBackToTop(Solo solo, String name) {
        clickOnItem(solo, "Back to top");
    }

    /*

     */
    public static void clickBackTo(Solo solo, String name) {
        clickOnItem(solo, "Back to: " + name);
    }

    /*

     */
    public static void clickOnDrillDown(Solo solo, String name) {
        clickOnItem(solo, name + " ...");
    }

    /*

     */
    public static void clickOnItem(Solo solo, String name) {
        ModuleHelper.clickTab(solo, name);
    }

    public static boolean doesItemExist(Solo solo, String name) {
        return (solo.searchText(name));
    }

    public static boolean doesDrillDownExist(Solo solo, String name) {
        return (doesItemExist(solo, name + " ..."));
    }

    public static boolean doesBackToExist(Solo solo, String name) {
        return (doesItemExist(solo, "Back to: " + name));
    }

}
