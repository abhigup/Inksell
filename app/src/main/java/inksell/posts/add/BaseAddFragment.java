package inksell.posts.add;

import android.os.Bundle;
import android.view.LayoutInflater;
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
    public void initFragment(Bundle savedInstanceState)
    {}

    @Override
    public void initView(LayoutInflater inflater, View view, Bundle savedInstanceState)
    {
        if(forEdit)
        {
            setEditableView(inflater, view, savedInstanceState);
        }
        initViewAfterSettingEditableView(inflater, view, savedInstanceState);
    }

    public void setData(IPostEntity entity, CategoryType type)
    {
        forEdit = true;
        iPostEntity = entity;
        categoryType = type;
    }

    public abstract boolean verifyAndGetPost(IPostEntity iPostEntity, CategoryType categoryType);

    public abstract void initViewAfterSettingEditableView(LayoutInflater inflater, View view, Bundle savedInstanceState);

    public abstract void setEditableView(LayoutInflater inflater, View view, Bundle savedInstanceState);

}
