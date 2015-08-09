package inksell.posts.view;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import butterknife.ButterKnife;
import models.IPostEntity;

/**
 * Created by Abhinav on 27/07/15.
 */
public abstract class BaseViewFragment extends Fragment {

    public abstract void setData(IPostEntity postEntity);

    public abstract List<String> getImageUrl();

    public abstract int getViewResId();

    public abstract void initView();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view = inflater.inflate(getViewResId(), container, false);
        ButterKnife.inject(this, view);
        initView();
        return view;
    }
}
