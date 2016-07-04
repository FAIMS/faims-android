package au.org.intersect.faims.android.ui.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.GpsStatus;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.Button;

import au.org.intersect.faims.android.BuildConfig;
import au.org.intersect.faims.android.R;
import au.org.intersect.faims.android.app.FAIMSApplication;
import au.org.intersect.faims.android.data.Module;
import au.org.intersect.faims.android.log.FLog;
import au.org.intersect.faims.android.net.FAIMSClient;
import au.org.intersect.faims.android.net.FAIMSClientErrorCode;
import au.org.intersect.faims.android.net.FAIMSClientResultCode;
import au.org.intersect.faims.android.net.Result;
import au.org.intersect.faims.android.net.ServerDiscovery;
import au.org.intersect.faims.android.services.DownloadModuleService;
import au.org.intersect.faims.android.services.UpdateModuleDataService;
import au.org.intersect.faims.android.services.UpdateModuleSettingService;
import au.org.intersect.faims.android.tasks.ITaskListener;
import au.org.intersect.faims.android.tasks.LocateServerTask;
import au.org.intersect.faims.android.ui.dialog.BusyDialog;
import au.org.intersect.faims.android.ui.dialog.DialogResultCode;
import au.org.intersect.faims.android.ui.dialog.IDialogListener;
import au.org.intersect.faims.android.util.ClockskewCheckUtil;
import au.org.intersect.faims.android.util.FileUtil;
import au.org.intersect.faims.android.util.ModuleUtil;
import android.location.LocationManager;

import com.google.inject.Inject;

import java.lang.ref.WeakReference;

import au.org.intersect.faims.android.util.TimezoneCheckUtil;
import roboguice.activity.RoboActivity;

public class SplashActivity extends RoboActivity {

	enum Type {
		DOWNLOAD,
		UPDATE_SETTINGS,
		UPDATE_DATA
	}

	protected BusyDialog busyDialog;

	protected final SplashActivity.DownloadUpdateModuleHandler downloadHandler = new SplashActivity.DownloadUpdateModuleHandler(SplashActivity.this, Type.DOWNLOAD);

	protected Boolean downloading = false;
	@Inject
	FAIMSClient faimsClient;

	@Inject
	ServerDiscovery serverDiscovery;
	private AsyncTask<Void, Void, Void> locateTask;

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
		if (null != BuildConfig.COMMUNITY_SERVER && null != BuildConfig.COMMUNITY_PORT) {
			FAIMSApplication.getInstance().updateServerSettings(BuildConfig.COMMUNITY_SERVER, BuildConfig.COMMUNITY_PORT, false);
		}
		if (null != BuildConfig.COMMUNITY_MODULE) {
			FAIMSApplication.getInstance().saveModuleKey(BuildConfig.COMMUNITY_MODULE);
		}

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

	private void showFailureDialog(Result result, Type type) {
		//TODO
//		if (result.errorCode == FAIMSClientErrorCode.BUSY_ERROR) {
//			showBusyErrorDialog(type);
//		} else if (result.errorCode == FAIMSClientErrorCode.STORAGE_LIMIT_ERROR) {
//			showStorageErrorDialog(type);
//		} else if (result.errorCode == FAIMSClientErrorCode.DOWNLOAD_CORRUPTED_ERROR) {
//			showDownloadCorruptedDialog(type);
//		} else if (result.errorCode == FAIMSClientErrorCode.SERVER_ERROR) {
//			showServerErrorDialog(type);
//		} else {
//			showInterruptedDialog(type);
//		}
	}

	public static class DownloadUpdateModuleHandler extends Handler {

		private WeakReference<SplashActivity> activityRef;
		private Type type;

		public DownloadUpdateModuleHandler(SplashActivity activity, Type type) {
			this.activityRef = new WeakReference<SplashActivity>(activity);
			this.type = type;
		}

		public void handleMessage(Message message) {
			SplashActivity activity = activityRef.get();
			if (activity == null) {
				FLog.d("FetchModulesHandler cannot get activity");
				return;
			}

			activity.busyDialog.dismiss();

			Result result = (Result) message.obj;
			if (result.resultCode == FAIMSClientResultCode.SUCCESS) {
				// Show module static panel
				activity.openModule(BuildConfig.COMMUNITY_MODULE);
			} else if (result.resultCode == FAIMSClientResultCode.FAILURE ||
					result.resultCode == FAIMSClientResultCode.INTERRUPTED) {
				activity.showFailureDialog(result, type);
			}
		}

	};

