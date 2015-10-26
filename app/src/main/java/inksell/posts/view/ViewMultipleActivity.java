package inksell.posts.view;

import android.app.Activity;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import Constants.AppData;
import adapters.RVAdapter;
import butterknife.InjectView;
import de.hdodenhof.circleimageview.CircleImageView;
import inksell.common.BaseActionBarActivity;
import inksell.inksell.R;
import models.PostSummaryEntity;
import retrofit.Callback;
import services.InksellCallback;
import services.RestClient;
import utilities.FavouritesHelper;
import utilities.NavigationHelper;
import utilities.ResponseStatus;
import utilities.Utility;

public class ViewMultipleActivity extends BaseActionBarActivity {

    private PostSummaryEntity summaryEntity;
    private List<PostSummaryEntity> postSummaryList;

    private Activity activity = this;

    private RVAdapter rvAdapter;

    @InjectView(R.id.multiple_post_recycler_view)
    RecyclerView rv;

    @InjectView(R.id.tryAgainButton)
    Button tryAgainButton;

    @InjectView(R.id.layout_error_tryAgain)
    RelativeLayout layoutErrorTryAgain;

    @InjectView(R.id.loading_spinner)
    ProgressBar progressBar;

    @InjectView(R.id.multiple_collapsing_toolbar)
    CollapsingToolbarLayout collapsingToolbarLayout;

    @InjectView(R.id.toolbar)
    Toolbar toolbar;

    @InjectView(R.id.multiple_user_image)
    CircleImageView userImage;

    @InjectView(R.id.multiple_user_name)
    TextView userName;

    @Override
    protected void initDataAndLayout() {
        layoutErrorTryAgain.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);

        loadMultiplePosts();
    }

    private void loadMultiplePosts() {
        RestClient.get().getMultipleFullPostEntity(summaryEntity.PostId, AppData.UserGuid).enqueue(setListOnResponse());
    }

    @Override
    protected void initActivity() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        collapsingToolbarLayout.setTitle(summaryEntity.Title);

        tryAgainButton.setOnClickListener(refresh_click());

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rv.setLayoutManager(layoutManager);

        initSummaryView();
    }

    @Override
    protected int getActivityLayout() {
        return R.layout.activity_view_multiple;
    }

    @Override
    protected void setIntentExtras()
    {
        summaryEntity = Utility.GetObjectFromJSON(this.intentExtraMap.get("postSummary"), PostSummaryEntity.class);
    }

    private void initSummaryView() {

        Utility.setUserPic(userImage, summaryEntity.UserImageUrl, summaryEntity.PostedBy);
        userName.setText(summaryEntity.PostedBy);
    }

    private Callback<List<PostSummaryEntity>> setListOnResponse() {
        return new InksellCallback<List<PostSummaryEntity>>() {
            @Override
            public void onSuccess(List<PostSummaryEntity> postSummaryEntities) {

                layoutErrorTryAgain.setVisibility(View.GONE);
                progressBar.setVisibility(View.GONE);

                postSummaryList = postSummaryEntities;
                postSummaryList = FavouritesHelper.setFavourites(postSummaryList);
                postSummaryList = getSummaryEntityWithMultipleInfo(postSummaryList);

                if(rvAdapter==null) {
                    rvAdapter = new RVAdapter(postSummaryList, NavigationHelper.cardViewClickListener(rv, postSummaryList, activity));
                    rvAdapter.setIsMyPosts(true);
                    rv.setAdapter(rvAdapter);
                }
                else {
                    rvAdapter.Update(postSummaryList, NavigationHelper.cardViewClickListener(rv, postSummaryList, activity));
                }
            }

            @Override
            public void onError(ResponseStatus responseStatus)
            {
                progressBar.setVisibility(View.GONE);
                layoutErrorTryAgain.setVisibility(View.VISIBLE);
            }
        };
    }

    private List<PostSummaryEntity> getSummaryEntityWithMultipleInfo(List<PostSummaryEntity> entityList)
    {
        for(int i=0;i<entityList.size();i++)
        {
            entityList.get(i).CompanyId = summaryEntity.CompanyId;
            entityList.get(i).LocationId = summaryEntity.LocationId;
            entityList.get(i).Postdate = summaryEntity.Postdate;
            entityList.get(i).PostedBy = summaryEntity.PostedBy;
        }
        return entityList;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_view_multiple, menu);
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
