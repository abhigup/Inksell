package inksell.posts.view;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.DefaultSliderView;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.hdodenhof.circleimageview.CircleImageView;
import inksell.inksell.R;
import models.BaseActionBarActivity;
import models.PostSummaryEntity;
import utilities.Utility;

public class ViewPostCommon extends BaseActionBarActivity {

    private PostSummaryEntity summaryEntity;

    @InjectView(R.id.view_post_image_slider)
    SliderLayout image_slider;

    @InjectView(R.id.view_user_pic)
    CircleImageView userPic;

    @InjectView(R.id.view_post_title)
    TextView postTitle;

    @InjectView(R.id.view_post_postedby)
    TextView postedByWithTimeStamp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_post_common);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ButterKnife.inject(this);

        initSummaryView(savedInstanceState);
        loadFullPost();
    }

    private void initSummaryView(Bundle savedInstanceState) {

        DefaultSliderView defaultSliderView = new DefaultSliderView(this);

        if(summaryEntity.HasPostTitlePic()) {
            defaultSliderView
                    .image(summaryEntity.PostTitlePic).setScaleType(BaseSliderView.ScaleType.CenterCrop);
        }
        else
        {
            int defaultImageResId;
            switch (summaryEntity.CategoryId)
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

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.view_fragment_container, new PlaceholderFragment())
                    .commit();
        }
        Utility.setUserPic(userPic, summaryEntity.UserImageUrl, summaryEntity.PostedBy);
        postTitle.setText(summaryEntity.Title);

        postedByWithTimeStamp.setText(summaryEntity.PostedBy + " \u2022 " + Utility.StringDateToRelativeStringDate(summaryEntity.Postdate));
    }

    @Override
    protected void onStop() {
        image_slider.stopAutoCycle();
        super.onStop();
    }

    private void loadFullPost() {

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

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_view_post_common, container, false);
            return rootView;
        }
    }
}
