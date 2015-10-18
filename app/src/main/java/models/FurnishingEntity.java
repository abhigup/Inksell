package models;

import enums.FurnishingType;
import utilities.Utility;

/**
 * Created by Abhinav on 17/10/15.
 */
public class FurnishingEntity {

    public FurnishingType type;

    String furnishingString;

    public FurnishingEntity(FurnishingType furnishingType)
    {
        type = furnishingType;
        furnishingString = Utility.getFurnishedString(furnishingType);
    }

    @Override
    public String toString() {
        return this.furnishingString;            // What to display in the Spinner list.
    }
}
