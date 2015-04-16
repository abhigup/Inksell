package utilities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;

import com.google.gson.Gson;

import java.util.Map;
import java.util.UUID;

/**
 * Created by Abhinav on 08/04/15.
 */
public class Utility {

    public static void ShowInfoDialog(int resId){
        ShowInfoDialog(GetResourceString(resId));
    }

    public static void ShowInfoDialog(String message){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ConfigurationManager.CurrentActivityContext);
        alertDialogBuilder.setMessage(message);
        alertDialogBuilder.setPositiveButton("ok", null);

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();

    }

    public static void ShowDialog(String message, DialogInterface.OnClickListener positive, DialogInterface.OnClickListener negative){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ConfigurationManager.CurrentActivityContext);
        alertDialogBuilder.setMessage(message);
        alertDialogBuilder.setPositiveButton("yes", positive);
        alertDialogBuilder.setNegativeButton("no", negative);

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();

    }

    public static UUID GetUUID(String str)
    {
        try{
            if(str==null) return null;
            UUID uuid = UUID.fromString(str);
            return uuid;
        }
        catch (Exception e)
        {
            return null;
        }
    }

    public static boolean IsStringNullorEmpty(String str)
    {
        if(str == null || str.isEmpty() || str.trim().isEmpty())
        {
            return true;
        }
        return false;
    }

    public static String GetResourceString(int resId) {
        return ConfigurationManager.CurrentActivityContext.getString(resId);
    }

    public static void NavigateTo(Class clazz)
    {
        NavigateTo(clazz, null);
    }

    public static void NavigateTo(Class clazz, Map<String, Object> map)
    {
        Intent intent = new Intent(ConfigurationManager.CurrentActivityContext, clazz);
        if(map!=null) {
            intent.putExtra("intentExtra", GetJSONString(map));
        }
        ConfigurationManager.CurrentActivityContext.startActivity(intent);
    }



    public static String GetJSONString(Object entity)
    {
        Gson usergson=new Gson();
        String jsonString= usergson.toJson(entity);
        return jsonString;
    }

    public static <T> T GetObjectFromJSON(String jsonString, Class<T> clazz)
    {
        Gson gson=new Gson();
        return gson.fromJson(jsonString, clazz);
    }
}
