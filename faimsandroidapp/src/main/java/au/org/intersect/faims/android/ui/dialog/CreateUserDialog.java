package au.org.intersect.faims.android.ui.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import au.org.intersect.faims.android.R;
import au.org.intersect.faims.android.data.ICreateUser;
import au.org.intersect.faims.android.ui.activity.ShowModuleActivity;

/**
 * Created by Wes Cilldhaire on 11/05/16.
 */
public class CreateUserDialog extends DialogFragment implements DialogInterface.OnShowListener {
    View createUserDialogView;
    AlertDialog createUserAlertDialog;

    ICreateUser mICreateUser;

    EditText fname;
    EditText lname;
    EditText email;
    EditText password;
    EditText password_confirmation;

    String callback;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        callback = getArguments().getString("callback");
        createUserDialogView = getActivity().getLayoutInflater().inflate(R.layout.activity_create_user,null);
        fname = (EditText) createUserDialogView.findViewById(R.id.create_user_edittext_fname);
        lname = (EditText) createUserDialogView.findViewById(R.id.create_user_edittext_lname);
        email = (EditText) createUserDialogView.findViewById(R.id.create_user_edittext_email);
        password = (EditText) createUserDialogView.findViewById(R.id.create_user_edittext_password);
        password_confirmation = (EditText) createUserDialogView.findViewById(R.id.create_user_edittext_password_confirmation);

        AlertDialog.Builder createUserDialog = new AlertDialog.Builder(getActivity());
        createUserDialog.setTitle("Sign up");
        createUserDialog.setCancelable(true);
        createUserDialog.setView(createUserDialogView);
        createUserDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        createUserDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // do nothing, dialog closes
            }
        });
        createUserAlertDialog = createUserDialog.create();
        createUserAlertDialog.setOnShowListener(this);
        return createUserAlertDialog;
    }


    @Override
    public void onAttach(Activity activity) {
        mICreateUser = (ICreateUser) ((ShowModuleActivity) activity).beanShellLinker;
        super.onAttach(activity);
    }

    @Override
    public void onShow(DialogInterface dialog) {
        createUserAlertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Log.d("CREATE", fname.getText().toString());
//                Log.d("CREATE", lname.getText().toString());
//                Log.d("CREATE", email.getText().toString());
//                Log.d("CREATE", password.getText().toString());
//                Log.d("CREATE", password_confirmation.getText().toString());
                if (!fname.getText().toString().trim().equals("")) {
                    if (!lname.getText().toString().trim().equals("")) {
                        if (Patterns.EMAIL_ADDRESS.matcher(email.getText().toString()).matches()) {
                            if (!password.getText().toString().isEmpty()) {
                                if (password.getText().toString().equals(password_confirmation.getText().toString())) {
//                                    results.putString("fname", fname.getText().toString());
//                                    results.putString("lname", lname.getText().toString());
//                                    results.putString("email", email.getText().toString());
//                                    results.putString("password", password.getText().toString());
                                    mICreateUser.createUser(
                                            fname.getText().toString(),
                                            lname.getText().toString(),
                                            email.getText().toString(),
                                            password.getText().toString(),
                                            callback
                                    );
                                    createUserAlertDialog.dismiss();
                                } else {
                                    Toast.makeText(createUserDialogView.getContext(), "Passwords don't match", Toast.LENGTH_SHORT).show();
                                    password_confirmation.setText(null);
                                    password.requestFocus();
                                    password.selectAll();
                                }
                            } else {
                                Toast.makeText(createUserDialogView.getContext(), "Password can not be empty", Toast.LENGTH_SHORT).show();
                                password_confirmation.setText(null);
                                password.requestFocus();
                                password.selectAll();
                            }
                        } else {
                            Toast.makeText(createUserDialogView.getContext(), "Email does not appear to be valid", Toast.LENGTH_SHORT).show();
                            email.requestFocus();
                            email.selectAll();
                        }
                    } else {
                        Toast.makeText(createUserDialogView.getContext(), "Last name can not be empty", Toast.LENGTH_SHORT).show();
                        lname.requestFocus();
                        lname.selectAll();
                    }
                } else {
                    Toast.makeText(createUserDialogView.getContext(), "First name can not be empty", Toast.LENGTH_SHORT).show();
                    fname.requestFocus();
                    fname.selectAll();
                }
            }
        });
   }
}