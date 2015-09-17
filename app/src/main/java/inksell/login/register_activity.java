package inksell.login;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.List;

import Constants.StorageConstants;
import butterknife.InjectView;
import inksell.inksell.R;
import inksell.common.BaseActionBarActivity;
import models.CompanyEntity;
import models.LocationEntity;
import models.VerifyUserEntity;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import services.InksellCallback;
import services.RestClient;
import utilities.LocalStorageHandler;
import utilities.NavigationHelper;
import utilities.Utility;

public class register_activity extends BaseActionBarActivity implements AdapterView.OnItemSelectedListener {

    ArrayAdapter companyAdapter;
    ArrayAdapter locationAdapter;

    @InjectView(R.id.spncompany)
    Spinner companySpinner;

    @InjectView(R.id.spnlocation)
    Spinner locationSpinner;

    @InjectView(R.id.txtname)
    EditText name;

    @InjectView(R.id.txtemail)
    EditText email;

    @InjectView(R.id.txtdomain)
    TextView domain;

    @Override
    protected void initDataAndLayout() {

        populateCompanies();

    }

    @Override
    protected void initActivity() {

        companyAdapter = new ArrayAdapter(this, R.layout.spinner_item);
        locationAdapter = new ArrayAdapter(this, R.layout.spinner_item);

        companySpinner.setAdapter(companyAdapter);
        companySpinner.setOnItemSelectedListener((AdapterView.OnItemSelectedListener) this);

        locationSpinner.setAdapter(locationAdapter);
        locationSpinner.setOnItemSelectedListener((AdapterView.OnItemSelectedListener) this);
    }

    @Override
    protected int getActivityLayout() {
        return R.layout.activity_register_activity;
    }

    private void populateCompanies() {

        RestClient.get().getCompanies(new Callback<List<CompanyEntity>>() {
            @Override
            public void success(List<CompanyEntity> companyEntities, Response response) {
                companyAdapter.addAll(companyEntities);
                domain.setText(companyEntities.get(0).companyDomain);
                populateLocations(companyEntities.get(0));
            }

            @Override
            public void failure(RetrofitError error) {

            }
        });


    }

    public void register_click(View view) {

        VerifyUserEntity entity = new VerifyUserEntity();
        entity.CompanyId = ((CompanyEntity)companySpinner.getSelectedItem()).companyId;
        entity.LocationId = ((LocationEntity)locationSpinner.getSelectedItem()).locationId;
        entity.Username = name.getText().toString();
        entity.CorporateEmail = email.getText().toString();

        if(Utility.IsStringNullorEmpty(entity.Username) ||
                Utility.IsStringNullorEmpty(entity.CorporateEmail))
        {
            Utility.ShowInfoDialog(R.string.ErrorEmptyFields);
            return;
        }

        entity.CorporateEmail = entity.CorporateEmail + domain.getText();
        RestClient.post().registerUser(entity, new InksellCallback<String>() {
            @Override
            public void onSuccess(String s, Response response) {
                if(Utility.GetUUID(s)!=null) {
                    LocalStorageHandler.SaveData(StorageConstants.UserUUID, s);
                    NavigationHelper.NavigateTo(verify_activity.class);
                }
            }

            @Override
            public void onFailure(RetrofitError error) {

            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()) {
            case R.id.spncompany: {
                CompanyEntity entity = (CompanyEntity) parent.getSelectedItem();
                domain.setText(entity.companyDomain);
                locationAdapter.clear();
                populateLocations(entity);
                break;
            }
            case R.id.spnlocation: {
                LocationEntity entity = (LocationEntity) parent.getSelectedItem();
                break;
            }
        }
    }

    private void populateLocations(CompanyEntity selectedItem) {

        RestClient.get().getLocations(selectedItem.companyId, new Callback<List<LocationEntity>>() {
            @Override
            public void success(List<LocationEntity> locationEntities, Response response) {
                locationAdapter.addAll((locationEntities));
            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
    }

    public void cannot_find_click(View view) {
        NavigationHelper.NavigateTo(new_company.class);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
