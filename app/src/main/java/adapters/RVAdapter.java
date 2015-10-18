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

import java.util.ArrayList;
import java.util.List;

import inksell.inksell.R;
import models.PostSummaryEntity;
import utilities.ConfigurationManager;
import utilities.FavouritesHelper;
import utilities.Utility;

/**
 * Created by Abhinav on 20/07/15.
 */
public class RVAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    List<PostSummaryEntity> postSummaryEntityList;

    boolean isMyPosts = false;
    boolean isFavPosts = false;
    boolean isSearchPosts = false;
    boolean showFavToggleButton = true;

    View.OnClickListener cvClickListener;

    public RVAdapter(List<PostSummaryEntity> persons, View.OnClickListener clickListener){
        this.postSummaryEntityList = persons;
        this.cvClickListener = clickListener;
    }

    public void setIsMyPosts(boolean isMyPosts)
    {
        this.isMyPosts = isMyPosts;
    }
    public void setIsSearchPosts(boolean isSearchPosts)
    {
        this.isSearchPosts = isSearchPosts;
    }
    public void setIsFavPosts(boolean isFavPosts)
    {
        this.isFavPosts = isFavPosts;
    }
    public void setShowFavToggleButton(boolean showFavButton)
    {
        this.showFavToggleButton = showFavButton;
    }

    public void Update(List<PostSummaryEntity> persons, View.OnClickListener clickListener)
    {
        List<PostSummaryEntity> postSummaryEntities = new ArrayList<>(persons);
        this.postSummaryEntityList.clear();
        this.postSummaryEntityList.addAll(postSummaryEntities);
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

        if(viewType==1 && !isMyPosts)
        {
            resource = R.layout.posts_card_view_without_pic;
        }
        else if(viewType ==1 && (isMyPosts || isSearchPosts))
        {
            resource = R.layout.search_card_view_without_pic;
        }

        View v = LayoutInflater.from(viewGroup.getContext()).inflate(resource, viewGroup, false);
        PostViewHolder pvh = new PostViewHolder(v);

        if(isMyPosts) {
            pvh.postedBy.setVisibility(View.GONE);
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
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, final int position) {

        PostViewHolder postViewHolder = (PostViewHolder)viewHolder;
        final PostSummaryEntity postSummaryEntity = postSummaryEntityList.get(position);

        postViewHolder.cv.setOnClickListener(this.cvClickListener);
        postViewHolder.postedBy.setText(postSummaryEntity.PostedBy);
        postViewHolder.postedOn.setText(Utility.StringDateToRelativeStringDate(postSummaryEntity.Postdate));
        postViewHolder.postTitle.setText(postSummaryEntity.Title);

        //Set data for search/my posts cardview above
        if(isMyPosts)
            return;

        if(!showFavToggleButton)
        {
            postViewHolder.fav.setVisibility(View.GONE);
        }

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
