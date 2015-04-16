package inksell.login;

import android.content.Intent;
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
import utilities.ResponseStatus;
import utilities.Utility;

public class already_activity extends ActionBarActivity {

    @InjectView(R.id.AlreadyTextEmail)
    TextView txtEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_already_activity);

        ConfigurationManager.CurrentActivityContext = this;

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
                    Intent intent = new Intent(getBaseContext(), verify_activity.class);
                    intent.putExtra("guid", s);
                    startActivity(intent);
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
