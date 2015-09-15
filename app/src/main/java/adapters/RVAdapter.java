package adapters;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.squareup.picasso.Picasso;

import java.util.List;

import inksell.inksell.R;
import models.PostSummaryEntity;
import utilities.ConfigurationManager;
import utilities.FavouritesHelper;
import utilities.Utility;

/**
 * Created by Abhinav on 20/07/15.
 */
public class RVAdapter extends RecyclerView.Adapter<RVAdapter.PostViewHolder> {

    List<PostSummaryEntity> postSummaryEntityList;

    boolean isMyPosts = false;
    boolean isFavPosts = false;

    View.OnClickListener cvClickListener;

    public RVAdapter(List<PostSummaryEntity> persons, View.OnClickListener clickListener){
        this.postSummaryEntityList = persons;
        this.cvClickListener = clickListener;
    }

    public void setIsMyPosts(boolean isMyPosts)
    {
        this.isMyPosts = isMyPosts;
    }
    public void setIsFavPosts(boolean isFavPosts)
    {
        this.isFavPosts = isFavPosts;
    }

    public void Update(List<PostSummaryEntity> persons, View.OnClickListener clickListener)
    {
        this.postSummaryEntityList.clear();
        this.postSummaryEntityList = persons;
        this.cvClickListener = clickListener;
        this.notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return postSummaryEntityList.size();
    }

    @Override
    public PostViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        int resource = isMyPosts?R.layout.my_posts_card_view:R.layout.posts_card_view;

        if(viewType==1)
        {
            resource = R.layout.posts_card_view_without_pic;
        }

        View v = LayoutInflater.from(viewGroup.getContext()).inflate(resource, viewGroup, false);
        PostViewHolder pvh = new PostViewHolder(v);

        if(isMyPosts) {
            pvh.fav.setVisibility(View.GONE);
            pvh.postedBy.setVisibility(View.GONE);
            if(pvh.userPic!=null) {
                pvh.userPic.setVisibility(View.GONE);
            }
        }

        if(isFavPosts)
        {
            pvh.fav.setVisibility(View.GONE);
        }

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
    public void onBindViewHolder(PostViewHolder postViewHolder, final int position) {
        final PostSummaryEntity postSummaryEntity = postSummaryEntityList.get(position);

        postViewHolder.cv.setOnClickListener(this.cvClickListener);
        postViewHolder.postedBy.setText(postSummaryEntity.PostedBy);
        postViewHolder.postedOn.setText(Utility.StringDateToRelativeStringDate(postSummaryEntity.Postdate));
        postViewHolder.postTitle.setText(postSummaryEntity.Title);

        if(postSummaryEntity.HasPostTitlePic())
        {
            Picasso.with(ConfigurationManager.CurrentActivityContext)
                    .load(postSummaryEntity.PostDefaultImage)
                    .into(postViewHolder.titlePic);
        }

        Utility.setUserPic(postViewHolder.userPic, postSummaryEntity.UserImageUrl, postSummaryEntity.PostedBy);
        postViewHolder.fav.setChecked(postSummaryEntity.isFavourite);

        postViewHolder.fav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PostSummaryEntity post = postSummaryEntityList.get(position);
                if(FavouritesHelper.IsFavourite(post.PostId))
                {
                    post.isFavourite = false;
                    FavouritesHelper.RemoveFromFavourites(post.PostId);
                }
                else
                {
                    post.isFavourite = true;
                    FavouritesHelper.AddToFavourites(post);
                }
            }
        });

    }

    public void updateFavourite(List<PostSummaryEntity> postSummaryEntityList) {
        this.postSummaryEntityList = postSummaryEntityList;
        this.notifyDataSetChanged();
    }

    public static class PostViewHolder extends RecyclerView.ViewHolder {
        CardView cv;
        TextView postedBy;
        TextView postTitle;
        TextView postedOn;
        ImageView titlePic;
        ImageView userPic;
        ToggleButton fav;

        PostViewHolder(View itemView) {
            super(itemView);
            cv = (CardView)itemView.findViewById(R.id.cv);
            postTitle = (TextView)itemView.findViewById(R.id.post_title);
            postedBy = (TextView)itemView.findViewById(R.id.person_name);
            postedOn = (TextView)itemView.findViewById(R.id.posted_on);
            titlePic = (ImageView)itemView.findViewById(R.id.card_title_pic);
            userPic = (ImageView)itemView.findViewById(R.id.card_user_pic);
            fav = (ToggleButton)itemView.findViewById(R.id.card_fav_toggle);
        }
    }

}
