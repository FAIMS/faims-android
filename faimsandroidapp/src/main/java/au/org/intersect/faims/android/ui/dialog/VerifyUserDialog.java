package au.org.intersect.faims.android.ui.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import au.org.intersect.faims.android.R;
import au.org.intersect.faims.android.data.IVerifyUser;
import au.org.intersect.faims.android.data.User;
import au.org.intersect.faims.android.ui.activity.ShowModuleActivity;

/**
 * Created by Wes Cilldhaire on 11/05/16.
 */
public class VerifyUserDialog extends DialogFragment implements DialogInterface.OnShowListener {
    View verifyUserDialogView;
    AlertDialog verifyUserAlertDialog;

    IVerifyUser mIVerifyUser;

    EditText password;

    String callback;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        callback = getArguments().getString("callback");
        verifyUserDialogView = getActivity().getLayoutInflater().inflate(R.layout.activity_verify_user,null);
        password = (EditText) verifyUserDialogView.findViewById(R.id.verify_user_edittext_password);

        AlertDialog.Builder verifyUserDialog = new AlertDialog.Builder(getActivity());
        verifyUserDialog.setTitle("Login");
        verifyUserDialog.setCancelable(true);
        verifyUserDialog.setView(verifyUserDialogView);
        verifyUserDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        verifyUserDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // do nothing, dialog closes
            }
        });
        verifyUserAlertDialog = verifyUserDialog.create();
        verifyUserAlertDialog.setOnShowListener(this);
        return verifyUserAlertDialog;
    }


    @Override
    public void onAttach(Activity activity) {
        mIVerifyUser = (IVerifyUser) ((ShowModuleActivity) activity).beanShellLinker;
        super.onAttach(activity);
    }

    @Override
    public void onShow(DialogInterface dialog) {
        verifyUserAlertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!password.getText().toString().isEmpty()) {
                    mIVerifyUser.verifyUser(
                            password.getText().toString(),
                            callback
                    );
                    verifyUserAlertDialog.dismiss();
                } else {
                    Toast.makeText(verifyUserDialogView.getContext(), "Password can not be empty", Toast.LENGTH_SHORT).show();
                    password.requestFocus();
                    password.selectAll();
                }
            }
        });
   }
}