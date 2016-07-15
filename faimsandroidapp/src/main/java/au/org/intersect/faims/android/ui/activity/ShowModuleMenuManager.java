package au.org.intersect.faims.android.ui.activity;

import java.lang.ref.WeakReference;
import java.util.LinkedHashMap;
import java.util.Map;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import au.org.intersect.faims.android.R;
import au.org.intersect.faims.android.app.FAIMSApplication;
import au.org.intersect.faims.android.beanshell.callbacks.ActionButtonCallback;
import au.org.intersect.faims.android.beanshell.callbacks.ToggleActionButtonCallback;
import au.org.intersect.faims.android.gps.GPSDataManager;
import au.org.intersect.faims.android.log.FLog;
import au.org.intersect.faims.android.managers.AutoSaveManager;
import au.org.intersect.faims.android.managers.BluetoothManager;
import au.org.intersect.faims.android.util.Arch16n;
import au.org.intersect.faims.android.util.BitmapUtil;
import au.org.intersect.faims.android.util.MeasurementUtil;

import com.google.inject.Inject;
import com.nutiteq.utils.UnscaledBitmapLoader;

public class ShowModuleMenuManager {
	
	@Inject
	GPSDataManager gpsDataManager;
	
	@Inject
	BluetoothManager bluetoothManager;
	
	@Inject
	AutoSaveManager autoSaveManager;
	
	@Inject
	Arch16n arch16n;
	
	private boolean pathIndicatorVisible;
	private float pathDistance;
	private boolean pathValid;
	private float pathBearing;
	private Float pathHeading;
	private int pathIndex;
	private int pathLength;

	private BitmapDrawable validArrow;
	private BitmapDrawable invalidArrow;
	private Bitmap tempBitmap;
	private Animation rotation;
	
	private LinkedHashMap<String, ActionButtonCallback> menuItems;
	
	private WeakReference<ShowModuleActivity> activityRef;
	
	public ShowModuleMenuManager(ShowModuleActivity activity) {
		FAIMSApplication.getInstance().injectMembers(this);
		this.activityRef = new WeakReference<ShowModuleActivity>(activity);
		
		menuItems = new LinkedHashMap<String, ActionButtonCallback>();
		
		rotation = AnimationUtils.loadAnimation(activity, R.anim.clockwise);
		rotation.setRepeatCount(Animation.INFINITE);
	}

	public void addMenuItem(String name, ActionButtonCallback callback) {
		menuItems.put(name, callback);
	}
	
	public void removeMenuItem(String name) {
		menuItems.remove(name);
	}
	
	public void updateActionBar(Menu menu) {
		menu.clear();
		activityRef.get().getMenuInflater().inflate(R.menu.activity_show_module, menu);
		for(Map.Entry<String, ActionButtonCallback> entry : menuItems.entrySet()) {
			ActionButtonCallback actionItem = entry.getValue();
			if (actionItem instanceof ToggleActionButtonCallback) {
				if (((ToggleActionButtonCallback) actionItem).isActionOff()) {
					addOnActionToMenu(actionItem, menu, true);
				} else {
					addOffActionToMenu((ToggleActionButtonCallback) actionItem, menu);
				}
			} else {
				addOnActionToMenu(actionItem, menu, false);
			}
		}
	}
	
