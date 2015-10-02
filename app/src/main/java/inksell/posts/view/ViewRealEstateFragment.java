package inksell.posts.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

import butterknife.InjectView;
import de.hdodenhof.circleimageview.CircleImageView;
import enums.FurnishingType;
import inksell.inksell.R;
import models.IPostEntity;
import models.RealEstateEntity;
import utilities.Utility;

public class ViewRealEstateFragment extends BaseViewFragment implements OnMapReadyCallback {

    @InjectView(R.id.view_property_rent)
    TextView rent;

    @InjectView(R.id.view_property_maintenance)
    TextView maintenance;

    @InjectView(R.id.view_property_bhk)
    TextView bhk;

    @InjectView(R.id.view_property_available)
    TextView availableFrom;

    @InjectView(R.id.view_property_area)
    TextView area;

    @InjectView(R.id.view_property_furnishing)
    TextView furnishing;

    @InjectView(R.id.view_property_property_address)
    TextView propertyAddress;

    @InjectView(R.id.view_property_description)
    TextView description;

    @InjectView(R.id.view_user_pic)
    CircleImageView userPic;

    @InjectView(R.id.view_post_postedBy)
    TextView postedBy;

    @InjectView(R.id.view_post_contactName)
    TextView contactName;

    @InjectView(R.id.view_post_userEmail)
    TextView userEmail;

    @InjectView(R.id.view_post_address)
    TextView contactAddress;

    @InjectView(R.id.water_btn)
    ImageButton waterBtn;
    private boolean isWater = false;

    @InjectView(R.id.power_btn)
    ImageButton powerBtn;
    private boolean isPower = false;

    @InjectView(R.id.internet_btn)
    ImageButton internetBtn;
    private boolean isInternet = false;

    @InjectView(R.id.parking_btn)
    ImageButton parkingBtn;
    private boolean isParking = false;

    @InjectView(R.id.view_map)
    MapView mapView;

    private RealEstateEntity entity;

    public ViewRealEstateFragment() {
        // Required empty public constructor
    }

    @Override
    public void setData(IPostEntity postEntity) {
        entity = (RealEstateEntity)postEntity;
    }

    @Override
    public List<String> getImageUrls() {
        return entity.PostImagesUrl;
    }

    @Override
    public String[] getEmailAndCall() {
        if(entity==null)
            return null;

        String[] str = new String[2];
        str[0] = entity.ContactAddress.ContactEmail;
        str[1] = entity.ContactAddress.ContactNumber;
        return str;
    }

    @Override
    public int getViewResId() {
        return R.layout.fragment_view_real_estate;
    }

    @Override
    public void initView(LayoutInflater inflater, View view, Bundle savedInstanceState) {

        rent.setText(Utility.GetLocalCurrencySymbol() + " " + entity.RentPrice + "  ");
        maintenance.setText(Utility.getStringValue(entity.MaintenancePrice));

        bhk.setText(Utility.getStringValue(entity.Bhk));
        availableFrom.setText(Utility.getStringValue(entity.AvailableFrom));

        area.setText(Utility.getStringValue(entity.Area));
        furnishing.setText(Utility.getStringValue(Utility.getFurnishedString(FurnishingType.values()[entity.FurnishedType])));

        entity.PropertyAddress = Utility.getStringValue(entity.PropertyAddress.replace("\r\n","\n").replace("\r","\n"));
        propertyAddress.setText(entity.PropertyAddress);

        entity.PostDescription = Utility.getStringValue(entity.PostDescription.replace("\r\n","\n").replace("\r","\n"));

        description.setText(Utility.getStringValue(entity.PostDescription));

        Utility.setUserPic(userPic, entity.UserImageUrl, entity.PostedBy);
        postedBy.setText(entity.PostedBy);
        contactName.setText(entity.ContactAddress.contactName);

        String address = Utility.IsStringNullorEmpty(entity.ContactAddress.Address)
                ?Utility.getStringValue(entity.ContactAddress.City)
                : entity.ContactAddress.Address +
                (Utility.IsStringNullorEmpty(entity.ContactAddress.City)
                        ?""
                        : "\n" + entity.ContactAddress.City);
        contactAddress.setText(address);
        userEmail.setText(entity.UserOfficialEmail);

        toggleFeatures();

        Utility.setMap(savedInstanceState, mapView, this);
    }

    private void toggleFeatures()
    {
        isWater = entity.Is24x7Water;
        isPower = entity.IsPowerBackup;
        isInternet = entity.IsInternet;
        isParking = entity.IsParking;

        toggleWaterButton();
        togglePowerButton();
        toggleInternetButton();
        toggleParkingButton();
    }

    private void toggleWaterButton()
    {
        if(isWater) {
            waterBtn.setBackgroundResource(android.R.color.holo_blue_bright);
        }
        else
        {
            waterBtn.setBackgroundResource(R.color.background);
        }
    }

    private void togglePowerButton()
    {
        if(isPower) {
            powerBtn.setBackgroundResource(android.R.color.holo_orange_light);
        }
        else
        {
            powerBtn.setBackgroundResource(R.color.background);
        }
    }

    private void toggleInternetButton()
    {
        if(isInternet) {
            internetBtn.setBackgroundResource(android.R.color.holo_blue_dark);
        }
        else
        {
            internetBtn.setBackgroundResource(R.color.background);
        }
    }

    private void toggleParkingButton()
    {
        if(isParking) {
            parkingBtn.setBackgroundResource(android.R.color.holo_red_light);
        }
        else
        {
            parkingBtn.setBackgroundResource(R.color.background);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        if(entity.latitude!=0 && entity.longitude!=0) {
            LatLng latLng = new LatLng(entity.latitude, entity.longitude);

            googleMap.addMarker(new MarkerOptions().position(new LatLng(entity.latitude, entity.longitude)).title(entity.PostTitle));
            CameraPosition cameraPosition = new CameraPosition.Builder().target(latLng).zoom(13).build();
            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }
    }

    @Override
    public void onResume()
    {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause()
    {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory()
    {
        super.onLowMemory();
        mapView.onLowMemory();
    }
}
