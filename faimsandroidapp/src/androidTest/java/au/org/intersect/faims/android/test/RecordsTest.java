package au.org.intersect.faims.android.test;

import au.org.intersect.faims.android.ui.activity.SplashActivity;
import au.org.intersect.faims.android.util.TestModuleUtil;
import au.org.intersect.faims.android.util.ModuleSol1HardwareUtil;
import au.org.intersect.faims.android.util.TestRunID;

import com.robotium.solo.*;
import android.test.ActivityInstrumentationTestCase2;
import android.util.Log;
import android.widget.EditText;


public class RecordsTest extends ActivityInstrumentationTestCase2<SplashActivity> {
	private Solo solo;
	private TestRunID runIDInstance = TestRunID.getInstance();

	public RecordsTest() {
		super(SplashActivity.class);
  	}

  	public void setUp() throws Exception {
        super.setUp();
		if (runIDInstance.getRunID().equals("")) {
			runIDInstance.setRunID(TestModuleUtil.getDateRunID() + "_" + TestModuleUtil.getStringRunID());
		}
		Log.d("RunID", runIDInstance.getRunID());
		solo = new Solo(getInstrumentation());
		getActivity();
  	}

	/*
	 	Test: Turn sync on amd off
	 	Requires: FAIMS server and module Sol1 Hardware loaded
	 */
	public void testRun1() {

		// Load app, set server and module
		TestModuleUtil.roboConnectToServer(solo, TestModuleUtil.SERVER_NAME_TEST1, TestModuleUtil.SERVER_PORT_80);
		TestModuleUtil.roboLoadModule(solo, TestModuleUtil.MODULE_SOL1_HARDWARE);

		//Click on Kebab menu (vertical dot dot dot)
		toggleSyncOn();
		turnSyncOff();
		turnSyncOn();

	}

	/*
		Test: Test saving record
	 	Requires: FAIMS server and module Sol1 Hardware loaded
	 */
	public void testRun2() {
		initToLogin();
		saveRecord(1);
		autosaveAndGoBack();
		//	Now check the record
	}

	/*
		Test: Load previously saved record and verify saved data
		Requires: The same runID as testRun2
	 */
	public void testRun3() {
		initToLogin();
		loadRecord(1);	//TODO: This isn't the same runID, need to preserve it between runs
		checkSavedSerial("1001");
	}

	/*
		Test: Test barcode scanner
		Requires: Tablet to setup and pointing at barcode ean-13_0123456790124.png
	 */
	public void testRun4() {
		initToLogin();
		saveRecord(2);
		//Click on Scan Code
		ModuleSol1HardwareUtil.clickButton_Serial_Trigger(solo);
		// Wait for the scanner to load
		solo.sleep(5000);
		//Wait for activity: 'au.org.intersect.faims.android.ui.activity.ShowModuleActivity'
		assertTrue("au.org.intersect.faims.android.ui.activity.ShowModuleActivity is not found!", solo.waitForActivity(au.org.intersect.faims.android.ui.activity.ShowModuleActivity.class));

		// WAit for the scanner to close
		solo.sleep(3000);

		//	This is barcode dependant
		checkSavedSerial("123456790124");
		autosaveAndGoBack();
		loadRecord(2);
		checkSavedSerial("123456790124");
		solo.goBack();	// Just in case the scanner is still open
	}

	/*
		Test: Test saving 50 records
	 	Requires: FAIMS server and module Sol1 Hardware loaded
	 */
	public void testRun5() {
		initToLogin();
		turnSyncOff();

		int recordsToGenerate = 50;
		for (int count = 1; count < recordsToGenerate; count++) {
			// Lets make this range populate from 10 to 60 to be unique in this run
			saveRecord(count + 10);
			autosaveAndGoBack();
		}

		turnSyncOn();
		solo.sleep(10000);
		// TODO: has the sync finished

		loadRecord(11);
		checkSavedSerial("1011");
		autosaveAndGoBack();

		loadRecord(30);
		checkSavedSerial("1030");
		autosaveAndGoBack();

		loadRecord(59);
		checkSavedSerial("1059");
		autosaveAndGoBack();


	}

	/*
		Save a record
	 */
	private void saveRecord(int count) {

		Log.d("Debug Save", "Run ID = " + runIDInstance.getRunID() + ", count = " + count);

		//Click on Empty Text View
		ModuleSol1HardwareUtil.clickButton_Record_Asset(solo);

		//Click on drop down
		solo.clickOnView(ModuleSol1HardwareUtil.get_AssetCustomerCustomer(solo));
		//Click on Sol1
		ModuleSol1HardwareUtil.clickText_Sol1(solo);
		//Click on Hardware
		ModuleSol1HardwareUtil.clickTab_Hardware(solo);

		// Enter the data
		EditText manufactureDate = ModuleSol1HardwareUtil.getEditText_AssetHardwareManufacture_Date(solo);
		TestModuleUtil.editTextField(solo, manufactureDate, "1980");

		EditText owner = ModuleSol1HardwareUtil.getEditText_AssetHardwareOwner(solo);
		TestModuleUtil.editTextField(solo, owner, runIDInstance.getRunID());

		EditText model = ModuleSol1HardwareUtil.getEditText_AssetHardwareModel(solo);
		TestModuleUtil.editTextField(solo, model, "count:" + Integer.toString(count) + ".");

		EditText make = ModuleSol1HardwareUtil.getEditText_AssetHardwareMake(solo);
		TestModuleUtil.editTextField(solo, make, "save");

		EditText serial = ModuleSol1HardwareUtil.getEditText_AssetHardwareSerial(solo);
		TestModuleUtil.editTextField(solo, serial, Integer.toString(count  + 1000));

	}

