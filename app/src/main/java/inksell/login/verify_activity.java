package inksell.login;

import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;

import Constants.AppData;
import Constants.StorageConstants;
import butterknife.InjectView;
import inksell.common.BaseActionBarActivity;
import inksell.inksell.Home;
import inksell.inksell.R;
import inksell.user.EditMyDetails;
import models.UserEntity;
import services.InksellCallback;
import services.RestClient;
import utilities.LocalStorageHandler;
import utilities.NavigationHelper;
import utilities.ResponseStatus;
import utilities.Utility;

public class verify_activity extends BaseActionBarActivity {

    String guid;
    boolean isAlreadyRegistered = false;

    @InjectView(R.id.verifyTextCode)
    TextView txtCode;

    @InjectView(R.id.loading_full_page)
    RelativeLayout loadingFullPage;

    @InjectView(R.id.loading_Text)
    TextView loadingText;

    @Override
    protected void initDataAndLayout() {

    }

    @Override
    protected void initActivity() {

        String uuid = LocalStorageHandler.GetData(StorageConstants.UserUUID, String.class);
        if(!Utility.IsStringNullorEmpty(uuid))
        {
            guid = uuid;
        }

        isAlreadyRegistered = Boolean.parseBoolean(LocalStorageHandler.GetData(StorageConstants.IsAlreadyRegistered, String.class));

        loadingFullPage.setVisibility(View.GONE);
        loadingText.setText(getString(R.string.verifying));
    }

    @Override
    protected int getActivityLayout() {
        return R.layout.activity_verify_activity;
    }

    public void verify_click(View view) {
        if(Utility.IsStringNullorEmpty(guid))
        {
            Utility.ShowInfoDialog(R.string.ErrorNotRegistered);
            return;
        }

        if(Utility.IsStringNullorEmpty(txtCode.getText().toString()))
        {
            Utility.ShowInfoDialog(R.string.ErrorVerifyEmptyCode);
            return;
        }

        loadingFullPage.setVisibility(View.VISIBLE);
        RestClient.get().verifyNewUser(guid, txtCode.getText().toString(), isAlreadyRegistered?1:0).enqueue(new InksellCallback<Integer>() {
            @Override
            public void onSuccess(Integer integer) {
                if(ResponseStatus.values()[integer] == ResponseStatus.UserSuccessfullyVerified)
                {
                    LocalStorageHandler.SaveData(StorageConstants.UserVerified, true);
                    AppData.UserGuid = LocalStorageHandler.GetData(StorageConstants.UserUUID, String.class);
                    loadUserData();
                }
                loadingFullPage.setVisibility(View.GONE);
            }

            @Override
            public void onError(ResponseStatus responseStatus) {
                loadingFullPage.setVisibility(View.GONE);
            }
        });
    }

    private void loadUserData()
    {
        RestClient.get().getUserDetails(AppData.UserGuid).enqueue(new InksellCallback<UserEntity>() {
            @Override
            public void onSuccess(UserEntity userEntity) {
                LocalStorageHandler.SaveData(StorageConstants.UserData, userEntity);
                AppData.UserData = userEntity;
                if(isAlreadyRegistered) {
                    NavigationHelper.NavigateTo(Home.class, true);
                }
                else
                {
                    Map<String, String> map = new HashMap<String, String>();
                    map.put("isNewUser", "true");
                    NavigationHelper.NavigateTo(EditMyDetails.class, map, true, null);
                }
            }

            @Override
            public void onError(ResponseStatus responseStatus)
            {
                NavigationHelper.NavigateTo(Home.class, true);
            }
        });

    }
}
