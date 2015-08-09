package inksell.posts.view;

import android.widget.ImageButton;
import android.widget.TextView;

import java.util.List;

import butterknife.InjectView;
import de.hdodenhof.circleimageview.CircleImageView;
import inksell.inksell.R;
import models.AutomobileEntity;
import models.IPostEntity;
import utilities.Utility;

public class ViewAutomobileFragment extends BaseViewFragment {
    @InjectView(R.id.view_automobile_actualPrice)
    TextView actualPrice;

    @InjectView(R.id.view_automobile_price)
    TextView price;

    @InjectView(R.id.view_automobile_description)
    TextView description;

    @InjectView(R.id.view_automobile_usedperiod)
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

    private AutomobileEntity automobileEntity;

    public ViewAutomobileFragment() {
        // Required empty public constructor
    }

    @Override
    public void setData(IPostEntity postEntity) {
        automobileEntity = (AutomobileEntity)postEntity;
    }

    @Override
    public List<String> getImageUrl() {
        return automobileEntity.PostImagesUrl;
    }

    @Override
    public int getViewResId() {
        return R.layout.fragment_view_automobile;
    }

    @Override
    public void initView() {
        price.setText(Utility.GetLocalCurrencySymbol() + " " + automobileEntity.ExpectedPrice + "  ");
        usedPeriod.setText(Utility.IsStringNullorEmpty(automobileEntity.UsedPeriod)?"-": automobileEntity.UsedPeriod);
        automobileEntity.PostDescription = automobileEntity.PostDescription.replace("\r\n","\n").replace("\r","\n");
        description.setText(Utility.IsStringNullorEmpty(automobileEntity.PostDescription)?"-": automobileEntity.PostDescription);
        actualPrice.setText(Utility.IsStringNullorEmpty(automobileEntity.ActualPrice)?"-":(Utility.GetLocalCurrencySymbol() + " " + automobileEntity.ActualPrice));
        Utility.setUserPic(userPic, automobileEntity.UserImageUrl, automobileEntity.PostedBy);
        postedBy.setText(automobileEntity.PostedBy);
        contactName.setText(automobileEntity.ContactAddress.contactName);

        String address = Utility.IsStringNullorEmpty(automobileEntity.ContactAddress.Address)
                ?(Utility.IsStringNullorEmpty(automobileEntity.ContactAddress.City)
                ?"-"
                : automobileEntity.ContactAddress.City)
                : automobileEntity.ContactAddress.Address +
                (Utility.IsStringNullorEmpty(automobileEntity.ContactAddress.City)
                        ?""
                        : "\n" + automobileEntity.ContactAddress.City);
        contactAddress.setText(address);

        userEmail.setText(automobileEntity.UserOfficialEmail);

        Utility.setCallAndEmailButton(btnCall, btnEmail, automobileEntity.ContactAddress.ContactNumber, automobileEntity.ContactAddress.ContactEmail);
    }
}
