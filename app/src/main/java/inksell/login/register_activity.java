package inksell.login;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.util.List;

import inksell.inksell.R;
import models.CompanyEntity;
import models.LocationEntity;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import services.RestClient;

public class register_activity extends ActionBarActivity implements AdapterView.OnItemSelectedListener {

    ArrayAdapter companyAdapter;
    ArrayAdapter locationAdapter;

    int companyId;
    int locationId;
    String name;
    String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_activity);

        populateCompanies();

        companyAdapter = new ArrayAdapter(this, R.layout.spinner_item);
        locationAdapter = new ArrayAdapter(this, R.layout.spinner_item);

        Spinner companySpinner = (Spinner) findViewById(R.id.spncompany);
        companySpinner.setAdapter(companyAdapter);
        companySpinner.setOnItemSelectedListener((AdapterView.OnItemSelectedListener) this);

        Spinner locationSpinner = (Spinner) findViewById(R.id.spnlocation);
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
        Intent intent = new Intent(this, verify_activity.class);
        startActivity(intent);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()) {
            case R.id.spncompany: {
                CompanyEntity entity = (CompanyEntity) parent.getSelectedItem();
                locationAdapter.clear();
                populateLocations(entity);
                this.companyId = entity.companyId;
                break;
            }
            case R.id.spnlocation: {
                LocationEntity entity = (LocationEntity) parent.getSelectedItem();
                this.locationId = entity.locationId;
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
