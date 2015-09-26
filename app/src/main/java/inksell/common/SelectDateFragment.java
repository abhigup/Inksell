package inksell.common;


import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;

import java.util.Calendar;

import utilities.ConfigurationManager;

public class SelectDateFragment extends android.support.v4.app.DialogFragment {

    DatePickerDialog.OnDateSetListener listener;

    public static SelectDateFragment newInstance(DatePickerDialog.OnDateSetListener onDateSetListener) {
        SelectDateFragment f = new SelectDateFragment();
        f.listener = onDateSetListener;
        return f;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Calendar calendar = Calendar.getInstance();
        int yy = calendar.get(Calendar.YEAR);
        int mm = calendar.get(Calendar.MONTH);
        int dd = calendar.get(Calendar.DAY_OF_MONTH);
        return new DatePickerDialog(ConfigurationManager.CurrentActivityContext, listener, yy, mm, dd);
    }

}
