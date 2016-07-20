package au.org.intersect.faims.android.test;

import android.test.ActivityInstrumentationTestCase2;
import android.util.Log;

import com.robotium.solo.Solo;

import au.org.intersect.faims.android.BuildConfig;
import au.org.intersect.faims.android.ui.activity.SplashActivity;
import au.org.intersect.faims.android.util.AppModuleUtil;


public class LoadCommunityModuleTest extends ActivityInstrumentationTestCase2<SplashActivity> {
	private Solo solo;

  	public LoadCommunityModuleTest() {
		super(SplashActivity.class);
  	}

  	public void setUp() throws Exception {
        super.setUp();
		solo = new Solo(getInstrumentation(), getActivity());
  	}

	/*
		Test: Start app, set server and module
		Requires: FAIMS server and module PAZC loaded
	 */
	public void testRun1() {
		initCommunityModule(false);
	}

	/*
		Test: Start app and load named module
	 	Requires: Server previously set and module "CSIRO Geochemistry Sampling" loaded on server
	 */
//	public void testRun2() {
//		AppModuleUtil.roboUseCurrentServer(solo);
//		AppModuleUtil.roboLoadModule(solo, AppModuleUtil.MODULE_CSIRO_GEOCHEMISTRY_SAMPLING);
//	}

	private void initCommunityModule(Boolean doGridCheck) {
		//Load the app: Wait for activity: 'au.org.intersect.faims.android.ui.activity.SplashActivity'
		solo.waitForActivity(SplashActivity.class, 5000);

		assertFalse(AppModuleUtil.hasGPSTimeError(solo));
		assertFalse(AppModuleUtil.hasGPSTimezoneError(solo));

		Log.d("Loading Module", "check");
		if (solo.searchText("Loading module")) {
			// wait for the loading screen to close
			// Debug may die here because loading takes a while when debugging
			Log.d("Loading Module", "true");
			if (solo.waitForDialogToClose(300000)) {
				assertTrue("Wait for loading screen failed", true);
			}
		}

		AppModuleUtil.roboCheckForLogicErrors(solo);
		if (doGridCheck) {
			AppModuleUtil.roboCheckInitGridMessage(solo);        // This is only needed on modules like PACZ with grids.
		}

		assertTrue("Loaded module name " + BuildConfig.COMMUNITY_APPNAME + " not found.", solo.searchText(BuildConfig.COMMUNITY_APPNAME));
	}

	@Override
	public void tearDown() throws Exception {
		solo.finishOpenedActivities();
		super.tearDown();
	}

}
