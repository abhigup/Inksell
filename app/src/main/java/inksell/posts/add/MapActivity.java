package inksell.posts.add;

import android.content.Intent;
import android.content.IntentSender;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import Constants.InksellConstants;
import adapters.PlaceAutocompleteAdapter;
import butterknife.InjectView;
import inksell.common.BaseFragmentActivity;
import inksell.inksell.R;
import utilities.Utility;

public class MapActivity extends BaseFragmentActivity implements LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, ResultCallback<LocationSettingsResult>, GoogleMap.OnCameraChangeListener {

    @InjectView(R.id.map_autocomplete)
    AutoCompleteTextView mAutocompleteView;

    @InjectView(R.id.map_cancel)
    Button cancel;

    @InjectView(R.id.map_done)
    Button done;

    @InjectView(R.id.button_clear)
    ImageButton clear;

    LatLng propertyLatLng;

    GoogleMap map;
    private LocationRequest mLocationRequest;

    private GoogleApiClient mGoogleApiClient;

    private PlaceAutocompleteAdapter mAdapter;
    public static final String TAG = MapActivity.class.getSimpleName();

    private LatLngBounds latLngBounds;
    private boolean setFromPrediction = false;

    @Override
    protected void initDataAndLayout() {

    }

    @Override
    protected void initActivity(){
        setUpMapIfNeeded();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .addApi(Places.GEO_DATA_API)
                .build();

        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(10 * 1000)        // 10 seconds, in milliseconds
                .setFastestInterval(1 * 1000); // 1 second, in milliseconds

        cancel.setOnClickListener(clickListener);

        done.setOnClickListener(clickListener);

        // Register a listener that receives callbacks when a suggestion has been selected

        mAutocompleteView.setOnItemClickListener(mAutocompleteClickListener);

        clear.setOnClickListener(clickListener);

        checkLocationSettings();

    }

