package inksell.inksell;

import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import Constants.AppData;
import butterknife.ButterKnife;
import butterknife.InjectView;
import de.hdodenhof.circleimageview.CircleImageView;
import enums.CategoryType;
import inksell.search.SearchResultsActivity;
import inksell.user.MyAccount;
import inksell.user.SubscriptionFragment;
import models.BaseActionBarActivity;
import models.UserEntity;
import retrofit.client.Response;
import services.InksellCallback;
import services.RestClient;
import utilities.Utility;

public class Home extends BaseActionBarActivity implements HomeListFragment.OnFragmentInteractionListener{
    @InjectView(R.id.drawer_layout)
    DrawerLayout mDrawer;

    @InjectView(R.id.toolbar)
    Toolbar toolbar;

    @InjectView(R.id.nvView)
    NavigationView nvDrawer;

    @InjectView(R.id.circleView)
    CircleImageView circleView;

    @InjectView(R.id.navHeadEmail)
    TextView navHeadEmail;

    @InjectView(R.id.navHeadName)
    TextView navHeadName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        ButterKnife.inject(this);

        init();
    }

    private void init()
    {
        setupDrawerContent(nvDrawer);

        // Set a Toolbar to replace the ActionBar.
        setSupportActionBar(toolbar);

        // Set the menu icon instead of the launcher icon.
        final ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.ic_menu);
        ab.setDisplayHomeAsUpEnabled(true);

        selectDrawerItem(nvDrawer.getMenu().getItem(0));
        loadUserData();


    }

    private void loadUserData()
    {
        RestClient.get().getUserDetails(AppData.UserGuid, new InksellCallback<UserEntity>() {
            @Override
            public void onSuccess(UserEntity userEntity, Response response) {
                AppData.UserData = userEntity;

                navHeadEmail.setText(userEntity.CorporateEmail);
                navHeadName.setText(userEntity.Username);

                Picasso.with(getApplicationContext()).load(userEntity.UserImageUrl)
                        .into(circleView);
            }
        });

    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        selectDrawerItem(menuItem);
                        return true;
                    }
                });
    }

    public void selectDrawerItem(MenuItem menuItem) {
        // Create a new fragment and specify the planet to show based on
        // position
        Fragment fragment = null;

        Class fragmentClass;
        switch(menuItem.getItemId()) {
            case R.id.nav_home:
                fragmentClass = HomeListFragment.class;
                break;
            case R.id.nav_subscriptions:
                fragmentClass = SubscriptionFragment.class;
                break;
            case R.id.nav_favourites:
                fragmentClass = FavouriteFragment.class;
                break;
            case R.id.nav_my_account:
                Utility.NavigateTo(MyAccount.class);
                return;
            case R.id.nav_logout:
                Utility.ShowDialog("Are you sure you want to logout?", logout());
                return;
            default:
                fragmentClass = HomeListFragment.class;
        }

        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Insert the fragment by replacing any existing fragment
        this.getSupportFragmentManager().beginTransaction().replace(R.id.flContent, fragment).commit();

        // Highlight the selected item, update the title, and close the drawer
        menuItem.setChecked(true);
        setTitle(menuItem.getTitle());
        mDrawer.closeDrawers();
    }

    private DialogInterface.OnClickListener logout() {
        return new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:

                        break;

                    case DialogInterface.BUTTON_NEGATIVE:

                        break;
                }
            }
        };
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // The action bar home/up action should open or close the drawer.
        CategoryType filterType;

        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawer.openDrawer(GravityCompat.START);
                return true;
            case R.id.menu_search:
                Utility.NavigateTo(SearchResultsActivity.class);
                return true;
            case R.id.filter_all:
                return setFilteredList(CategoryType.AllCategory);
            case R.id.filter_autos:
                return setFilteredList(CategoryType.Automobile);
            case R.id.filter_electronics:
                return setFilteredList(CategoryType.Electronics);
            case R.id.filter_furniture:
                return setFilteredList(CategoryType.Furniture);
            case R.id.filter_other:
                return setFilteredList(CategoryType.Others);
            case R.id.filter_real_estate:
                return setFilteredList(CategoryType.RealState);
        }

        return super.onOptionsItemSelected(item);
    }

    private boolean setFilteredList(CategoryType type)
    {
        HomeListFragment homeListFragment = (HomeListFragment)
                getSupportFragmentManager().findFragmentById(R.id.flContent);
        homeListFragment.setFilteredList(type);

        return true;
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onBackPressed()
    {
        if(mDrawer.isDrawerOpen(GravityCompat.START))
        {
            mDrawer.closeDrawers();
            return;
        }
        super.onBackPressed();
    }
}
