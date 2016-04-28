package au.org.intersect.faims.android.test;

import au.org.intersect.faims.android.ui.activity.SplashActivity;
import au.org.intersect.faims.android.util.TestModuleUtil;

import com.robotium.solo.*;
import android.test.ActivityInstrumentationTestCase2;


public class recordsTest extends ActivityInstrumentationTestCase2<SplashActivity> {
  	private Solo solo;
  	
  	public recordsTest() {
		super(SplashActivity.class);
  	}

  	public void setUp() throws Exception {
        super.setUp();
		solo = new Solo(getInstrumentation());
		getActivity();
  	}

	/*
	 turn sync on (shame it doesn't remember this)
	 */
	public void testRun1() {
		// Load app, set server and module
		TestModuleUtil.roboConnectToServer(solo, "test1.fedarch.org", "80");
		TestModuleUtil.roboLoadModule(solo, "Saving and Loading");

		//Click on Kebab menu (vertical dot dot dot)

		//Click on Turn Sync on
		solo.clickOnActionBarItem(0);	// TODO: Addressing action bar items by name would be nice
//		solo.clickOnActionBarItem(au.org.intersect.faims.android.R.id.menu);
//		solo.clickInList(1, 1);
		assertTrue(true); //TODO: sync on check pls

	}

	public void testRun2() {
		// Load app, start last session
		TestModuleUtil.roboContinueLastSession(solo);

		//Click on Turn Sync on
		solo.clickOnActionBarItem(0);

        //Click on Faims Admin
		solo.clickOnText("Faims Admin");

        //Click on Save and Load Entity
//		solo.clickOnView(solo.getView(android.R.id.text1));
		solo.clickOnText("Save and Load Entity");

		//Click on Empty Text View
		solo.clickOnView(solo.getView(android.widget.EditText.class, 0));
        //Enter the text: 'Test1'
		solo.clearEditText((android.widget.EditText) solo.getView(android.widget.EditText.class, 0));
		solo.enterText((android.widget.EditText) solo.getView(android.widget.EditText.class, 0), "Test1");



        //Click on Scan Code
		solo.clickOnButton("Scan Code");

		//Wait for activity: 'au.org.intersect.faims.android.ui.activity.ShowModuleActivity'
		assertTrue("au.org.intersect.faims.android.ui.activity.ShowModuleActivity is not found!", solo.waitForActivity(au.org.intersect.faims.android.ui.activity.ShowModuleActivity.class));

		//	This is barcode dependant
		// TODO: Need to have a static barcode, gif in repo most likely
		assertTrue(solo.searchText("8806086322614"));

        //Click on Save
		solo.clickOnButton("Save");

		assertTrue(true);

	}

	@Override
	public void tearDown() throws Exception {
		solo.finishOpenedActivities();
		super.tearDown();
	}

}
