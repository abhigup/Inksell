package inksell.posts.view;

import android.os.Bundle;

import java.util.List;

import inksell.common.BaseFragment;
import models.IPostEntity;

/**
 * Created by Abhinav on 27/07/15.
 */
public abstract class BaseViewFragment extends BaseFragment {

    public abstract void setData(IPostEntity postEntity);

    public abstract List<String> getImageUrls();

    public abstract String[] getEmailAndCall();

    @Override
    public void initFragment(Bundle savedInstanceState)
    {}
}
