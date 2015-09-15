package utilities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import inksell.inksell.R;
import inksell.posts.view.ViewPostActivity;
import models.PostSummaryEntity;

/**
 * Created by Abhinav on 24/08/15.
 */
public class NavigationHelper {

    public static void NavigateTo(Class clazz)
    {
        NavigateTo(clazz, null);
    }

    public static void NavigateTo(Class clazz, boolean clearStack)
    {
        NavigateTo(clazz, null, clearStack, null);
    }

    public static void NavigateTo(Class clazz, Map<String, String> map)
    {
        NavigateTo(clazz, map, false, null);
    }

    public static void NavigateTo(Class clazz, Map<String, String> map, Bundle bundle)
    {
        NavigateTo(clazz, map, false, bundle);
    }

    public static void NavigateTo(Class clazz, Map<String, String> map, boolean clearStack, @Nullable Bundle bundle)
    {
        Intent intent = new Intent(ConfigurationManager.CurrentActivityContext, clazz);
        if(clearStack) {
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        if(map!=null) {
            intent.putExtra("intentExtra", Utility.GetJSONString(map));
        }
        ConfigurationManager.CurrentActivityContext.startActivity(intent, bundle);
    }


    public static View.OnClickListener cardViewClickListener(final RecyclerView rv, final List<PostSummaryEntity> postSummaryList, final Activity activity) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int childItemPosition = rv.getChildAdapterPosition(v);
                PostSummaryEntity postSummaryEntity = postSummaryList.get(childItemPosition);

                Map<String, String> map = new HashMap<String, String>();
                map.put("postSummary", Utility.GetJSONString(postSummaryEntity));

                if(!postSummaryEntity.HasPostTitlePic())
                {
                    NavigateTo(ViewPostActivity.class, map);
                }
                else {
                    ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(activity, v.findViewById(R.id.card_title_pic), activity.getString(R.string.cardTitlePicTransition));
                    NavigateTo(ViewPostActivity.class, map, options.toBundle());
                }
            }
        };
    }
}