	private void showBusyDialog(final Type type) {
		busyDialog = new BusyDialog(SplashActivity.this,
				getString(R.string.busy_title),
				getString(R.string.download_busy_message),
				new IDialogListener() {

					@Override
					public void handleDialogResponse(
							DialogResultCode resultCode) {
						if (type == Type.DOWNLOAD) {
							if (resultCode == DialogResultCode.CANCEL) {
								// stop service
								Intent intent = new Intent(SplashActivity.this, DownloadModuleService.class);

								stopService(intent);
							}
						} else if (type == Type.UPDATE_SETTINGS) {
							if (resultCode == DialogResultCode.CANCEL) {
								// stop service
								Intent intent = new Intent(SplashActivity.this, UpdateModuleSettingService.class);

								stopService(intent);
							}

						} else if (type == Type.UPDATE_DATA) {
							if (resultCode == DialogResultCode.CANCEL) {
								// stop service
								Intent intent = new Intent(SplashActivity.this, UpdateModuleDataService.class);

								stopService(intent);
							}
						}
					}

				});
		busyDialog.show();
	}

	protected void downloadModule() {
		if (null != serverDiscovery && serverDiscovery.isServerHostValid()) {
			showBusyDialog(Type.DOWNLOAD);

			// start service
			Intent intent = new Intent(SplashActivity.this, DownloadModuleService.class);

			Messenger messenger = new Messenger(downloadHandler);
			Module serviceModule = new Module(BuildConfig.COMMUNITY_APPNAME, BuildConfig.COMMUNITY_MODULE);
			serviceModule.setHost(BuildConfig.COMMUNITY_SERVER);
			intent.putExtra("MESSENGER", messenger);
			intent.putExtra("module", serviceModule);
			intent.putExtra("overwrite", true);
			startService(intent);
		} else {
			showBusyDialog(Type.DOWNLOAD);

			locateTask = new LocateServerTask(serverDiscovery, new ITaskListener() {

				@Override
				public void handleTaskCompleted(Object result) {
					SplashActivity.this.busyDialog.dismiss();

					if ((Boolean) result) {
						downloadModule();
					} else {
						//TODO
//						showLocateServerDownloadArchiveFailureDialog(overwrite);
					}
				}
			}).execute();
		}

	}

	private void updateButtons() {

		Button connectDemo = (Button) findViewById(R.id.splash_connect_demo);
		Button connectServer = (Button) findViewById(R.id.splash_connect_server);
		Button loadModule = (Button) findViewById(R.id.splash_load);
		connectDemo.setVisibility(View.GONE);
		connectServer.setVisibility(View.GONE);
		loadModule.setVisibility(View.GONE);
		if (null != BuildConfig.COMMUNITY_MODULE && null == ModuleUtil.getModule(BuildConfig.COMMUNITY_MODULE)) {
			 //download the module
			if (!downloading) {
				downloading = true;
				downloadModule();
			}
			return;
		} else
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
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		String arch = "faims.properties";
		if (null != key && null != ModuleUtil.getModule(key) && ModuleUtil.getModule(key).getArch16nFiles().size() > 0) {
			arch = prefs.getString("module-arch16n", FileUtil.sortArch16nFiles(ModuleUtil.getModule(key).getArch16nFiles()).get(0));
		}
		final String arch16n = arch;
		if ((key != null && ModuleUtil.getModule(key) != null) && null != BuildConfig.COMMUNITY_MODULE) {
			Intent showModuleIntent = new Intent(this, ShowModuleActivity.class);
			showModuleIntent.putExtra("key", key);
			showModuleIntent.putExtra("arch16n", arch16n);
			startActivityForResult(showModuleIntent, 1);
			SplashActivity.this.finish();
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

	private void openModule(String key) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		String arch = "faims.properties";
		if (ModuleUtil.getModule(key).getArch16nFiles().size() > 0) {
			arch = prefs.getString("module-arch16n", FileUtil.sortArch16nFiles(ModuleUtil.getModule(key).getArch16nFiles()).get(0));
		}
		final String arch16n = arch;
		Intent showModuleIntent = new Intent(this, ShowModuleActivity.class);
		showModuleIntent.putExtra("key", key);
		showModuleIntent.putExtra("arch16n", arch16n);
		startActivityForResult(showModuleIntent, 1);
		SplashActivity.this.finish();
	}

	private void connectToDemoServer() {
		FAIMSApplication.getInstance().updateServerSettings(getResources().getString(R.string.demo_server_host),
				getResources().getString(R.string.demo_server_port), false);
		
		Intent mainIntent = new Intent(SplashActivity.this, MainActivity.class);
        startActivity(mainIntent);
	}
	 
}
