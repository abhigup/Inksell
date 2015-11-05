package inksell.user;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import Constants.AppData;
import Constants.StorageConstants;
import butterknife.InjectView;
import enums.PlatformType;
import inksell.common.BaseFragment;
import inksell.gcm.RegistrationIntentService;
import inksell.inksell.R;
import models.SubscriptionEntity;
import models.TagsEntity;
import services.InksellCallback;
import services.RestClient;
import utilities.EmptyTemplateHelper;
import utilities.LocalStorageHandler;
import utilities.ResponseStatus;
import utilities.Utility;

public class SubscriptionFragment extends BaseFragment implements AdapterView.OnItemClickListener{

    @InjectView(R.id.subscriptions_autocomplete)
    AutoCompleteTextView autoCompleteTextView;

    @InjectView(R.id.subscriptions_add_button)
    ImageButton addButton;

    @InjectView(R.id.subscriptions_buttons_layout1)
    LinearLayout buttonsLayout1;

    @InjectView(R.id.subscriptions_buttons_layout2)
    LinearLayout buttonsLayout2;

    @InjectView(R.id.loading_full_page)
    RelativeLayout loadingFullPage;

    @InjectView(R.id.loading_Text)
    TextView loadingText;

    EmptyTemplateHelper emptyTemplateHelper;

    private List<SubscriptionEntity> subscriptionEntityList;

    ArrayAdapter<TagsEntity> tagsAdapter;
    String token;

    @Override
    public int getViewResId() {
        return R.layout.fragment_subscription;
    }

    @Override
    public void initFragment(Bundle savedInstanceState) {
        subscriptionEntityList = new ArrayList<>();
        tagsAdapter = new ArrayAdapter(getActivity(), R.layout.spinner_item);

        if (Utility.checkPlayServices(getActivity())) {
            Intent intent = new Intent(getActivity(), RegistrationIntentService.class);
            getActivity().startService(intent);
        }
    }

    private void GetAllSubscriptionTags(final int failCount)
    {
        RestClient.get().getAllSubscriptionsTags().enqueue(new InksellCallback<List<TagsEntity>>() {
            @Override
            public void onSuccess(List<TagsEntity> tagsEntities) {
                tagsAdapter.clear();
                tagsAdapter.addAll(tagsEntities);
                autoCompleteTextView.setThreshold(1);
                autoCompleteTextView.setAdapter(tagsAdapter);
            }

            @Override
            public void onError(ResponseStatus responseStatus) {
                if (failCount < 5) {
                    GetAllSubscriptionTags(failCount + 1);
                }
            }
        });
    }

    @Override
    public void initView(LayoutInflater inflater, View view, Bundle savedInstanceState) {
        autoCompleteTextView.setOnItemClickListener(this);
        emptyTemplateHelper = new EmptyTemplateHelper(view);
        emptyTemplateHelper.setEmptyTemplate(R.drawable.subscription_none, R.string.emptySubscriptionList);

        loadingFullPage.setVisibility(View.GONE);
        addButton.setOnClickListener(addNewTag());

        GetAllSubscriptionTags(0);
        setSubscriptionListFromLocal();
    }

    private View.OnClickListener addNewTag() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                boolean isNew = false;
                String tag = autoCompleteTextView.getText().toString();
                for(int i=0;i<tagsAdapter.getCount();i++)
                {
                    TagsEntity tagsEntity = tagsAdapter.getItem(i);
                    if(tagsEntity!=null && tag.equalsIgnoreCase(tagsEntity.tagName))
                    {
                        addNewSubscription(tagsEntity.tagName, tagsEntity.tagId, false);
                        isNew = true;
                        break;
                    }
                }

