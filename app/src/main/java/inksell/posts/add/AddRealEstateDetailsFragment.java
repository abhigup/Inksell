package inksell.posts.add;


import android.app.DatePickerDialog;
import android.text.InputType;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ToggleButton;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import butterknife.InjectView;
import enums.CategoryType;
import inksell.inksell.R;
import models.IPostEntity;
import utilities.ConfigurationManager;
import utilities.Utility;

public class AddRealEstateDetailsFragment extends BaseAddFragment {


    enum PropertyType
    {
        rent,
        sale
    }

    Calendar myCalendar = Calendar.getInstance();

    private boolean isRent = true;
    private boolean toggleLoopBack = false;

    @InjectView(R.id.add_rent_btn)
    ToggleButton rent_btn;

    @InjectView(R.id.add_sale_btn)
    ToggleButton sale_btn;

    @InjectView(R.id.add_post_title)
    EditText title;

    @InjectView(R.id.add_post_availability)
    EditText availableFrom;

    public AddRealEstateDetailsFragment() {
        // Required empty public constructor
    }


    @Override
    public int getViewResId() {
        return R.layout.fragment_add_real_estate_details;
    }

    @Override
    public void initViewAfterSettingEditableView(View view) {
        rent_btn.setOnCheckedChangeListener(changeChecker);
        sale_btn.setOnCheckedChangeListener(changeChecker);

        availableFrom.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    openDateFragment(v);
                }
            }
        });
        availableFrom.setInputType(InputType.TYPE_NULL);
        availableFrom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDateFragment(v);
            }
        });
    }

    @Override
    public void setEditableView(View view) {

    }

    private void openDateFragment(View v)
    {
        new DatePickerDialog(ConfigurationManager.CurrentActivityContext, date, myCalendar
                .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH)).show();

        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(getActivity().INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

    }

    DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            // TODO Auto-generated method stub
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateLabel();
        }

    };

    private void updateLabel() {

        String myFormat = "dd/MM/yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Utility.getLocale);
        availableFrom.setText(sdf.format(myCalendar.getTime()));
    }

    CompoundButton.OnCheckedChangeListener changeChecker = new CompoundButton.OnCheckedChangeListener() {

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

            if(!isChecked)
            {
                if(toggleLoopBack==true)
                {
                    toggleLoopBack=false;
                }
                else {
                    buttonView.setChecked(true);
                }
            }
            else {
                toggleLoopBack = true;
                if (buttonView == rent_btn) {
                    sale_btn.setChecked(false);
                    toggleRentSale(PropertyType.rent);
                }
                if (buttonView == sale_btn) {
                    rent_btn.setChecked(false);
                    toggleRentSale(PropertyType.sale);
                }
            }
        }
    };

    private void toggleRentSale(PropertyType propertyType)
    {
        if(propertyType == PropertyType.sale)
        {
            title.setText("Sale");
        }
        else
        {
            title.setText("Rent");
        }
    }

    @Override
    public boolean verifyAndGetPost(IPostEntity iPostEntity, CategoryType categoryType) {
        return false;
    }
}
