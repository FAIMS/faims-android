package au.org.intersect.faims.android.ui.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.GpsStatus;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.Button;

import au.org.intersect.faims.android.BuildConfig;
import au.org.intersect.faims.android.R;
import au.org.intersect.faims.android.app.FAIMSApplication;
import au.org.intersect.faims.android.util.ClockskewCheckUtil;
import au.org.intersect.faims.android.util.ModuleUtil;
import android.location.LocationManager;
import au.org.intersect.faims.android.util.TimezoneCheckUtil;

public class SplashActivity extends Activity {

	@SuppressLint("ClickableViewAccessibility")
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.splashscreen);
	    
	    FAIMSApplication.getInstance().setApplication(getApplication());
	    Log.d("FAIMS","Loading...");
		LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		if (null == lm.getProvider("gps")) {
			Log.d("FAIMS", "No gps found");
		} else {
			if (lm.addNmeaListener(new ClockskewCheckUtil())) {
				Log.d("FAIMS", "NMEA Listener added successfully");
			} else {
				Log.d("FAIMS", "NMEA Listener failed");
			}
		}

        if (!TimezoneCheckUtil.isActioned()) {
            lm.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER, 1l, 1l, new TimezoneCheckUtil(this));
			if (null != lm.getProvider("gps")) {
				lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1l, 1l, new TimezoneCheckUtil(this));
			}
        }

        WebView attribution = (WebView) findViewById(R.id.splashscreen_attribution);
	    attribution.loadUrl("file:///android_asset/attribution.html");
	    attribution.setLongClickable(false);
	    attribution.setOnTouchListener(new View.OnTouchListener() {
			// TODO
			// modify attribution.html to include links for both intersect attribution policy and sol1's homepage,
			// handle clicks on each link here to open new browser window to appropriate urls
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if(event.getAction() == MotionEvent.ACTION_UP) {
					Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.intersect.org.au/attribution-policy"));
					SplashActivity.this.startActivity(browserIntent);
				}
				return true;
			}
		});
		Log.d("BConfig","server: " + BuildConfig.COMMUNITY_SERVER);
		Log.d("BConfig","server: " + BuildConfig.COMMUNITY_MODULE);
	    updateButtons();
	}
	

    @Override
    protected void onResume() {
    	super.onResume();
    	updateButtons();
    }


	@Override
	public void onBackPressed() {
		Intent intent = new Intent(Intent.ACTION_MAIN);
		intent.addCategory(Intent.CATEGORY_HOME);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(intent);
	}


	private void updateButtons() {
		Button connectDemo = (Button) findViewById(R.id.splash_connect_demo);
		Button connectServer = (Button) findViewById(R.id.splash_connect_server);
		Button loadModule = (Button) findViewById(R.id.splash_load);
		connectDemo.setVisibility(View.GONE);
		connectServer.setVisibility(View.GONE);
		loadModule.setVisibility(View.GONE);
//		if ((ModuleUtil.getModules() == null || ModuleUtil.getModules().isEmpty()) && null != BuildConfig.COMMUNITY_MODULE && null != BuildConfig.COMMUNITY_SERVER) {
//			// download the module
//
//		} else
		if (ModuleUtil.getModules() == null || ModuleUtil.getModules().isEmpty()) {
			connectDemo.setVisibility(View.VISIBLE);
			connectDemo.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					connectToDemoServer();
				}
			});
			
			connectServer.setVisibility(View.VISIBLE);
			connectServer.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					Intent serverSettings = new Intent(SplashActivity.this, ServerSettingsActivity.class);
					startActivity(serverSettings);
				}
			});
		} else {
			loadModule.setVisibility(View.VISIBLE);
			loadModule.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					Intent mainIntent = new Intent(SplashActivity.this, MainActivity.class);
		            SplashActivity.this.startActivity(mainIntent);
				}
			});
		}
		
		Button continueSession = (Button) findViewById(R.id.splash_continue);
		final String key = FAIMSApplication.getInstance().getSessionModuleKey();
		final String arch16n = FAIMSApplication.getInstance().getSessionModuleArch16n();
		if ((key != null && ModuleUtil.getModule(key) != null) && null != BuildConfig.COMMUNITY_MODULE) {
			Intent showModuleIntent = new Intent(this, ShowModuleActivity.class);
			showModuleIntent.putExtra("key", key);
			showModuleIntent.putExtra("arch16n", arch16n);
			startActivityForResult(showModuleIntent, 1);
		} else
		if (key != null && ModuleUtil.getModule(key) != null) {
		    continueSession.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					Intent showModuleIntent = new Intent(SplashActivity.this, ShowModuleActivity.class);
					showModuleIntent.putExtra("key", key);
					showModuleIntent.putExtra("arch16n", arch16n);
					SplashActivity.this.startActivityForResult(showModuleIntent, 1);
				}
			});
	    } else {
	    	continueSession.setVisibility(View.GONE);
	    }
	}
	
	private void connectToDemoServer() {
		FAIMSApplication.getInstance().updateServerSettings(getResources().getString(R.string.demo_server_host),
				getResources().getString(R.string.demo_server_port), false);
		
		Intent mainIntent = new Intent(SplashActivity.this, MainActivity.class);
        startActivity(mainIntent);
	}
	 
}
