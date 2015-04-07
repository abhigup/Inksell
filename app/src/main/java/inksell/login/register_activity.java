package inksell.login;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import inksell.inksell.R;
import models.CompanyEntity;
import models.LocationEntity;
import models.VerifyUserEntity;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import services.RestClient;

public class register_activity extends ActionBarActivity implements AdapterView.OnItemSelectedListener {

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_activity);

        ButterKnife.inject(this);

        populateCompanies();

        companyAdapter = new ArrayAdapter(this, R.layout.spinner_item);
        locationAdapter = new ArrayAdapter(this, R.layout.spinner_item);

        companySpinner.setAdapter(companyAdapter);
        companySpinner.setOnItemSelectedListener((AdapterView.OnItemSelectedListener) this);

        locationSpinner.setAdapter(locationAdapter);
        locationSpinner.setOnItemSelectedListener((AdapterView.OnItemSelectedListener) this);

    }

    private void populateCompanies() {

        RestClient.get().getCompanies(new Callback<List<CompanyEntity>>() {
            @Override
            public void success(List<CompanyEntity> companyEntities, Response response) {
                companyAdapter.addAll(companyEntities);
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

        RestClient.post().registerUser(entity, new Callback<String>() {
            @Override
            public void success(String s, Response response) {
                Intent intent = new Intent(getBaseContext(), verify_activity.class);
                    intent.putExtra("guid", s);
                    startActivity(intent);

            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()) {
            case R.id.spncompany: {
                CompanyEntity entity = (CompanyEntity) parent.getSelectedItem();
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


    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
