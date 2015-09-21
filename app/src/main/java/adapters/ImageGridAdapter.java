package adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

import inksell.inksell.R;
import models.ImageEntity;

/**
 * Created by Abhinav on 19/09/15.
 */
public class ImageGridAdapter extends RecyclerView.Adapter<ImageGridAdapter.ImageGridViewHolder> {

    private Context context;
    private List<Bitmap> bitmapList;
    private List<Uri> imageUriList;

    ImageGridDeleteListener listener;

    private ImageGridAdapter adapter = this;

    public ImageGridAdapter(Context context, List<Bitmap> bitmapList, ImageGridDeleteListener listener) {
        this.context = context;
        this.bitmapList = bitmapList;
        this.listener = listener;
    }

    public List<Uri> getImageUri()
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
        Bitmap item = bitmapList.get(position);
        imageGridViewHolder.imageView.setImageBitmap(item);
        imageGridViewHolder.imageDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bitmapList.remove(position);
                imageUriList.remove(position);
                adapter.notifyItemRemoved(position);
                adapter.notifyDataSetChanged();

                if(bitmapList.size()==0)
                {
                    adapter.listener.onImageDelete();
                }
            }
        });
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public int getItemCount() {
        return this.bitmapList==null?0:this.bitmapList.size();
    }

    public void AddImages(ArrayList<ImageEntity> imageEntityArrayList)
    {
        if(bitmapList==null)
        {
            bitmapList = new ArrayList<>();
            imageUriList = new ArrayList<>();
        }

        for(int i=0;i<imageEntityArrayList.size();i++) {
            bitmapList.add(imageEntityArrayList.get(i).bitmap);
            imageUriList.add(imageEntityArrayList.get(i).imageUri);
        }
        this.notifyDataSetChanged();
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
        }
    }
}
