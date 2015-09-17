package inksell.common;

import android.support.v4.view.ViewPager;

import java.util.Arrays;
import java.util.List;

import adapters.FullScreenImageAdapter;
import butterknife.InjectView;
import inksell.inksell.R;
import utilities.Utility;

public class fullscreen_view extends BaseActionBarActivity {

    @InjectView(R.id.pager)
    ViewPager viewPager;

    private FullScreenImageAdapter adapter;

    private List<String> imageUrls;

    @Override
    protected void initDataAndLayout() {

    }

    @Override
    protected void initActivity() {

        adapter = new FullScreenImageAdapter(this,imageUrls);

        viewPager.setAdapter(adapter);
    }

    @Override
    protected int getActivityLayout() {
        return R.layout.activity_fullscreen_view;
    }

    @Override
    protected void setIntentExtras()
    {
        imageUrls = Arrays.asList(Utility.GetObjectFromJSON(this.intentExtraMap.get("images"), String[].class));
    }

}
