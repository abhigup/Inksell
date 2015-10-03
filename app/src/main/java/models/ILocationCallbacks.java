package models;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Abhinav on 04/10/15.
 */
public interface ILocationCallbacks {

    void loadDefaultLocation(LatLng latLng);

    void handleNewLocation(LatLng latLng);

}
