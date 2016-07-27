package au.org.intersect.faims.android.test;

import android.test.ActivityInstrumentationTestCase2;
import android.util.Log;

import com.robotium.solo.Solo;

import au.org.intersect.faims.android.ui.activity.SplashActivity;
import au.org.intersect.faims.android.util.AppModuleUtil;
import au.org.intersect.faims.android.util.AppSignupUtil;
import au.org.intersect.faims.android.util.ModuleSol1HardwareUtil;
import au.org.intersect.faims.android.util.TestRunID;


public class SignupTest extends ActivityInstrumentationTestCase2<SplashActivity> {
	public static final String PASSWORD = "aB!45678901234567890";	// 20 chars, uppper, lower, num and symbol
	public static final String BAD_PASSWORD = "wabbit";

	private Solo solo;
	private TestRunID runIDInstance = TestRunID.getInstance();

  	public SignupTest() {
		super(SplashActivity.class);
  	}

  	public void setUp() throws Exception {
        super.setUp();
		if (runIDInstance.getRunID().equals("")) {
			runIDInstance.setRunID(AppModuleUtil.getDateRunID() + "." + AppModuleUtil.getStringRunID());
		}
		Log.d("RunID", runIDInstance.getRunID());
		solo = new Solo(getInstrumentation());
		getActivity();
	}
  
	/*
		Test: Login to existing web created user
		Requires: FAIMS server with Sol1 Hardware module with sign up enabled
	 */
	public void testRun1() {
		AppModuleUtil.roboConnectToServer(solo, AppModuleUtil.SERVER_NAME_TEST1, AppModuleUtil.SERVER_PORT_80);
		AppModuleUtil.roboLoadModule(solo, AppModuleUtil.MODULE_SIGN_UP);
//		AppModuleUtil.roboConnectToServer(solo, AppModuleUtil.SERVER_NAME_TEST1, AppModuleUtil.SERVER_PORT_80);
//		AppModuleUtil.roboLoadModule(solo, "Sol1 Hardware Sign In");

		//Click on User List Drop Down
		solo.clickOnView(ModuleSol1HardwareUtil.get_UserUserSelect_User(solo));
		solo.sleep(1000);
		//Click on Faims Admin
		solo.clickOnText("Test Password");
		ModuleSol1HardwareUtil.clickButton_Login(solo);
		AppModuleUtil.editTextField(solo, AppSignupUtil.getEditText_LoginPassword(solo), PASSWORD);
		AppSignupUtil.clickButton_LoginOk(solo);
		assertFalse("Password is incorrect", solo.searchText("Password incorrect", true));
		assertTrue("Didn't load module", solo.searchButton("Record Asset", true));
	}

	/*
		Test: Successful Sign up
		Requires: FAIMS server with Sol1 Hardware module with sign up enabled
	 */
	public void testRun2() {
		// Load app, start last session
		AppModuleUtil.roboContinueLastSession(solo);

		AppSignupUtil.clickButton_Signup(solo);
		setSignupValues(runIDInstance.getRunID(), "test2", runIDInstance.getRunID() + "_test2@example.com", PASSWORD, PASSWORD);
		AppSignupUtil.clickButton_SignupOk(solo);
		solo.sleep(1000); // Give the server time to save the user
		assertFalse("Unable to contact server", solo.searchText("Unable to contact server"));

		solo.clickOnView(ModuleSol1HardwareUtil.get_UserUserSelect_User(solo));
		assertTrue(solo.searchText(runIDInstance.getRunID() + " test2"));
	}

	/*
		Test: Login after successful sign up
		Requires: Successful testRun2
	 */
	public void testRun3() {
		// Load app, start last session
		AppModuleUtil.roboContinueLastSession(solo);

		//Click on User List Drop Down
		solo.clickOnView(ModuleSol1HardwareUtil.get_UserUserSelect_User(solo));
		solo.clickOnText(runIDInstance.getRunID() + " test2");

		ModuleSol1HardwareUtil.clickButton_Login(solo);
		AppModuleUtil.editTextField(solo, AppSignupUtil.getEditText_LoginPassword(solo), PASSWORD);
		AppSignupUtil.clickButton_LoginOk(solo);
		assertTrue(solo.searchButton("Record Asset", true));
	}

	/*
		Test: Login fails with wrong password sign up
		Requires: Successful testRun2
	 */
	public void testRun4() {
		// Load app, start last session
		AppModuleUtil.roboContinueLastSession(solo);
		//Click on User List Drop Down
		solo.clickOnView(ModuleSol1HardwareUtil.get_UserUserSelect_User(solo));
		solo.clickOnText(runIDInstance.getRunID() + " test2");
		solo.sleep(500);

		ModuleSol1HardwareUtil.clickButton_Login(solo);
		AppModuleUtil.editTextField(solo, AppSignupUtil.getEditText_LoginPassword(solo), "wabbit");
		AppSignupUtil.clickButton_LoginOk(solo);
		assertTrue(solo.searchText("Password incorrect"));
	}

