package org.studia.barterapplication.activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

/**
 * This class refers to report dialog when user click on report post button.
 */
public class ReportDialog extends DialogFragment {


    int position = 0;

    /**
     * This interface is used in order to listen positive and negative click on report dialog.
     */
    public interface ReportypeListener{
        void positiveClicked(String[] reportTypes, int position );
        void negativeClicked();
    }

    ReportypeListener myListener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        myListener = (ReportypeListener) context;
    }

    /**
     * This method creates the dialog
     * @param savedInstanceState
     * @return
     */
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        AlertDialog.Builder dialogbuilder = new AlertDialog.Builder(getActivity());

        //report types
        String reports[] = {"Nieodpowiednia treść lub kategoria", "Spam lub treść wprowadzająca w błąd", "Przeterminowane ogłoszenie lub sprzedane", "Podejrzenie oszustwa"};

        //title
        dialogbuilder.setTitle("Dlaczego chcesz zgłosić to ogłoszenie?");


        // report options users can choose
        dialogbuilder.setSingleChoiceItems(reports, position, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                position = which;
            }
        });

        //submit and cancel buttons
        dialogbuilder.setNegativeButton("Anuluj", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                myListener.negativeClicked();
            }
        });
        dialogbuilder.setPositiveButton("Potwierdź", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                myListener.positiveClicked(reports, position);
            }
        });

        return dialogbuilder.create();
    }
}
