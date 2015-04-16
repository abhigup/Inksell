package inksell.inksell;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import inksell.login.already_activity;
import inksell.login.register_activity;
import inksell.login.verify_activity;
import utilities.ConfigurationManager;

public class StartPage extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_page);

        ConfigurationManager.CurrentActivityContext = getApplicationContext();
    }

    public void register_click(View view) {
        Intent intent = new Intent(this, register_activity.class);
        startActivity(intent);
    }

    public void verify_click(View view) {
        Intent intent = new Intent(this, verify_activity.class);
        startActivity(intent);
    }

    public void already_click(View view) {
        Intent intent = new Intent(this, already_activity.class);
        startActivity(intent);
    }
}
