package utilities;

import android.app.Activity;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import inksell.inksell.R;

/**
 * Created by Abhinav on 19/10/15.
 */
public class EmptyTemplateHelper {

    View view;
    ImageView icon;
    TextView textLabel;
    RelativeLayout emptyLayout;

    public EmptyTemplateHelper(View view)
    {
        this.view = view;
    }

    public EmptyTemplateHelper(Activity activity)
    {
        this.view = activity.getWindow().getDecorView().findViewById(android.R.id.content);
    }

    public void setEmptyTemplate(int iconDrawable, int label)
    {
        icon = (ImageView)view.findViewById(R.id.empty_image);
        textLabel = (TextView)view.findViewById(R.id.empty_label);
        emptyLayout = (RelativeLayout)view.findViewById(R.id.empty_layout);

        icon.setImageResource(iconDrawable);
        textLabel.setText(ConfigurationManager.CurrentActivityContext.getString(label));
        emptyLayout.setVisibility(View.GONE);
    }

    public void setLayoutVisibility(int visibility)
    {
        if(emptyLayout!=null)
        {
            emptyLayout.setVisibility(visibility);
        }
    }
}
