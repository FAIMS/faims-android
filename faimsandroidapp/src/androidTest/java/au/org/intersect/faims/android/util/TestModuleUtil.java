package au.org.intersect.faims.android.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Random;

import org.apache.commons.compress.utils.IOUtils;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.res.AssetManager;
import android.os.Environment;
import android.view.View;

import au.org.intersect.faims.android.R;
import au.org.intersect.faims.android.constants.FaimsSettings;
import au.org.intersect.faims.android.ui.activity.ShowModuleActivity;
import au.org.intersect.faims.android.ui.activity.SplashActivity;

import com.google.gson.JsonObject;
import com.robotium.solo.*;

import static android.view.KeyEvent.KEYCODE_MENU;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;


public class TestModuleUtil {
	public static final String CONTINUE_LAST_SESSION = "Continue Last Session";

	public static final String MODULE_CSIRO_GEOCHEMISTRY_SAMPLING = "Geochemistry Sampling";
	public static final String MODULE_PAZC = "PAZC";
	public static final String MODULE_SOL1_HARDWARE = "Sol1 Hardware";

	public static final String SERVER_NAME_TEST1 = "test1.fedarch.org";
	public static final String SERVER_PORT_80 = "80";

	public static final String USER_FAIMS_ADMIN = "Faims Admin";


	public static void createModuleFrom(String name, String key, String dirname, AssetManager assetManager) {
		try {
			String dir = Environment.getExternalStorageDirectory() + FaimsSettings.modulesDir + key;
			File file = new File(dir);
			if (!file.isDirectory())
				file.mkdirs();
			
			copyFile(assetManager.open(dirname + "/data_schema.xml"), new File(dir + "/data_schema.xml"));
			copyFile(assetManager.open(dirname + "/ui_schema.xml"), new File(dir + "/ui_schema.xml"));
			copyFile(assetManager.open(dirname + "/ui_logic.bsh"), new File(dir + "/ui_logic.bsh"));
			copyFile(assetManager.open(dirname + "/db.sqlite"), new File(dir + "/db.sqlite"));
			
			JsonObject object = new JsonObject();
	    	object.addProperty("name", name);
	    	object.addProperty("key", key);
	    	
	    	BufferedWriter writer = new BufferedWriter(new FileWriter(dir + "/module.settings"));
	    	writer.write(object.toString());
	    	writer.flush();
	    	writer.close();
		} catch (IOException e) {
			System.out.println(e);
			e.printStackTrace();
		}
	}
	
	public static void copyFile(InputStream source, File destFile) throws IOException {
		if(!destFile.exists()) {
			destFile.createNewFile();
	    }
		
		OutputStream destination = null;
		
		try {
			destination = new FileOutputStream(destFile);
			IOUtils.copy(source, destination);
		}
		finally {
			if(source != null) {
				source.close();
			}
			if(destination != null) {
				destination.close();
			}
		}
		
	}
	
	public static void recreateActivity(Instrumentation instrumentation, final Activity activity) {
		instrumentation.runOnMainSync(new Runnable() {
			
			@Override
			public void run()
			{
				activity.recreate();
			}
			
		});
	}
	
	public static String getNewModuleName(String baseName) {
		return baseName + "-" + System.currentTimeMillis();
	}

	public static void initCleanTest(Solo solo) {
		solo.assertCurrentActivity("wrong activity", ShowModuleActivity.class);
		solo.waitForDialogToClose();
	}

	public static void roboConnectToServer(Solo solo, String serverHost, String serverPort) {
		roboUseCurrentServer(solo);


		//Click on action bar item
		solo.clickOnActionBarItem(au.org.intersect.faims.android.R.id.faims_server_setting);
		//Wait for activity: 'au.org.intersect.faims.android.ui.activity.ServerSettingsActivity'
		assertTrue("au.org.intersect.faims.android.ui.activity.ServerSettingsActivity is not found!", solo.waitForActivity(au.org.intersect.faims.android.ui.activity.ServerSettingsActivity.class));

		//Set the server host
		View viewHost = solo.getView(R.id.host_field);
		solo.clickOnView(viewHost);
		solo.clearEditText((android.widget.EditText) viewHost);
		solo.enterText((android.widget.EditText) viewHost, serverHost);

		//Set the server port
		View viewPort = solo.getView(R.id.port_field);
		solo.clickOnView(viewPort);
		solo.clearEditText((android.widget.EditText) viewPort);
		solo.enterText((android.widget.EditText) viewPort, serverPort);

		//Click on Connect
		solo.clickOnView(solo.getView(au.org.intersect.faims.android.R.id.connect_to_server));
		//Wait for activity: 'au.org.intersect.faims.android.ui.activity.MainActivity'
		assertTrue("au.org.intersect.faims.android.ui.activity.MainActivity is not found!", solo.waitForActivity(au.org.intersect.faims.android.ui.activity.MainActivity.class));
	}

