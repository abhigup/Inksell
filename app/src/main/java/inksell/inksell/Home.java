package inksell.inksell;

import android.app.Fragment;
import android.app.FragmentManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import Constants.AppData;
import butterknife.ButterKnife;
import butterknife.InjectView;
import de.hdodenhof.circleimageview.CircleImageView;
import models.BaseActionBarActivity;
import models.UserEntity;
import retrofit.client.Response;
import services.InksellCallback;
import services.RestClient;

public class Home extends BaseActionBarActivity implements DrawerLayout.DrawerListener,HomeListFragment.OnFragmentInteractionListener, SettingsFragment.OnFragmentInteractionListener {
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
            case R.id.nav_first_fragment:
                fragmentClass = HomeListFragment.class;
                break;
            case R.id.nav_second_fragment:
                fragmentClass = SettingsFragment.class;
                break;
            case R.id.nav_third_fragment:
                fragmentClass = SettingsFragment.class;
                break;
            default:
                fragmentClass = HomeListFragment.class;
        }

        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Insert the fragment by replacing any existing fragment
        FragmentManager fragmentManager = this.getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();

        // Highlight the selected item, update the title, and close the drawer
        menuItem.setChecked(true);
        setTitle(menuItem.getTitle());
        mDrawer.closeDrawers();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // The action bar home/up action should open or close the drawer.
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawer.openDrawer(GravityCompat.START);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onDrawerSlide(View drawerView, float slideOffset) {

    }

    @Override
    public void onDrawerOpened(View drawerView) {



    }

    @Override
    public void onDrawerClosed(View drawerView) {

    }

    @Override
    public void onDrawerStateChanged(int newState) {

    }
}
