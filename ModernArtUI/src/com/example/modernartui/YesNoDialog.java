/**
 * 
 */
package com.example.modernartui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

/**
 * @author Troutbum
 *
Generic DialogFragment subclasses like YesNoDialog and OkDialog, 
and pass in title and message if you use dialogs a lot in your app.

Then call it using the following:

    DialogFragment dialog = new YesNoDialog();
    Bundle args = new Bundle();
    args.putString("title", title);
    args.putString("message", message);
    dialog.setArguments(args);
    dialog.setTargetFragment(this, YES_NO_CALL);
    dialog.show(getFragmentManager(), "tag");

And handle the result in onActivityResult().
 *
 *
 */

public class YesNoDialog extends DialogFragment
{
    public YesNoDialog()
    {

    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        Bundle args = getArguments();
        String title = args.getString("title", "");
        String message = args.getString("message", "");

        return new AlertDialog.Builder(getActivity())
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialog, int which)
                {
                    getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, null);
                }
            })
            .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialog, int which)
                {
                    getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_CANCELED, null);
                }
            })
            .create();
    }
}
