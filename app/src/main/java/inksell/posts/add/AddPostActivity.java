package inksell.posts.add;

import android.support.v4.app.FragmentTransaction;
import android.view.MenuItem;
import android.widget.Button;

import butterknife.InjectView;
import enums.CategoryType;
import inksell.common.BaseActionBarActivity;
import inksell.inksell.R;

public class AddPostActivity extends BaseActionBarActivity {

    private CategoryType categoryType;
    private boolean isMultiple;

    @InjectView(R.id.add_submit)
    Button submit;

    @Override
    protected void initDataAndLayout() {

    }

    @Override
    protected void setIntentExtras()
    {
        categoryType = CategoryType.values()[Integer.parseInt(intentExtraMap.get("category"))];
        isMultiple = intentExtraMap.containsKey("multiple");
    }

    @Override
    protected void initActivity() {

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        submit.setOnClickListener(null);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        BaseAddFragment addPostDetailsFragment;

        int detailsContainer;
        if(categoryType==CategoryType.RealState)
        {
            addPostDetailsFragment = new AddRealEstateDetailsFragment();
            AddRealEstateMapFragment addRealEstateMapFragment = new AddRealEstateMapFragment();
            transaction.add(R.id.add_map_container, addRealEstateMapFragment);
        }
        else
        {
            addPostDetailsFragment = new AddPostDetailsFragment();
        }
        transaction.add(R.id.add_post_details_container, addPostDetailsFragment);

        transaction.commit();
    }

    @Override
    protected int getActivityLayout() {
        return R.layout.activity_add_post;
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
}
