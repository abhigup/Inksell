package inksell.inksell;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import java.util.List;

import adapters.RVAdapter;
import butterknife.InjectView;
import inksell.common.BaseFragment;
import models.PostSummaryEntity;
import utilities.FavouritesHelper;
import utilities.NavigationHelper;
import utilities.SwipableRecyclerView;
import utilities.Utility;

public class FavouriteFragment extends BaseFragment implements SwipableRecyclerView.OnSwipeListener {

    private RVAdapter frvAdapter;

    private List<PostSummaryEntity> postSummaryEntityList;

    @InjectView(R.id.favouritesListRecycleView)
    RecyclerView frv;

    @Override
    public int getViewResId() {
        return R.layout.fragment_favourites;
    }

    @Override
    public void initView(LayoutInflater inflater, View view, Bundle savedInstanceState) {
        initFavList();
    }

    @Override
    public void initFragment(Bundle savedInstanceState) {
        setHasOptionsMenu(true);

        postSummaryEntityList = FavouritesHelper.getFavourites();
    }

    private void initFavList() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        frv.setLayoutManager(layoutManager);

        if(frvAdapter ==null) {
            frvAdapter = new RVAdapter(postSummaryEntityList, NavigationHelper.cardViewClickListener(frv, postSummaryEntityList, getActivity()));
            frvAdapter.setIsFavPosts(true);
            frv.setAdapter(frvAdapter);
        }
        else {
            frvAdapter.Update(postSummaryEntityList, NavigationHelper.cardViewClickListener(frv, postSummaryEntityList, getActivity()));
        }

        SwipableRecyclerView.setSwipeBehaviour(frv, this);
    }


    @Override
    public void onCreateOptionsMenu(
            Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_favourites, menu);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_clear_fav:
                Utility.ShowDialog("Are you sure to clear all favourite posts?", clearAllFavPosts());
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private DialogInterface.OnClickListener clearAllFavPosts() {
        return new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        FavouritesHelper.ClearAllFromFavourites();
                        postSummaryEntityList.clear();
                        FavouritesHelper.ClearAllFromFavourites();
                        frvAdapter.notifyDataSetChanged();
                        break;
                }
            }
        };
    }

    @Override
    public void onResume() {
        super.onResume();

        if(frvAdapter!=null) {
            postSummaryEntityList = FavouritesHelper.getFavourites();
            frvAdapter.updateFavourite(postSummaryEntityList);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onSwiped(int position) {
        int postId = postSummaryEntityList.get(position).PostId;
        postSummaryEntityList.remove(position);
        FavouritesHelper.RemoveFromFavourites(postId);
        frvAdapter.notifyItemRemoved(position);
    }
}
