package adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;

import inksell.inksell.R;

/**
 * Created by Abhinav on 19/09/15.
 */
public class EditImageGridAdapter extends RecyclerView.Adapter<EditImageGridAdapter.ImageGridViewHolder> {

    private Context context;
    private List<String> imageUriList;

    private EditImageGridAdapter adapter = this;

    public EditImageGridAdapter(Context context, List<String> imageUriList) {
        this.context = context;
        this.imageUriList = imageUriList;
    }

    public List<String> getImageUri()
    {
        return imageUriList;
    }

    @Override
    public ImageGridViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.image_grid_item_layout, viewGroup, false);
        return new ImageGridViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ImageGridViewHolder imageGridViewHolder, final int position) {
        String imageUrl = imageUriList.get(position);
        Picasso.with(context)
                .load(imageUrl)
                .into(imageGridViewHolder.imageView);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public int getItemCount() {
        return this.imageUriList==null?0:this.imageUriList.size();
    }

    public static class ImageGridViewHolder extends RecyclerView.ViewHolder
    {
        ImageView imageView;
        ImageButton imageDeleteButton;

        ImageGridViewHolder(View itemView)
        {
            super(itemView);
            imageView = (ImageView)itemView.findViewById(R.id.imageGridItem);
            imageDeleteButton = (ImageButton) itemView.findViewById(R.id.imageGridItemDeleteButton);
            imageDeleteButton.setVisibility(View.GONE);
        }
    }
}
