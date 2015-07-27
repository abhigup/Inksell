package adapters;

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
        if(!postSummaryEntityList.get(position).HasPostTitlePic())
        {
            return 1;
        }

        return 0;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public void onBindViewHolder(PersonViewHolder personViewHolder, int position) {
        PostSummaryEntity postSummaryEntity = postSummaryEntityList.get(position);

        personViewHolder.cv.setOnClickListener(this.cvClickListener);
        personViewHolder.postedBy.setText(postSummaryEntity.PostedBy);
        personViewHolder.postedOn.setText(Utility.StringDateToRelativeStringDate(postSummaryEntity.Postdate));
        personViewHolder.postTitle.setText(postSummaryEntity.Title);

        if(postSummaryEntity.HasPostTitlePic())
        {
            Picasso.with(ConfigurationManager.CurrentActivityContext)
                    .load(postSummaryEntity.PostTitlePic)
                    .into(personViewHolder.titlePic);
        }

        Utility.setUserPic(personViewHolder.userPic, postSummaryEntity.UserImageUrl, postSummaryEntity.PostedBy);
    }

    public static class PersonViewHolder extends RecyclerView.ViewHolder {
        CardView cv;
        TextView postedBy;
        TextView postTitle;
        TextView postedOn;
        ImageView titlePic;
        ImageView userPic;

        PersonViewHolder(View itemView) {
            super(itemView);
            cv = (CardView)itemView.findViewById(R.id.cv);
            postTitle = (TextView)itemView.findViewById(R.id.post_title);
            postedBy = (TextView)itemView.findViewById(R.id.person_name);
            postedOn = (TextView)itemView.findViewById(R.id.posted_on);
            titlePic = (ImageView)itemView.findViewById(R.id.card_title_pic);
            userPic = (ImageView)itemView.findViewById(R.id.card_user_pic);
        }
    }

}