	public static void roboUseCurrentServer(Solo solo) {
		//Load the app: Wait for activity: 'au.org.intersect.faims.android.ui.activity.SplashActivity'
		solo.waitForActivity(SplashActivity.class, 5000);

		assertFalse(hasGPSTimeError(solo));
		assertFalse(hasGPSTimezoneError(solo));

		//Assert that: 'ImageView' is shown
 		assertTrue("'ImageView' is not shown!", solo.waitForView(solo.getView(android.widget.ImageView.class, 0)));


//		View splashLoad = solo.getCurrentActivity().findViewById(R.id.splash_load);
//		View splashConnect = solo.getView(au.org.intersect.faims.android.R.id.splash_connect_server);

		if (solo.searchButton("Show Modules", true)) {					//Click on Show Modules
			solo.clickOnButton("Show Modules");
//			Wait for activity: 'au.org.intersect.faims.android.ui.activity.MainActivity'
			assertTrue("au.org.intersect.faims.android.ui.activity.MainActivity is not found!", solo.waitForActivity(au.org.intersect.faims.android.ui.activity.MainActivity.class));
		} else if (solo.searchButton("Enter Server Details", true)) {		//Click on Enter Server Details
			solo.clickOnButton("Enter Server Details");
			//Wait for activity: 'au.org.intersect.faims.android.ui.activity.ServerSettingsActivity'
			assertTrue("au.org.intersect.faims.android.ui.activity.ServerSettingsActivity is not found!", solo.waitForActivity(au.org.intersect.faims.android.ui.activity.ServerSettingsActivity.class));
		}
	}

	public static boolean hasGPSTimeError(Solo solo) {
		// Make sure there are no errors about GPS time
		return (solo.searchText("The time from GPS", true) && solo.searchText("does not match your system time", true));
	}

	public static boolean hasGPSTimezoneError(Solo solo) {
		// Make sure there are no errors about GPS time
		return (solo.searchText("Timezone from", true) && solo.searchText("does not match your system timezone", true));
	}

	public static void roboContinueLastSession(Solo solo) {
		//Wait for activity: 'au.org.intersect.faims.android.ui.activity.SplashActivity'
		solo.waitForActivity(au.org.intersect.faims.android.ui.activity.SplashActivity.class, 2000);
		solo.clickOnButton(TestModuleUtil.CONTINUE_LAST_SESSION);
		solo.sleep(2000);
		solo.waitForActivity(SplashActivity.class, 15000);
		waitForModuleLoad(solo);

	}

	public static void roboLoadModule(Solo solo, String moduleName) {
		//Select Module
		solo.clickOnText(moduleName);

		//Click on YES on Load module confirmation (if it exist)
		//Wait for dialog
		solo.waitForDialogToOpen(1500);
		if (solo.searchText("Download Module", true) && solo.searchButton("YES", true)) {
			solo.clickOnButton("YES");
			solo.waitForActivity(SplashActivity.class);
		}

		//Click on Load Module
		solo.clickOnView(solo.getView(au.org.intersect.faims.android.R.id.static_load_module));
		//Wait for activity: 'au.org.intersect.faims.android.ui.activity.ShowModuleActivity'
		solo.waitForActivity(SplashActivity.class, 15000);
		assertTrue("au.org.intersect.faims.android.ui.activity.ShowModuleActivity is not found!", solo.waitForActivity(ShowModuleActivity.class));

		waitForModuleLoad(solo);

		// We assume the module name is part of the app's title bar
		assertTrue("Loaded module name " + moduleName + " not found.", solo.searchText(moduleName));

	}

	private static void waitForModuleLoad(Solo solo) {
		// wait for the loading screen to close
		if (solo.searchText("please wait")) {
			// Debug may die here because loading takes a while when debugging
			assertTrue("Wait for loading screen failed", solo.waitForDialogToClose(60000));
		}

		roboCheckForLogicErrors(solo);
	}

	/*
		Check for logic error after module load
		continue of error is known and valid
	 */
	private static void roboCheckForLogicErrors(Solo solo) {

		int logicError = 0;
		while (solo.searchText("Logic Error", true) && logicError < 10) {
			if (solo.searchButton("OK", true)) {
				if (solo.searchText("Error trying to start internal gps")) {
					assertTrue("Error trying to start internal gps", true);
					solo.clickOnButton("OK");
				} else {
					assertFalse("Unknown Logic Error", true);
					solo.clickOnButton("OK");
				}
			} else {
				assertFalse("Unknown Logic Error", true);
			}
			logicError++;
		}

	}

	public static void roboClickOnKebabMenu(Solo solo) {
		solo.sendKey(KEYCODE_MENU);
	}

	public static void roboClickOnKebabItem(Solo solo, String menuItemName) {
		roboClickOnKebabMenu(solo);
		solo.clickOnText(menuItemName);
	}

	/*
		Random int between 1000 to 2000
	 */
	public static int getRandomRunID() {
		Random rand = new Random();
		return rand.nextInt(2000) + 1000;
	}
}
