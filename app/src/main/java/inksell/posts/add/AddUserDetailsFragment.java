package inksell.posts.add;


import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import Constants.AppData;
import butterknife.InjectView;
import de.hdodenhof.circleimageview.CircleImageView;
import enums.CategoryType;
import inksell.inksell.R;
import models.AutomobileEntity;
import models.ContactAddressEntity;
import models.ElectronicEntity;
import models.FurnitureEntity;
import models.IPostEntity;
import models.OtherEntity;
import models.RealEstateEntity;
import utilities.Utility;

/**
 * A simple {@link Fragment} subclass.
 */
public class AddUserDetailsFragment extends BaseAddFragment {

    @InjectView(R.id.add_post_contact_person)
    EditText contactPerson;

    @InjectView(R.id.add_post_contact_number)
    EditText contactNumber;

    @InjectView(R.id.add_post_contact_email)
    EditText contactEmail;

    @InjectView(R.id.add_post_contact_address)
    EditText contactAddress;

    @InjectView(R.id.add_post_contact_city)
    EditText contactCity;

    @InjectView(R.id.checkBoxUseMyContactInfo)
    CheckBox useMyContactInfo;

    @InjectView(R.id.my_image)
    CircleImageView myImage;

    @InjectView(R.id.my_name)
    TextView myName;

    public AddUserDetailsFragment() {
        // Required empty public constructor
    }


    @Override
    public int getViewResId() {
        return R.layout.fragment_add_user_details;
    }

    @Override
    public void initViewAfterSettingEditableView(LayoutInflater inflater, View view, Bundle savedInstanceState) {

        Picasso.with(getActivity()).load(AppData.UserData.UserImageUrl)
                .into(myImage);

        myName.setText(AppData.UserData.Username);

        useMyContactInfo.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked && AppData.UserData!=null)
                {
                    contactPerson.setText(AppData.UserData.Username);
                    contactNumber.setText(AppData.UserData.PhoneNumber);
                    contactEmail.setText(AppData.UserData.PersonalEmail);
                    contactAddress.setText(AppData.UserData.Address);
                    contactCity.setText(AppData.UserData.City);
                }
            }
        });

    }

    @Override
    public void setEditableView(LayoutInflater inflater, View view, Bundle savedInstanceState) {
        switch (categoryType) {
            case Electronics:
                setContactDetails(((ElectronicEntity)iPostEntity).ContactAddress);
                break;
            case Automobile:
                setContactDetails(((AutomobileEntity)iPostEntity).ContactAddress);
                break;
            case Furniture:
                setContactDetails(((FurnitureEntity)iPostEntity).ContactAddress);
                break;
            case RealState:
                setContactDetails(((RealEstateEntity)iPostEntity).ContactAddress);
                break;
            case Others:
                setContactDetails(((OtherEntity)iPostEntity).ContactAddress);
                break;
        }
    }

    private void setContactDetails(ContactAddressEntity contactAddressEntity)
    {
        contactPerson.setText(contactAddressEntity.contactName);
        contactAddress.setText(contactAddressEntity.Address);
        contactNumber.setText(contactAddressEntity.ContactNumber);
        contactCity.setText(contactAddressEntity.City);
        contactEmail.setText(contactAddressEntity.ContactEmail);
    }

    @Override
    public boolean verifyAndGetPost(IPostEntity iPostEntity, CategoryType categoryType) {
        if(Utility.IsStringNullorEmpty(contactEmail.getText().toString()) && Utility.IsStringNullorEmpty(contactNumber.getText().toString()))
        {
            Utility.ShowToast(R.string.ErrorEmailOrNumberRequired);
            return false;
        }

        ContactAddressEntity contactAddressEntity = new ContactAddressEntity();
        contactAddressEntity.Address = contactAddress.getText().toString();
        contactAddressEntity.City = contactCity.getText().toString();
        contactAddressEntity.ContactEmail = contactEmail.getText().toString();
        contactAddressEntity.contactName = contactPerson.getText().toString();
        contactAddressEntity.ContactNumber = contactNumber.getText().toString();

        switch (categoryType)
        {
            case Others: {
                OtherEntity entity = (OtherEntity) iPostEntity;
                entity.ContactAddress = contactAddressEntity;
                break;
            }
            case Automobile:{
                AutomobileEntity entity = (AutomobileEntity) iPostEntity;
                entity.ContactAddress = contactAddressEntity;
                break;
            }
            case Electronics:{
                ElectronicEntity entity = (ElectronicEntity) iPostEntity;
                entity.ContactAddress = contactAddressEntity;
                break;
            }
            case RealState:{
                RealEstateEntity entity = (RealEstateEntity) iPostEntity;
                entity.ContactAddress = contactAddressEntity;
                break;
            }
            case Furniture:{
                FurnitureEntity entity = (FurnitureEntity) iPostEntity;
                entity.ContactAddress = contactAddressEntity;
                break;
            }
        }
        return true;
    }
}
