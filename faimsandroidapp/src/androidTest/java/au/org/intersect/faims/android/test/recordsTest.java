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
	 	Test: Turn sync on (shame it doesn't remember this)
	 */
	public void testRun1() {
		// Load app, set server and module
		TestModuleUtil.roboConnectToServer(solo, "test1.fedarch.org", "80");
		TestModuleUtil.roboLoadModule(solo, "Saving and Loading");

		//Click on Kebab menu (vertical dot dot dot)
		turnSyncOn();


	}

	/*
		Test: Test saving record
	 */
	public void testRun2() {
		initSaveAndLoadEntity();

		//Click on Empty Text View
		solo.clickOnView(solo.getView(android.widget.EditText.class, 0));
		//Enter the text: 'Test1'
		solo.clearEditText((android.widget.EditText) solo.getView(android.widget.EditText.class, 0));
		solo.enterText((android.widget.EditText) solo.getView(android.widget.EditText.class, 0), "Test 2");

		//Click on Save
		solo.clickOnButton("Save");
		assertTrue(true);	//TODO: how do we know the data was saved?

	}

	/*
		Test: Test barcode scanner
		Requires: Tablet to setup and pointing at barcode ean-13_0123456790124.png
	 */
	public void testRun3() {
		initSaveAndLoadEntity();

		//Click on Empty Text View
		solo.clickOnView(solo.getView(android.widget.EditText.class, 0));
		//Enter the text: 'Test1'
		solo.clearEditText((android.widget.EditText) solo.getView(android.widget.EditText.class, 0));
		solo.enterText((android.widget.EditText) solo.getView(android.widget.EditText.class, 0), "Test 3");

		//Click on Scan Code
		solo.clickOnButton("Scan Code");

		//Wait for activity: 'au.org.intersect.faims.android.ui.activity.ShowModuleActivity'
		assertTrue("au.org.intersect.faims.android.ui.activity.ShowModuleActivity is not found!", solo.waitForActivity(au.org.intersect.faims.android.ui.activity.ShowModuleActivity.class));

		//	This is barcode dependant
		assertTrue(solo.searchText("123456790124"));

		//Click on Save
		solo.clickOnButton("Save");

		assertTrue(true);

	}

	/*
		Test: Load previously saved record and verify saved data
	 */
	public void testRun4() {
		initSaveAndLoadEntity();

		//Click on Load Entity
		solo.clickOnText("Load Entity");
		//Click on Small 1
		solo.clickOnText("Small 1");
		//Assert that: 'Description:' is shown
		assertTrue("'Description:' is not shown!", solo.waitForText(java.util.regex.Pattern.quote("Description:"), 1, 20000, true, true));

		assertTrue(solo.searchText("Test 2"));
		//Click on Empty Text View
		solo.clickOnView(solo.getView(android.widget.EditText.class, 1));
		//Enter the text: 'description'
		solo.clearEditText((android.widget.EditText) solo.getView(android.widget.EditText.class, 1));
		solo.enterText((android.widget.EditText) solo.getView(android.widget.EditText.class, 1), "description");
		//Click on Save
		solo.clickOnText(java.util.regex.Pattern.quote("Save"));


	}


	@Override
	public void tearDown() throws Exception {
		solo.finishOpenedActivities();
		super.tearDown();
	}

	private void turnSyncOn() {
		//Click on Turn Sync on
		TestModuleUtil.roboClickOnKebabItem(solo, "Sync On");	// TODO: Addressing action bar items by name would be nice
//		solo.clickOnActionBarItem(au.org.intersect.faims.android.R.id.menu);
//		solo.clickInList(1, 1);
		assertTrue(true); //TODO: sync on check pls
	}

	private void initToLogin() {
		// Load app, start last session
		TestModuleUtil.roboContinueLastSession(solo);
		turnSyncOn();

		//Click on Faims Admin
		solo.clickOnText("Faims Admin");
	}

	private void initSaveAndLoadEntity() {
		initToLogin();

		//Click on Save and Load Entity
		solo.clickOnText("Save and Load Entity");
	}


}
