package inksell.inksell;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import java.util.UUID;

import Constants.AppData;
import Constants.StorageConstants;
import butterknife.ButterKnife;
import butterknife.InjectView;
import inksell.login.already_activity;
import inksell.login.register_activity;
import inksell.login.verify_activity;
import models.PostSummaryEntity;
import models.UserEntity;
import services.InksellCallback;
import services.RestClient;
import utilities.ConfigurationManager;
import utilities.LocalStorageHandler;
import utilities.NavigationHelper;
import utilities.ResponseStatus;
import utilities.Utility;

public class StartPage extends Activity {

    @InjectView(R.id.start_layout_buttons)
    LinearLayout startLayout;

    @InjectView(R.id.start_progess)
    ProgressBar startProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_page);
        ConfigurationManager.CurrentActivityContext = this;

        ButterKnife.inject(this);

        UUID UserGUID = Utility.GetUUID(LocalStorageHandler.GetData(StorageConstants.UserUUID, String.class));
        String isUserVerifiedString = LocalStorageHandler.GetData(StorageConstants.UserVerified, String.class);
        boolean isUserVerified = (isUserVerifiedString == null)?false:Boolean.valueOf(isUserVerifiedString);


        Bundle extras = getIntent().getExtras();
        if(extras!=null) {
            PostSummaryEntity postSummaryEntity = Utility.GetObjectFromJSON(extras.getString("postSummary"), PostSummaryEntity.class);
            if(postSummaryEntity!=null)
            {
                NavigationHelper.NavigateToViewPosts(postSummaryEntity, true);
                return;
            }
        }


        if(UserGUID !=null
            && isUserVerified==true)
        {
            ConfigurationManager.setLocalData();

            if(AppData.UserData==null) {
                startProgressBar.setVisibility(View.VISIBLE);
                loadUserData();
            }
            else
            {
                NavigationHelper.NavigateTo(Home.class, true);
            }
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
                LocalStorageHandler.SaveData(StorageConstants.UserData, userEntity);
                startProgressBar.setVisibility(View.GONE);
                NavigationHelper.NavigateTo(Home.class, true);
            }

            @Override
            public void onError(ResponseStatus responseStatus) {
                startProgressBar.setVisibility(View.GONE);
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
