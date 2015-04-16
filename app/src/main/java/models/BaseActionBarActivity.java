package models;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import java.util.HashMap;
import java.util.Map;

import utilities.ConfigurationManager;
import utilities.Utility;

/**
 * Created by Abhinav on 17/04/15.
 */
public abstract class BaseActionBarActivity extends ActionBarActivity {

    public Map<String, Object> intentExtraMap = new HashMap<String, Object>();

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

    protected void setIntentExtras()
    {}
}
