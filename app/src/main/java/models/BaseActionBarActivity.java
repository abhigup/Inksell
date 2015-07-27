package models;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import java.util.HashMap;
import java.util.Map;

import utilities.ConfigurationManager;
import utilities.Utility;

/**
 * Created by Abhinav on 17/04/15.
 */
public abstract class BaseActionBarActivity extends AppCompatActivity {

    public Map<String, String> intentExtraMap = new HashMap<String, String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ConfigurationManager.CurrentActivityContext = this;

        Bundle extras = getIntent().getExtras();
        if(extras!=null) {
            intentExtraMap = Utility.GetObjectFromJSON(extras.getString("intentExtra"), Map.class);
        }
        if(intentExtraMap!=null && !intentExtraMap.isEmpty())
        {
            this.setIntentExtras();
        }
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        ConfigurationManager.CurrentActivityContext = this;
        //Restore state here
    }

    protected void setIntentExtras()
    {}
}
