package inksell.posts.add;

import android.support.v4.app.FragmentTransaction;
import android.view.MenuItem;
import android.widget.Button;

import butterknife.InjectView;
import inksell.inksell.R;
import models.BaseActionBarActivity;

public class AddPostActivity extends BaseActionBarActivity {

    @InjectView(R.id.add_submit)
    Button submit;

    @Override
    protected void initDataAndLayout() {

    }

    @Override
    protected void initActivity() {

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        submit.setOnClickListener(null);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        AddPostDetailsFragment addPostDetailsFragment = new AddPostDetailsFragment();
        AddImagesFragment addImagesFragment = new AddImagesFragment();
        AddUserDetailsFragment addUserDetailsFragment = new AddUserDetailsFragment();

        transaction.add(R.id.add_post_details_container, addPostDetailsFragment);
        transaction.add(R.id.add_images_container, addImagesFragment);
        transaction.add(R.id.add_user_details_container, addUserDetailsFragment);

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
