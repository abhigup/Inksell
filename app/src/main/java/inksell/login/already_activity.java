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
import retrofit.RetrofitError;
import retrofit.client.Response;
import services.InksellCallback;
import services.RestClient;
import utilities.LocalStorageHandler;
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

        RestClient.get().registerUserAgain(email, new InksellCallback<String>() {
            @Override
            public void onSuccess(String s, Response response) {
                if(Utility.GetUUID(s)!=null) {
                    LocalStorageHandler.SaveData(StorageConstants.UserUUID, s);
                    Map<String, Object> map = new HashMap<String, Object>();
                    map.put("isAlreadyRegistered", true);
                    Utility.NavigateTo(verify_activity.class, map);
                }
            }

            @Override
            public void onFailure(RetrofitError error) {

            }
        });
    }
}
