package inksell.inksell;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Constants.AppData;
import adapters.RVAdapter;
import butterknife.ButterKnife;
import butterknife.InjectView;
import enums.CategoryType;
import inksell.posts.add.AddPostActivity;
import inksell.posts.view.ViewPostActivity;
import models.PostSummaryEntity;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import services.InksellCallback;
import services.RestClient;
import utilities.ConfigurationManager;
import utilities.FavouritesHelper;
import utilities.NavigationHelper;
import utilities.Utility;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link HomeListFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link HomeListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeListFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private CategoryType categoryType;

    @InjectView(R.id.homeListRecycleView)
    RecyclerView rv;

    @InjectView(R.id.home_swipe_container)
    SwipeRefreshLayout swipeContainer;

    @InjectView(R.id.fab_add_button)
    FloatingActionsMenu fabMenu;

    @InjectView(R.id.loading_spinner)
    ProgressBar progressBar;

    @InjectView(R.id.layout_error_tryAgain)
    RelativeLayout layoutErrorTryAgain;

    @InjectView(R.id.tryAgainButton)
    Button tryAgainButton;

    @InjectView(R.id.fab_autos)
    FloatingActionButton fab_auto;

    @InjectView(R.id.fab_electronics)
    FloatingActionButton fab_electronics;

    @InjectView(R.id.fab_furniture)
    FloatingActionButton fab_furniture;

    @InjectView(R.id.fab_multiple)
    FloatingActionButton fab_multiple;

    @InjectView(R.id.fab_other)
    FloatingActionButton fab_other;

    @InjectView(R.id.fab_realestate)
    FloatingActionButton fab_realestate;

    private RVAdapter rvAdapter;

    private List<PostSummaryEntity> postSummaryList;
    private OnFragmentInteractionListener mListener;

    public HomeListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);

        loadPostsData(CategoryType.AllCategory);
    }

    private View.OnClickListener refresh_click() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layoutErrorTryAgain.setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);

                onRefresh();
            }
        };
    }

    private void loadPostsData(CategoryType type)
    {
        this.categoryType = type;

        int lastPostid = 0;

        switch (type)
        {
            case AllCategory:
                RestClient.get().getPostSummaryAll(lastPostid, AppData.UserGuid, setListOnResponse());
                break;
            default:
                RestClient.get().getFilteredPostSummary(lastPostid, type.ordinal(), AppData.UserGuid, setListOnResponse());
        }

    }

    @Override
    public void onResume() {
        super.onResume();

        if(rvAdapter!=null) {
            postSummaryList = FavouritesHelper.setFavourites(postSummaryList);
            rvAdapter.updateFavourite(postSummaryList);
        }
    }

    private Callback<List<PostSummaryEntity>> setListOnResponse() {
        return new InksellCallback<List<PostSummaryEntity>>() {
            @Override
            public void onSuccess(List<PostSummaryEntity> postSummaryEntities, Response response) {

                layoutErrorTryAgain.setVisibility(View.GONE);
                progressBar.setVisibility(View.GONE);

                postSummaryList = postSummaryEntities;
                postSummaryList = FavouritesHelper.setFavourites(postSummaryList);

                if(rvAdapter==null) {
                    rvAdapter = new RVAdapter(postSummaryList, NavigationHelper.cardViewClickListener(rv, postSummaryList, getActivity()));
                    rv.setAdapter(rvAdapter);
                }
                else {
                    rvAdapter.Update(postSummaryList, NavigationHelper.cardViewClickListener(rv, postSummaryList, getActivity()));
                }
                swipeContainer.setRefreshing(false);
            }

            @Override
            public void onFailure(RetrofitError error)
            {
                progressBar.setVisibility(View.GONE);
                layoutErrorTryAgain.setVisibility(View.VISIBLE);

                swipeContainer.setRefreshing(false);
            }
        };
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateOptionsMenu(
            Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.home, menu);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_home_list, container, false);
        ButterKnife.inject(this, view);

        tryAgainButton.setOnClickListener(refresh_click());

        fab_auto.setOnClickListener(addPostClick(CategoryType.Automobile));
        fab_electronics.setOnClickListener(addPostClick(CategoryType.Electronics));
        fab_furniture.setOnClickListener(addPostClick(CategoryType.Furniture));
        fab_multiple.setOnClickListener(addPostClick(CategoryType.Multiple));
        fab_other.setOnClickListener(addPostClick(CategoryType.Others));
        fab_realestate.setOnClickListener(addPostClick(CategoryType.RealState));

        progressBar.setVisibility(View.VISIBLE);
        layoutErrorTryAgain.setVisibility(View.GONE);

        swipeContainer.setOnRefreshListener(this);
        swipeContainer.setColorSchemeResources(R.color.TitlePrimaryDark);
        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (fabMenu.isExpanded()) {
                    fabMenu.collapse();
                }
                return true;
            }
        });

        rv.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(ConfigurationManager.CurrentActivityContext);
        rv.setLayoutManager(llm);


        return view;

    }

    private View.OnClickListener addPostClick(final CategoryType categoryType) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (categoryType)
                {
                    case Multiple:
                        break;
                    default:
                        NavigationHelper.NavigateTo(AddPostActivity.class);
                }
            }
        };
    }


    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onRefresh() {
        loadPostsData(this.categoryType);
    }

    public void setFilteredList(CategoryType type)
    {
        loadPostsData(type);
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

}


