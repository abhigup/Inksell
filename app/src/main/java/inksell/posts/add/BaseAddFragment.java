package inksell.posts.add;

import enums.CategoryType;
import inksell.common.BaseFragment;
import models.IPostEntity;

/**
 * Created by Abhinav on 15/09/15.
 */
public abstract class BaseAddFragment extends BaseFragment {

    @Override
    public void initFragment()
    {}

    public abstract boolean verifyAndGetPost(IPostEntity iPostEntity, CategoryType categoryType);

}
