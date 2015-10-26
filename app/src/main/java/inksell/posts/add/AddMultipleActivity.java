package inksell.posts.add;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Constants.AppData;
import Constants.InksellConstants;
import adapters.RVAdapter;
import butterknife.InjectView;
import enums.CategoryType;
import inksell.common.BaseActionBarActivity;
import inksell.inksell.R;
import models.MultiplePostDetailsEntity;
import models.PostIdWithCategory;
import models.PostSummaryEntity;
import services.InksellCallback;
import services.RestClient;
import utilities.NavigationHelper;
import utilities.ResponseStatus;
import utilities.Utility;

public class AddMultipleActivity extends BaseActionBarActivity {

    private RVAdapter rvAdapter;

    private List<PostSummaryEntity> postSummaryList = new ArrayList<>();

    @InjectView(R.id.add_multiple_recycler_view)
    RecyclerView rv;

    @InjectView(R.id.fab_autos)
    FloatingActionButton fab_auto;

    @InjectView(R.id.fab_electronics)
    FloatingActionButton fab_electronics;

    @InjectView(R.id.fab_furniture)
    FloatingActionButton fab_furniture;

    @InjectView(R.id.fab_other)
    FloatingActionButton fab_other;

    @InjectView(R.id.fab_realestate)
    FloatingActionButton fab_realestate;

    @InjectView(R.id.fab_add_button)
    FloatingActionsMenu fabMenu;

    @InjectView(R.id.cvHomeTransparency)
    CardView cvHomeTransparency;

    @InjectView(R.id.add_submit)
    Button submit;

    @InjectView(R.id.add_post_multiple_title)
    EditText title;

    @InjectView(R.id.loading_full_page)
    RelativeLayout loadingFullPage;

    @InjectView(R.id.loading_Text)
    TextView loadingText;

    @InjectView(R.id.toolbar)
    Toolbar toolbar;

    @InjectView(R.id.my_collapsing_toolbar)
    CollapsingToolbarLayout collapsingToolbarLayout;

    MultiplePostDetailsEntity multiplePostDetailsEntity = new MultiplePostDetailsEntity();

    @Override
    protected void initDataAndLayout() {

    }

    @Override
    protected void initActivity() {

        loadingFullPage.setVisibility(View.GONE);
        submit.setEnabled(false);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        collapsingToolbarLayout.setTitleEnabled(false);
        collapsingToolbarLayout.setTitle("");
        toolbar.setTitle("");

        cvHomeTransparency.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fabMenu.collapse();
                cvHomeTransparency.setVisibility(View.GONE);
            }
        });

        fabMenu.setOnFloatingActionsMenuUpdateListener(new FloatingActionsMenu.OnFloatingActionsMenuUpdateListener() {
            @Override
            public void onMenuExpanded() {
                cvHomeTransparency.setVisibility(View.VISIBLE);
            }

            @Override
            public void onMenuCollapsed() {
                cvHomeTransparency.setVisibility(View.GONE);
            }
        });

        fab_auto.setOnClickListener(addPostClick(CategoryType.Automobile));
        fab_electronics.setOnClickListener(addPostClick(CategoryType.Electronics));
        fab_furniture.setOnClickListener(addPostClick(CategoryType.Furniture));
        fab_other.setOnClickListener(addPostClick(CategoryType.Others));
        fab_realestate.setOnClickListener(addPostClick(CategoryType.RealState));

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rv.setLayoutManager(layoutManager);

        submit.setOnClickListener(submitClickListener);
    }

    private View.OnClickListener submitClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(Utility.IsEditTextNullorEmpty(title))
            {
                Utility.ShowToast(R.string.ErrorTitleIsRequired);
                return;
            }

            if(postSummaryList==null || postSummaryList.isEmpty())
            {
                Utility.ShowToast(R.string.ErrorAddMultipleNoPosts);
                return;
            }

            loadingFullPage.setVisibility(View.VISIBLE);
            loadingText.setText(getString(R.string.saving));

            multiplePostDetailsEntity.postTitle = title.getText().toString();
            multiplePostDetailsEntity.userGuid = AppData.UserGuid;
            multiplePostDetailsEntity.associatedPostList = new ArrayList<>();

            for(int i=0;i<postSummaryList.size();i++)
            {
                PostSummaryEntity postSummaryEntity = postSummaryList.get(i);
                PostIdWithCategory postIdWithCategory = new PostIdWithCategory();

                postIdWithCategory.categoryId = postSummaryEntity.categoryid;
                postIdWithCategory.postId = postSummaryEntity.PostId;
                postIdWithCategory.postTitle = postSummaryEntity.Title;

                multiplePostDetailsEntity.associatedPostList.add(postIdWithCategory);
            }

            RestClient.post().addMultiplePost(multiplePostDetailsEntity).enqueue(new InksellCallback<Integer>() {
                @Override
                public void onSuccess(Integer integer) {
                    finish();
                }

                @Override
                public void onError(ResponseStatus responseStatus) {
                    loadingFullPage.setVisibility(View.GONE);
                }
            });

        }
    };

    @Override
    protected int getActivityLayout() {
        return R.layout.activity_add_multiple;
    }

    private View.OnClickListener addPostClick(final CategoryType categoryType) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<String, String> map = new HashMap<>();
                        map.put("category", String.valueOf(categoryType.ordinal()));
                        map.put("multiple", Boolean.TRUE.toString());
                        NavigationHelper.StartActivityForResult(AddMultipleActivity.this, InksellConstants.REQUEST_ADD_MULTIPLE, AddPostActivity.class, map);
            }
        };
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode==RESULT_OK && requestCode==InksellConstants.REQUEST_ADD_MULTIPLE)
        {
            PostSummaryEntity postSummaryEntity = Utility.GetObjectFromJSON(data.getStringExtra("postSummary"), PostSummaryEntity.class);
            postSummaryList.add(postSummaryEntity);

            if (rvAdapter == null) {
                RVAdapter adapter = new RVAdapter(postSummaryList, null);
                adapter.setShowFavToggleButton(false);
                rvAdapter = adapter;
                rv.setAdapter(rvAdapter);
            } else {
                rvAdapter.Update(postSummaryList, null);
            }

            submit.setEnabled(true);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_multiple, menu);
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
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if(postSummaryList==null || postSummaryList.isEmpty()) {
            super.onBackPressed();
        }
        else {
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
    }
}
