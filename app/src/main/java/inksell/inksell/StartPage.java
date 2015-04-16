package inksell.inksell;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import inksell.login.already_activity;
import inksell.login.register_activity;
import inksell.login.verify_activity;
import utilities.ConfigurationManager;
import utilities.Utility;

public class StartPage extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_page);

        ConfigurationManager.CurrentActivityContext = this;
    }

    public void register_click(View view) {
        Utility.NavigateTo(register_activity.class);
    }

    public void verify_click(View view) {
        Utility.NavigateTo(verify_activity.class);
    }

    public void already_click(View view) {
        Utility.NavigateTo(already_activity.class);
    }
}
