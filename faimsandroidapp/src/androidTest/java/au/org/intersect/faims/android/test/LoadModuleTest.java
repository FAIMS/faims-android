package au.org.intersect.faims.android.test;

import au.org.intersect.faims.android.ui.activity.SplashActivity;
import com.robotium.solo.*;

import android.test.ActivityInstrumentationTestCase2;

import au.org.intersect.faims.android.util.AppModuleUtil;


public class LoadModuleTest extends ActivityInstrumentationTestCase2<SplashActivity> {
	private Solo solo;
  	
  	public LoadModuleTest() {
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
		AppModuleUtil.roboConnectToServer(solo, AppModuleUtil.SERVER_NAME_TEST1, AppModuleUtil.SERVER_PORT_80);
		AppModuleUtil.roboLoadModule(solo, AppModuleUtil.MODULE_PAZC);
	}

	/*
		Test: Start app and load named module
	 	Requires: Server previously set and module "CSIRO Geochemistry Sampling" loaded on server
	 */
	public void testRun2() {
		AppModuleUtil.roboUseCurrentServer(solo);
		AppModuleUtil.roboLoadModule(solo, AppModuleUtil.MODULE_CSIRO_GEOCHEMISTRY_SAMPLING);
	}

	/*
		Test: Start app and load most recent module
		Requires: Existing server and module "CSIRO Geochemistry Sampling" previously selected
	 */
	public void testRun3() {
		AppModuleUtil.roboContinueLastSession(solo);
	}

	@Override
	public void tearDown() throws Exception {
		solo.finishOpenedActivities();
		super.tearDown();
	}

}
