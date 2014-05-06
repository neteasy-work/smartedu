package com.engc.smartedu.ui.maintimeline;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import com.engc.smartedu.R;

/**
 * User: qii
 * Date: 12-10-22
 */
@SuppressLint("NewApi")
public class SaveDraftDialog extends DialogFragment {

    public interface IDraft {
        public void saveToDraft();
    }

    public SaveDraftDialog() {

    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getString(R.string.save_to_draft))
                .setMessage(getString(R.string.do_you_want_to_save))
                .setPositiveButton(getString(R.string.save), new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ((IDraft) getActivity()).saveToDraft();
                    }
                })
                .setNegativeButton(getString(R.string.cancel_draft), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        getActivity().finish();
                    }
                });

        return builder.create();
    }
}