    View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId())
            {
                case R.id.button_clear:
                    mAutocompleteView.setText("");
                    break;
                case R.id.map_done:
                    if (propertyLatLng == null) {
                        Utility.ShowToast("No Location selected");
                        return;
                    }

                    Intent returnIntent = new Intent();
                    returnIntent.putExtra("lat", propertyLatLng.latitude);
                    returnIntent.putExtra("lng", propertyLatLng.longitude);
                    returnIntent.putExtra("address", mAutocompleteView.getText().toString());

                    setResult(RESULT_OK, returnIntent);
                    finish();
                    break;
                case R.id.map_cancel:
                    onBackPressed();
                    break;
            }
        }
    };

    private AdapterView.OnItemClickListener mAutocompleteClickListener
            = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            setFromPrediction = true;
            /*
             Retrieve the place ID of the selected item from the Adapter.
             The adapter stores each Place suggestion in a AutocompletePrediction from which we
             read the place ID and title.
              */
            final AutocompletePrediction item = mAdapter.getItem(position);
            final String placeId = item.getPlaceId();
            /*
             Issue a request to the Places Geo Data API to retrieve a Place object with additional
             details about the place.
              */
            PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
                    .getPlaceById(mGoogleApiClient, placeId);
            placeResult.setResultCallback(mUpdatePlaceDetailsCallback);
            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    };

    private ResultCallback<PlaceBuffer> mUpdatePlaceDetailsCallback
            = new ResultCallback<PlaceBuffer>() {
        @Override
        public void onResult(PlaceBuffer places) {
            if (!places.getStatus().isSuccess()) {
                // Request did not complete successfully
                Log.e(TAG, "Place query did not complete. Error: " + places.getStatus().toString());
                places.release();
                return;
            }
            // Get the Place object from the buffer.
            final Place place = places.get(0);
            propertyLatLng = place.getLatLng();

            handleNewLocation();
            places.release();
        }
    };

    private void checkLocationSettings()
    {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);
        builder.setAlwaysShow(true);

        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());
        result.setResultCallback(this);
    }

    @Override
    protected int getActivityLayout() {
        return R.layout.activity_map;
    }

    private void zoom() {
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(propertyLatLng,
                15));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == InksellConstants.REQUEST_CHECK_SETTINGS && resultCode == RESULT_OK)
        {


        }
    }

    @Override
    protected void setIntentExtras()
    {
        double lat = Double.parseDouble(intentExtraMap.get("lat"));
        double lng = Double.parseDouble(intentExtraMap.get("lng"));
        if(lat!=0 && lng!=0) {
            propertyLatLng = new LatLng(lat, lng);
        }
        else
        {
            propertyLatLng = null;
        }
    }

    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (map == null) {
            // Try to obtain the map from the SupportMapFragment.
            map = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();

        }
    }

    @Override
    public void onLocationChanged(Location location) {
        propertyLatLng = new LatLng(location.getLatitude(),
                location.getLongitude());
        handleNewLocation();
    }

    @Override
    public void onConnected(Bundle bundle) {

        Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (location == null) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, (com.google.android.gms.location.LocationListener) this);
        }
        else {
            propertyLatLng = new LatLng(location.getLatitude(),
                    location.getLongitude());
            handleNewLocation();
        }
    }

    private void handleNewLocation() {
        zoom();
        latLngBounds = new LatLngBounds(propertyLatLng, propertyLatLng);
        map.setOnCameraChangeListener(this);
        if(mAdapter==null) {
            mAdapter = new PlaceAutocompleteAdapter(this, mGoogleApiClient, latLngBounds,
                    null);
            mAutocompleteView.setAdapter(mAdapter);
        }
        else
        {
            mAdapter.setBounds(latLngBounds);
        }
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            mGoogleApiClient.disconnect();
        }
    }

    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        if (connectionResult.hasResolution()) {
            try {
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(this, CONNECTION_FAILURE_RESOLUTION_REQUEST);
            } catch (IntentSender.SendIntentException e) {
                e.printStackTrace();
            }
        } else {
            Log.i(TAG, "Location services connection failed with code " + connectionResult.getErrorCode());
        }
    }

    @Override
    public void onResult(LocationSettingsResult locationSettingsResult) {
        final Status status = locationSettingsResult.getStatus();
        switch (status.getStatusCode()) {
            case LocationSettingsStatusCodes.SUCCESS:

                // NO need to show the dialog;

                break;

            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                //  Location settings are not satisfied. Show the user a dialog

                try {
                    // Show the dialog by calling startResolutionForResult(), and check the result
                    // in onActivityResult().

                    status.startResolutionForResult(this, InksellConstants.REQUEST_CHECK_SETTINGS);

                } catch (IntentSender.SendIntentException e) {

                    //unable to execute request
                }
                break;

            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                // Location settings are inadequate, and cannot be fixed here. Dialog not created
                break;
        }
    }

    @Override
    public void onCameraChange(CameraPosition cameraPosition) {
        propertyLatLng = cameraPosition.target;
        if(!setFromPrediction) {
            mAutocompleteView.setFocusable(false);
            mAutocompleteView.setFocusableInTouchMode(false);
            mAutocompleteView.setText(getReverseGeocodeAddress(propertyLatLng));
            mAutocompleteView.setFocusable(true);
            mAutocompleteView.setFocusableInTouchMode(true);
        }
        setFromPrediction = false;
    }

    private String getReverseGeocodeAddress(LatLng latlng)
    {
        Address address = null;
        try {
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(
                    latlng.latitude,
                    latlng.longitude,
                    1);
            address = (addresses==null || addresses.size()==0)?null:addresses.get(0);
        } catch (IOException ioException) {

        } catch (IllegalArgumentException illegalArgumentException) {

        }

        if(address==null)
        {
            return null;
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i <= address.getMaxAddressLineIndex(); i++) {
            if (i > 0) {
                sb.append(',');
            }

            String line = address.getAddressLine(i);
            if (Utility.IsStringNullorEmpty(line)) {

            } else {
                sb.append(line);
            }
        }

        return sb.toString();
    }
}
