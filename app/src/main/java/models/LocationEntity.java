package models;

/**
 * Created by Abhinav on 06/04/15.
 */
public class LocationEntity {

    public int locationId;

    public String locationName;

    public String countryName;

    @Override
    public String toString() {
        return this.locationName;            // What to display in the Spinner list.
    }

}
