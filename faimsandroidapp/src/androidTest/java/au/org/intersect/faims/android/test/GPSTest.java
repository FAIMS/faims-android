package au.org.intersect.faims.android.test;

import au.org.intersect.faims.android.ui.activity.SplashActivity;
import au.org.intersect.faims.android.util.AppModuleUtil;
import au.org.intersect.faims.android.util.ClockskewCheckUtil;
import au.org.intersect.faims.android.util.MockLocationProvider;

import android.location.LocationManager;

import com.robotium.solo.*;
import android.test.ActivityInstrumentationTestCase2;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;


public class GPSTest extends ActivityInstrumentationTestCase2<SplashActivity> {
  	private Solo solo;
  	private MockLocationProvider locationNetwork;
	private MockLocationProvider locationGPS;

  	public GPSTest() {
		super(SplashActivity.class);
  	}

  	public void setUp() throws Exception {
        super.setUp();
		solo = new Solo(getInstrumentation());
		getActivity();
	}
  
	/*
		Test: Does the GPS time error appear if the gps time and system time is mismatched
		Requires:
	 */

	/* 	
	public void testRun1() {
		solo.waitForActivity(SplashActivity.class, 5000);

		ClockskewCheckUtil.checkTime(makeMockNEMA());

		int i = 0;
		boolean hasGPSError = false;
		while (i < 10 && ! hasGPSError) {
			if (AppModuleUtil.hasGPSTimeError(solo)) {
				hasGPSError = true;
				assertTrue("GPS time is out", true);
			} else {
				solo.sleep(1000);
			}
			i++;
		}

		assertTrue("GPS time error is " + hasGPSError, hasGPSError);
	}
 */

	/*
		Test: Does the GPS timezone error appear if the gps and system timezone is mismatched
		Requires:
	 */
	public void testRun2() {
		solo.waitForActivity(SplashActivity.class, 5000);

		setMockGPS();

		int i = 0;
		boolean hasGPSError = false;
		while (i < 10 && ! hasGPSError) {
			if (AppModuleUtil.hasGPSTimezoneError(solo)) {
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

	/*
		Test: Does the GPS turn on and off
		Requires:
	 */
	public void testRun3() {
		AppModuleUtil.roboConnectToServer(solo, AppModuleUtil.SERVER_NAME_TEST1, AppModuleUtil.SERVER_PORT_80);
		AppModuleUtil.roboLoadModule(solo, AppModuleUtil.MODULE_SOL1_HARDWARE);

		AppModuleUtil.roboClickOnKebabItem(solo, "Enable Internal GPS");
		assertTrue(solo.searchText("Internal GPS enabled"));
		// TODO: Can we tell that it is actually on?
		solo.sleep(1500);

		AppModuleUtil.roboClickOnKebabItem(solo, "Disable Internal GPS");
		assertTrue(solo.searchText("Internal GPS disabled"));
		solo.sleep(1500);

		AppModuleUtil.roboClickOnKebabItem(solo, "Enable External GPS");
		assertTrue(solo.searchText("Please enable bluetooth"));
		solo.sleep(1500);

	}

	/*
		Set Mock GPS location
	 */
	private void setMockGPS() {
		// South Georgia/Sandwich Is (GMT -2:00)
		double lat = -54.17;
		double lon = -36.30;

		locationGPS = new MockLocationProvider(LocationManager.GPS_PROVIDER, getActivity().getApplicationContext());
		locationNetwork = new MockLocationProvider(LocationManager.NETWORK_PROVIDER, getActivity().getApplicationContext());
		locationGPS.pushLocation(lat, lon);
		locationNetwork.pushLocation(lat, lon);
	}

	/*
		Fake a NEMA string 3 hours before now amd call the time check
		sample sting: $GPGGA,183730,3907.356,N,12102.482,W,1,05,1.6,646.4,M,-24.1,M,,*75
	 */
	private String makeMockNEMA() {
		TimeZone timeZone = TimeZone.getTimeZone("UTC");
		Calendar cal = Calendar.getInstance(timeZone);
//		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EE MMM dd HH:mm:ss zzz yyyy", Locale.US);
//		simpleDateFormat.setTimeZone(timeZone);
		cal.add(Calendar.HOUR, -3);

		SimpleDateFormat gpsTimeFormat = new SimpleDateFormat("HHmmss", Locale.US);
		gpsTimeFormat.setTimeZone(timeZone);
		String fakeTime = gpsTimeFormat.format(cal.getTime());
		return "$GPGGA," + fakeTime + ",3907.356,N,12102.482,W,1,05,1.6,646.4,M,-24.1,M,,*75";
	}

	@Override
	public void tearDown() throws Exception {
		solo.finishOpenedActivities();
		super.tearDown();
	}
}
