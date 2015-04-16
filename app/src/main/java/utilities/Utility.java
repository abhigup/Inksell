package utilities;

import android.app.AlertDialog;
import android.content.DialogInterface;

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
}
