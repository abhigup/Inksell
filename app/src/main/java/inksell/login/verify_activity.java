package inksell.login;

import android.view.View;
import android.widget.TextView;

import Constants.AppData;
import Constants.StorageConstants;
import butterknife.InjectView;
import inksell.inksell.Home;
import inksell.inksell.R;
import models.BaseActionBarActivity;
import retrofit.RetrofitError;
import retrofit.client.Response;
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
    }

    @Override
    protected int getActivityLayout() {
        return R.layout.activity_verify_activity;
    }

    @Override
    protected void setIntentExtras()
    {
        isAlreadyRegistered = Boolean.parseBoolean(this.intentExtraMap.get("isAlreadyRegistered").toString());
    }

    public void verify_click(View view) {
        if(Utility.IsStringNullorEmpty(guid))
        {
            Utility.ShowInfoDialog(R.string.ErrorNotRegistered);
            isAlreadyRegistered = false;
            return;
        }
        else
        {
            isAlreadyRegistered = true;
        }

        if(Utility.IsStringNullorEmpty(txtCode.getText().toString()))
        {
            Utility.ShowInfoDialog(R.string.ErrorVerifyEmptyCode);
            return;
        }
        RestClient.get().verifyNewUser(guid, txtCode.getText().toString(), isAlreadyRegistered?1:0, new InksellCallback<Integer>() {
            @Override
            public void onSuccess(Integer integer, Response response) {
                if(ResponseStatus.values()[integer] == ResponseStatus.UserSuccessfullyVerified)
                {
                    LocalStorageHandler.SaveData(StorageConstants.UserVerified, true);
                    AppData.UserGuid = LocalStorageHandler.GetData(StorageConstants.UserUUID, String.class);
                    NavigationHelper.NavigateTo(Home.class, true);
                }
            }

            @Override
            public void onFailure(RetrofitError error) {

            }
        });
    }
}
