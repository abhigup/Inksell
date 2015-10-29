package inksell.login;

import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import Constants.StorageConstants;
import butterknife.InjectView;
import inksell.common.BaseActionBarActivity;
import inksell.inksell.R;
import services.InksellCallback;
import services.RestClient;
import utilities.LocalStorageHandler;
import utilities.NavigationHelper;
import utilities.ResponseStatus;
import utilities.Utility;

public class already_activity extends BaseActionBarActivity {

    @InjectView(R.id.AlreadyTextEmail)
    TextView txtEmail;

    @InjectView(R.id.loading_full_page)
    RelativeLayout loadingFullPage;

    @InjectView(R.id.loading_Text)
    TextView loadingText;

    @Override
    protected void initDataAndLayout() {

    }

    @Override
    protected void initActivity() {
        loadingFullPage.setVisibility(View.GONE);
        loadingText.setText(getString(R.string.verifying));
    }

    @Override
    protected int getActivityLayout() {
        return R.layout.activity_already_activity;
    }

    public void submit_click(View view) {
        String email = txtEmail.getText().toString();

        if(email.isEmpty()) {
            Utility.ShowInfoDialog(R.string.ErrorAlreadyEmptyEmail);
            return;
        }

        loadingFullPage.setVisibility(View.VISIBLE);
        RestClient.get().registerUserAgain(email).enqueue(new InksellCallback<String>() {
            @Override
            public void onSuccess(String s) {
                if(Utility.GetUUID(s)!=null) {
                    LocalStorageHandler.SaveData(StorageConstants.UserUUID, s);
                    LocalStorageHandler.SaveData(StorageConstants.IsAlreadyRegistered, true);
                    NavigationHelper.NavigateTo(verify_activity.class);
                }
                loadingFullPage.setVisibility(View.GONE);
            }

            @Override
            public void onError(ResponseStatus responseStatus) {
                loadingFullPage.setVisibility(View.GONE);
            }
        });
    }
}
