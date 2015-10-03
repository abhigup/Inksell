package inksell.posts.add;


import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.HashMap;
import java.util.Map;

import Constants.InksellConstants;
import butterknife.InjectView;
import enums.CategoryType;
import inksell.inksell.R;
import models.IPostEntity;
import utilities.NavigationHelper;
import utilities.Utility;

/**
 * A simple {@link Fragment} subclass.
 */
public class AddRealEstateMapFragment extends BaseAddFragment implements OnMapReadyCallback {

    @InjectView(R.id.add_map)
    MapView mapView;

    @InjectView(R.id.add_property_address)
    EditText propertyAddress;

    @InjectView(R.id.emptyMapView)
    TextView emptyMapView;

    LatLng propertyLatLng;
    GoogleMap map;
    Marker marker;

    public AddRealEstateMapFragment() {
        // Required empty public constructor
    }


    @Override
    public int getViewResId() {
        return R.layout.fragment_add_real_estate_map;
    }

    @Override
    public void initViewAfterSettingEditableView(LayoutInflater inflater, View view, Bundle savedInstanceState) {
        Utility.setMap(savedInstanceState, mapView, this);
    }


    public void setMapData(LatLng latLng, String address)
    {
        propertyAddress.setText(address);
        propertyLatLng = latLng;
        handleLocationChange();
    }

    @Override
    public void setEditableView(LayoutInflater inflater, View view, Bundle savedInstanceState) {

    }

    @Override
    public boolean verifyAndGetPost(IPostEntity iPostEntity, CategoryType categoryType) {
        return false;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.setOnMapClickListener(navigateToMapActivity());
        handleLocationChange();
    }

    private void handleLocationChange()
    {
        if(map==null || propertyLatLng==null)
            return;

        emptyMapView.setVisibility(View.GONE);

        if(marker==null) {
            marker = map.addMarker(new MarkerOptions().position(propertyLatLng));
        }
        marker.setPosition(propertyLatLng);
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(propertyLatLng,
                16));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == InksellConstants.REQUEST_MAP_RESULT && resultCode == getActivity().RESULT_OK)
        {
            double lat = data.getDoubleExtra("lat", 0);
            double lng = data.getDoubleExtra("lng", 0);
            String address = data.getStringExtra("address");

            setMapData(new LatLng(lat, lng), address);
        }
    }


    private GoogleMap.OnMapClickListener navigateToMapActivity() {
        return new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                Map<String , String> map = new HashMap<>();
                map.put("lat", propertyLatLng!=null?String.valueOf(propertyLatLng.latitude):"0");
                map.put("lng", propertyLatLng!=null?String.valueOf(propertyLatLng.longitude):"0");
                NavigationHelper.StartActivityForResultFromFragment(fragment, InksellConstants.REQUEST_MAP_RESULT, MapActivity.class, map);
            }
        };
    }


}
