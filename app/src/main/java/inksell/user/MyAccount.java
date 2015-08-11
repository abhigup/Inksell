package inksell.user;

import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Constants.AppData;
import adapters.RVAdapter;
import butterknife.ButterKnife;
import butterknife.InjectView;
import de.hdodenhof.circleimageview.CircleImageView;
import inksell.inksell.R;
import inksell.posts.view.ViewPostActivity;
import models.BaseActionBarActivity;
import models.PostSummaryEntity;
import retrofit.RetrofitError;
import retrofit.client.Response;
import services.InksellCallback;
import services.RestClient;
import utilities.SwipableRecyclerView;
import utilities.Utility;

public class MyAccount extends BaseActionBarActivity implements SwipableRecyclerView.OnSwipeListener{

    private RVAdapter rvAdapter;

    private List<PostSummaryEntity> postSummaryList;

    @InjectView(R.id.toolbar)
    Toolbar toolbar;

    @InjectView(R.id.my_image)
    CircleImageView myImage;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_account);
        ButterKnife.inject(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        collapsingToolbarLayout.setTitle(AppData.UserData.Username);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getParent());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rv.setLayoutManager(layoutManager);

        SwipableRecyclerView.setSwipeBehaviour(rv, this);

        initMyData();

        loadMyPostsData();
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
                    RVAdapter adapter = new RVAdapter(postSummaryList, cardViewClickListener());
                    adapter.setIsMyPosts(true);
                    rvAdapter = adapter;
                    rv.setAdapter(adapter);
                }
                else {
                    rvAdapter.Update(postSummaryList, cardViewClickListener());
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

    private View.OnClickListener cardViewClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int childItemPosition = rv.getChildAdapterPosition(v);
                PostSummaryEntity postSummaryEntity = postSummaryList.get(childItemPosition);

                Map<String, String> map = new HashMap<String, String>();
                map.put("object", Utility.GetJSONString(postSummaryEntity));

                if(!postSummaryEntity.HasPostTitlePic())
                {
                    Utility.NavigateTo(ViewPostActivity.class, map);
                }
                else {
                    ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(getParent(), v.findViewById(R.id.card_title_pic), getString(R.string.cardTitlePicTransition));
                    Utility.NavigateTo(ViewPostActivity.class, map, options.toBundle());
                }
            }
        };
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

    public void refresh_click(View view) {
        layoutErrorTryAgain.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);

        loadMyPostsData();
    }

    @Override
    public void onSwiped(int position) {
        postSummaryList.remove(position);
        rvAdapter.notifyItemRemoved(position);
    }
}
