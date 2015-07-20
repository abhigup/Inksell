package adapters;

import android.graphics.drawable.Drawable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import inksell.inksell.R;
import models.PostSummaryEntity;
import utilities.ConfigurationManager;
import utilities.Utility;

/**
 * Created by Abhinav on 20/07/15.
 */
public class RVAdapter extends RecyclerView.Adapter<RVAdapter.PersonViewHolder> {

    List<PostSummaryEntity> postSummaryEntityList;

    View.OnClickListener cvClickListener;

    public RVAdapter(List<PostSummaryEntity> persons, View.OnClickListener clickListener){
        this.postSummaryEntityList = persons;
        this.cvClickListener = clickListener;
    }

    @Override
    public int getItemCount() {
        return postSummaryEntityList.size();
    }

    @Override
    public PersonViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        int resource = R.layout.posts_card_view;
        if(viewType==1)
        {
            resource = R.layout.posts_card_view_without_pic;
        }
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(resource, viewGroup, false);
        PersonViewHolder pvh = new PersonViewHolder(v);
        return pvh;
    }

    @Override
    public int getItemViewType(int position) {
        // Just as an example, return 0 or 2 depending on position
        // Note that unlike in ListView adapters, types don't have to be contiguous
        return position%2;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public void onBindViewHolder(PersonViewHolder personViewHolder, int i) {
        personViewHolder.cv.setOnClickListener(this.cvClickListener);
        personViewHolder.personName.setText(postSummaryEntityList.get(i).PostedBy);
        personViewHolder.personAge.setText(postSummaryEntityList.get(i).Postdate.toString());

        if(i%2==1)
        {
            personViewHolder.personName.setBackgroundColor(Utility.generateRandomColour());
        }

        if(Utility.IsStringNullorEmpty(postSummaryEntityList.get(i).UserImageUrl))
        {
            Drawable myDrawable = ConfigurationManager.CurrentActivityContext.getResources().getDrawable(R.drawable.ic_person);
            personViewHolder.userPic.setImageDrawable(myDrawable);
        }
        else {
            Picasso.with(ConfigurationManager.CurrentActivityContext)
                    .load(postSummaryEntityList.get(i).UserImageUrl)
                    .placeholder(R.drawable.ic_person)
                    .into(personViewHolder.userPic);
        }
    }

    public static class PersonViewHolder extends RecyclerView.ViewHolder {
        CardView cv;
        TextView personName;
        TextView personAge;
        ImageView titlePic;
        ImageView userPic;

        PersonViewHolder(View itemView) {
            super(itemView);
            cv = (CardView)itemView.findViewById(R.id.cv);
            personName = (TextView)itemView.findViewById(R.id.person_name);
            personAge = (TextView)itemView.findViewById(R.id.person_age);
            titlePic = (ImageView)itemView.findViewById(R.id.card_title_pic);
            userPic = (ImageView)itemView.findViewById(R.id.card_user_pic);
        }
    }

}
