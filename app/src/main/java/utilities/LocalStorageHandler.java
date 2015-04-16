package utilities;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

import com.google.gson.Gson;
/**
 * Created by Abhinav on 16/04/15.
 */
public class LocalStorageHandler {

    private static SharedPreferences settings;
    private static SharedPreferences.Editor editor;

    private static String GetString(Object entity)
    {
        Gson usergson=new Gson();
        String jsonString= usergson.toJson(entity);
        return jsonString;
    }

    public static void SaveData(String key, Object value) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(ConfigurationManager.CurrentActivityContext);
        Editor editor = sharedPreferences.edit();
        editor.putString(key, GetString(value));
        editor.commit();
    }

    public static <T> T GetData(String key, Class<T> clazz) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(ConfigurationManager.CurrentActivityContext);
        //Editor editor = sharedPreferences.edit();
        String dataString=sharedPreferences.getString(key, null);
        if (dataString==null) return null;
        Gson gson=new Gson();
        return gson.fromJson(dataString, clazz);
    }
}
