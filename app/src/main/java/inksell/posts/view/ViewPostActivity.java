package inksell.posts.view;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.DefaultSliderView;

import Constants.AppData;
import butterknife.ButterKnife;
import butterknife.InjectView;
import enums.CategoryType;
import inksell.inksell.R;
import models.AutomobileEntity;
import models.BaseActionBarActivity;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_post_common);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ButterKnife.inject(this);

        initSummaryView();

        loadFullPost();

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

                if(summaryEntity.HasPostTitlePic() && fragment.getImageUrl()!=null && fragment.getImageUrl().size()>1) {
                    image_slider.removeAllSliders();
                    for (int i = 0; i < fragment.getImageUrl().size(); i++) {
                        DefaultSliderView sliderImageView = new DefaultSliderView(ConfigurationManager.CurrentActivityContext);
                        sliderImageView
                                .image(fragment.getImageUrl().get(i)).setScaleType(BaseSliderView.ScaleType.CenterCrop);
                        image_slider.addSlider(sliderImageView);
                    }


                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        public void run() {
                            if (fragment.getImageUrl().size() > 1) {
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
        summaryEntity = Utility.GetObjectFromJSON(this.intentExtraMap.get("object"), PostSummaryEntity.class);
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

    public void refresh_click(View view) {
        layoutErrorTryAgain.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);

        loadFullPost();
    }
}
