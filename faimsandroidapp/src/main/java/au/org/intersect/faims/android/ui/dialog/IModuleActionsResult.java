package au.org.intersect.faims.android.ui.dialog;

/**
 * Created by Wes Cilldhaire on 24/05/16.
 */
public interface IModuleActionsResult {
    void moduleActionsUpdate(String selectedItem);
    void moduleActionsRestore(String selectedItem);
    void moduleActionsForce(String selectedItem);
}
