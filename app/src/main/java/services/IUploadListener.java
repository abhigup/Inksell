package services;

import java.util.List;

/**
 * Created by Abhinav on 27/09/15.
 */
public interface IUploadListener {

    public void onUpdateSuccess(List<String> imageUrls);
}
