package inksell.login;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;

import Constants.StorageConstants;
import butterknife.ButterKnife;
import butterknife.InjectView;
import inksell.inksell.R;
import models.BaseActionBarActivity;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import services.RestClient;
import utilities.LocalStorageHandler;
import utilities.ResponseStatus;
import utilities.Utility;

public class already_activity extends BaseActionBarActivity {

    @InjectView(R.id.AlreadyTextEmail)
    TextView txtEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_already_activity);

        ButterKnife.inject(this);
    }


    public void submit_click(View view) {
        String email = txtEmail.getText().toString();

        if(email.isEmpty()) {
            Utility.ShowInfoDialog(R.string.ErrorAlreadyEmptyEmail);
            return;
        }

        RestClient.get().registerUserAgain(email, new Callback<String>() {
            @Override
            public void success(String s, Response response) {
                if(Utility.GetUUID(s)!=null) {
                    LocalStorageHandler.SaveData(StorageConstants.UserUUID, s);
                    Map<String, Object> map = new HashMap<String, Object>();
                    map.put("isAlreadyRegistered", true);
                    Utility.NavigateTo(verify_activity.class, map);
                }
                else if(ResponseStatus.values()[Integer.parseInt(s)] == ResponseStatus.UserNotExists)
                {
                    Utility.ShowInfoDialog(R.string.ErrorUserNotExists);
                }
            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
    }
}
