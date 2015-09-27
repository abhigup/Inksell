package inksell.posts.add;


import android.app.Fragment;
import android.view.View;
import android.widget.EditText;

import butterknife.InjectView;
import enums.CategoryType;
import inksell.inksell.R;
import models.IPostEntity;
import models.OtherEntity;
import utilities.Utility;

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

    @Override
    public boolean verifyAndGetPost(IPostEntity iPostEntity, CategoryType categoryType) {

        if(Utility.IsEditTextNullorEmpty(postTitle))
        {
            return false;
        }

        switch (categoryType)
        {
            case Others:
                OtherEntity otherEntity = (OtherEntity) iPostEntity;
                otherEntity.PostTitle = postTitle.getText().toString();
                otherEntity.UsedPeriod = usedPeriod.getText().toString();
                otherEntity.ActualPrice = usedPeriod.getText().toString();
                otherEntity.ExpectedPrice = usedPeriod.getText().toString();
                otherEntity.PostDescription = description.getText().toString();
                break;
        }
        return true;
    }
}
