package au.org.intersect.faims.android.test;

import android.test.ActivityInstrumentationTestCase2;

import com.robotium.solo.Solo;

import au.org.intersect.faims.android.ui.activity.SplashActivity;
import au.org.intersect.faims.android.util.AppModuleUtil;
import au.org.intersect.faims.android.util.ModuleWidgetTestUtil;


public class WidgetTest extends ActivityInstrumentationTestCase2<SplashActivity> {
	private Solo solo;

  	public WidgetTest() {
		super(SplashActivity.class);
  	}

  	public void setUp() throws Exception {
        super.setUp();
		solo = new Solo(getInstrumentation(), getActivity());
  	}

	/*
		Test: Heirarchical spinner dropdown Test
		Requires: FAIMS server and module Widget Test loaded
	 */
	public void testRun1() {
		AppModuleUtil.roboConnectToServer(solo, AppModuleUtil.SERVER_NAME_TEST1, AppModuleUtil.SERVER_PORT_80);
		AppModuleUtil.roboLoadModule(solo, AppModuleUtil.MODULE_WIDGET_TEST);
		solo.clickOnText("Faims Admin");
		solo.clickOnView(solo.getView((Object) "control/control/entityTypes"));
		solo.clickOnText("Bone");
		ModuleWidgetTestUtil.clickButton_Record_Feature(solo);

		solo.clickOnView(solo.getView((Object) "oldBone/basicIdentification/clusterType"));

		ModuleWidgetTestUtil.clickOnDrillDown(solo, ModuleWidgetTestUtil.BONE_ONLY);
		assertTrue(ModuleWidgetTestUtil.doesItemExist(solo, ModuleWidgetTestUtil.BACK_TO_TOP));
		assertTrue(ModuleWidgetTestUtil.doesItemExist(solo, ModuleWidgetTestUtil.INSITBO));
		assertTrue(ModuleWidgetTestUtil.doesDrillDownExist(solo, ModuleWidgetTestUtil.SURFBO));

		ModuleWidgetTestUtil.clickOnDrillDown(solo, ModuleWidgetTestUtil.SURFBO);
		assertTrue(ModuleWidgetTestUtil.doesItemExist(solo, ModuleWidgetTestUtil.BACK_TO_TOP));
		assertTrue(ModuleWidgetTestUtil.doesBackToExist(solo, ModuleWidgetTestUtil.BONE_ONLY));
		assertTrue(ModuleWidgetTestUtil.doesItemExist(solo, ModuleWidgetTestUtil.INSIT_SURF));

		ModuleWidgetTestUtil.clickOnItem(solo, ModuleWidgetTestUtil.INSIT_SURF);
		assertTrue(ModuleWidgetTestUtil.doesItemExist(solo, ModuleWidgetTestUtil.BONE_ONLY + " > " + ModuleWidgetTestUtil.SURFBO + " > " + ModuleWidgetTestUtil.INSIT_SURF));

	}

	/*
		Test: Hamburger menu Test
		Requires: FAIMS server and module Widget Test loaded
	 */
	public void testRun2() {
		AppModuleUtil.roboContinueLastSession(solo);
		solo.clickOnText("Faims Admin");
		AppModuleUtil.roboCLickOnHamburger(solo);
		assertTrue(solo.searchText("Main Menu", true));
		solo.clickOnText("User List");
		assertTrue(solo.searchText("Faims Admin", true));
	}

	@Override
	public void tearDown() throws Exception {
		solo.finishOpenedActivities();
		super.tearDown();
	}

}
