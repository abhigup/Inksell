package inksell.posts.view;

import android.os.Handler;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.DefaultSliderView;

import java.util.HashMap;
import java.util.Map;

import Constants.AppData;
import butterknife.InjectView;
import enums.CategoryType;
import inksell.common.BaseActionBarActivity;
import inksell.common.fullscreen_view;
import inksell.inksell.R;
import models.AutomobileEntity;
import models.ElectronicEntity;
import models.FurnitureEntity;
import models.IPostEntity;
import models.OtherEntity;
import models.PostSummaryEntity;
import models.RealEstateEntity;
import services.InksellCallback;
import services.RestClient;
import utilities.ConfigurationManager;
import utilities.FavouritesHelper;
import utilities.NavigationHelper;
import utilities.Utility;

public class ViewPostActivity extends BaseActionBarActivity {

    private PostSummaryEntity summaryEntity;
    private IPostEntity iPostEntity = null;

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

    @InjectView(R.id.tryAgainButton)
    Button tryAgainButton;

    @InjectView(R.id.view_fab_edit)
    FloatingActionButton fabEdit;

    @InjectView(R.id.toolbar)
    Toolbar toolbar;

    @InjectView(R.id.view_post_call)
    ImageButton btnCall;

    @InjectView(R.id.view_post_email)
    ImageButton btnEmail;

    @InjectView(R.id.my_collapsing_toolbar)
    CollapsingToolbarLayout collapsingToolbarLayout;

    @Override
    protected void initDataAndLayout() {
        layoutErrorTryAgain.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);

        loadFullPost();
    }

    @Override
    protected void initActivity() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        //Set fab edit button
        if(!summaryEntity.isEditable)
        {
            CoordinatorLayout.LayoutParams p = (CoordinatorLayout.LayoutParams) fabEdit.getLayoutParams();
            p.setAnchorId(View.NO_ID);
            fabEdit.setLayoutParams(p);
            fabEdit.setVisibility(View.GONE);
        }

        collapsingToolbarLayout.setTitleEnabled(false);
        collapsingToolbarLayout.setTitle("");
        toolbar.setTitle("");

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

        postTitle.setText(summaryEntity.Title);

        postedOn.setText(Utility.StringDateToRelativeStringDate(summaryEntity.Postdate));

    }

    @Override
    protected void onStop() {
        image_slider.stopAutoCycle();
        super.onStop();
    }

    private void loadFullPost() {

        Fragment fragment = null;
        switch (CategoryType.values()[summaryEntity.categoryid]) {
            case Automobile:
                RestClient.get()
                        .getAutomobilesFullPostEntity(summaryEntity.PostId, AppData.UserGuid).enqueue(ViewFragmentsCallback(AutomobileEntity.class, new ViewAutomobileFragment()));
                break;
            case Electronics:
                RestClient.get()
                        .getElectronicsFullPostEntity(summaryEntity.PostId, AppData.UserGuid).enqueue(ViewFragmentsCallback(ElectronicEntity.class, new ViewElectronicsFragment()));
                break;
            case Furniture:
                RestClient.get()
                        .getFurnitureFullPostEntity(summaryEntity.PostId, AppData.UserGuid).enqueue(ViewFragmentsCallback(FurnitureEntity.class, new ViewFurnitureFragment()));
                break;
            case Multiple:
                break;
            case Others:
                RestClient.get()
                        .getOtherFullPostEntity(summaryEntity.PostId, AppData.UserGuid).enqueue(ViewFragmentsCallback(OtherEntity.class, new ViewOtherFragment()));
                break;
            case RealState:
                RestClient.get()
                        .getRealEstateFullPostEntity(summaryEntity.PostId, AppData.UserGuid).enqueue(ViewFragmentsCallback(RealEstateEntity.class, new ViewRealEstateFragment()));
                break;
        }
    }

    private <T> InksellCallback<T> ViewFragmentsCallback(Class<T> clazz, final BaseViewFragment fragment) {
        return new InksellCallback<T>() {
            @Override
            public void onSuccess(T t) {

                layoutErrorTryAgain.setVisibility(View.GONE);
                progressBar.setVisibility(View.GONE);

                iPostEntity = (IPostEntity)t;
                fragment.setData(iPostEntity);

                getSupportFragmentManager().beginTransaction()
                        .add(R.id.view_fragment_container, fragment)
                        .commit();

                if(fragment.getEmailAndCall()!=null) {
                    Utility.setCallAndEmailButton(summaryEntity.Title, btnCall, btnEmail, fragment.getEmailAndCall()[1], fragment.getEmailAndCall()[0]);
                }

                //Set Edit FAB Button
                if(summaryEntity.isEditable)
                {
                    fabEdit.setVisibility(View.VISIBLE);
                    fabEdit.setOnClickListener(NavigationHelper.addPostClick(CategoryType.values()[summaryEntity.categoryid], true, iPostEntity));
                }

                //Set Image Slider View
                if (fragment.getImageUrls() != null && fragment.getImageUrls().size() > 0) {

                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        public void run() {

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

                            if (fragment.getImageUrls().size() > 1) {
                                image_slider.startAutoCycle();
                            }
                        }
                    }, 900);


                }
            }

            @Override
            public void onError()
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
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem item = menu.findItem(R.id.action_favourite);
        if(FavouritesHelper.IsFavourite(summaryEntity.PostId))
        {
            item.setChecked(true);
            item.setIcon(R.drawable.ic_favorite_on);
        }
        else {
            item.setChecked(false);
            item.setIcon(R.drawable.ic_favorite_off);
        }

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
            case R.id.action_favourite:
                item.setChecked(!item.isChecked());
                if(item.isChecked())
                {
                    FavouritesHelper.AddToFavourites(summaryEntity);
                    item.setIcon(R.drawable.ic_favorite_on);
                }
                else
                {
                    FavouritesHelper.RemoveFromFavourites(summaryEntity.PostId);
                    item.setIcon(R.drawable.ic_favorite_off);
                }
        }
        return super.onOptionsItemSelected(item);
    }
}
