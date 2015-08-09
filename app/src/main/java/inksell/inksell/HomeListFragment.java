package inksell.inksell;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityOptionsCompat;
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
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.getbase.floatingactionbutton.FloatingActionsMenu;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Constants.AppData;
import adapters.RVAdapter;
import butterknife.ButterKnife;
import butterknife.InjectView;
import enums.CategoryType;
import inksell.posts.view.ViewPostActivity;
import models.PostSummaryEntity;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import services.InksellCallback;
import services.RestClient;
import utilities.ConfigurationManager;
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

    private Callback<List<PostSummaryEntity>> setListOnResponse() {
        return new InksellCallback<List<PostSummaryEntity>>() {
            @Override
            public void onSuccess(List<PostSummaryEntity> postSummaryEntities, Response response) {

                layoutErrorTryAgain.setVisibility(View.GONE);
                progressBar.setVisibility(View.GONE);

                postSummaryList = postSummaryEntities;

                if(rvAdapter==null) {
                    RVAdapter adapter = new RVAdapter(postSummaryList, cardViewClickListener());
                    rv.setAdapter(adapter);
                }
                else {
                    rvAdapter.Update(postSummaryList, cardViewClickListener());
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

    public void refresh_click(View view) {
        layoutErrorTryAgain.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);

        onRefresh();
    }

    private View.OnClickListener cardViewClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int childItemPosition = rv.getChildPosition(v);
                PostSummaryEntity postSummaryEntity = postSummaryList.get(childItemPosition);

                Map<String, String> map = new HashMap<String, String>();
                map.put("object", Utility.GetJSONString(postSummaryEntity));

                if(!postSummaryEntity.HasPostTitlePic())
                {
                    Utility.NavigateTo(ViewPostActivity.class, map);
                }
                else {
                    ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(), v.findViewById(R.id.card_title_pic), getString(R.string.cardTitlePicTransition));
                    Utility.NavigateTo(ViewPostActivity.class, map, options.toBundle());
                }
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


