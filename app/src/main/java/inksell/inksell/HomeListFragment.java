package inksell.inksell;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TimerTask;

import Constants.AppData;
import Constants.InksellConstants;
import Constants.StorageConstants;
import adapters.RVAdapter;
import butterknife.InjectView;
import enums.CategoryType;
import inksell.common.BaseFragment;
import models.ILocationCallbacks;
import models.PostSummaryEntity;
import models.PropertyMapEntity;
import retrofit.Callback;
import services.InksellCallback;
import services.RestClient;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseSequence;
import uk.co.deanwild.materialshowcaseview.ShowcaseConfig;
import utilities.ConfigurationManager;
import utilities.FavouritesHelper;
import utilities.LocalStorageHandler;
import utilities.LocationWrapper;
import utilities.NavigationHelper;
import utilities.ResponseStatus;
import utilities.TimerHelper;
import utilities.Utility;


public class HomeListFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener, OnMapReadyCallback, ILocationCallbacks{

    public CategoryType categoryType;
    private Menu homeMenu;

    @InjectView(R.id.map_autocomplete)
    AutoCompleteTextView mAutocompleteView;

    @InjectView(R.id.button_clear)
    ImageButton clear;

    @InjectView(R.id.load_address)
    ProgressBar loadAddress;

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

    @InjectView(R.id.real_estate_fab)
    android.support.design.widget.FloatingActionButton realEstateFab;

    @InjectView(R.id.real_estate_map)
    MapView mapView;

    @InjectView(R.id.mapView_layout)
    RelativeLayout mapViewLayout;

    LocationWrapper locationWrapper;
    GoogleMap map;

    private RVAdapter rvAdapter;
    private boolean isMap = false;
    private List<PostSummaryEntity> postSummaryList;
    private TimerHelper timerHelper = new TimerHelper(3000);
    private Map<Marker, PropertyMapEntity> markerPropertyMapEntityMap = new HashMap<>();
    private boolean markerSelected = false;

    @Override
    public int getViewResId() {
        return R.layout.fragment_home_list;
    }

    @Override
    public void initFragment(Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        loadPostsData(CategoryType.AllCategory);
    }

