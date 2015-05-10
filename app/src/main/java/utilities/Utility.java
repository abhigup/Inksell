package utilities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Looper;
import android.widget.EditText;

import com.google.gson.Gson;

import java.util.Map;
import java.util.UUID;

import inksell.inksell.R;

/**
 * Created by Abhinav on 08/04/15.
 */


public class Utility {

    public static void ShowInfoDialog(int resId){
        ShowInfoDialog(GetResourceString(resId));
    }

    public static void ShowInfoDialog(final String message){
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(
                new Runnable() {
                    @Override
                    public void run() {
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ConfigurationManager.CurrentActivityContext);
                        alertDialogBuilder.setMessage(message);
                        alertDialogBuilder.setPositiveButton("ok", null);

                        AlertDialog alertDialog = alertDialogBuilder.create();
                        alertDialog.show();
                    }
                }
        );

    }

    public static void ShowDialog(final String message, final DialogInterface.OnClickListener positive, final DialogInterface.OnClickListener negative){
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(
                new Runnable() {
                    @Override
                    public void run() {
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ConfigurationManager.CurrentActivityContext);
                        alertDialogBuilder.setMessage(message);
                        alertDialogBuilder.setPositiveButton("yes", positive);
                        alertDialogBuilder.setNegativeButton("no", negative);

                        AlertDialog alertDialog = alertDialogBuilder.create();
                        alertDialog.show();
                    }
                }
        );
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

    public static boolean IsEditTextNullorEmpty(EditText editText)
    {
        boolean isEmpty = IsStringNullorEmpty(editText.getText().toString());
        if(isEmpty)
        {
            ShowInfoDialog(editText.getHint().toString());
        }
        return isEmpty;
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

    public static boolean IsNetworkAvailable()
    {
        ConnectivityManager cm =
                (ConnectivityManager)ConfigurationManager.CurrentActivityContext.getSystemService(ConfigurationManager.CurrentActivityContext.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        return isConnected;
    }

    public static boolean IsWiFi()
    {
        ConnectivityManager cm =
                (ConnectivityManager)ConfigurationManager.CurrentActivityContext.getSystemService(ConfigurationManager.CurrentActivityContext.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        boolean isWiFi = false;
        if(isConnected)
        {
            isWiFi = activeNetwork.getType() == ConnectivityManager.TYPE_WIFI;
        }
        return false;
    }

    public static boolean isInteger(String str)
    {
        try{
            int n = Integer.parseInt(str);
            return true;
        }
        catch (NumberFormatException ex)
        {
            return false;
        }
    }

    public static int GetResponseError(ResponseStatus responseStatus)
    {
        switch (responseStatus)
        {
            case UserNotExists:
                return R.string.ErrorUserNotExists;
            default:
                return R.string.ErrorUnknownResponseStatus;
        }
    }
}
