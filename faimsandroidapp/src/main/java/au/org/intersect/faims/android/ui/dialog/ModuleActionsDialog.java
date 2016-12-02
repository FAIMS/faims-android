package au.org.intersect.faims.android.ui.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import au.org.intersect.faims.android.R;
import au.org.intersect.faims.android.ui.activity.MainActivity;

/**
 * Created by Wes Cilldhaire on 11/05/16.
 */
public class ModuleActionsDialog extends DialogFragment implements DialogInterface.OnShowListener {
    View moduleActionsView;
    AlertDialog modulesActionsAlertDialog;
    IModuleActionsResult mIModuleActionsResult;

    String selectedItem;

    TextView moduleActionsText;
    Button moduleActionsRestoreButton;
    Button moduleActionsCancelButton;
    Button moduleActionsUpdateButton;
    Button moduleActionsForceButton;


    public ModuleActionsDialog() {
        super();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        moduleActionsView = getActivity().getLayoutInflater().inflate(R.layout.activity_module_actions,null);

        selectedItem = getArguments().getString("selectedItem");
        moduleActionsText = (TextView) moduleActionsView.findViewById(R.id.module_actions_text);
        moduleActionsCancelButton = (Button) moduleActionsView.findViewById(R.id.module_actions_cancel_button);
        moduleActionsRestoreButton = (Button) moduleActionsView.findViewById(R.id.module_actions_restore_button);
        moduleActionsUpdateButton = (Button) moduleActionsView.findViewById(R.id.module_actions_update_button);
        moduleActionsForceButton = (Button) moduleActionsView.findViewById(R.id.module_actions_force_button);

        moduleActionsText.setText(getString(R.string.confirm_download_or_update_module_message) + " " + selectedItem + "?");

        moduleActionsCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        moduleActionsRestoreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mIModuleActionsResult.moduleActionsRestore(selectedItem);
                dismiss();
            }
        });

        moduleActionsUpdateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mIModuleActionsResult.moduleActionsUpdate(selectedItem);
                dismiss();
            }
        });

        moduleActionsForceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mIModuleActionsResult.moduleActionsForce(selectedItem);
                dismiss();
            }
        });

        AlertDialog.Builder moduleActionsDialog = new AlertDialog.Builder(getActivity());
        moduleActionsDialog.setTitle("Update or Restore Module");
        moduleActionsDialog.setCancelable(true);
        moduleActionsDialog.setView(moduleActionsView);
        modulesActionsAlertDialog = moduleActionsDialog.create();
        modulesActionsAlertDialog.setOnShowListener(this);
        return modulesActionsAlertDialog;
    }


    @Override
    public void onAttach(Activity activity) {
//        mICreateUser = (ICreateUser) ((ShowModuleActivity) activity).beanShellLinker;
        mIModuleActionsResult = (IModuleActionsResult) activity;
        super.onAttach(activity);
    }

    @Override
    public void onShow(DialogInterface dialog) {
   }
}