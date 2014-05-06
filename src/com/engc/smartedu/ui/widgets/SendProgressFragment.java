package com.engc.smartedu.ui.widgets;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.app.DialogFragment;
import com.engc.smartedu.R;

/**
 * User: qii
 * Date: 12-8-13
 */
public class SendProgressFragment extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        ProgressDialog dialog = new ProgressDialog(getActivity());
        dialog.setMessage(getString(R.string.sending));
        dialog.setIndeterminate(false);
        dialog.setCancelable(true);
        return dialog;
    }
}
