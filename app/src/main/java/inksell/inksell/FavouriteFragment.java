package inksell.inksell;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import adapters.RVAdapter;
import butterknife.ButterKnife;
import butterknife.InjectView;
import inksell.posts.view.ViewPostActivity;
import models.PostSummaryEntity;
import utilities.FavouritesHelper;
import utilities.NavigationHelper;
import utilities.SwipableRecyclerView;
import utilities.Utility;

public class FavouriteFragment extends Fragment implements SwipableRecyclerView.OnSwipeListener {

    private RVAdapter frvAdapter;

    private List<PostSummaryEntity> postSummaryEntityList;

    @InjectView(R.id.favouritesListRecycleView)
    RecyclerView frv;

    public FavouriteFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        postSummaryEntityList = FavouritesHelper.getFavourites();

    }

    private View.OnClickListener cardViewClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int childItemPosition = frv.getChildAdapterPosition(v);
                PostSummaryEntity postSummaryEntity = FavouritesHelper.getFavourites().get(childItemPosition);

                Map<String, String> map = new HashMap<String, String>();
                map.put("postSummary", Utility.GetJSONString(postSummaryEntity));

                if(!postSummaryEntity.HasPostTitlePic())
                {
                    NavigationHelper.NavigateTo(ViewPostActivity.class, map);
                }
                else {
                    ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(), v.findViewById(R.id.card_title_pic), getString(R.string.cardTitlePicTransition));
                    NavigationHelper.NavigateTo(ViewPostActivity.class, map, options.toBundle());
                }
            }
        };
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_favourites, container, false);
        ButterKnife.inject(this, view);

        initFavList();

        return view;
    }

    private void initFavList() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        frv.setLayoutManager(layoutManager);

        if(frvAdapter ==null) {
            frvAdapter = new RVAdapter(postSummaryEntityList, cardViewClickListener());
            frvAdapter.setIsFavPosts(true);
            frv.setAdapter(frvAdapter);
        }
        else {
            frvAdapter.Update(postSummaryEntityList, cardViewClickListener());
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