                if(!isNew)
                {
                    addNewSubscription(tag, 0, true);
                    GetAllSubscriptionTags(1);
                }
            }
        };
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        TagsEntity tagsEntity = (TagsEntity) parent.getItemAtPosition(position);

        Utility.hideSoftKeyboard(getActivity());

        if(subscriptionEntityList.size()>=3)
        {
            Utility.ShowToast(R.string.ErrorSubscriptionLimitReached);
            return;
        }

        for(int i=0;i<subscriptionEntityList.size();i++)
        {
            if(subscriptionEntityList.get(i).tagId==tagsEntity.tagId)
            {
                Utility.ShowToast(R.string.ErrorSubscriptionAlreadyAdded);
                return;
            }
        }

        addNewSubscription(tagsEntity.tagName, tagsEntity.tagId, false);
    }

    private void addNewSubscription(String tagName, int tagId, boolean isNew)
    {
        final SubscriptionEntity subscriptionEntity = new SubscriptionEntity();
        subscriptionEntity.tagId = tagId;
        subscriptionEntity.tagName = tagName;
        subscriptionEntity.IsNew = isNew;

        if(Utility.IsStringNullorEmpty(AppData.NotificationToken))
        {
            Utility.ShowToast("Unable to setup notifications! Please try again");
            return;
        }

        if(subscriptionEntity==null)
            return;


        subscriptionEntity.userGuid = AppData.UserGuid;
        subscriptionEntity.platformType = PlatformType.Android.ordinal();
        subscriptionEntity.userUri = AppData.NotificationToken;

        loadingText.setText(getString(R.string.addTag));
        loadingFullPage.setVisibility(View.VISIBLE);

        RestClient.post().addSubscriptionV2(subscriptionEntity).enqueue(new InksellCallback<String>() {
            @Override
            public void onSuccess(String s) {
                subscriptionEntity.registrationId = s;
                Utility.ShowToast(R.string.subscriptionAddSuccess);

                subscriptionEntityList.add(subscriptionEntity);
                saveSubscriptions();
                addSubscriptionsButtons(subscriptionEntity);

                loadingFullPage.setVisibility(View.GONE);
            }

            @Override
            public void onError(ResponseStatus responseStatus) {
                Utility.ShowToast(R.string.subscriptionAddFailure);
                loadingFullPage.setVisibility(View.GONE);
            }
        });
    }

    private void addSubscriptionsButtons(SubscriptionEntity subscriptionEntity)
    {
        //ToDo : Add logic to make a call and then add the button on success
        Button btnTag = new Button(getActivity());
        btnTag.setBackgroundResource(R.drawable.oval_background);
        btnTag.setTextColor(Color.WHITE);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(0,0, Utility.GetPixelsFromDp(16), 0);
        btnTag.setLayoutParams(params);
        btnTag.setText(subscriptionEntity.tagName);
        btnTag.setId(subscriptionEntity.tagId);
        btnTag.setTag(subscriptionEntity);
        btnTag.setOnClickListener(removeTag());
        //add button to the layout
        if(subscriptionEntityList.indexOf(subscriptionEntity)<2) {
            buttonsLayout1.addView(btnTag);
        }
        else {
            buttonsLayout2.addView(btnTag);
        }
            autoCompleteTextView.setText("");

    }

    private View.OnClickListener removeTag() {
        return new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                Button button = (Button) v;
                final SubscriptionEntity subscriptionEntity = (SubscriptionEntity) button.getTag();
                Utility.ShowDialog(getString(R.string.removeSubscription), unSubscribe(subscriptionEntity, v));
            }
        };
    }

    private DialogInterface.OnClickListener unSubscribe(final SubscriptionEntity subscriptionEntity, final View v) {
        return new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        unsubscribeTags(subscriptionEntity, v);
                }
            }
        };
    }

    private void unsubscribeTags(final SubscriptionEntity subscriptionEntity, final View v)
    {
        loadingText.setText(getString(R.string.removeTag));
        loadingFullPage.setVisibility(View.VISIBLE);

        for(int i=0;i<subscriptionEntityList.size();i++)
        {
            if(subscriptionEntityList.get(i).tagId==subscriptionEntity.tagId)
            {
                List<SubscriptionEntity> subscriptionEntities = new ArrayList<>();
                subscriptionEntities.add(subscriptionEntity);
                final int finalI = i;

                RestClient.post().removeListedSubscriptionV2(subscriptionEntities).enqueue(new InksellCallback<Integer>() {
                    @Override
                    public void onSuccess(Integer integer) {

                        if(finalI <2) {
                            buttonsLayout1.removeView(v);
                            if(subscriptionEntityList.size()>2)
                            {
                                View view = buttonsLayout2.findViewById(subscriptionEntityList.get(2).tagId);
                                if(view!=null)
                                {
                                    buttonsLayout2.removeView(view);
                                    buttonsLayout1.addView(view);
                                }
                            }
                        }
                        else
                        {
                            buttonsLayout2.removeView(v);
                        }
                        subscriptionEntityList.remove(finalI);
                        saveSubscriptions();
                        loadingFullPage.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError(ResponseStatus responseStatus) {

                        loadingFullPage.setVisibility(View.GONE);
                    }
                });
            }
        }


    }

    private void saveSubscriptions()
    {
        LocalStorageHandler.SaveData(StorageConstants.SubscriptionTagEntities, subscriptionEntityList);
        if(subscriptionEntityList==null || subscriptionEntityList.isEmpty())
        {
            emptyTemplateHelper.setLayoutVisibility(View.VISIBLE);
        }
        else
        {
            emptyTemplateHelper.setLayoutVisibility(View.GONE);
        }
    }

    private void setSubscriptionListFromLocal()
    {
        SubscriptionEntity[] subscriptionEntities = LocalStorageHandler.GetData(StorageConstants.SubscriptionTagEntities, SubscriptionEntity[].class);
        if(subscriptionEntities!=null && subscriptionEntities.length>0)
        {
            subscriptionEntityList = new ArrayList(Arrays.asList(subscriptionEntities));
            for(int i=0;i<subscriptionEntityList.size();i++)
            {
                addSubscriptionsButtons(subscriptionEntityList.get(i));
            }
            emptyTemplateHelper.setLayoutVisibility(View.GONE);
        }
        else
        {
            emptyTemplateHelper.setLayoutVisibility(View.VISIBLE);
        }
    }

}
