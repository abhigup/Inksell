package inksell.posts.add;

import android.support.v7.widget.CardView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;

import java.util.HashMap;
import java.util.Map;

import butterknife.InjectView;
import enums.CategoryType;
import inksell.common.BaseActionBarActivity;
import inksell.inksell.R;
import utilities.NavigationHelper;

public class AddMultipleActivity extends BaseActionBarActivity {

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

    @Override
    protected void initDataAndLayout() {

    }

    @Override
    protected void initActivity() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

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
    }

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
                        NavigationHelper.NavigateTo(AddPostActivity.class, map);
            }
        };
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
}
