package inksell.posts.add;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;

/**
 * Created by Abhinav on 15/09/15.
 */
public abstract class BaseAddFragment extends Fragment {

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
