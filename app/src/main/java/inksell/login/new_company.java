package inksell.login;

import android.view.View;
import android.widget.EditText;

import butterknife.InjectView;
import inksell.inksell.R;
import inksell.common.BaseActionBarActivity;
import services.InksellCallback;
import services.RestClient;
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


    @Override
    protected void initDataAndLayout() {

    }

    @Override
    protected void initActivity() {
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
        if (validateUserDetails())
            RestClient.post().newCompanyRequest(txtCompany.getText().toString(), txtLocation.getText().toString()
                    , txtName.getText().toString(), txtEmail.getText().toString()).enqueue(new InksellCallback<String>() {
                @Override
                public void onSuccess(String s) {;
                }
            });
    }
}
