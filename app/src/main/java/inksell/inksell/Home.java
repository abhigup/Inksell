package inksell.inksell;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;

import Constants.AppData;
import Constants.StorageConstants;
import butterknife.InjectView;
import de.hdodenhof.circleimageview.CircleImageView;
import enums.CategoryType;
import inksell.common.BaseActionBarActivity;
import inksell.search.SearchResultsActivity;
import inksell.user.MyAccount;
import inksell.user.SubscriptionFragment;
import models.SubscriptionEntity;
import retrofit.Callback;
import services.InksellCallback;
import services.RestClient;
import utilities.ConfigurationManager;
import utilities.LocalStorageHandler;
import utilities.NavigationHelper;
import utilities.ResponseStatus;
import utilities.Utility;

public class Home extends BaseActionBarActivity{

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
    protected void initDataAndLayout() {
        loadUserData();
    }

    @Override
    protected void initActivity() {

        setupDrawerContent(nvDrawer);

        // Set a Toolbar to replace the ActionBar.
        setSupportActionBar(toolbar);

        // Set the menu icon instead of the launcher icon.
        final ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.ic_menu);
        ab.setDisplayHomeAsUpEnabled(true);

        selectDrawerItem(nvDrawer.getMenu().getItem(0));

    }

    @Override
    protected int getActivityLayout() {
        return R.layout.activity_home;
    }

    private void loadUserData()
    {
        navHeadEmail.setText(AppData.UserData.CorporateEmail);
        navHeadName.setText(AppData.UserData.Username);

        Utility.setUserPic(circleView, AppData.UserData.UserImageUrl, AppData.UserData.Username);

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
                NavigationHelper.NavigateTo(MyAccount.class);
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
                        LocalStorageHandler.ClearAll();
                        SubscriptionEntity[] subscriptionEntities = LocalStorageHandler.GetData(StorageConstants.SubscriptionTagEntities, SubscriptionEntity[].class);
                        if(subscriptionEntities!=null && subscriptionEntities.length>0)
                        {
                            RestClient.post().removeListedSubscriptionV2(new ArrayList(Arrays.asList(subscriptionEntities))).enqueue(logoutCallBack());
                        }
                        else {
                            logoutSucess();
                        }
                        break;
                }
            }
        };
    }

    private Callback logoutCallBack() {
        return new InksellCallback<Integer>() {
            @Override
            public void onSuccess(Integer response) {
                logoutSucess();
            }

            @Override
            public void onError(ResponseStatus responseStatus) {
                Utility.ShowToast(R.string.logoutFailure);
            }
        };
    }

    private void logoutSucess()
    {
        Utility.ShowToast(R.string.logoutSuccess);
        NavigationHelper.NavigateTo(StartPage.class, true);
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
                NavigationHelper.NavigateTo(SearchResultsActivity.class);
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
    public void onResume()
    {
        super.onResume();
        ConfigurationManager.setLocalData();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }

    @Override
    public void onBackPressed()
    {
        if(mDrawer.isDrawerOpen(GravityCompat.START))
        {
            mDrawer.closeDrawers();
            return;
        }
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.flContent);
        if(fragment instanceof HomeListFragment) {
            HomeListFragment homeListFragment = (HomeListFragment)fragment;
            if (homeListFragment.categoryType != CategoryType.AllCategory) {
                homeListFragment.setFilteredList(CategoryType.AllCategory);
                return;
            }
        }
        super.onBackPressed();
    }
}
