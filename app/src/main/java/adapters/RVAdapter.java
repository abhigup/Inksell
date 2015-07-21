package adapters;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
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
        if(position==3 || position==5 || position==6 || position==7)
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
        personViewHolder.cv.setOnClickListener(this.cvClickListener);
        personViewHolder.postedBy.setText(postSummaryEntityList.get(position).PostedBy);
        personViewHolder.postedOn.setText(Utility.StringDateToRelativeStringDate(postSummaryEntityList.get(position).Postdate));
        personViewHolder.postTitle.setText(postSummaryEntityList.get(position).Title);

        if(position==3 || position==5 || position==6 || position==7)
        {
            //personViewHolder.postTitle.setBackgroundColor(Utility.generateRandomColour());
        }
        else
        {
            String titlepic = null;
            switch (position)
            {
                case 2:
                    titlepic = "https://inksell.blob.core.windows.net/posts/ccf75ef3-3b07-40b8-b0a1-e04471dda1ca/28122013182625041.jpg";
                    break;
                case 4:
                    titlepic = "https://inksell.blob.core.windows.net/posts/d6388f53-ed36-473a-bfd6-fbf76bebe18f/31012014185643388.jpg";
                    break;
                case 1:
                    titlepic = "http://cnet1.cbsistatic.com/hub/i/r/2013/11/25/a5dfa579-84b8-11e3-beb9-14feb5ca9861/thumbnail/770x433/94c1ef0e2322a3c1448cfe623d8bf598/Dell_Venue_8_Pro_35828030__05.jpg";
                    break;
                case 0:
                    titlepic = "http://img01.olx.in/images_olxin/52935875_2_1000x700_color-tv-21-hathway-set-top-box-wooden-tv-corner-.jpg";
                    break;
                case 8:
                    titlepic = "https://inksell.blob.core.windows.net/posts/80e8fb86-efd5-485c-b3e5-595919fb6bab/05012014184518022.jpg";
                    break;
                case 9:
                    titlepic = "https://inksell.blob.core.windows.net/posts/3077ccdd-d56e-4d61-884b-9ca77644b6c3/05012014151013285.jpg";
                    break;

            }

            if(!Utility.IsStringNullorEmpty(titlepic))
            {
                Picasso.with(ConfigurationManager.CurrentActivityContext)
                        .load(titlepic)
                        .into(personViewHolder.titlePic);
            }
            else
            {
                personViewHolder.titlePic.setImageBitmap(null);
            }

        }

        if(Utility.IsStringNullorEmpty(postSummaryEntityList.get(position).UserImageUrl))
        {
            ColorGenerator generator = ColorGenerator.MATERIAL;
            int color2 = generator.getColor(postSummaryEntityList.get(position).PostedBy);

            TextDrawable.IBuilder builder = TextDrawable.builder()
                    .beginConfig()
                .width(50)  // width in px
                .height(50) // height in px
                .endConfig()
                    .rect();

            TextDrawable userImage = builder.build(postSummaryEntityList.get(position).PostedBy.substring(0,1), color2);

            personViewHolder.userPic.setImageDrawable(userImage);
        }
        else {
            Picasso.with(ConfigurationManager.CurrentActivityContext)
                    .load(postSummaryEntityList.get(position).UserImageUrl)
                    .placeholder(R.drawable.ic_person)
                    .into(personViewHolder.userPic);
        }
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
