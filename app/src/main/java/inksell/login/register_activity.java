package inksell.login;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;

import inksell.inksell.R;

public class register_activity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_activity);
    }

    public void register_click(View view) {
        Intent intent = new Intent(this, verify_activity.class);
        startActivity(intent);
    }
}
