package inksell.user;

import android.app.Activity;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import Constants.StorageConstants;
import butterknife.InjectView;
import inksell.common.BaseFragment;
import inksell.inksell.R;
import models.TagsEntity;
import services.InksellCallback;
import services.RestClient;
import utilities.LocalStorageHandler;
import utilities.Utility;

public class SubscriptionFragment extends BaseFragment implements AdapterView.OnItemClickListener {

    @InjectView(R.id.subscriptions_autocomplete)
    AutoCompleteTextView autoCompleteTextView;

    @InjectView(R.id.subscriptions_buttons_layout)
    LinearLayout buttonsLayout;

    private List<TagsEntity> tagsEntityList;

    ArrayAdapter tagsAdapter;

    @Override
    public int getViewResId() {
        return R.layout.fragment_subscription;
    }

    @Override
    public void initFragment(Bundle savedInstanceState) {
        tagsEntityList = new ArrayList<>();
        tagsAdapter = new ArrayAdapter(getActivity(), R.layout.spinner_item);
    }

    private void GetAllSubscriptionTags(final int failCount)
    {
        RestClient.get().getAllSubscriptionsTags().enqueue(new InksellCallback<List<TagsEntity>>() {
            @Override
            public void onSuccess(List<TagsEntity> tagsEntities) {
                tagsAdapter.addAll(tagsEntities);
                autoCompleteTextView.setThreshold(1);
                autoCompleteTextView.setAdapter(tagsAdapter);
            }

            @Override
            public void onError() {
                if (failCount < 5) {
                    GetAllSubscriptionTags(failCount + 1);
                }
            }
        });
    }

    @Override
    public void initView(LayoutInflater inflater, View view, Bundle savedInstanceState) {
        autoCompleteTextView.setOnItemClickListener(this);
        GetAllSubscriptionTags(0);
        setSubscriptionListFromLocal();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        TagsEntity tagsEntity = (TagsEntity) parent.getItemAtPosition(position);

        if(tagsEntityList.size()>=3)
        {
            Utility.ShowToast(R.string.ErrorSubscriptionLimitReached);
            return;
        }

        for(int i=0;i<tagsEntityList.size();i++)
        {
            if(tagsEntityList.get(i).tagId==tagsEntity.tagId)
            {
                Utility.ShowToast(R.string.ErrorSubscriptionAlreadyAdded);
                return;
            }
        }

        tagsEntityList.add(tagsEntity);
        saveSubscriptions();
        addSubscriptionsButtons(tagsEntity);
    }

    private void addSubscriptionsButtons(TagsEntity tagsEntity)
    {
        //ToDo : Add logic to make a call and then add the button on success
        Button btnTag = new Button(getActivity());
        btnTag.setBackgroundResource(R.drawable.oval_background);
        btnTag.setTextColor(Color.WHITE);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(0,0, Utility.GetPixelsFromDp(16), 0);
        btnTag.setLayoutParams(params);
        btnTag.setText(tagsEntity.tagName);
        btnTag.setId(tagsEntity.tagId);
        btnTag.setTag(tagsEntity);
        btnTag.setOnClickListener(removeTag());
        //add button to the layout
        buttonsLayout.addView(btnTag);
        autoCompleteTextView.setText("");
    }

    private View.OnClickListener removeTag() {
        return new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                Button button = (Button) v;
                final TagsEntity tagsEntity = (TagsEntity) button.getTag();
                Utility.ShowDialog(getString(R.string.removeSubscription), unSubscribe(tagsEntity, v));
            }
        };
    }

    private DialogInterface.OnClickListener unSubscribe(final TagsEntity tagsEntity, final View v) {
        return new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which)
                        {
                            case DialogInterface.BUTTON_POSITIVE:
                                for(int i=0;i<tagsEntityList.size();i++)
                                {
                                    if(tagsEntityList.get(i).tagId==tagsEntity.tagId)
                                    {
                                        buttonsLayout.removeView(v);
                                        tagsEntityList.remove(i);
                                    }
                                }
                                saveSubscriptions();
                        }
                    }
        };
    }

    private void saveSubscriptions()
    {
        LocalStorageHandler.SaveData(StorageConstants.SubscriptionTagEntities, tagsEntityList);
    }

    private void setSubscriptionListFromLocal()
    {
        TagsEntity[] tagsEntities = LocalStorageHandler.GetData(StorageConstants.SubscriptionTagEntities, TagsEntity[].class);
        if(tagsEntities!=null && tagsEntities.length>0)
        {
            tagsEntityList = new ArrayList(Arrays.asList(tagsEntities));
            for(int i=0;i<tagsEntityList.size();i++)
            {
                addSubscriptionsButtons(tagsEntityList.get(i));
            }
        }
    }

}
