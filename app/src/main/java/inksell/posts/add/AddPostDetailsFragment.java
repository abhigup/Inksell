package inksell.posts.add;


import android.app.Fragment;
import android.view.View;
import android.widget.EditText;

import butterknife.InjectView;
import inksell.inksell.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class AddPostDetailsFragment extends BaseAddFragment {

    @InjectView(R.id.add_post_title)
    EditText postTitle;

    @InjectView(R.id.add_post_used_period)
    EditText usedPeriod;

    @InjectView(R.id.add_post_expected_price)
    EditText expectedPrice;

    @InjectView(R.id.add_post_actual_price)
    EditText actualPrice;

    @InjectView(R.id.add_post_description)
    EditText description;

    public AddPostDetailsFragment() {
        // Required empty public constructor
    }


    @Override
    public int getViewResId() {
        return R.layout.fragment_add_post_details;
    }

    @Override
    public void initView(View view) {

    }
}
