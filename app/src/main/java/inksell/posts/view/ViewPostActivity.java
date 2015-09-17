package inksell.posts.view;

import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.DefaultSliderView;

import java.util.HashMap;
import java.util.Map;

import Constants.AppData;
import butterknife.InjectView;
import enums.CategoryType;
import inksell.common.fullscreen_view;
import inksell.inksell.R;
import models.AutomobileEntity;
import inksell.common.BaseActionBarActivity;
import models.ElectronicEntity;
import models.FurnitureEntity;
import models.IPostEntity;
import models.OtherEntity;
import models.PostSummaryEntity;
import models.RealEstateEntity;
import retrofit.RetrofitError;
import retrofit.client.Response;
import services.InksellCallback;
import services.RestClient;
import utilities.ConfigurationManager;
import utilities.FavouritesHelper;
import utilities.NavigationHelper;
import utilities.Utility;

public class ViewPostActivity extends BaseActionBarActivity {

    private PostSummaryEntity summaryEntity;

    @InjectView(R.id.view_post_image_slider)
    SliderLayout image_slider;

    @InjectView(R.id.view_post_title)
    TextView postTitle;

    @InjectView(R.id.view_post_postedon)
    TextView postedOn;

    @InjectView(R.id.loading_spinner)
    ProgressBar progressBar;

    @InjectView(R.id.layout_error_tryAgain)
    RelativeLayout layoutErrorTryAgain;

    @InjectView(R.id.view_fav_toggle)
    ToggleButton favButton;

    @InjectView(R.id.tryAgainButton)
    Button tryAgainButton;

    @Override
    protected void initDataAndLayout() {
        layoutErrorTryAgain.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);

        loadFullPost();
    }

    @Override
    protected void initActivity() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        tryAgainButton.setOnClickListener(refresh_click());

        initSummaryView();
    }

    @Override
    protected int getActivityLayout() {
        return R.layout.activity_view_post_common;
    }

    private void initSummaryView() {

        DefaultSliderView defaultSliderView = new DefaultSliderView(this);

        if(summaryEntity.HasPostTitlePic()) {
            defaultSliderView
                    .image(summaryEntity.PostDefaultImage).setScaleType(BaseSliderView.ScaleType.CenterCrop);
        }
        else
        {
            int defaultImageResId;
            switch (summaryEntity.categoryid)
            {
                default:
                    defaultImageResId = R.drawable.splash;
            }
            defaultSliderView
                    .image(defaultImageResId).setScaleType(BaseSliderView.ScaleType.CenterCrop);
        }
        image_slider.addSlider(defaultSliderView);
        image_slider.stopAutoCycle();
        if(image_slider.getChildCount()>1) {
            image_slider.startAutoCycle();
        }

        postTitle.setText(summaryEntity.Title);

        postedOn.setText(Utility.StringDateToRelativeStringDate(summaryEntity.Postdate));

        //set Favourite Toggle Button
        if(FavouritesHelper.IsFavourite(summaryEntity.PostId))
        {
            favButton.setChecked(true);
        }

        favButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                {
                    FavouritesHelper.AddToFavourites(summaryEntity);
                }
                else
                {
                    FavouritesHelper.RemoveFromFavourites(summaryEntity.PostId);
                }
            }
        });
    }

    @Override
    protected void onStop() {
        image_slider.stopAutoCycle();
        super.onStop();
    }

    private void loadFullPost() {

        Fragment fragment = null;
        switch (CategoryType.values()[summaryEntity.categoryid])
        {
            case Automobile:
                RestClient.get()
                        .getAutomobilesFullPostEntity(summaryEntity.PostId, AppData.UserGuid, ViewFragmentsCallback(AutomobileEntity.class, new ViewAutomobileFragment()));
                break;
            case Electronics:
                RestClient.get()
                        .getElectronicsFullPostEntity(summaryEntity.PostId, AppData.UserGuid, ViewFragmentsCallback(ElectronicEntity.class, new ViewElectronicsFragment()));
                break;
            case Furniture:
                RestClient.get()
                        .getFurnitureFullPostEntity(summaryEntity.PostId, AppData.UserGuid, ViewFragmentsCallback(FurnitureEntity.class, new ViewFurnitureFragment()));
                break;
            case Multiple:
                break;
            case Others:
                RestClient.get()
                        .getOtherFullPostEntity(summaryEntity.PostId, AppData.UserGuid, ViewFragmentsCallback(OtherEntity.class, new ViewOtherFragment()));
                break;
            case RealState:
                RestClient.get()
                        .getRealEstateFullPostEntity(summaryEntity.PostId, AppData.UserGuid, ViewFragmentsCallback(RealEstateEntity.class, new ViewRealEstateFragment()));
                break;
        }
    }

    private <T> InksellCallback<T> ViewFragmentsCallback(Class<T> clazz, final BaseViewFragment fragment) {
        return new InksellCallback<T>() {
            @Override
            public void onSuccess(T t, Response response) {

                layoutErrorTryAgain.setVisibility(View.GONE);
                progressBar.setVisibility(View.GONE);

                fragment.setData((IPostEntity)t);
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.view_fragment_container, fragment)
                        .commit();

                if(fragment.getImageUrls()!=null && fragment.getImageUrls().size()>0) {
                    image_slider.removeAllSliders();
                    for (int i = 0; i < fragment.getImageUrls().size(); i++) {
                        DefaultSliderView sliderImageView = new DefaultSliderView(ConfigurationManager.CurrentActivityContext);
                        sliderImageView
                                .image(fragment.getImageUrls().get(i)).setScaleType(BaseSliderView.ScaleType.CenterCrop);
                        sliderImageView.setOnSliderClickListener(new BaseSliderView.OnSliderClickListener() {
                            @Override
                            public void onSliderClick(BaseSliderView baseSliderView) {
                                Map<String, String> map = new HashMap<String, String>();
                                map.put("images", Utility.GetJSONString(fragment.getImageUrls()));
                                NavigationHelper.NavigateTo(fullscreen_view.class, map);
                            }
                        });
                        image_slider.addSlider(sliderImageView);
                    }


                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        public void run() {
                            if (fragment.getImageUrls().size() > 1) {
                                image_slider.startAutoCycle();
                            }
                        }
                    }, 5000);


                }
            }

            @Override
            public void onFailure(RetrofitError error)
            {
                progressBar.setVisibility(View.GONE);
                layoutErrorTryAgain.setVisibility(View.VISIBLE);
            }
        };
    }


    @Override
    protected void setIntentExtras()
    {
        summaryEntity = Utility.GetObjectFromJSON(this.intentExtraMap.get("postSummary"), PostSummaryEntity.class);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_view_post_common, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                super.onBackPressed();
                return true;
            case R.id.action_forward:
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


}
