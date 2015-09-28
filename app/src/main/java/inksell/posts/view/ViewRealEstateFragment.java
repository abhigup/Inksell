package inksell.posts.view;

import android.view.View;
import android.widget.TextView;

import java.util.List;

import butterknife.InjectView;
import de.hdodenhof.circleimageview.CircleImageView;
import inksell.inksell.R;
import models.IPostEntity;
import models.RealEstateEntity;
import utilities.Utility;

public class ViewRealEstateFragment extends BaseViewFragment {

    @InjectView(R.id.view_property_rent)
    TextView rent;

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
    public void initView(View view) {
        rent.setText(Utility.GetLocalCurrencySymbol() + " " + entity.RentPrice + "  ");

        entity.PostDescription = entity.PostDescription.replace("\r\n","\n").replace("\r","\n");
        description.setText(Utility.IsStringNullorEmpty(entity.PostDescription) ? "-" : entity.PostDescription);

        Utility.setUserPic(userPic, entity.UserImageUrl, entity.PostedBy);
        postedBy.setText(entity.PostedBy);
        contactName.setText(entity.ContactAddress.contactName);

        String address = Utility.IsStringNullorEmpty(entity.ContactAddress.Address)
                ?(Utility.IsStringNullorEmpty(entity.ContactAddress.City)
                ?"-"
                : entity.ContactAddress.City)
                : entity.ContactAddress.Address +
                (Utility.IsStringNullorEmpty(entity.ContactAddress.City)
                        ?""
                        : "\n" + entity.ContactAddress.City);
        contactAddress.setText(address);

        userEmail.setText(entity.UserOfficialEmail);

    }
}
