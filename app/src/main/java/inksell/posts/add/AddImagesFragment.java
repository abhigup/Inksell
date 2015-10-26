package inksell.posts.add;

import android.content.ClipData;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import Constants.InksellConstants;
import adapters.EditImageGridAdapter;
import adapters.ImageGridAdapter;
import adapters.ImageGridDeleteListener;
import butterknife.InjectView;
import enums.CategoryType;
import inksell.inksell.R;
import models.AutomobileEntity;
import models.ElectronicEntity;
import models.FurnitureEntity;
import models.IPostEntity;
import models.ImageEntity;
import models.OtherEntity;
import models.RealEstateEntity;
import services.IUploadListener;
import services.UploadImages;
import utilities.Utility;

public class AddImagesFragment extends BaseAddFragment implements ImageGridDeleteListener, IUploadListener {

    private Uri cameraCaptureUri;
    private List<String> editModeImageUriList;

    @InjectView(R.id.add_btn_camera)
    Button btnCamera;

    @InjectView(R.id.add_btn_gallery)
    Button btnGallery;

    @InjectView(R.id.imageRecyclerView)
    RecyclerView imageRecyclerView;

    private ImageGridAdapter imageGridAdapter;
    private EditImageGridAdapter editImageGridAdapter;

    @Override
    public int getViewResId() {
        return R.layout.fragment_add_images;
    }

    @Override
    public void initViewAfterSettingEditableView(LayoutInflater inflater, View view, Bundle savedInstanceState) {
        if(!forEdit) {
            btnCamera.setOnClickListener(dispatchTakePictureIntent());
            btnGallery.setOnClickListener(dispatchPickFromGallery());

            imageGridAdapter = new ImageGridAdapter(getActivity(), null, this);
            imageRecyclerView.setAdapter(imageGridAdapter);
        }
        LinearLayoutManager itemslayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        itemslayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        imageRecyclerView.setLayoutManager(itemslayoutManager);
    }

    @Override
    public void setEditableView(LayoutInflater inflater, View view, Bundle savedInstanceState) {
        btnCamera.setOnClickListener(editImageEdit());
        btnGallery.setOnClickListener(editImageEdit());

        switch (categoryType) {
            case Electronics: {
                ElectronicEntity entity = (ElectronicEntity) iPostEntity;
                editModeImageUriList = entity.PostImagesUrl;
            }
            break;
            case Automobile: {
                AutomobileEntity entity = (AutomobileEntity) iPostEntity;
                editModeImageUriList = entity.PostImagesUrl;
            }
            break;
            case Furniture: {
                FurnitureEntity entity = (FurnitureEntity) iPostEntity;
                editModeImageUriList = entity.PostImagesUrl;
            }
            break;
            case Others: {
                OtherEntity entity = (OtherEntity) iPostEntity;
                editModeImageUriList = entity.PostImagesUrl;
            }
            break;
            case RealState: {
                RealEstateEntity entity = (RealEstateEntity) iPostEntity;
                editModeImageUriList = entity.PostImagesUrl;
            }
        }

        editImageGridAdapter = new EditImageGridAdapter(getActivity(), editModeImageUriList);
        imageRecyclerView.setAdapter(editImageGridAdapter);

        if(editModeImageUriList!=null && !editModeImageUriList.isEmpty())
        {
            imageRecyclerView.setVisibility(View.VISIBLE);
        }
    }

    private View.OnClickListener editImageEdit() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utility.ShowToast("This feature is currently not available in Edit Mode.");
            }
        };
    }

    private View.OnClickListener dispatchPickFromGallery() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent();
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                intent.setAction(Intent.ACTION_GET_CONTENT);
//
//                Intent chooseIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//// Show only images, no videos or anything else
//                chooseIntent.setType("image/*");
//                chooseIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
// Always show the chooser (if there are multiple options available)
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), InksellConstants.PICK_IMAGE_REQUEST);
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
                    startActivityForResult(takePictureIntent, InksellConstants.REQUEST_IMAGE_CAPTURE);
                }
            }
        };
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == InksellConstants.REQUEST_IMAGE_CAPTURE && resultCode == getActivity().RESULT_OK) {

            ArrayList<ImageEntity> list = new ArrayList<ImageEntity>();

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), cameraCaptureUri);
                list.add(new ImageEntity(Utility.getResizedBitmap(bitmap, 0, 100, true), getCacheImagesUri(cameraCaptureUri)));
                imageGridAdapter.AddImages(list);
                imageRecyclerView.setVisibility(View.VISIBLE);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (requestCode == InksellConstants.PICK_IMAGE_REQUEST && resultCode == getActivity().RESULT_OK) {
            ClipData clipdata = data.getClipData();
            ArrayList<ImageEntity> list = new ArrayList<>();

            if(clipdata==null)
            {
                Uri uri = data.getData();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), uri);
                    list.add(new ImageEntity(Utility.getResizedBitmap(bitmap, 0, 100, true), getCacheImagesUri(uri)));
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
            else {
                for (int i = 0; i < clipdata.getItemCount(); i++) {
                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), clipdata.getItemAt(i).getUri());
                        list.add(new ImageEntity(Utility.getResizedBitmap(bitmap, 0, 100, true), getCacheImagesUri(clipdata.getItemAt(i).getUri())));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
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

    @Override
    public boolean verifyAndGetPost(IPostEntity iPostEntity, CategoryType categoryType) {
        if(forEdit)
        {
            ((AddPostActivity) getActivity()).onImageUploaded(true, editModeImageUriList);
            return true;
        }

        if (imageGridAdapter==null || imageGridAdapter.getImageUri() == null || imageGridAdapter.getImageUri().size() == 0) {
            ((AddPostActivity) getActivity()).onImageUploaded(true, new ArrayList<String>());
        }
        else {
            new UploadImages(this).execute(imageGridAdapter.getImageUri());
        }

        return true;
    }

    private Uri getCacheImagesUri(Uri imageUri)
    {
        Uri mediaFile = Utility.getOutputMediaFileUri();

        File file = new File(mediaFile.getPath());
        FileOutputStream fOut;

        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 2;
            Bitmap b= BitmapFactory.decodeStream(getActivity().getContentResolver().openInputStream(imageUri), null, options);

            fOut = new FileOutputStream(file);
            b.compress(Bitmap.CompressFormat.JPEG, 90, fOut);
            fOut.flush();
            fOut.close();
            b.recycle();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return mediaFile;
    }


    @Override
    public void onUpdateSuccess(List<String> imageUrls) {
        ((AddPostActivity)getActivity()).onImageUploaded(imageUrls==null?false:true, imageUrls);
    }
}
