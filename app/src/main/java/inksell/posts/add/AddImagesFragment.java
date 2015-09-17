package inksell.posts.add;

import android.content.Intent;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;

import butterknife.InjectView;
import inksell.inksell.R;

public class AddImagesFragment extends BaseAddFragment {

    @InjectView(R.id.add_btn_camera)
    Button btnCamera;

    @InjectView(R.id.add_btn_gallery)
    Button btnGallery;

    @Override
    public int getViewResId() {
        return R.layout.fragment_add_images;
    }

    @Override
    public void initView(View view) {
        btnCamera.setOnClickListener(dispatchTakePictureIntent());

    }

    private View.OnClickListener dispatchTakePictureIntent() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                }
            }
        };
    }

    static final int REQUEST_IMAGE_CAPTURE = 1;

}
