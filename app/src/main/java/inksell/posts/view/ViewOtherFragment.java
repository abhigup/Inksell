package inksell.posts.view;

import android.app.Fragment;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.List;

import butterknife.InjectView;
import de.hdodenhof.circleimageview.CircleImageView;
import inksell.inksell.R;
import models.AutomobileEntity;
import models.IPostEntity;
import models.OtherEntity;
import utilities.Utility;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ViewOtherFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ViewOtherFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ViewOtherFragment extends BaseViewFragment {
    @InjectView(R.id.view_other_actualPrice)
    TextView actualPrice;

    @InjectView(R.id.view_other_price)
    TextView price;

    @InjectView(R.id.view_other_description)
    TextView description;

    @InjectView(R.id.view_other_usedperiod)
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

    private OtherEntity otherEntity;

    public ViewOtherFragment() {
        // Required empty public constructor
    }

    @Override
    public void setData(IPostEntity postEntity) {
        otherEntity = (OtherEntity)postEntity;
    }

    @Override
    public List<String> getImageUrls() {
        return otherEntity.PostImagesUrl;
    }

    @Override
    public int getViewResId() {
        return R.layout.fragment_view_other;
    }

    @Override
    public void initView(View view) {
        price.setText(Utility.GetLocalCurrencySymbol() + " " + otherEntity.ExpectedPrice + "  ");
        usedPeriod.setText(Utility.IsStringNullorEmpty(otherEntity.UsedPeriod)?"-": otherEntity.UsedPeriod);
        otherEntity.PostDescription = otherEntity.PostDescription.replace("\r\n","\n").replace("\r","\n");
        description.setText(Utility.IsStringNullorEmpty(otherEntity.PostDescription)?"-": otherEntity.PostDescription);
        actualPrice.setText(Utility.IsStringNullorEmpty(otherEntity.ActualPrice)?"-":(Utility.GetLocalCurrencySymbol() + " " + otherEntity.ActualPrice));
        Utility.setUserPic(userPic, otherEntity.UserImageUrl, otherEntity.PostedBy);
        postedBy.setText(otherEntity.PostedBy);
        contactName.setText(otherEntity.ContactAddress.contactName);

        String address = Utility.IsStringNullorEmpty(otherEntity.ContactAddress.Address)
                ?(Utility.IsStringNullorEmpty(otherEntity.ContactAddress.City)
                ?"-"
                : otherEntity.ContactAddress.City)
                : otherEntity.ContactAddress.Address +
                (Utility.IsStringNullorEmpty(otherEntity.ContactAddress.City)
                        ?""
                        : "\n" + otherEntity.ContactAddress.City);
        contactAddress.setText(address);

        userEmail.setText(otherEntity.UserOfficialEmail);

        Utility.setCallAndEmailButton(getActivity(), otherEntity.PostTitle, btnCall, btnEmail, otherEntity.ContactAddress.ContactNumber, otherEntity.ContactAddress.ContactEmail);
    }
}
