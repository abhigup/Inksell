package inksell.common;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;
import retrofit.Callback;
import utilities.ConfigurationManager;
import utilities.Utility;

/**
 * Created by Abhinav on 17/04/15.
 */
public abstract class BaseActionBarActivity extends AppCompatActivity {

    public Map<String, String> intentExtraMap = new HashMap<String, String>();

    protected abstract void initDataAndLayout();

    protected abstract void initActivity();

    protected abstract int getActivityLayout();

    protected List<Callback> callbacks;

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
        callbacks = new ArrayList<>();

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
        //Restore state here
    }

    protected void setIntentExtras()
    {}

    @Override
    protected void onDestroy()
    {
//        for (int i=0;i<callbacks.size();i++)
//        {
//            callbacks.get(i)
//        }
        super.onDestroy();
    }

}
