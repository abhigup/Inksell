package utilities;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
/**
 * Created by Abhinav on 16/04/15.
 */
public class LocalStorageHandler {

    private static SharedPreferences settings;
    private static SharedPreferences.Editor editor;



    public static void SaveData(String key, Object value) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(ConfigurationManager.CurrentActivityContext);
        Editor editor = sharedPreferences.edit();
        editor.putString(key, Utility.GetJSONString(value));
        editor.commit();
    }

    public static <T> T GetData(String key, Class<T> clazz) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(ConfigurationManager.CurrentActivityContext);
        String dataString=sharedPreferences.getString(key, null);
        if (dataString==null) {
            return null;
        }
        return Utility.GetObjectFromJSON(dataString, clazz);
    }

    public static void ClearAll()
    {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(ConfigurationManager.CurrentActivityContext);
        Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.commit();
    }
}