	/*
    Test: Sign up validation
    Requires: FAIMS server with Sol1 Hardware module with sign up enabled
	 */
	public void testRun5() {
		// Load app, start last session
		AppModuleUtil.roboContinueLastSession(solo);
		String testID = runIDInstance.getRunID() + " test5";

		AppSignupUtil.clickButton_Signup(solo);

		// Validate firstname
		setSignupValues("", testID, runIDInstance.getRunID() + "_test5@example.com", PASSWORD, PASSWORD);
		AppSignupUtil.clickButton_SignupOk(solo);
		assertTrue("Sign up validation on first name failed", solo.searchText("First name can not be empty"));

		// Validate lastname
		setSignupValues(testID, "", runIDInstance.getRunID() + "_test5@example.com", PASSWORD, PASSWORD);
		AppSignupUtil.clickButton_SignupOk(solo);
		assertTrue("Sign up validation on last name failed", solo.searchText("Last name can not be empty"));

		// Validate email
		setSignupValues(testID, testID, "", PASSWORD, PASSWORD);
		AppSignupUtil.clickButton_SignupOk(solo);
		assertTrue("Sign up validation on email failed", solo.searchText("Email does not appear to be valid"));

		// Validate matching password
		setSignupValues(testID, testID, runIDInstance.getRunID() + "_test2@example.com", PASSWORD, BAD_PASSWORD);
		AppSignupUtil.clickButton_SignupOk(solo);
		assertTrue("Sign up validation on password match failed", solo.searchText("Passwords don't match"));

		// Validate empty password
		setSignupValues(testID, testID, runIDInstance.getRunID() + "_test2@example.com", "", "");
		AppSignupUtil.clickButton_SignupOk(solo);
		assertTrue("Sign up validation on password empty failed", solo.searchText("Password can not be empty"));

		AppSignupUtil.clickButton_SignupCancel(solo);
		solo.clickOnView(ModuleSol1HardwareUtil.get_UserUserSelect_User(solo));
		assertFalse("ID " + testID + " found in user list. It shouldn't exist", solo.searchText(testID));

	}

	/*
    Test: Bad password validation
    Requires: FAIMS server with Sol1 Hardware module with sign up enabled
	 */
	public void testRun6() {
		// Load app, start last session
		AppModuleUtil.roboContinueLastSession(solo);
		String testID = runIDInstance.getRunID() + " test6";

		AppSignupUtil.clickButton_Signup(solo);

		// Validate bad password
		setSignupValues(testID, testID, runIDInstance.getRunID() + "_test6@example.com", BAD_PASSWORD, BAD_PASSWORD);
		AppSignupUtil.clickButton_SignupOk(solo);
		solo.sleep(1000);
		// The text changes depending on how the password is incorrect, seems to be a consistant snippet
		assertTrue("Sign up validation on password empty failed", solo.searchText("ust be between 6 and 20 characters"));
		AppSignupUtil.clickButton_ErrorOk(solo);


		solo.clickOnView(ModuleSol1HardwareUtil.get_UserUserSelect_User(solo));
		assertFalse("ID " + testID + " found in user list. It shouldn't exist", solo.searchText(testID));

	}

	/*
		Test: Offline Sign up (not allowed)
		Requires: FAIMS server with Sol1 Hardware module with sign up enabled
	 */
	public void testRun7() {
		// Load app, start last session
		AppModuleUtil.roboContinueLastSession(solo);

		// Turn wifi off for this test
		solo.setWiFiData(false);

		AppSignupUtil.clickButton_Signup(solo);
		setSignupValues(runIDInstance.getRunID(), "test7", runIDInstance.getRunID() + "_test2@example.com", PASSWORD, PASSWORD);
		AppSignupUtil.clickButton_SignupOk(solo);

		assertTrue("Sign up validation on offline server failed", solo.searchText("Please check your network connection and try again. Contact an administrator if the problem persists.",true));
		AppSignupUtil.clickButton_ErrorOk(solo);

		solo.setWiFiData(true);

		solo.clickOnView(ModuleSol1HardwareUtil.get_UserUserSelect_User(solo));
		assertFalse("Found user created when app offline", solo.searchText(runIDInstance.getRunID() + " test6"));
	}


	// Convince for filling in the form
	private void setSignupValues(String firstname, String lastname, String email, String password, String passwordConfirmation) {
		AppModuleUtil.editTextField(solo, AppSignupUtil.getEditText_FirstName(solo), firstname);
		AppModuleUtil.editTextField(solo, AppSignupUtil.getEditText_LastName(solo), lastname);
		AppModuleUtil.editTextField(solo, AppSignupUtil.getEditText_Email(solo), email);
		AppModuleUtil.editTextField(solo, AppSignupUtil.getEditText_Password(solo), password);
		AppModuleUtil.editTextField(solo, AppSignupUtil.getEditText_PasswordConfirmation(solo), passwordConfirmation);
	}

	@Override
	public void tearDown() throws Exception {
		solo.finishOpenedActivities();
		super.tearDown();
	}
}
