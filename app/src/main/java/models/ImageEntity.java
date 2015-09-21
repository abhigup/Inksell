package models;

import android.graphics.Bitmap;
import android.net.Uri;

/**
 * Created by Abhinav on 21/09/15.
 */
public class ImageEntity {

    public ImageEntity(Bitmap bitmap, Uri uri)
    {
        this.bitmap = bitmap;
        imageUri = uri;
    }

    public Bitmap bitmap;

    public Uri imageUri;
}
