package inksell.posts.add;

import android.content.Intent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

import java.util.TimerTask;

import Constants.InksellConstants;
import butterknife.InjectView;
import inksell.common.BaseFragmentActivity;
import inksell.inksell.R;
import models.ILocationCallbacks;
import utilities.LocationWrapper;
import utilities.TimerHelper;
import utilities.Utility;

public class MapActivity extends BaseFragmentActivity implements ILocationCallbacks, GoogleMap.OnCameraChangeListener {

    @InjectView(R.id.map_autocomplete)
    AutoCompleteTextView mAutocompleteView;

    @InjectView(R.id.load_address)
    ProgressBar loadAddress;

    @InjectView(R.id.map_cancel)
    Button cancel;

    @InjectView(R.id.map_done)
    Button done;

    @InjectView(R.id.button_clear)
    ImageButton clear;

    @InjectView(R.id.my_location_btn)
    ImageButton myLocationBtn;

    LatLng propertyLatLng;
    String defaultPropertyAddress;

    GoogleMap map;

    TimerHelper timerHelper;

    private boolean setFromPrediction = false;

    private LocationWrapper locationWrapper;

    @Override
    protected void initDataAndLayout() {

    }

    @Override
    protected void initActivity(){
        setUpMapIfNeeded();

        locationWrapper = new LocationWrapper(this, this, propertyLatLng);
        timerHelper = new TimerHelper(3000);

        cancel.setOnClickListener(clickListener);

        done.setOnClickListener(clickListener);

        // Register a listener that receives callbacks when a suggestion has been selected

        mAutocompleteView.setOnItemClickListener(mAutocompleteClickListener);

        clear.setOnClickListener(clickListener);

        locationWrapper.checkLocationSettings();

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
            final AutocompletePrediction item = locationWrapper.getItemAtPosition(position);
            if(item==null)
                return;

            final String placeId = item.getPlaceId();
            locationWrapper.updateLocationFromPlaceId(placeId);

            //Close Keyboard
            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    };




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
            locationWrapper.setShowMyLocation(true);
            myLocationBtn.setVisibility(View.VISIBLE);
        }
        if (requestCode == InksellConstants.REQUEST_CHECK_SETTINGS && resultCode == RESULT_CANCELED)
        {
            onBackPressed();
        }
    }

    @Override
    protected void setIntentExtras()
    {
        double lat = Double.parseDouble(intentExtraMap.get("lat"));
        double lng = Double.parseDouble(intentExtraMap.get("lng"));
        defaultPropertyAddress = intentExtraMap.get("address");
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


            map.setOnCameraChangeListener(this);

            map.setMyLocationEnabled(true);

            myLocationBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    locationWrapper.onLocationChanged(map.getMyLocation());
                }
            });
        }
    }


    @Override
    public void loadDefaultLocation(LatLng latLng) {
        propertyLatLng = latLng;
        setFromPrediction = true;
        mAutocompleteView.setText(defaultPropertyAddress);
        myLocationBtn.setVisibility(View.VISIBLE);
        zoom();
        locationWrapper.updateAdapterBounds(mAutocompleteView, latLng);
    }

    @Override
    public void handleNewLocation(LatLng latLng) {
        propertyLatLng = latLng;
        zoom();
        locationWrapper.updateAdapterBounds(mAutocompleteView, latLng);
        myLocationBtn.setVisibility(View.VISIBLE);
    }


    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
        if(locationWrapper!=null) {
            locationWrapper.onResume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(locationWrapper!=null) {
            locationWrapper.onPause();
        }
    }



    @Override
    public void onCameraChange(CameraPosition cameraPosition) {
        propertyLatLng = cameraPosition.target;


        if (!setFromPrediction) {
            {
                loadAddress.setVisibility(View.VISIBLE);
                clear.setVisibility(View.GONE);

                TimerTask timerTask = new TimerTask() {
                    @Override
                    public void run() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mAutocompleteView.setFocusable(false);
                                mAutocompleteView.setFocusableInTouchMode(false);
                                mAutocompleteView.setText(LocationWrapper.getReverseGeocodeAddress(propertyLatLng));
                                mAutocompleteView.setFocusable(true);
                                mAutocompleteView.setFocusableInTouchMode(true);
                                loadAddress.setVisibility(View.GONE);
                                clear.setVisibility(View.VISIBLE);
                            }
                        });
                    }
                };
                timerHelper.startTask(timerTask);
            }
        }
        setFromPrediction = false;
    }
}
