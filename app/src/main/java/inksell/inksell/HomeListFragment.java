package inksell.inksell;

import android.app.Activity;
import android.net.Uri;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;

import java.util.List;

import Constants.AppData;
import Constants.InksellConstants;
import Constants.StorageConstants;
import adapters.RVAdapter;
import butterknife.InjectView;
import enums.CategoryType;
import inksell.common.BaseFragment;
import models.PostSummaryEntity;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import services.InksellCallback;
import services.RestClient;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseSequence;
import uk.co.deanwild.materialshowcaseview.ShowcaseConfig;
import utilities.ConfigurationManager;
import utilities.FavouritesHelper;
import utilities.LocalStorageHandler;
import utilities.NavigationHelper;


public class HomeListFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener {

    private CategoryType categoryType;
    private Menu homeMenu;

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

    @InjectView(R.id.cvHomeTransparency)
    CardView cvHomeTransparency;

    private RVAdapter rvAdapter;

    private List<PostSummaryEntity> postSummaryList;
    private OnFragmentInteractionListener mListener;

    @Override
    public int getViewResId() {
        return R.layout.fragment_home_list;
    }

    @Override
    public void initFragment() {
        setHasOptionsMenu(true);
        loadPostsData(CategoryType.AllCategory);
    }

    @Override
    public void initView(View view) {

        cvHomeTransparency.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fabMenu.collapse();
                cvHomeTransparency.setVisibility(View.GONE);
            }
        });

        fabMenu.setOnFloatingActionsMenuUpdateListener(new FloatingActionsMenu.OnFloatingActionsMenuUpdateListener() {
            @Override
            public void onMenuExpanded() {
                cvHomeTransparency.setVisibility(View.VISIBLE);
            }

            @Override
            public void onMenuCollapsed() {
                cvHomeTransparency.setVisibility(View.GONE);
            }
        });

        tryAgainButton.setOnClickListener(refresh_click());

        fab_auto.setOnClickListener(NavigationHelper.addPostClick(CategoryType.Automobile));
        fab_electronics.setOnClickListener(NavigationHelper.addPostClick(CategoryType.Electronics));
        fab_furniture.setOnClickListener(NavigationHelper.addPostClick(CategoryType.Furniture));
        fab_multiple.setOnClickListener(NavigationHelper.addPostClick(CategoryType.Multiple));
        fab_other.setOnClickListener(NavigationHelper.addPostClick(CategoryType.Others));
        fab_realestate.setOnClickListener(NavigationHelper.addPostClick(CategoryType.RealState));

        progressBar.setVisibility(View.VISIBLE);
        layoutErrorTryAgain.setVisibility(View.GONE);

        swipeContainer.setOnRefreshListener(this);
        swipeContainer.setColorSchemeResources(R.color.TitlePrimaryDark);
        rv.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(ConfigurationManager.CurrentActivityContext);
        rv.setLayoutManager(llm);
    }

    private void setupFRE()
    {
        if(LocalStorageHandler.ContainsKey(StorageConstants.AppLoginCount))
        {
            LocalStorageHandler.SaveData(StorageConstants.AppLoginCount, Long.parseLong(LocalStorageHandler.GetData(StorageConstants.AppLoginCount, String.class))+1);
        }
        else
        {
            LocalStorageHandler.SaveData(StorageConstants.AppLoginCount,1);
            ShowcaseConfig config = new ShowcaseConfig();
            config.setDelay(500); // half second between each showcase view

            MaterialShowcaseSequence sequence = new MaterialShowcaseSequence(getActivity(), InksellConstants.HOME_SHOWCASE_ID);

            sequence.setConfig(config);

            sequence.addSequenceItem(getActivity().findViewById(R.id.menu_search),
                    "Search any post within your organisation", "GOT IT");

            sequence.addSequenceItem(getActivity().findViewById(R.id.menu_filter),
                "Filter posts according to categories", "GOT IT");


            sequence.start();
        }
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
                android.os.Handler handler = new android.os.Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {
                        setupFRE();
                    }
                }, 600);
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
        this.homeMenu = menu;
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


