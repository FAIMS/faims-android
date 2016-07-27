package au.org.intersect.faims.android.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

import org.apache.commons.compress.utils.IOUtils;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.res.AssetManager;
import android.os.Environment;
import android.view.View;
import android.widget.EditText;

import au.org.intersect.faims.android.R;
import au.org.intersect.faims.android.constants.FaimsSettings;
import au.org.intersect.faims.android.ui.activity.ShowModuleActivity;
import au.org.intersect.faims.android.ui.activity.SplashActivity;

import com.google.gson.JsonObject;
import com.robotium.solo.*;

import static android.view.KeyEvent.KEYCODE_MENU;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;


public class AppModuleUtil {
	public static final String CONTINUE_LAST_SESSION = "Continue Last Session";

	public static final String SERVER_NAME_TEST1 = "test1.fedarch.org";
	public static final String SERVER_PORT_80 = "80";

	public static final String MODULE_CSIRO_GEOCHEMISTRY_SAMPLING = "CSIRO Geochemistry Sampling";
	public static final String MODULE_PAZC = "PAZC";
	public static final String MODULE_SIGN_UP = "Sign Up";
	public static final String MODULE_WIDGET_TEST = "Widget Test";
	public static final String MODULE_SOL1_HARDWARE = "Sol1 Hardware";

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


		View showModules = solo.getView(R.id.splash_load);
		View enterServerDetails = solo.getView(R.id.splash_connect_server);
		if (showModules.getVisibility() == View.VISIBLE) {
			solo.clickOnView(showModules); 			//Click on Show Modules
			assertTrue("au.org.intersect.faims.android.ui.activity.MainActivity is not found!", solo.waitForActivity(au.org.intersect.faims.android.ui.activity.MainActivity.class));
		} else if (enterServerDetails.getVisibility() == View.VISIBLE) {
			solo.clickOnView(enterServerDetails); 	//Click on Enter Server Details
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
		solo.clickOnButton(AppModuleUtil.CONTINUE_LAST_SESSION);
		solo.sleep(2000);
		solo.waitForActivity(SplashActivity.class, 15000);
		waitForModuleLoad(solo);

	}

	public static void roboLoadModule(Solo solo, String moduleName) {
		//Select Module
		solo.clickOnText(moduleName);
		waitForModuleLoadDownload(solo);

		//Click on Load Module
		solo.clickOnView(solo.getView(au.org.intersect.faims.android.R.id.static_load_module));
		//Wait for activity: 'au.org.intersect.faims.android.ui.activity.ShowModuleActivity'
		solo.waitForActivity(SplashActivity.class, 15000);
		assertTrue("au.org.intersect.faims.android.ui.activity.ShowModuleActivity is not found!", solo.waitForActivity(ShowModuleActivity.class));

		waitForModuleLoad(solo);

		// We assume the module name is part of the app's title bar
		assertTrue("Loaded module name " + moduleName + " not found.", solo.searchText(moduleName));

	}

	/*
		Waits for a downloaded module to load
	 */
	private static void waitForModuleLoad(Solo solo) {
		waitForModuleLoad(solo, 120000);
	}

	/*
		Waits for a module to download, can take a very long time
	 */
	private static void waitForModuleLoadDownload(Solo solo) {
		//Click on YES on Load module confirmation (if it exist)
		//Wait for dialog
		solo.waitForDialogToOpen(1500);
		if (solo.searchText("Download Module", true) && solo.searchButton("YES", true)) {
			solo.clickOnButton("YES");
			solo.waitForActivity(SplashActivity.class);
		}
		waitForModuleLoad(solo, 300000, false);
	}

	private static void waitForModuleLoad(Solo solo, long timeout) {
		waitForModuleLoad(solo, timeout, true);
	}

	private static void waitForModuleLoad(Solo solo, long timeout, boolean checkForMessages) {
		solo.sleep(2000);
		solo.waitForActivity(ShowModuleActivity.class);
		if (solo.searchText("please wait")) {
			// wait for the loading screen to close
			// Debug may die here because loading takes a while when debugging
			assertTrue("Wait for loading screen failed", solo.waitForDialogToClose(timeout));
		}

		if (checkForMessages){
			roboCheckForLogicErrors(solo);
			roboCheckInitGridMessage(solo);
		}
	}

	/*
		Check for logic error after module load
		continue of error is known and valid
	 */
	public static void roboCheckForLogicErrors(Solo solo) {

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

	public static void roboCheckInitGridMessage(Solo solo) {
		int count = 1;
		while ( count < 20 ) {
			count++;
			if (solo.searchText("You have not yet initialised the grid.") ) {
				ModuleWidgetTestUtil.clickCloseDialogButton(solo, "OK");
				count = 20;
			}
		}
	}


	public static void roboClickOnKebabMenu(Solo solo) {
		solo.sendKey(KEYCODE_MENU);
		solo.sleep(500);
	}

	public static void roboClickOnKebabItem(Solo solo, String menuItemName) {
		roboClickOnKebabMenu(solo);
		solo.clickOnText(menuItemName);
		solo.sleep(500);
	}


	public static void editTextField(Solo solo, EditText field, String text) {
		solo.clearEditText(field);
		solo.enterText(field, text);
		solo.sleep(200);
	}


	/*
		Random int between 1000 to 2000
	 */
	public static int getRandomRunID() {
		Random rand = new Random();
		return rand.nextInt(2000) + 1000;
	}

	public static String getStringRunID() {
		Random r = new Random();
		char c;
		String s = "";
		for (int i = 0; i < 2; i++) {
			c = (char) (r.nextInt(26) + 'a');
			s += c;
		}
		return s;
	}

	/*
		Day of the year + hour and minuite
	 */
	public static String getDateRunID() {
		return new SimpleDateFormat("DDD_HHmm", Locale.US).format(new Date());
	}
}
