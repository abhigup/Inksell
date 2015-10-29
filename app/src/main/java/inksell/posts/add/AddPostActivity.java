package inksell.posts.add;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.app.FragmentTransaction;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import Constants.AppData;
import butterknife.InjectView;
import enums.CategoryType;
import inksell.common.BaseActionBarActivity;
import inksell.inksell.R;
import models.AutomobileEntity;
import models.ElectronicEntity;
import models.FurnitureEntity;
import models.IPostEntity;
import models.OtherEntity;
import models.PostSummaryEntity;
import models.RealEstateEntity;
import retrofit.Call;
import retrofit.Callback;
import services.InksellCallback;
import services.RestClient;
import utilities.ResponseStatus;
import utilities.Utility;

public class AddPostActivity extends BaseActionBarActivity {

    private IPostEntity iPostEntity = null;
    private IPostEntity editableEntity = null;

    private CategoryType categoryType;
    private boolean isMultiple;
    private boolean forEdit;
    private String title;
    private List<String> postImages;

    @InjectView(R.id.add_submit)
    Button submit;

    @InjectView(R.id.loading_full_page)
    RelativeLayout loadingFullPage;

    @InjectView(R.id.loading_Text)
    TextView loadingText;

    BaseAddFragment userDetailsFragment;
    BaseAddFragment imagesFragment;
    BaseAddFragment addPostDetailsFragment;
    BaseAddFragment addRealEstateMapFragment;

    Call<Integer> addUpdateCall = null;

    @Override
    protected void initDataAndLayout() {

    }

    @Override
    protected void setIntentExtras()
    {
        categoryType = CategoryType.values()[Integer.parseInt(intentExtraMap.get("category"))];
        isMultiple = intentExtraMap.containsKey("multiple");
        forEdit = Boolean.parseBoolean(this.intentExtraMap.get("forEdit"));

        if(forEdit) {
            editableEntity = (IPostEntity)Utility.GetObjectFromJSON(this.intentExtraMap.get("entity"), Utility.getClassFromCategory(categoryType));
        }
    }

    @Override
    protected void initActivity() {

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        submit.setOnClickListener(submit_click());
        loadingFullPage.setVisibility(View.GONE);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        int detailsContainer;
        if(categoryType==CategoryType.RealState)
        {
            addPostDetailsFragment = new AddRealEstateDetailsFragment();
            addRealEstateMapFragment = new AddRealEstateMapFragment();

            transaction.add(R.id.add_map_container, addRealEstateMapFragment);
        }
        else
        {
            addPostDetailsFragment = new AddPostDetailsFragment();
        }

        userDetailsFragment = new AddUserDetailsFragment();
        imagesFragment = new AddImagesFragment();

        transaction.add(R.id.add_post_details_container, addPostDetailsFragment);
        transaction.add(R.id.add_images_container, imagesFragment);
        transaction.add(R.id.add_user_details_container, userDetailsFragment);

        if(forEdit) {
            addPostDetailsFragment.setData(editableEntity, categoryType);
            userDetailsFragment.setData(editableEntity, categoryType);
            imagesFragment.setData(editableEntity, categoryType);
            if(addRealEstateMapFragment!=null) {
                addRealEstateMapFragment.setData(editableEntity, categoryType);
            }
            submit.setText(R.string.update);
        }

        transaction.commit();
    }


    private View.OnClickListener submit_click() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingFullPage.setVisibility(View.VISIBLE);
                submit.setEnabled(false);

                if(forEdit) {
                    iPostEntity = editableEntity;
                }
                else
                {
                    switch (categoryType) {
                        case Others:
                            iPostEntity = new OtherEntity();
                            break;
                        case Electronics:
                            iPostEntity = new ElectronicEntity();
                            break;
                        case Automobile:
                            iPostEntity = new AutomobileEntity();
                            break;
                        case RealState:
                            iPostEntity = new RealEstateEntity();
                            break;
                        case Furniture:
                            iPostEntity = new FurnitureEntity();
                            break;
                    }
                }

                boolean isVerified = false;

                if(addPostDetailsFragment.verifyAndGetPost(iPostEntity, categoryType))
                {
                    if(categoryType==CategoryType.RealState) {
                        if(addRealEstateMapFragment.verifyAndGetPost(iPostEntity, categoryType))
                        {

                        }
                        else {
                            loadingFullPage.setVisibility(View.GONE);
                            submit.setEnabled(true);
                            return;
                        }
                    }

                    if(userDetailsFragment.verifyAndGetPost(iPostEntity, categoryType))
                    {
                        loadingText.setText("Uploading Images");
                        if(imagesFragment.verifyAndGetPost(iPostEntity, categoryType))
                        {
                            isVerified = true;
                        }
                    }
                }