	private void checkSavedSerial(String serialText) {
		String serial = ModuleSol1HardwareUtil.getEditText_AssetHardwareSerial(solo).getText().toString();
		assertTrue("Serial value " + serial + " doesn't not equal " + serialText + " in record " + runIDInstance.getRunID(), serial.equals(serialText));
	}

	private void loadRecord(int count) {

		Log.d("Debug Load", "Run ID = " + runIDInstance.getRunID() + ", count = " + count);

		// Goto the records tab
		ModuleSol1HardwareUtil.clickTab_Records(solo);
		solo.waitForActivity(au.org.intersect.faims.android.ui.activity.ShowModuleActivity.class);
		int i = 1;
		while (solo.searchText("Options not loaded") && i < 120) {
			solo.sleep(500);
			i++;
		}

		// Search for a field value to find something close
		EditText search = ModuleSol1HardwareUtil.getEditText_ControlSearchSearch_Term(solo);
		TestModuleUtil.editTextField(solo, search, "count:" + count + ".");
		ModuleSol1HardwareUtil.clickButton_Search(solo);
		// TODO: There is a spinner when searching, not sure how we tap in to that
		solo.sleep(1000);
		solo.waitForActivity(au.org.intersect.faims.android.ui.activity.ShowModuleActivity.class, 30000);

		// Load the exact record
		solo.clickOnText(runIDInstance.getRunID() + "'s save count:" + count + ".");
		ModuleSol1HardwareUtil.clickTab_Hardware(solo);
	}

	private void autosaveAndGoBack() {
		// Let autosave have a chance to do it's thing
		solo.sleep(3000);	// TODO: we should be checking for sync state
		solo.goBack();
		assertTrue(true);
	}


	@Override
	public void tearDown() throws Exception {
		solo.finishOpenedActivities();
		super.tearDown();
	}

	private void toggleSyncOn() {
		TestModuleUtil.roboClickOnKebabMenu(solo);
		if (solo.searchText("Disable Sync")) {
			TestModuleUtil.roboClickOnKebabMenu(solo);
			assertTrue("Sync already enabled", true);
		} else if (solo.searchText("Enable Sync")) {
			TestModuleUtil.roboClickOnKebabMenu(solo);
			turnSyncOn();
		}
	}

	private void turnSyncOn() {
		//Click on Turn Sync on
//		TestModuleUtil.roboClickOnKebabMenu(solo);
//		Log.d("Debug Sync on: ", "Enable sync is " + solo.searchText("Enable Sync"));
		TestModuleUtil.roboClickOnKebabItem(solo, "Enable Sync");
//		assertTrue("'Sync enabled' message shown", solo.searchText("Sync enabled"));
		assertTrue("Sync On equals: " + isSyncOn(), isSyncOn());
		//TODO: is sync icon on

	}

	private void turnSyncOff() {
		//Click on Turn Sync on
		TestModuleUtil.roboClickOnKebabItem(solo, "Disable Sync");
		boolean syncDisabled = solo.searchText("Sync disabled");
		if (syncDisabled) {
			Log.d("Debug Sync off", "Disabled sync is " + syncDisabled);
//		} else {
//			assertFalse("Failed to turn sync off (syncDiabled is " + syncDisabled, true);
		}

		if (solo.searchText("Syncing is still in progress. Do you want to") && solo.searchText(" stop the sync?")) {
			Log.d("Debug Sync off", "Disabled sync is " + syncDisabled + " and sync is in progress");
			solo.clickOnButton("Yes");
		}

		assertTrue("Sync Off equals: " + isSyncOff(), isSyncOff());
		//TODO: is sync icon off
	}

	/*
		Checks if the sync is on (cause timing of transient messages screws things up)
	 */
	private boolean isSyncOn() {
		TestModuleUtil.roboClickOnKebabMenu(solo);
		boolean syncOn = solo.searchText("Disable Sync");
		TestModuleUtil.roboClickOnKebabMenu(solo);
		return syncOn;
	}

	/*
		Checks if the sync is off (cause timing of transient messages screws things up)
	 */
	private boolean isSyncOff() {
		TestModuleUtil.roboClickOnKebabMenu(solo);
		boolean syncOff = solo.searchText("Enable Sync");
		TestModuleUtil.roboClickOnKebabMenu(solo);
		return syncOff;
	}

	private void initToLogin() {
		// Load app, start last session
		TestModuleUtil.roboContinueLastSession(solo);
		toggleSyncOn();

		//Click on User List Drop Down
		solo.clickOnView(ModuleSol1HardwareUtil.get_UserUserSelect_User(solo));
		//Click on Faims Admin
		solo.clickOnText(TestModuleUtil.USER_FAIMS_ADMIN);
		ModuleSol1HardwareUtil.clickButton_Login(solo);

	}

}
