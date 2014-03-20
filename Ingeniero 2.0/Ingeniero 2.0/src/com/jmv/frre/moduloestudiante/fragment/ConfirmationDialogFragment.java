package com.jmv.frre.moduloestudiante.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;

import com.actionbarsherlock.app.SherlockDialogFragment;


public class ConfirmationDialogFragment extends SherlockDialogFragment implements OnClickListener,
        OnCancelListener {

    private static final String ARG_DIALOG_ID = "dialog_id";
    private static final String ARG_TITLE = "title";
    private static final String ARG_MESSAGE = "message";
    private static final String ARG_CONFIRM_TEXT = "confirm";
    private static final String ARG_CANCEL_TEXT = "cancel";


    public static ConfirmationDialogFragment newInstance(int dialogId, String title, String message,
            String confirmText, String cancelText) {
        ConfirmationDialogFragment fragment = new ConfirmationDialogFragment();

        Bundle args = new Bundle();
        args.putInt(ARG_DIALOG_ID, dialogId);
        args.putString(ARG_TITLE, title);
        args.putString(ARG_MESSAGE, message);
        args.putString(ARG_CONFIRM_TEXT, confirmText);
        args.putString(ARG_CANCEL_TEXT, cancelText);
        fragment.setArguments(args);

        return fragment;
    }


    public interface ConfirmationDialogFragmentListener {
        void doPositiveClick(int dialogId);
        void doNegativeClick(int dialogId);
        void dialogCancelled(int dialogId);
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Bundle args = getArguments();
        String title = args.getString(ARG_TITLE);
        String message = args.getString(ARG_MESSAGE);
        String confirmText = args.getString(ARG_CONFIRM_TEXT);
        String cancelText = args.getString(ARG_CANCEL_TEXT);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton(confirmText, this);
        builder.setNegativeButton(cancelText, this);

        return builder.create();
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        switch (which) {
            case DialogInterface.BUTTON_POSITIVE: {
                getListener().doPositiveClick(getDialogId());
                break;
            }
            case DialogInterface.BUTTON_NEGATIVE: {
                getListener().doNegativeClick(getDialogId());
                break;
            }
        }
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
        getListener().dialogCancelled(getDialogId());
    }

    private int getDialogId() {
        return getArguments().getInt(ARG_DIALOG_ID);
    }

    private ConfirmationDialogFragmentListener getListener() {
        try {
            return (ConfirmationDialogFragmentListener) getTargetFragment();
        } catch (ClassCastException e) {
            throw new ClassCastException(getTargetFragment().getClass() +
                    " must implement ConfirmationDialogFragmentListener");
        }
    }
}
