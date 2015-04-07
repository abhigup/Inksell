package inksell.login;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.TextView;

import butterknife.ButterKnife;
import butterknife.InjectView;
import inksell.inksell.R;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import services.RestClient;
import utilities.Utility;

public class verify_activity extends ActionBarActivity {

    Context context = this;

    String guid;

    @InjectView(R.id.txtCode)
    TextView txtCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_activity);

        ButterKnife.inject(this);

        Bundle extras = getIntent().getExtras();
        guid = extras.getString("guid");
    }

    public void verify_click(View view) {
        RestClient.get().verifyNewUser(guid, txtCode.getText().toString(), new Callback<Integer>() {
            @Override
            public void success(Integer integer, Response response) {
                Utility.ShowDialog(context,integer.toString());
            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
    }
}
