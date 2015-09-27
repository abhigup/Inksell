package inksell.posts.add;

import android.content.DialogInterface;
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
import models.IPostEntity;
import models.OtherEntity;
import retrofit.RetrofitError;
import retrofit.client.Response;
import services.InksellCallback;
import services.RestClient;
import utilities.Utility;

public class AddPostActivity extends BaseActionBarActivity {

    private IPostEntity iPostEntity = null;
    private IPostEntity editableEntity = null;

    private CategoryType categoryType;
    private boolean isMultiple;
    private boolean forEdit;


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
        transaction.add(R.id.add_post_details_container, addPostDetailsFragment);

        userDetailsFragment = (BaseAddFragment)getSupportFragmentManager().findFragmentById(R.id.add_user_details_container);
        imagesFragment = (BaseAddFragment)getSupportFragmentManager().findFragmentById(R.id.add_images_container);

        if(forEdit) {
            addPostDetailsFragment.setData(editableEntity, categoryType);
            userDetailsFragment.setData(editableEntity, categoryType);
            imagesFragment.setData(editableEntity, categoryType);
            if(addRealEstateMapFragment!=null) {
                addRealEstateMapFragment.setData(editableEntity, categoryType);
            }
        }

        transaction.commit();
    }

    private View.OnClickListener submit_click() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingFullPage.setVisibility(View.VISIBLE);
                submit.setEnabled(false);

                switch (categoryType)
                {
                    case Others:
                        iPostEntity = new OtherEntity();
                        break;
                }

                boolean isVerified = false;

                if(addPostDetailsFragment.verifyAndGetPost(iPostEntity, categoryType))
                {
                    if(((BaseAddFragment)userDetailsFragment).verifyAndGetPost(iPostEntity, categoryType))
                    {
                        loadingText.setText("Uploading Images");
                        if(((BaseAddFragment)imagesFragment).verifyAndGetPost(iPostEntity, categoryType))
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
        Utility.ShowDialog(getString(R.string.discardAddPost), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        finish();
                        break;
                }
            }
        });
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

        loadingText.setText("Saving Post");
        switch (categoryType)
        {
            case Others:
                ((OtherEntity)iPostEntity).PostImagesUrl = imagesUrl;
                ((OtherEntity)iPostEntity).UserGuid = AppData.UserGuid;
                RestClient.post().addOtherPost((OtherEntity)iPostEntity, isMultiple?1:0, new InksellCallback<Integer>() {
                    @Override
                    public void onSuccess(Integer integer, Response response) {
                        finish();
                    }

                    @Override
                    public void onFailure(RetrofitError retrofitError)
                    {
                        loadingFullPage.setVisibility(View.GONE);
                        submit.setEnabled(true);
                        Utility.ShowToast(retrofitError.toString());
                    }
                });
        }
    }
}
