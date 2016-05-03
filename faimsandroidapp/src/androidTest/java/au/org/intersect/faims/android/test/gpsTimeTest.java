package au.org.intersect.faims.android.test;

import au.org.intersect.faims.android.ui.activity.SplashActivity;
import au.org.intersect.faims.android.util.TestModuleUtil;
import au.org.intersect.faims.android.util.MockLocationProvider;

import android.location.LocationManager;

import com.robotium.solo.*;
import android.test.ActivityInstrumentationTestCase2;
import android.view.KeyEvent;

import static android.view.KeyEvent.*;


public class gpsTimeTest extends ActivityInstrumentationTestCase2<SplashActivity> {
  	private Solo solo;
  	private MockLocationProvider locationNetwork;
	private MockLocationProvider locationGPS;

  	public gpsTimeTest() {
		super(SplashActivity.class);
  	}

  	public void setUp() throws Exception {
        super.setUp();
		solo = new Solo(getInstrumentation());
		getActivity();

		locationGPS = new MockLocationProvider(LocationManager.GPS_PROVIDER, getActivity().getApplicationContext());
		locationNetwork = new MockLocationProvider(LocationManager.NETWORK_PROVIDER, getActivity().getApplicationContext());
		locationGPS.pushLocation(-12.34, 23.45);
		locationNetwork.pushLocation(-12.34, 23.45);
  	}
  
	/*
		Test: Does the GPS time error appear if the time is wrong
		Requires: Tablet to have the wrong time set
	 */
	public void testRun1() {
		TestModuleUtil.roboConnectToServer(solo, "test1.fedarch.org", "80");
		TestModuleUtil.roboLoadModule(solo, "PAZC");

		TestModuleUtil.roboClickOnKebabItem(solo, "Enable Internal GPS");
//		assertTrue(solo.searchText("Internal GPS Enabled"));


		int i = 0;
		boolean hasGPSError = false;
		while (i < 60 && ! hasGPSError) {
			if (TestModuleUtil.hasGPSTimeError(solo)) {
				hasGPSError = true;
				assertTrue("GPS time is out", true);
			} else {
				solo.sleep(1000);
			}
			i++;
		}

		assertTrue("GPS time error is " + hasGPSError, hasGPSError);
	}

	/*
		Test: Does the GPS timezone error appear
		Requires: Tablet to have the wrong timezone set and testRun1 to have run
	 */
	public void testRun2() {
		// TODO: Change the time to the correct time, make sure the timezone is wrong
		TestModuleUtil.roboContinueLastSession(solo);

		TestModuleUtil.roboClickOnKebabItem(solo, "Enable Internal GPS");
//		assertTrue(solo.searchText("Internal GPS Enabled"));

		int i = 0;
		boolean hasGPSError = false;
		while (i < 60 && ! hasGPSError) {
			if (TestModuleUtil.hasGPSTimezoneError(solo)) {
				hasGPSError = true;
				assertTrue("GPS timezone is out", true);
			} else {
				solo.sleep(1000);
			}
			i++;
		}

		assertTrue("GPS timezone error is " + hasGPSError, hasGPSError);

		solo.sleep(10000);

	}

	@Override
	public void tearDown() throws Exception {
		solo.finishOpenedActivities();
		super.tearDown();
	}
}
