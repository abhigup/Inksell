package utilities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Looper;
import android.widget.EditText;

import com.google.gson.Gson;

import java.util.Map;
import java.util.Random;
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

    public final static int generateRandomColour() {

        Random r = new Random();
        return Color.argb(200,r.nextInt(100),r.nextInt(100),r.nextInt(100));
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

    public static void NavigateTo(Class clazz, boolean clearStack)
    {
        NavigateTo(clazz, null, clearStack);
    }

    public static void NavigateTo(Class clazz, Map<String, Object> map)
    {
        NavigateTo(clazz, map, false);
    }

    public static void NavigateTo(Class clazz, Map<String, Object> map, boolean clearStack)
    {
        Intent intent = new Intent(ConfigurationManager.CurrentActivityContext, clazz);
        if(clearStack) {
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        }
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

    public static boolean ShouldProcessFurtherAndShowResponseError(ResponseStatus responseStatus)
    {
        int displayMessage;
        boolean processFurther = false;
        switch (responseStatus)
        {
            case UserNotRegistered:
                displayMessage = R.string.ErrorUserNotExists;
                break;
            case UserAlreadyExist:
                displayMessage = R.string.ErrorUserAlreadyExist;
                break;
            case UserAlreadyRegistered:
                displayMessage = R.string.ErrorUserAlreadyRegistered;
                break;
            case UserAlreadyVerified:
                displayMessage = R.string.ErrorUserAlreadyVerified;
                break;
            case UserSuccessfullyVerified:
                displayMessage = 1;
                processFurther = true;
                break;
            case WrongVerificationCode:
                displayMessage = R.string.ErrorWrongVerificationCode;
                break;
            case SomeErrorOccured:
                displayMessage = R.string.ErrorSomeErrorOccured;
                break;
            case PartialUserAddedSuccess:
                displayMessage = 1;
                break;
            case PartialUserNotAdded:
                displayMessage = -1;
                break;
            case ErrorInTransferringDataInVUTable:
                displayMessage = -1;
                break;
            case UserTableUpdateSuccess:
                displayMessage = 1;
                break;
            case UserTableNotUpdated:
                displayMessage = -1;
                break;
            case UserAddedSuccess:
                displayMessage = R.string.SuccessUserAddedSuccess;
                processFurther = true;
                break;
            case UserAddedFailed:
                displayMessage = R.string.ErrorUserAddedFailed;
                break;
            case UserNotAuthorized:
                displayMessage = R.string.ErrorUserNotAuthorized;
                break;
            case PostAddedSuccess:
                displayMessage = R.string.SuccessPostAddedSuccess;
                processFurther = true;
                break;
            case PostSummaryAdded:
                displayMessage = 1;
                break;
            case PostSummaryNotAdded:
                displayMessage = -1;
                break;
            case UserNotExists:
                displayMessage = R.string.ErrorUserNotExists;
                break;
            case ErrorInUpdationOfVerifyUser:
                displayMessage = -1;
                break;
            case PostSummaryDeletedSuccess:
                displayMessage = 1;
                break;
            case PostSummaryDeletionFailed:
                displayMessage = -1;
                break;
            case PostDeletedSuccess:
                displayMessage = R.string.SuccessPostDeletedSuccess;
                processFurther = true;
                break;
            case PostDeletionFailed:
                displayMessage = R.string.ErrorPostDeletionFailed;
                break;
            case PostUpdationSuccess:
                displayMessage = R.string.SuccessPostUpdationSuccess;
                processFurther = true;
                break;
            case PostUpdationFailed:
                displayMessage = R.string.ErrorPostUpdationFailed;
                break;
            case PostUpdationDenied:
                displayMessage = -1;
                break;
            case CompanyRequestAdded:
                displayMessage = R.string.SuccessCompanyRequestAdded;
                processFurther = true;
                break;
            case CompanyRequestFailed:
                displayMessage = R.string.ErrorCompanyRequestFailed;
                break;
            case EmailIdNotPermitted:
                displayMessage = R.string.ErrorEmailIdNotPermitted;
                break;
            case UserNotPermitted:
                displayMessage = -1;
                break;
            case SubscriptionAdded:
                displayMessage = 1;
                processFurther = true;
                break;
            case SubscriptionFailed:
                displayMessage = R.string.ErrorSubscriptionFailed;
                break;
            case UserUriUpdated:
                displayMessage = 1;
                break;
            case UserUriNotUpdated:
                displayMessage = -1;
                break;
            case UnsubscribeSuccess:
                displayMessage = R.string.SucessUnsubscribeSuccess;
                processFurther = true;
                break;
            case UnSubscribeFailed:
                displayMessage = R.string.ErrorUnSubscribeFailed;
                break;
            case PostSoldOutUpdated:
                displayMessage = -1;
                processFurther = true;
                break;
            case PostSoldOutUpdateFailed:
                displayMessage = R.string.ErrorPostSoldOutUpdateFailed;
                break;
            case PostSummarySoldOutUpdate:
                displayMessage = -1;
                break;
            case PostSummarySoldOutFailed:
                displayMessage = -1;
                break;
            default:
                displayMessage = R.string.ErrorUnknownResponseStatus;
        }

        if(displayMessage!=-1 && displayMessage!=1) {
            Utility.ShowInfoDialog(displayMessage);
        }
            return processFurther;
    }
}
