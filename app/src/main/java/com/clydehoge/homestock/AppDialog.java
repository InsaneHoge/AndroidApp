package com.clydehoge.homestock;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

/**
 * By Clyde Hogenstijn 9-06-2020
 *
 */

public class AppDialog extends DialogFragment {
    private static final String TAG = "AppDialog";

    public static final String DIALOG_ID = "id";
    public static final String DIALOG_MESSAGE = "message";
    public static final String DIALOG_POSITIVE_RID = "positive_rid";
    public static final String DIALOG_NEGATIVE_RID = "negative_rid";


    /**
     * The dialogue's callback is to notify of user selected results (deletion confirmed etc)
     */
    interface DialogEvents {
        void onPositiveDialogResult(int dialogID, Bundle arg);

        void onNegativeDialogResult(int dialogID, Bundle arg);

        void onDialogCancelled(int dialogID);
    }

    private DialogEvents mDialogEvents;

    @Override
    public void onAttach(@NonNull Context context) {
        Log.d(TAG, "onAttach: Entering onAttach, activity is " + context.toString());
        super.onAttach(context);

        //Activities containing this fragment must implement is's callback
        if (!(context instanceof DialogEvents)) {
            throw new ClassCastException(context.toString() + " must implement AppDialog.DialogEvents interface");
        }

        mDialogEvents = (DialogEvents) context;
    }

    @Override
    public void onDetach() {
        Log.d(TAG, "onDetach: Entering ...");
        super.onDetach();

        //Resets the active callbacks interface, because we don't have an activity any longer.
        mDialogEvents = null;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateDialog: starts");

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final Bundle arguments = getArguments();
        final int dialogID;
        String messageString;
        int positiveStringID;
        int negativeStringID;

        if (arguments != null) {
            dialogID = arguments.getInt(DIALOG_ID);
            messageString = arguments.getString(DIALOG_MESSAGE);

            if(dialogID ==0 || messageString == null){
                throw new IllegalArgumentException("DIALOG_ID and/or DIALOG_MESSAGE not present in the bundle.");
            }

            positiveStringID = arguments.getInt(DIALOG_POSITIVE_RID);
            if (positiveStringID == 0) {
                positiveStringID = R.string.ok;
            }
            negativeStringID = arguments.getInt(DIALOG_NEGATIVE_RID);
            if (negativeStringID == 0) {
                negativeStringID = R.string.cancel;
            }
        } else {
            throw new IllegalArgumentException("Must pass DIALOG_ID and DIALOG_MESSAGE in the bundle.");
        }

        builder.setMessage(messageString)
                .setPositiveButton(positiveStringID, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //callback positive result method
                        if(mDialogEvents != null){
                            mDialogEvents.onPositiveDialogResult(dialogID, arguments);
                        }
                    }
                })
                .setNegativeButton(negativeStringID, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //callback negative result method
                        if(mDialogEvents != null){
                            mDialogEvents.onNegativeDialogResult(dialogID, arguments);
                        }
                    }
                });

        return builder.create();
    }

    @Override
    public void onCancel(@NonNull DialogInterface dialog) {
        Log.d(TAG, "onCancel: called");

        if(mDialogEvents !=null){
            int dialogID = getArguments().getInt(DIALOG_ID);
            mDialogEvents.onDialogCancelled(dialogID);
        }
    }
}
