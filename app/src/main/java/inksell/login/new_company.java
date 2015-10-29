package inksell.login;

import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.InjectView;
import inksell.inksell.R;
import inksell.common.BaseActionBarActivity;
import services.InksellCallback;
import services.RestClient;
import utilities.ResponseStatus;
import utilities.Utility;

public class new_company extends BaseActionBarActivity {

    @InjectView(R.id.newtextcompany)
    EditText txtCompany;

    @InjectView(R.id.newtextname)
    EditText txtName;

    @InjectView(R.id.newtextlocation)
    EditText txtLocation;

    @InjectView(R.id.newtextemail)
    EditText txtEmail;

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
        loadingText.setText(getString(R.string.addingNewCompany));
    }

    @Override
    protected int getActivityLayout() {
        return R.layout.activity_new_company;
    }

    private boolean validateUserDetails()
    {
        if(Utility.IsEditTextNullorEmpty(txtName) || Utility.IsEditTextNullorEmpty(txtCompany)
                || Utility.IsEditTextNullorEmpty(txtLocation) || Utility.IsEditTextNullorEmpty(txtEmail))
        {
            return false;
        }
        return true;
    }

    public void submit_click(View view) {
        loadingFullPage.setVisibility(View.VISIBLE);
        if (validateUserDetails())
            RestClient.post().newCompanyRequest(txtCompany.getText().toString(), txtLocation.getText().toString()
                    , txtName.getText().toString(), txtEmail.getText().toString()).enqueue(new InksellCallback<String>() {
                @Override
                public void onSuccess(String s) {
                    loadingFullPage.setVisibility(View.GONE);
                }

                @Override
                public void onError(ResponseStatus responseStatus) {
                    loadingFullPage.setVisibility(View.GONE);
                }
            });
    }
}
