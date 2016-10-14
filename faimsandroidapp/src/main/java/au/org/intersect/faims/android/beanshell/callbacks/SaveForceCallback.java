package au.org.intersect.faims.android.beanshell.callbacks;

public interface SaveForceCallback extends IBeanShellCallback {

	public void onSave(String uuid, boolean newRecord, boolean hasChanges);
	public void onSaveAssociation(String entityId, String relationshpId);

}
