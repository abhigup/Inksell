package inksell.posts.add;

import android.content.ClipData;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import Constants.Constants;
import adapters.ImageGridAdapter;
import adapters.ImageGridDeleteListener;
import butterknife.InjectView;
import inksell.inksell.R;
import models.ImageEntity;
import utilities.Utility;

public class AddImagesFragment extends BaseAddFragment implements ImageGridDeleteListener {

    private Uri cameraCaptureUri;

    @InjectView(R.id.add_btn_camera)
    Button btnCamera;

    @InjectView(R.id.add_btn_gallery)
    Button btnGallery;

    @InjectView(R.id.imageRecyclerView)
    RecyclerView imageRecyclerView;

    private ImageGridAdapter imageGridAdapter;

    @Override
    public int getViewResId() {
        return R.layout.fragment_add_images;
    }

    @Override
    public void initView(View view) {
        btnCamera.setOnClickListener(dispatchTakePictureIntent());
        btnGallery.setOnClickListener(dispatchPickFromGallery());

        LinearLayoutManager itemslayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        itemslayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        imageRecyclerView.setLayoutManager(itemslayoutManager);

        imageGridAdapter = new ImageGridAdapter(getActivity(), null, this);
        imageRecyclerView.setAdapter(imageGridAdapter);

    }

    private View.OnClickListener dispatchPickFromGallery() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Intent chooseIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
// Show only images, no videos or anything else
                chooseIntent.setType("image/*");
                chooseIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
// Always show the chooser (if there are multiple options available)
                startActivityForResult(Intent.createChooser(chooseIntent, "Select Picture"), Constants.PICK_IMAGE_REQUEST);
            }
        };
    }

    private View.OnClickListener dispatchTakePictureIntent() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                    cameraCaptureUri = Utility.getOutputMediaFileUri();
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, cameraCaptureUri);
                    startActivityForResult(takePictureIntent, Constants.REQUEST_IMAGE_CAPTURE);
                }
            }
        };
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Constants.REQUEST_IMAGE_CAPTURE && resultCode == getActivity().RESULT_OK) {

            ArrayList<ImageEntity> list = new ArrayList<ImageEntity>();

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), cameraCaptureUri);
                list.add(new ImageEntity(Utility.getResizedBitmap(bitmap, 0, 100, true), cameraCaptureUri));
                imageGridAdapter.AddImages(list);
                imageRecyclerView.setVisibility(View.VISIBLE);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (requestCode == Constants.PICK_IMAGE_REQUEST && resultCode == getActivity().RESULT_OK) {
            ClipData clipdata = data.getClipData();
            ArrayList<ImageEntity> list = new ArrayList<>();

            for (int i=0; i<clipdata.getItemCount();i++)
            {
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), clipdata.getItemAt(i).getUri());
                    list.add(new ImageEntity(Utility.getResizedBitmap(bitmap, 0, 100, true), clipdata.getItemAt(i).getUri()));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(list.size()>0)
            {
                imageGridAdapter.AddImages(list);
                imageRecyclerView.setVisibility(View.VISIBLE);
            }
            else
            {
                imageRecyclerView.setVisibility(View.GONE);
            }

        }
    }


    @Override
    public void onImageDelete() {
        imageRecyclerView.setVisibility(View.GONE);
    }
}
