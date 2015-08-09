package models;

import java.util.Date;
import java.util.List;

/**
 * Created by Abhinav on 28/07/15.
 */
public class AutomobileEntity implements IPostEntity {

    public int PostId;

    public Date LastModifiedDate;

    public String PostDescription;

    public String PostTitle;

    public String PostedBy;

    public String UserGuid;

    public String UserImageUrl;

    public String UserOfficialEmail;

    public String ActualPrice;

    public String ExpectedPrice;

    public boolean IsSoldOut;

    public boolean IsVisibleToAll;

    public String MakeBrand;

    public List<String> PostImagesUrl;

    public String UsedPeriod;

    public ContactAddressEntity ContactAddress;
}
