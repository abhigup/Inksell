package inksell.common;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;

import java.util.HashMap;
import java.util.Map;

import butterknife.ButterKnife;
import utilities.ConfigurationManager;
import utilities.Utility;

/**
 * Created by Abhinav on 30/09/15.
 */
public abstract class BaseFragmentActivity extends FragmentActivity {

    public Map<String, String> intentExtraMap = new HashMap<String, String>();

    protected abstract int getActivityLayout();

    protected abstract void initActivity();

    protected abstract void initDataAndLayout();

    protected void setIntentExtras()
    {}

    protected View.OnClickListener refresh_click() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refresh();
            }
        };
    }

    protected void refresh()
    {
        this.initDataAndLayout();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getActivityLayout());

        ButterKnife.inject(this, this.findViewById(android.R.id.content));

        ConfigurationManager.CurrentActivityContext = this;

        Bundle extras = getIntent().getExtras();
        if(extras!=null) {
            intentExtraMap = Utility.GetObjectFromJSON(extras.getString("intentExtra"), Map.class);
        }
        if(intentExtraMap!=null && !intentExtraMap.isEmpty())
        {
            this.setIntentExtras();
        }
        initActivity();
        initDataAndLayout();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        ConfigurationManager.CurrentActivityContext = this;
        onBaseResume();
        //Restore state here
    }

    protected void onBaseResume()
    {

    }

}
