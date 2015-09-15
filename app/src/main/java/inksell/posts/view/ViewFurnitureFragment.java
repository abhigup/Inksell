package inksell.posts.view;

import android.widget.ImageButton;
import android.widget.TextView;

import java.util.List;

import butterknife.InjectView;
import de.hdodenhof.circleimageview.CircleImageView;
import inksell.inksell.R;
import models.FurnitureEntity;
import models.IPostEntity;
import utilities.Utility;

public class ViewFurnitureFragment extends BaseViewFragment {

    @InjectView(R.id.view_furniture_actualPrice)
    TextView actualPrice;

    @InjectView(R.id.view_furniture_price)
    TextView price;

    @InjectView(R.id.view_furniture_description)
    TextView description;

    @InjectView(R.id.view_furniture_usedperiod)
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

    private FurnitureEntity furnitureEntity;

    public ViewFurnitureFragment() {
        // Required empty public constructor
    }

    @Override
    public void setData(IPostEntity postEntity) {
        furnitureEntity = (FurnitureEntity)postEntity;
    }

    @Override
    public List<String> getImageUrls() {
        return furnitureEntity.PostImagesUrl;
    }

    @Override
    public int getViewResId() {
        return R.layout.fragment_view_furniture;
    }

    @Override
    public void initView() {
        price.setText(Utility.GetLocalCurrencySymbol() + " " + furnitureEntity.ExpectedPrice + "  ");
        usedPeriod.setText(Utility.IsStringNullorEmpty(furnitureEntity.UsedPeriod)?"-": furnitureEntity.UsedPeriod);
        furnitureEntity.PostDescription = furnitureEntity.PostDescription.replace("\r\n","\n").replace("\r","\n");
        description.setText(Utility.IsStringNullorEmpty(furnitureEntity.PostDescription)?"-": furnitureEntity.PostDescription);
        actualPrice.setText(Utility.IsStringNullorEmpty(furnitureEntity.ActualPrice)?"-":(Utility.GetLocalCurrencySymbol() + " " + furnitureEntity.ActualPrice));

        Utility.setUserPic(userPic, furnitureEntity.UserImageUrl, furnitureEntity.PostedBy);

        postedBy.setText(furnitureEntity.PostedBy);
        contactName.setText(furnitureEntity.ContactAddress.contactName);

        String address = Utility.IsStringNullorEmpty(furnitureEntity.ContactAddress.Address)
                ?(Utility.IsStringNullorEmpty(furnitureEntity.ContactAddress.City)
                ?"-"
                : furnitureEntity.ContactAddress.City)
                : furnitureEntity.ContactAddress.Address +
                (Utility.IsStringNullorEmpty(furnitureEntity.ContactAddress.City)
                        ?""
                        : "\n" + furnitureEntity.ContactAddress.City);
        contactAddress.setText(address);

        userEmail.setText(furnitureEntity.UserOfficialEmail);

        Utility.setCallAndEmailButton(btnCall, btnEmail, furnitureEntity.ContactAddress.ContactNumber, furnitureEntity.ContactAddress.ContactEmail);
    }
}
