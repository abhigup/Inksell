package utilities;

import android.app.Activity;
import android.content.IntentSender;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.widget.AutoCompleteTextView;

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
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import Constants.InksellConstants;
import adapters.PlaceAutocompleteAdapter;
import models.ILocationCallbacks;

/**
 * Created by Abhinav on 03/10/15.
 */
public class LocationWrapper implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, ResultCallback<LocationSettingsResult>, LocationListener{

    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private Activity activity;
    ILocationCallbacks iLocationCallbacks;
    private PlaceAutocompleteAdapter mAdapter;
    private LatLng defaultLatlng;
    private boolean showMyLocation = false;

    public LocationWrapper(Activity activityParam, ILocationCallbacks locationCallbacks, LatLng latLng)
    {
        activity = activityParam;
        iLocationCallbacks = locationCallbacks;
        defaultLatlng = latLng;

        mGoogleApiClient = new GoogleApiClient.Builder(ConfigurationManager.CurrentActivityContext)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .addApi(Places.GEO_DATA_API)
                .build();

        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(10 * 1000)        // 10 seconds, in milliseconds
                .setFastestInterval(1 * 1000); // 1 second, in milliseconds
    }

    public GoogleApiClient getGoogleApiClient()
    {
        return mGoogleApiClient;
    }

    public LocationRequest getLocationRequest()
    {
        return mLocationRequest;
    }

    public boolean getShowMyLocation(){
        return showMyLocation;
    }

    public void setShowMyLocation(boolean showMyLocation){
        this.showMyLocation = showMyLocation;
    }

    public void checkLocationSettings()
    {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);
        builder.setAlwaysShow(true);

        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());
        result.setResultCallback(this);
    }

    @Override
    public void onConnected(Bundle bundle) {
        if(defaultLatlng==null) {
            Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            if (location == null) {
                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, (com.google.android.gms.location.LocationListener) this);
            } else {
                iLocationCallbacks.handleNewLocation(new LatLng(location.getLatitude(),
                        location.getLongitude()));
            }
        }
        else
        {
            iLocationCallbacks.loadDefaultLocation(defaultLatlng);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        if (connectionResult.hasResolution()) {
            try {
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(activity, CONNECTION_FAILURE_RESOLUTION_REQUEST);
            } catch (IntentSender.SendIntentException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onResult(LocationSettingsResult locationSettingsResult) {
        final Status status = locationSettingsResult.getStatus();
        switch (status.getStatusCode()) {
            case LocationSettingsStatusCodes.SUCCESS:
                showMyLocation = true;
                break;

            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                //  Location settings are not satisfied. Show the user a dialog

                try {
                    // Show the dialog by calling startResolutionForResult(), and check the result
                    // in onActivityResult().

                    status.startResolutionForResult(activity, InksellConstants.REQUEST_CHECK_SETTINGS);

                } catch (IntentSender.SendIntentException e) {

                    //unable to execute request
                }
                break;

            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                // Location settings are inadequate, and cannot be fixed here. Dialog not created
                break;
        }
    }

    public void onPause()
    {
        if (mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            mGoogleApiClient.disconnect();
        }
    }

    public void onResume()
    {
        mGoogleApiClient.connect();
    }

    @Override
    public void onLocationChanged(Location location) {
        iLocationCallbacks.handleNewLocation(new LatLng(location.getLatitude(),
                location.getLongitude()));
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
    }

    public void updateLocationFromPlaceId(String placeId)
    {
        PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
                .getPlaceById(mGoogleApiClient, placeId);
        placeResult.setResultCallback(mUpdatePlaceDetailsCallback);
    }

    private ResultCallback<PlaceBuffer> mUpdatePlaceDetailsCallback
            = new ResultCallback<PlaceBuffer>() {
        @Override
        public void onResult(PlaceBuffer places) {
            if (!places.getStatus().isSuccess()) {
                // Request did not complete successfully
                places.release();
                return;
            }
            // Get the Place object from the buffer.
            final Place place = places.get(0);
            iLocationCallbacks.handleNewLocation(place.getLatLng());

            places.release();
        }
    };

    public void updateAdapterBounds(AutoCompleteTextView mAutoCompleteTextView, LatLng latLng)
    {
        LatLngBounds latLngBounds = new LatLngBounds(latLng, latLng);
        if(mAdapter==null) {
            mAdapter = new PlaceAutocompleteAdapter(ConfigurationManager.CurrentActivityContext, mGoogleApiClient, latLngBounds,
                    null);
            mAutoCompleteTextView.setAdapter(mAdapter);
        }
        else
        {
            mAdapter.setBounds(latLngBounds);
        }
    }

    public AutocompletePrediction getItemAtPosition(int position) {
        if(mAdapter==null)
            return null;

        return mAdapter.getItem(position);
    }

    public static String getReverseGeocodeAddress(LatLng latlng)
    {
        Address address = null;
        try {
            Geocoder geocoder = new Geocoder(ConfigurationManager.CurrentActivityContext, Locale.getDefault());
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
