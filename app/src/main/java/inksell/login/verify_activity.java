package inksell.login;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.TextView;

import Constants.StorageConstants;
import butterknife.ButterKnife;
import butterknife.InjectView;
import inksell.inksell.R;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import services.RestClient;
import utilities.ConfigurationManager;
import utilities.LocalStorageHandler;
import utilities.Utility;

public class verify_activity extends ActionBarActivity {

    String guid;

    @InjectView(R.id.verifyTextCode)
    TextView txtCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_activity);

        ButterKnife.inject(this);

        ConfigurationManager.CurrentActivityContext = this;

        String uuid = LocalStorageHandler.GetData(StorageConstants.UserUUID, String.class);
        if(!Utility.IsStringNullorEmpty(uuid))
        {
            guid = uuid;
        }
    }

    public void verify_click(View view) {
        if(Utility.IsStringNullorEmpty(guid))
        {
            Utility.ShowInfoDialog(R.string.ErrorUserNotExists);
            return;
        }
        if(Utility.IsStringNullorEmpty(txtCode.getText().toString()))
        {
            Utility.ShowInfoDialog(R.string.ErrorVerifyEmptyCode);
        }
        RestClient.get().verifyNewUser(guid, txtCode.getText().toString(), new Callback<Integer>() {
            @Override
            public void success(Integer integer, Response response) {
                Utility.ShowInfoDialog(integer.toString());
            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
    }
}
