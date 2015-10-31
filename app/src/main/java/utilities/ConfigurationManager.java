package utilities;

import android.content.Context;

import Constants.AppData;
import Constants.StorageConstants;
import models.UserEntity;

/**
 * Created by Abhinav on 17/04/15.
 */
public class ConfigurationManager {
    public static Context CurrentActivityContext;

    public static void setLocalData()
    {
        AppData.UserGuid = LocalStorageHandler.GetData(StorageConstants.UserUUID, String.class);
        FavouritesHelper.setFavouritesFromLocal();
        AppData.UserData = LocalStorageHandler.GetData(StorageConstants.UserData, UserEntity.class);
    }
}
