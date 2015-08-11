package utilities;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import Constants.StorageConstants;
import models.PostSummaryEntity;

/**
 * Created by Abhinav on 11/08/15.
 */
public class FavouritesHelper {

    private static List<PostSummaryEntity> postSummaryEntityList;

    public static void RemoveFromFavourites(int postId)
    {
        if(postSummaryEntityList.isEmpty()) {
            return;
        }

        for(int i=0;i<postSummaryEntityList.size();i++)
        {
            if(postSummaryEntityList.get(i).PostId==postId)
            {
                postSummaryEntityList.remove(i);
                break;
            }
        }
        saveFavouritesList();
    }

    public static void ClearAllFromFavourites()
    {
        postSummaryEntityList.clear();
        saveFavouritesList();
    }

    private static void saveFavouritesList()
    {
        LocalStorageHandler.SaveData(StorageConstants.Favourites, postSummaryEntityList);
    }

    public static void AddToFavourites(PostSummaryEntity postSummaryEntity)
    {
        if(postSummaryEntityList==null)
        {
            postSummaryEntityList = new ArrayList<>();
        }
        postSummaryEntityList.add(postSummaryEntity);
        saveFavouritesList();
    }

    public static List<PostSummaryEntity> getFavourites()
    {
        setFavourites();
        return postSummaryEntityList;
    }

    public static void setFavourites()
    {
        postSummaryEntityList = new ArrayList<>();

        PostSummaryEntity[] postSummaryEntities = LocalStorageHandler.GetData(StorageConstants.Favourites, PostSummaryEntity[].class);

        if(postSummaryEntities!=null) {
            postSummaryEntityList = new ArrayList(Arrays.asList(postSummaryEntities));
        }
    }

    public static boolean IsFavourite(int postId)
    {
        if(postSummaryEntityList==null || postSummaryEntityList.isEmpty()) {
            return false;
        }

        for(int i=0;i<postSummaryEntityList.size();i++)
        {
            if(postSummaryEntityList.get(i).PostId==postId)
            {
                return true;
            }
        }

        return false;
    }
}
