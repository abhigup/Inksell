package inksell.posts.add;

import android.view.View;

import enums.CategoryType;
import inksell.common.BaseFragment;
import models.IPostEntity;

/**
 * Created by Abhinav on 15/09/15.
 */
public abstract class BaseAddFragment extends BaseFragment {

    protected IPostEntity iPostEntity;
    protected CategoryType categoryType;

    @Override
    public void initFragment()
    {}

    @Override
    public void initView(View view)
    {
        initViewAfterSettingEditableView(view);
    }

    public void setData(IPostEntity entity, CategoryType type)
    {
        iPostEntity = entity;
        categoryType = type;
    }

    public abstract boolean verifyAndGetPost(IPostEntity iPostEntity, CategoryType categoryType);

    public abstract void initViewAfterSettingEditableView(View view);

}
