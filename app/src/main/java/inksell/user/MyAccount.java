package inksell.user;

import android.content.DialogInterface;
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

import com.squareup.picasso.Picasso;

import java.util.List;

import Constants.AppData;
import adapters.RVAdapter;
import butterknife.InjectView;
import de.hdodenhof.circleimageview.CircleImageView;
import inksell.inksell.R;
import models.BaseActionBarActivity;
import models.PostSummaryEntity;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import services.InksellCallback;
import services.RestClient;
import utilities.NavigationHelper;
import utilities.ResponseStatus;
import utilities.SwipableRecyclerView;
import utilities.Utility;

public class MyAccount extends BaseActionBarActivity implements SwipableRecyclerView.OnSwipeListener{

    private RVAdapter rvAdapter;

    private List<PostSummaryEntity> postSummaryList;

    @InjectView(R.id.toolbar)
    Toolbar toolbar;

    @InjectView(R.id.my_image)
    CircleImageView myImage;

    @InjectView(R.id.my_fab_edit)
    FloatingActionButton fabEditButton;

    @InjectView(R.id.my_name)
    TextView myName;

    @InjectView(R.id.my_email)
    TextView myEmail;

    @InjectView(R.id.my_post_recycler_view)
    RecyclerView rv;

    @InjectView(R.id.my_collapsing_toolbar)
    CollapsingToolbarLayout collapsingToolbarLayout;

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

        Picasso.with(this).load(AppData.UserData.UserImageUrl)
                .into(myImage);

        myName.setText(AppData.UserData.Username);
        myEmail.setText(AppData.UserData.CorporateEmail);
    }

    private void loadMyPostsData() {
        RestClient.get().getMyPostSummary(AppData.UserGuid, new InksellCallback<List<PostSummaryEntity>>() {
            @Override
            public void onSuccess(List<PostSummaryEntity> postSummaryEntities, Response response) {

                layoutErrorTryAgain.setVisibility(View.GONE);
                progressBar.setVisibility(View.GONE);

                postSummaryList = postSummaryEntities;

                if(rvAdapter==null) {
                    RVAdapter adapter = new RVAdapter(postSummaryList, NavigationHelper.cardViewClickListener(rv, postSummaryList, getParent()));
                    adapter.setIsMyPosts(true);
                    rvAdapter = adapter;
                    rv.setAdapter(rvAdapter);
                }
                else {
                    rvAdapter.Update(postSummaryList, NavigationHelper.cardViewClickListener(rv, postSummaryList, getParent()));
                }
            }

            @Override
            public void onFailure(RetrofitError error)
            {
                progressBar.setVisibility(View.GONE);
                layoutErrorTryAgain.setVisibility(View.VISIBLE);
            }
        });
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
                        RestClient.get().deletePost(entity.PostId, AppData.UserGuid, entity.categoryid, deletePostCallback(position));
                        break;
                }
            }
        };
    }

    private Callback<Integer> deletePostCallback(final int position) {
        return new InksellCallback<Integer>() {
            @Override
            public void onSuccess(Integer integer, Response response) {
                ResponseStatus status = ResponseStatus.values()[integer];
                postSummaryList.remove(position);
                rvAdapter.notifyItemRemoved(position);
            }

            @Override
            public void onFailure(RetrofitError error)
            {
                Utility.ShowInfoDialog(R.string.deletePostFailure);
            }
        };
    }
}
