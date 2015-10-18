package inksell.user;

import android.content.DialogInterface;
import android.graphics.Color;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
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
import utilities.NavigationHelper;
import utilities.ResponseStatus;
import utilities.SwipableRecyclerView;
import utilities.Utility;

public class MyAccount extends BaseActionBarActivity implements SwipableRecyclerView.OnSwipeListener{

    private RVAdapter rvAdapter;

    private List<PostSummaryEntity> postSummaryList;

    @InjectView(R.id.my_post_recycler_view)
    RecyclerView rv;

    @InjectView(R.id.toolbar)
    Toolbar toolbar;

    @InjectView(R.id.my_collapsing_toolbar)
    CollapsingToolbarLayout collapsingToolbarLayout;

    @InjectView(R.id.my_fab_edit)
    FloatingActionButton fabEditButton;

    @InjectView(R.id.my_image)
    CircleImageView myImage;

    @InjectView(R.id.my_name)
    TextView myName;

    @InjectView(R.id.my_email)
    TextView myEmail;

    @InjectView(R.id.loading_spinner)
    ProgressBar progressBar;

    @InjectView(R.id.layout_error_tryAgain)
    RelativeLayout layoutErrorTryAgain;

    @InjectView(R.id.tryAgainButton)
    Button tryAgainButton;

    @Override
    protected void initDataAndLayout() {
        layoutErrorTryAgain.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);

        initMyData();

        loadMyPostsData();
    }

    @Override
    protected void initActivity() {

        fabEditButton.setOnClickListener(editFabClicked());

        tryAgainButton.setOnClickListener(refresh_click());

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        collapsingToolbarLayout.setTitle(AppData.UserData.Username);
        collapsingToolbarLayout.setExpandedTitleColor(Color.argb(0,255,255,255));

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rv.setLayoutManager(layoutManager);

        SwipableRecyclerView.setSwipeBehaviour(rv, this);
    }


    @Override
    protected int getActivityLayout() {
        return R.layout.activity_my_account;
    }


    private View.OnClickListener editFabClicked() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavigationHelper.NavigateTo(EditMyDetails.class);
            }
        };
    }

    private void initMyData() {

        Utility.setUserPic(myImage, AppData.UserData.UserImageUrl, AppData.UserData.Username);

        myName.setText(AppData.UserData.Username);
        myEmail.setText(AppData.UserData.CorporateEmail);
    }

    private void loadMyPostsData() {
        RestClient.get().getMyPostSummary(AppData.UserGuid).enqueue(new InksellCallback<List<PostSummaryEntity>>() {
            @Override
            public void onSuccess(List<PostSummaryEntity> postSummaryEntities) {

                layoutErrorTryAgain.setVisibility(View.GONE);
                progressBar.setVisibility(View.GONE);

                postSummaryList = setIsEditable(postSummaryEntities);

                if (rvAdapter == null) {
                    RVAdapter adapter = new RVAdapter(postSummaryList, NavigationHelper.cardViewClickListener(rv, postSummaryList, getParent()));
                    adapter.setIsMyPosts(true);
                    rvAdapter = adapter;
                    rv.setAdapter(rvAdapter);
                } else {
                    rvAdapter.Update(postSummaryList, NavigationHelper.cardViewClickListener(rv, postSummaryList, getParent()));
                }
            }

            @Override
            public void onError() {
                progressBar.setVisibility(View.GONE);
                layoutErrorTryAgain.setVisibility(View.VISIBLE);
            }
        });
    }

    private List<PostSummaryEntity> setIsEditable(List<PostSummaryEntity> entities)
    {
        for(int i=0;i<entities.size();i++)
        {
            entities.get(i).isEditable = true;
        }
        return entities;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_my_account, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                super.onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSwiped(int position) {

        Utility.ShowDialog(getString(R.string.deletePostConfirmation), deletePost(position));
    }

    private DialogInterface.OnClickListener deletePost(final int position) {

        final PostSummaryEntity entity = postSummaryList.get(position);

        return new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        RestClient.get().deletePost(entity.PostId, AppData.UserGuid, entity.categoryid).enqueue(deletePostCallback(position));
                        break;
                    case DialogInterface.BUTTON_NEGATIVE:
                        refresh();
                }
            }
        };
    }

    private Callback<Integer> deletePostCallback(final int position) {
        return new InksellCallback<Integer>() {
            @Override
            public void onSuccess(Integer integer) {
                ResponseStatus status = ResponseStatus.values()[integer];
                postSummaryList.remove(position);
                rvAdapter.notifyItemRemoved(position);
            }

            @Override
            public void onError()
            {
                Utility.ShowInfoDialog(R.string.deletePostFailure);
            }
        };
    }
}