                if(!isVerified)
                {
                    loadingFullPage.setVisibility(View.GONE);
                    submit.setEnabled(true);
                }

            }
        };
    }

    @Override
    protected int getActivityLayout() {
        return R.layout.activity_add_post;
    }

    @Override
    public void onBackPressed() {
        if(addPostDetailsFragment.canBeDiscarded()) {
            super.onBackPressed();
        }
        else
        {
            Utility.ShowDialog(getString(R.string.discardAddPost), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which) {
                        case DialogInterface.BUTTON_POSITIVE:

                            if(((AddImagesFragment)imagesFragment).isUploading()) {
                                ((AddImagesFragment) imagesFragment).stopUpload();
                            }

                            if(addUpdateCall!=null) {
                                addUpdateCall.cancel();
                            }

                            finish();
                            break;
                    }
                }
            });
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onImageUploaded(boolean imageUploadSucceeded, final List<String> imagesUrl)
    {
        if(!imageUploadSucceeded)
        {
            loadingFullPage.setVisibility(View.GONE);
            submit.setEnabled(true);
            Utility.ShowToast(R.string.ErrorImageUploadFailed);
            return;
        }

        if(forEdit)
        {
            loadingText.setText(getString(R.string.updating));
        }
        else {
            loadingText.setText(getString(R.string.saving));
        }

        postImages = imagesUrl;
        switch (categoryType)
        {
            case Others: {
                OtherEntity entity = (OtherEntity) iPostEntity;
                title = entity.PostTitle;
                entity.PostImagesUrl = imagesUrl;
                entity.UserGuid = AppData.UserGuid;
                if(forEdit) {
                    addUpdateCall = RestClient.post().updateOtherPost(entity, AppData.UserGuid);
                }
                else {
                    addUpdateCall = RestClient.post().addOtherPost(entity, isMultiple ? 1 : 0);
                }
            }
                break;
            case Automobile: {
                AutomobileEntity entity = (AutomobileEntity) iPostEntity;
                title = entity.PostTitle;
                entity.PostImagesUrl = imagesUrl;
                entity.UserGuid = AppData.UserGuid;
                if(forEdit) {
                    addUpdateCall = RestClient.post().updateAutomobilePost(entity, AppData.UserGuid);
                }
                else {
                    addUpdateCall = RestClient.post().addAutomobilePost(entity, isMultiple ? 1 : 0);
                }
            }
                break;
            case Electronics:{
                ElectronicEntity entity = (ElectronicEntity) iPostEntity;
                title = entity.PostTitle;
                entity.PostImagesUrl = imagesUrl;
                entity.UserGuid = AppData.UserGuid;
                if(forEdit) {
                    addUpdateCall = RestClient.post().updateElectronicsPost(entity, AppData.UserGuid);
                }
                else {
                    addUpdateCall = RestClient.post().addElectronicsPost(entity, isMultiple ? 1 : 0);
                }
            }
                break;
            case Furniture:{
                FurnitureEntity entity = (FurnitureEntity) iPostEntity;
                title = entity.PostTitle;
                entity.PostImagesUrl = imagesUrl;
                entity.UserGuid = AppData.UserGuid;
                if(forEdit) {
                    addUpdateCall = RestClient.post().updateFurniturePost(entity, AppData.UserGuid);
                }
                else {
                    addUpdateCall = RestClient.post().addFurniturePost(entity, isMultiple ? 1 : 0);
                }
            }
                break;
            case RealState:{
                RealEstateEntity entity = (RealEstateEntity) iPostEntity;
                title = entity.PostTitle;
                entity.PostImagesUrl = imagesUrl;
                entity.UserGuid = AppData.UserGuid;
                if(forEdit) {
                    addUpdateCall = RestClient.post().updateRealEstatePost(entity, AppData.UserGuid);
                }
                else {
                    addUpdateCall = RestClient.post().addRealEstatePost(entity, isMultiple ? 1 : 0);
                }
            }
                break;
        }

        addUpdateCall.enqueue(addPost());
    }

    private Callback<Integer> addPost() {
        return new InksellCallback<Integer>() {
            @Override
            public void onSuccess(Integer integer) {
                if(isMultiple && integer>1000)
                {
                    Intent returnIntent = new Intent();
                    PostSummaryEntity postSummaryEntity = new PostSummaryEntity();
                    postSummaryEntity.categoryid = categoryType.ordinal();
                    postSummaryEntity.CompanyId = AppData.UserData.CopmanyId;
                    postSummaryEntity.LocationId = AppData.UserData.LocationId;
                    postSummaryEntity.PostedBy = AppData.UserData.Username;
                    postSummaryEntity.UserImageUrl = AppData.UserData.UserImageUrl;
                    postSummaryEntity.Title = title;
                    postSummaryEntity.PostId = integer;
                    postSummaryEntity.Postdate = Utility.GetUTCdatetimeAsDate();
                    if(postImages!=null && !postImages.isEmpty())
                    {
                        postSummaryEntity.PostDefaultImage = postImages.get(0);
                    }

                    returnIntent.putExtra("postSummary", Utility.GetJSONString(postSummaryEntity));
                    setResult(RESULT_OK, returnIntent);
                }
                finish();
            }

            @Override
            public void onError(ResponseStatus responseStatus)
            {
                loadingFullPage.setVisibility(View.GONE);
                submit.setEnabled(true);
                //Utility.ShowToast(retrofitError.toString());
            }
        };
    }
}
