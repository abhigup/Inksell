package inksell.inksell;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import java.util.UUID;

import Constants.AppData;
import Constants.StorageConstants;
import butterknife.ButterKnife;
import butterknife.InjectView;
import inksell.login.already_activity;
import inksell.login.register_activity;
import inksell.login.verify_activity;
import models.UserEntity;
import services.InksellCallback;
import services.RestClient;
import utilities.ConfigurationManager;
import utilities.FavouritesHelper;
import utilities.LocalStorageHandler;
import utilities.NavigationHelper;
import utilities.Utility;

public class StartPage extends Activity {

    @InjectView(R.id.start_layout_buttons)
    LinearLayout startLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_page);
        ConfigurationManager.CurrentActivityContext = this;

        ButterKnife.inject(this);

        UUID UserGUID = Utility.GetUUID(LocalStorageHandler.GetData(StorageConstants.UserUUID, String.class));
        String isUserVerifiedString = LocalStorageHandler.GetData(StorageConstants.UserVerified, String.class);
        boolean isUserVerified = (isUserVerifiedString == null)?false:Boolean.valueOf(isUserVerifiedString);

        if(UserGUID !=null
            && isUserVerified==true)
        {
            AppData.UserGuid = LocalStorageHandler.GetData(StorageConstants.UserUUID, String.class);
            FavouritesHelper.setFavourites();
            loadUserData();
            startLayout.setVisibility(View.GONE);
        }
        else
        {
            startLayout.setVisibility(View.VISIBLE);
        }
    }

    private void loadUserData()
    {
        RestClient.get().getUserDetails(AppData.UserGuid).enqueue(new InksellCallback<UserEntity>() {
            @Override
            public void onSuccess(UserEntity userEntity) {
                AppData.UserData = userEntity;
                NavigationHelper.NavigateTo(Home.class, true);
            }

            @Override
            public void onError()
            {
                NavigationHelper.NavigateTo(Home.class, true);
            }
        });

    }

    public void register_click(View view) {
        NavigationHelper.NavigateTo(register_activity.class);
    }

    public void verify_click(View view) {
        NavigationHelper.NavigateTo(verify_activity.class);
    }

    public void already_click(View view) {
        NavigationHelper.NavigateTo(already_activity.class);
    }
}
