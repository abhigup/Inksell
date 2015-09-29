package models;

import java.util.Date;
import java.util.List;

/**
 * Created by Abhinav on 28/07/15.
 */
public class RealEstateEntity implements IPostEntity {

    public int PostId;

    public String UserGuid;

    public String UserOfficialEmail;

    public String UserImageUrl;

    public String PostDescription;

    public String PostedBy;

    public ContactAddressEntity ContactAddress;

    public String PropertyAddress;

    public String PostTitle;

    public boolean IsRent;

    public boolean Is24x7Water;

    public boolean IsParking;

    public boolean IsPowerBackup;

    public String Bhk;

    public boolean IsInternet;

    public String AvailableFrom;

    public String Area;

    public String PricePerSqFt;

    public String RentPrice;

    public String MaintenancePrice;

    public Date LastModifiedDate;

    public List<String> PostImagesUrl;

    public double latitude;

    public double longitude;

    public int FurnishedType;

    public boolean IsSoldOut;

    public boolean IsVisibleToAll;

}