	public void addOnActionToMenu(final ActionButtonCallback actionItem, final Menu menu, boolean showIcon) {
		menu.add(arch16n.substituteValue(actionItem.actionOnLabel()));
		if (showIcon) {
			menu.getItem(menu.size()-1).setIcon(R.drawable.toggle_on);
		}
		menu.getItem(menu.size()-1).setOnMenuItemClickListener(new OnMenuItemClickListener() {
			
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				try {
					actionItem.actionOn();
					updateActionBar(menu);
				} catch (Exception e) {
					showActionError(actionItem, e);
				}
				return false;
			}
		});
	}
	
	public void addOffActionToMenu(final ToggleActionButtonCallback actionItem, final Menu menu) {
		menu.add(arch16n.substituteValue(actionItem.actionOffLabel()));
		menu.getItem(menu.size()-1).setIcon(R.drawable.toggle_off);
		menu.getItem(menu.size()-1).setOnMenuItemClickListener(new OnMenuItemClickListener() {
			
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				try {
					actionItem.actionOff();
					updateActionBar(menu);
				} catch (Exception e) {
					showActionError(actionItem, e);
				}
				return false;
			}
		});
	}
	
	private void showActionError(ActionButtonCallback callback, Exception e) {
		try {
			FLog.e("error trying to call action bar menu action", e);
			callback.onError(e.getMessage());
		} catch (Exception ce) {
			FLog.e("error trying to call action bar menu onerror callback", ce);
			activityRef.get().beanShellLinker.reportError("error trying to call action bar menu onerror callback", ce);
		}
	}
	public void updateActionBarStatus(Menu menu) {
		// sync status
		menu.findItem(R.id.action_sync).setVisible(false);
		menu.findItem(R.id.action_sync_active).setVisible(false);
		menu.findItem(R.id.action_sync_error).setVisible(false);
		menu.findItem(R.id.action_sync_has_changes).setVisible(false);
		menu.findItem(R.id.action_sync_inactive).setVisible(false);

		//TODO fix animation
//		ImageView syncIcon = (ImageView) statusBar.findViewById(R.id.action_sync_active);
//		syncIcon.clearAnimation();

		switch (activityRef.get().getSyncStatus()) {
			case ACTIVE_SYNCING:
				menu.findItem(R.id.action_sync_active).setVisible(true);
//				syncIcon.setVisibility(View.VISIBLE);
//				syncIcon.startAnimation(rotation);
				break;
			case ERROR:
				menu.findItem(R.id.action_sync_error).setVisible(true);
				break;
			case ACTIVE_NO_CHANGES:
				menu.findItem(R.id.action_sync).setVisible(true);
				break;
			case ACTIVE_HAS_CHANGES:
				menu.findItem(R.id.action_sync_has_changes).setVisible(true);
				break;
			default:
				menu.findItem(R.id.action_sync_inactive).setVisible(true);
				break;
		}

		// tracker status
		menu.findItem(R.id.action_tracker_active_no_gps).setVisible(false);
		menu.findItem(R.id.action_tracker_active_has_gps).setVisible(false);
		menu.findItem(R.id.action_tracker_inactive).setVisible(false);
		if (gpsDataManager.isTrackingStarted()) {
			if (gpsDataManager.hasValidExternalGPSSignal()
					|| gpsDataManager.hasValidInternalGPSSignal()) {
				menu.findItem(R.id.action_tracker_active_has_gps).setVisible(true);
			} else {
				menu.findItem(R.id.action_tracker_active_no_gps).setVisible(true);
			}
		} else {
			menu.findItem(R.id.action_tracker_inactive).setVisible(true);
		}

		// gps status
		menu.findItem(R.id.action_gps_inactive).setVisible(false);
		menu.findItem(R.id.action_gps_active_has_signal).setVisible(false);
		menu.findItem(R.id.action_gps_active_no_signal).setVisible(false);
		if (gpsDataManager.isExternalGPSStarted()
				|| gpsDataManager.isInternalGPSStarted()) {
			if (gpsDataManager.hasValidExternalGPSSignal()
					|| gpsDataManager.hasValidInternalGPSSignal()) {
				menu.findItem(R.id.action_gps_active_has_signal).setVisible(true);
			} else {
				menu.findItem(R.id.action_gps_active_no_signal).setVisible(true);
			}
		} else {
			menu.findItem(R.id.action_gps_inactive).setVisible(true);
		}

		// bluetooth status
		menu.findItem(R.id.action_bluetooth_active).setVisible(false);
		menu.findItem(R.id.action_bluetooth_connected).setVisible(false);
		menu.findItem(R.id.action_bluetooth_disconnected).setVisible(false);
		menu.findItem(R.id.action_bluetooth_error).setVisible(false);

		switch (bluetoothManager.getBluetoothStatus()) {
			case ACTIVE:
				menu.findItem(R.id.action_bluetooth_active).setVisible(true);
				break;
			case CONNECTED:
				menu.findItem(R.id.action_bluetooth_connected).setVisible(true);
				break;
			case ERROR:
				menu.findItem(R.id.action_bluetooth_error).setVisible(true);
				break;
			default:
				menu.findItem(R.id.action_bluetooth_disconnected).setVisible(true);
				break;
		}

		// autosave status
		menu.findItem(R.id.action_autosave).setVisible(false);
		menu.findItem(R.id.action_autosave_inactive).setVisible(false);
		menu.findItem(R.id.action_autosave_ready).setVisible(false);
		menu.findItem(R.id.action_autosave_saving).setVisible(false);
		menu.findItem(R.id.action_autosave_error).setVisible(false);

		//TODO fix animation
//		ImageView autosaveIcon = (ImageView) menu.findItem(R.id.action_autosave_saving_item).getActionView();
//		autosaveIcon.clearAnimation();

		switch (autoSaveManager.getStatus()) {
			case SAVING:
				menu.findItem(R.id.action_autosave_saving).setVisible(true);
//				autosaveIcon.setVisibility(View.VISIBLE);
//				autosaveIcon.startAnimation(rotation);
				break;
			case READY:
				menu.findItem(R.id.action_autosave_ready).setVisible(true);
				break;
			case ACTIVE:
				menu.findItem(R.id.action_autosave).setVisible(true);
				break;
			case ERROR:
				menu.findItem(R.id.action_autosave_error).setVisible(true);
				break;
			default:
				menu.findItem(R.id.action_autosave_inactive).setVisible(true);
				break;
		}

		// follow status
		MenuItem distance_text = menu.findItem(R.id.distance_text);
		MenuItem direction_text = menu.findItem(R.id.direction_text);
		MenuItem direction_indicator = menu.findItem(R.id.direction_indicator);

		distance_text.setVisible(false);
		direction_text.setVisible(false);
		direction_indicator.setVisible(false);

		if (pathIndicatorVisible)  {
			distance_text.setVisible(true);
			direction_text.setVisible(true);
			direction_indicator.setVisible(true);
		}

		String distanceInfo = pathIndex < 0 ? "" : " to point (" + pathIndex
				+ "/" + pathLength + ")";
		if (pathDistance > 1000) {
			distance_text.setTitle(MeasurementUtil.displayAsKiloMeters(
					pathDistance / 1000, "###,###,###,###.0") + distanceInfo);
		} else {
			distance_text.setTitle(MeasurementUtil.displayAsMeters(
					pathDistance, "###,###,###,###") + distanceInfo);
		}

		direction_text.setTitle(MeasurementUtil.displayAsDegrees(pathBearing, "###"));

		if (pathHeading != null) {
			if (tempBitmap != null) {
				tempBitmap.recycle();
			}
			if (validArrow == null) {
				validArrow = new BitmapDrawable(
						activityRef.get().getResources(),
						UnscaledBitmapLoader
								.decodeResource(
										activityRef.get().getResources(),
										au.org.intersect.faims.android.R.drawable.arrow_valid));
			}
			if (invalidArrow == null) {
				invalidArrow = new BitmapDrawable(
						activityRef.get().getResources(),
						UnscaledBitmapLoader
								.decodeResource(
										activityRef.get().getResources(),
										au.org.intersect.faims.android.R.drawable.arrow_invalid));
			}
			if (tempBitmap != null) {
				tempBitmap.recycle();
			}
			this.tempBitmap = BitmapUtil.rotateBitmap(
					pathValid ? validArrow.getBitmap() : invalidArrow.getBitmap(),
					pathBearing - pathHeading);
			direction_indicator.setIcon(new BitmapDrawable(activityRef.get().getResources(),
					tempBitmap));
//			direction_indicator.setImageDrawable(new BitmapDrawable(activityRef.get().getResources(),
//					tempBitmap));
		} else {
			direction_indicator.setVisible(false);
		}

	}

	public void updateStatusBar(LinearLayout statusBar) {
		LinearLayout iconContainer = ((LinearLayout) statusBar.findViewById(R.id.status_bar_icons));
		for (int i=0; i < iconContainer.getChildCount(); i++) {
			View v = iconContainer.getChildAt(i);
			v.setOnLongClickListener(new OnLongClickListener() {

				@Override
				public boolean onLongClick(View v) {
					activityRef.get().beanShellLinker.showToast(v.getContentDescription().toString());
					return false;
				}
			});
		}
	}
	
	public void setPathVisible(boolean value) {
		this.pathIndicatorVisible = value;
	}

	public void setPathDistance(float value) {
		this.pathDistance = value;
	}

	public void setPathIndex(int value, int length) {
		this.pathIndex = value;
		this.pathLength = length;
	}

	public void setPathBearing(float value) {
		this.pathBearing = value;
	}

	public void setPathHeading(Float heading) {
		this.pathHeading = heading;
	}

	public void setPathValid(boolean value) {
		this.pathValid = value;
	}
	
}