    @Override
    public void initView(LayoutInflater inflater, View view, Bundle savedInstanceState) {

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

        realEstateFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isMap = !isMap;
                setRealEstateMapList();
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

    private void setRealEstateMapList() {

        if(realEstateFab!=null)
        {
            if(isMap)
            {
                mapViewLayout.setVisibility(View.VISIBLE);
                realEstateFab.setImageResource(R.drawable.ic_list);

                Utility.setMap(null, mapView, this);
            }
            else
            {
                realEstateFab.setImageResource(R.drawable.ic_map);
                mapViewLayout.setVisibility(View.GONE);
            }
        }
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
        isMap = false;
        setRealEstateMapList();

        if(fabMenu!=null) {
            if (categoryType == categoryType.RealState) {
                fabMenu.setVisibility(View.GONE);
                realEstateFab.setVisibility(View.VISIBLE);
            } else if (categoryType == CategoryType.AllCategory) {
                fabMenu.setVisibility(View.VISIBLE);
                realEstateFab.setVisibility(View.GONE);
            } else {
                fabMenu.setVisibility(View.GONE);
                realEstateFab.setVisibility(View.GONE);
            }
        }

        int lastPostid = 0;

        switch (type)
        {
            case AllCategory:
                RestClient.get().getPostSummaryAll(lastPostid, AppData.UserGuid).enqueue(setListOnResponse());
                break;
            default:
                RestClient.get().getFilteredPostSummary(lastPostid, type.ordinal(), AppData.UserGuid).enqueue(setListOnResponse());
        }

    }

    @Override
    public void onResume() {
        super.onResume();



        if(rvAdapter!=null) {
            postSummaryList = FavouritesHelper.setFavourites(postSummaryList);
            rvAdapter.updateFavourite(postSummaryList);
        }
        if(locationWrapper!=null) {
            locationWrapper.onResume();
        }

    }

    private Callback<List<PostSummaryEntity>> setListOnResponse() {
        return new InksellCallback<List<PostSummaryEntity>>() {
            @Override
            public void onSuccess(List<PostSummaryEntity> postSummaryEntities) {

                rv.setVisibility(View.VISIBLE);
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
            public void onError(ResponseStatus responseStatus)
            {
                progressBar.setVisibility(View.GONE);
                layoutErrorTryAgain.setVisibility(View.VISIBLE);

                rv.setVisibility(View.GONE);
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


    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onRefresh() {
        loadPostsData(this.categoryType);
    }

    public void setFilteredList(CategoryType type)
    {
        loadPostsData(type);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.setOnCameraChangeListener(cameraChangeListener());
        map.setMyLocationEnabled(true);

        mAutocompleteView.setOnItemClickListener(mAutocompleteClickListener);

        clear.setOnClickListener(clearClickListener);

        locationWrapper = new LocationWrapper(getActivity(), this, null);
        locationWrapper.onResume();
        locationWrapper.checkLocationSettings();
    }

    View.OnClickListener clearClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mAutocompleteView.setText("");
        }};

    private AdapterView.OnItemClickListener mAutocompleteClickListener
            = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            /*
             Retrieve the place ID of the selected item from the Adapter.
             The adapter stores each Place suggestion in a AutocompletePrediction from which we
             read the place ID and title.
              */
            final AutocompletePrediction item = locationWrapper.getItemAtPosition(position);
            if(item==null)
                return;

            final String placeId = item.getPlaceId();
            locationWrapper.updateLocationFromPlaceId(placeId);

            //Close Keyboard
            Utility.hideSoftKeyboard(getActivity());
        }
    };

    private GoogleMap.OnCameraChangeListener cameraChangeListener() {
        return new GoogleMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {
                if(markerSelected)
                {
                    markerSelected = false;
                    return;
                }
                loadAddress.setVisibility(View.VISIBLE);
                clear.setVisibility(View.GONE);

                TimerTask timerTask = new TimerTask() {
                    @Override
                    public void run() {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                loadPropertiesOnCameraChange();

                                loadAddress.setVisibility(View.GONE);
                                clear.setVisibility(View.VISIBLE);
                            }
                        });
                    }
                };
                timerHelper.startTask(timerTask);
            }
        };
    }

    private void loadPropertiesOnCameraChange()
    {
        CameraPosition cameraPosition = map.getCameraPosition();

        LatLngBounds bounds = map.getProjection().getVisibleRegion().latLngBounds;

        Location locationCenter = new Location("A");
        locationCenter.setLatitude(cameraPosition.target.latitude);
        locationCenter.setLongitude(cameraPosition.target.longitude);

        Location locationEast = new Location("B");
        locationEast.setLongitude(bounds.northeast.longitude);
        locationEast.setLatitude(cameraPosition.target.latitude);

        int distance = (int) locationCenter.distanceTo(locationEast);

        RestClient.get().getMapSummary(String.valueOf(distance), AppData.UserGuid, String.valueOf(cameraPosition.target.latitude), String.valueOf(cameraPosition.target.longitude)).enqueue(new InksellCallback<List<PropertyMapEntity>>() {
            @Override
            public void onSuccess(List<PropertyMapEntity> propertyMapEntities) {

                Iterator iterator = markerPropertyMapEntityMap.keySet().iterator();
                while (iterator.hasNext())
                {
                    ((Marker)iterator.next()).remove();
                }

                markerPropertyMapEntityMap.clear();

                map.setOnMarkerClickListener(onMarkerClickListener());
                for (int i = 0; i < propertyMapEntities.size(); i++) {
                    Marker marker = map.addMarker(new MarkerOptions().position(new LatLng(propertyMapEntities.get(i).latitude, propertyMapEntities.get(i).longitude)));
                    marker.setTitle(propertyMapEntities.get(i).PostTitle);
                    markerPropertyMapEntityMap.put(marker, propertyMapEntities.get(i));
                }
            }
        });
    }

    private GoogleMap.OnMarkerClickListener onMarkerClickListener() {
        return new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                markerSelected = true;

                if(marker.isInfoWindowShown()) {
                    PropertyMapEntity propertyMapEntity = markerPropertyMapEntityMap.get(marker);
                    return true;
                }
                return false;
            }
        };
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == InksellConstants.REQUEST_CHECK_SETTINGS && resultCode == getActivity().RESULT_OK)
        {
        }
    }

    @Override
    public void loadDefaultLocation(LatLng latLng) {

    }

    @Override
    public void onPause() {
        super.onPause();
        if(locationWrapper!=null) {
            locationWrapper.onPause();
        }
    }

    private void zoom(LatLng latLng) {
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,
                15));
    }

    @Override
    public void handleNewLocation(LatLng latLng) {
        zoom(latLng);

        if(locationWrapper!=null) {
            locationWrapper.updateAdapterBounds(mAutocompleteView, latLng);
        }
    }


}


