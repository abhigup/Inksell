package models;

import java.util.Date;
import java.util.List;

/**
 * Created by Abhinav on 27/07/15.
 */
public class ElectronicEntity implements IPostEntity {

    public int PostId;

    public Date LastModifiedDate;

    public String PostDescription;

    public String PostTitle;

    public String PostedBy;

    public String UserGuid;

    public String UserImageUrl;

    public String UserOfficialEmail;

    public int ActualPrice;

    public int ExpectedPrice;

    public boolean IsSoldOut;

    public boolean IsVisibleToAll;

    public String MakeBrand;

    public List<String> PostImagesUrl;

    public String UsedPeriod;

    public ContactAddressEntity ContactAddress;
}
