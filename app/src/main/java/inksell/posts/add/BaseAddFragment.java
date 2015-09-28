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
    protected boolean forEdit = false;

    @Override
    public void initFragment()
    {}

    @Override
    public void initView(View view)
    {
        if(forEdit)
        {
            setEditableView(view);
        }
        initViewAfterSettingEditableView(view);
    }

    public void setData(IPostEntity entity, CategoryType type)
    {
        forEdit = true;
        iPostEntity = entity;
        categoryType = type;
    }

    public abstract boolean verifyAndGetPost(IPostEntity iPostEntity, CategoryType categoryType);

    public abstract void initViewAfterSettingEditableView(View view);

    public abstract void setEditableView(View view);

}
