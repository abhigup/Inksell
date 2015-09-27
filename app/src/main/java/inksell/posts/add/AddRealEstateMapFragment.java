package inksell.posts.add;


import android.app.Fragment;
import android.view.View;

import enums.CategoryType;
import inksell.inksell.R;
import models.IPostEntity;

/**
 * A simple {@link Fragment} subclass.
 */
public class AddRealEstateMapFragment extends BaseAddFragment {


    public AddRealEstateMapFragment() {
        // Required empty public constructor
    }


    @Override
    public int getViewResId() {
        return R.layout.fragment_add_real_estate_map;
    }

    @Override
    public void initViewAfterSettingEditableView(View view) {

    }

    @Override
    public boolean verifyAndGetPost(IPostEntity iPostEntity, CategoryType categoryType) {
        return false;
    }
}
