package inksell.posts.view;

import android.view.View;
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

    private AutomobileEntity entity;

    public ViewAutomobileFragment() {
        // Required empty public constructor
    }

    @Override
    public void setData(IPostEntity postEntity) {
        entity = (AutomobileEntity)postEntity;
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
        return R.layout.fragment_view_automobile;
    }

    @Override
    public void initView(View view) {
        price.setText(Utility.GetLocalCurrencySymbol() + " " + entity.ExpectedPrice + "  ");
        usedPeriod.setText(Utility.IsStringNullorEmpty(entity.UsedPeriod)?"-": entity.UsedPeriod);
        entity.PostDescription = entity.PostDescription.replace("\r\n","\n").replace("\r","\n");
        description.setText(Utility.IsStringNullorEmpty(entity.PostDescription)?"-": entity.PostDescription);
        actualPrice.setText(Utility.IsStringNullorEmpty(entity.ActualPrice) ? "-" : (Utility.GetLocalCurrencySymbol() + " " + entity.ActualPrice));
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
