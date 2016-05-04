package au.org.intersect.faims.android.test;

import au.org.intersect.faims.android.ui.activity.SplashActivity;
import com.robotium.solo.*;

import android.os.StrictMode;
import android.test.ActivityInstrumentationTestCase2;
import au.org.intersect.faims.android.util.TestModuleUtil;



public class loadModuleTest extends ActivityInstrumentationTestCase2<SplashActivity> {
	private Solo solo;
  	
  	public loadModuleTest() {
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
		TestModuleUtil.roboConnectToServer(solo, TestModuleUtil.SERVER_NAME_TEST1, TestModuleUtil.SERVER_PORT_80);
		TestModuleUtil.roboLoadModule(solo, TestModuleUtil.MODULE_PAZC);
	}

	/*
		Test: Start app and load named module
	 	Requires: Server previously set and module "CSIRO Geochemistry Sampling" loaded on server
	 */
	public void testRun2() {
		TestModuleUtil.roboUseCurrentServer(solo);
		TestModuleUtil.roboLoadModule(solo, TestModuleUtil.MODULE_CSIRO_GEOCHEMISTRY_SAMPLING);
	}

	/*
		Test: Start app and load most recent module
		Requires: Existing server and module "CSIRO Geochemistry Sampling" previously selected
	 */
	public void testRun3() {
		TestModuleUtil.roboContinueLastSession(solo);
	}

	@Override
	public void tearDown() throws Exception {
		solo.finishOpenedActivities();
		super.tearDown();
	}

}
