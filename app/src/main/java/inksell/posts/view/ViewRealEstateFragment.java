package inksell.posts.view;

import android.view.View;
import android.widget.ImageButton;
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

    @InjectView(R.id.view_post_call)
    ImageButton btnCall;

    @InjectView(R.id.view_post_email)
    ImageButton btnEmail;

    private RealEstateEntity propertyEntity;

    public ViewRealEstateFragment() {
        // Required empty public constructor
    }

    @Override
    public void setData(IPostEntity postEntity) {
        propertyEntity = (RealEstateEntity)postEntity;
    }

    @Override
    public List<String> getImageUrls() {
        return propertyEntity.PostImagesUrl;
    }

    @Override
    public int getViewResId() {
        return R.layout.fragment_view_real_estate;
    }

    @Override
    public void initView(View view) {
        rent.setText(Utility.GetLocalCurrencySymbol() + " " + propertyEntity.RentPrice + "  ");

        propertyEntity.PostDescription = propertyEntity.PostDescription.replace("\r\n","\n").replace("\r","\n");
        description.setText(Utility.IsStringNullorEmpty(propertyEntity.PostDescription)?"-": propertyEntity.PostDescription);

        Utility.setUserPic(userPic, propertyEntity.UserImageUrl, propertyEntity.PostedBy);
        postedBy.setText(propertyEntity.PostedBy);
        contactName.setText(propertyEntity.ContactAddress.contactName);

        String address = Utility.IsStringNullorEmpty(propertyEntity.ContactAddress.Address)
                ?(Utility.IsStringNullorEmpty(propertyEntity.ContactAddress.City)
                ?"-"
                : propertyEntity.ContactAddress.City)
                : propertyEntity.ContactAddress.Address +
                (Utility.IsStringNullorEmpty(propertyEntity.ContactAddress.City)
                        ?""
                        : "\n" + propertyEntity.ContactAddress.City);
        contactAddress.setText(address);

        userEmail.setText(propertyEntity.UserOfficialEmail);

        Utility.setCallAndEmailButton(getActivity(), propertyEntity.PostTitle, btnCall, btnEmail, propertyEntity.ContactAddress.ContactNumber, propertyEntity.ContactAddress.ContactEmail);
    }
}
