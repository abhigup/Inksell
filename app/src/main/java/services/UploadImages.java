package services;

import android.net.Uri;
import android.os.AsyncTask;

import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.blob.CloudBlobClient;
import com.microsoft.azure.storage.blob.CloudBlobContainer;
import com.microsoft.azure.storage.blob.CloudBlockBlob;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import Constants.AppData;
import Constants.InksellConstants;
import utilities.Utility;

/**
 * Created by Abhinav on 27/09/15.
 */
public class UploadImages extends AsyncTask<List<Uri>, Integer, List<String>>{

    IUploadListener iUploadListener;

    public UploadImages(IUploadListener listener)
    {
        iUploadListener = listener;
    }

    protected void onPostExecute(List<String> imageUrls) {
        iUploadListener.onUpdateSuccess(imageUrls);
    }

    @Override
    protected List<String> doInBackground(List<Uri>... params) {
        List<String> urls = uploadImages(params[0]);
        return urls;
    }

    private List<String> uploadImages(List<Uri> imageEntityList)
    {
        List<String> imageUrls = null;
        try
        {
            imageUrls = new ArrayList<>();

            for(int i=0;i<imageEntityList.size();i++) {
                // Retrieve storage account from connection-string.
                CloudStorageAccount storageAccount = CloudStorageAccount.parse(InksellConstants.storageConnectionString);

                // Create the blob client.
                CloudBlobClient blobClient = storageAccount.createCloudBlobClient();

                // Retrieve reference to a previously created container.
                CloudBlobContainer container = blobClient.getContainerReference("posts");

                // Define the path to a local file.
                final String filePath = imageEntityList.get(i).getPath();

                // Create or overwrite the "myimage.jpg" blob with contents from a local file.
                String fileName = AppData.UserData.CorporateEmail.substring(0, AppData.UserData.CorporateEmail.indexOf("@"))+AppData.UserData.CopmanyId+AppData.UserData.LocationId + "/" + Utility.getTicks();

                CloudBlockBlob blob = container.getBlockBlobReference(fileName);
                File source = new File(filePath);
                blob.upload(new java.io.FileInputStream(source), source.length());

                imageUrls.add("http://inksell.blob.core.windows.net/posts/"+fileName);

            }
        }
        catch (Exception e)
        {
            // Output the stack trace.
            imageUrls = null;
            e.printStackTrace();
        }
        return imageUrls;
    }
}
