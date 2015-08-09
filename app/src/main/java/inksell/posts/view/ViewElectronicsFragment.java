package inksell.posts.view;

import android.widget.ImageButton;
import android.widget.TextView;

import java.util.List;

import butterknife.InjectView;
import de.hdodenhof.circleimageview.CircleImageView;
import inksell.inksell.R;
import models.ElectronicEntity;
import models.IPostEntity;
import utilities.Utility;

public class ViewElectronicsFragment extends BaseViewFragment {

    @InjectView(R.id.view_electronic_actualPrice)
    TextView actualPrice;

    @InjectView(R.id.view_electronic_price)
    TextView price;

    @InjectView(R.id.view_electronic_description)
    TextView description;

    @InjectView(R.id.view_electronic_usedperiod)
    TextView usedPeriod;

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

    private ElectronicEntity electronicEntity;

    public ViewElectronicsFragment() {
        // Required empty public constructor
    }

    @Override
    public void setData(IPostEntity postEntity) {
        electronicEntity = (ElectronicEntity)postEntity;
    }

    @Override
    public List<String> getImageUrl() {
        return electronicEntity.PostImagesUrl;
    }

    @Override
    public int getViewResId() {
        return R.layout.fragment_view_electronics;
    }

    @Override
    public void initView() {
        price.setText(Utility.GetLocalCurrencySymbol() + " " + electronicEntity.ExpectedPrice + "  ");
        usedPeriod.setText(Utility.IsStringNullorEmpty(electronicEntity.UsedPeriod)?"-":electronicEntity.UsedPeriod);
        electronicEntity.PostDescription = electronicEntity.PostDescription.replace("\r\n","\n").replace("\r","\n");
        description.setText(Utility.IsStringNullorEmpty(electronicEntity.PostDescription)?"-":electronicEntity.PostDescription);
        actualPrice.setText(Utility.IsStringNullorEmpty(electronicEntity.ActualPrice)?"-":(Utility.GetLocalCurrencySymbol() + " " + electronicEntity.ActualPrice));
        Utility.setUserPic(userPic, electronicEntity.UserImageUrl, electronicEntity.PostedBy);
        postedBy.setText(electronicEntity.PostedBy);
        contactName.setText(electronicEntity.ContactAddress.contactName);

        String address = Utility.IsStringNullorEmpty(electronicEntity.ContactAddress.Address)
                ?(Utility.IsStringNullorEmpty(electronicEntity.ContactAddress.City)
                    ?"-"
                    :electronicEntity.ContactAddress.City)
                :electronicEntity.ContactAddress.Address +
                        (Utility.IsStringNullorEmpty(electronicEntity.ContactAddress.City)
                            ?""
                            : "\n" + electronicEntity.ContactAddress.City);
        contactAddress.setText(address);

        userEmail.setText(electronicEntity.UserOfficialEmail);

        Utility.setCallAndEmailButton(btnCall, btnEmail, electronicEntity.ContactAddress.ContactNumber, electronicEntity.ContactAddress.ContactEmail);
    }

}
