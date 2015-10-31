package utilities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Currency;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.TimeZone;
import java.util.UUID;

import Constants.InksellConstants;
import enums.CategoryType;
import enums.FurnishingType;
import inksell.inksell.R;
import models.AutomobileEntity;
import models.CategoryEntity;
import models.ElectronicEntity;
import models.FurnitureEntity;
import models.OtherEntity;
import models.PostSummaryEntity;
import models.PropertyMapEntity;
import models.RealEstateEntity;

/**
 * Created by Abhinav on 08/04/15.
 */


public class Utility {

    private static Resources getResources = ConfigurationManager.CurrentActivityContext.getResources();

    private static Currency getCurrency()
    {
        if(getResources==null)
            return  null;

        return Currency.getInstance(getResources.getConfiguration().locale);
    }

    public static Locale getLocale()
    {
        if(getResources!=null) {
            return getResources.getConfiguration().locale;
        }
        return null;
    }


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

    public static void ShowDialog(final String message, final DialogInterface.OnClickListener listener){
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(
                new Runnable() {
                    @Override
                    public void run() {
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ConfigurationManager.CurrentActivityContext);
                        alertDialogBuilder.setMessage(message);
                        alertDialogBuilder.setPositiveButton("yes", listener);
                        alertDialogBuilder.setNegativeButton("no", listener);

                        AlertDialog alertDialog = alertDialogBuilder.create();
                        alertDialog.show();
                    }
                }
        );
    }

    public static void ShowToast(String message, int color) {
        Context context = ConfigurationManager.CurrentActivityContext;
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(context, message, duration);
        if(color!=-1) {
            View toastView = toast.getView();
            toastView.setBackgroundResource(color);
        }
        toast.show();
    }

    public static void ShowToast(String string)
    {
        ShowToast(string, -1);
    }

    public static void ShowToast(int stringResource)
    {
        ShowToast(ConfigurationManager.CurrentActivityContext.getString(stringResource), -1);
    }

    public static void ShowToast(int stringResource, int color)
    {
        ShowToast(ConfigurationManager.CurrentActivityContext.getString(stringResource), color);
    }

    public static int GetPixelsFromDp(int dp) {
        Resources r = ConfigurationManager.CurrentActivityContext.getResources();
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dp,
                r.getDisplayMetrics()
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

    public static String getStringValue(String str)
    {
        if(IsStringNullorEmpty(str))
        {
            return "-";
        }
        else
        {
            return str;
        }
    }

    public static boolean IsEditTextNullorEmpty(EditText editText)
    {
        boolean isEmpty = IsStringNullorEmpty(editText.getText().toString());
        if(isEmpty && editText.getHint()!=null)
        {
            ShowToast(editText.getHint() + " is required.");
        }
        return isEmpty;
    }

    public static String GetResourceString(int resId) {
        return ConfigurationManager.CurrentActivityContext.getString(resId);
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

    public static Date GetUTCdatetimeAsDate()
    {
        //note: doesn't check for null
        return StringDateToDate(GetUTCdatetimeAsString());
    }

    public static Date StringDateToDate(String StrDate)
    {
        Date dateToReturn = null;
        SimpleDateFormat dateFormat = new SimpleDateFormat(InksellConstants.DATEFORMAT);

        try
        {
            dateToReturn = (Date)dateFormat.parse(StrDate);
        }
        catch (ParseException e)
        {
            e.printStackTrace();
        }

        return dateToReturn;
    }

    private static String GetUTCdatetimeAsString()
    {
        final SimpleDateFormat sdf = new SimpleDateFormat(InksellConstants.DATEFORMAT);
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        final String utcTime = sdf.format(new Date());

        return utcTime;
    }

    public static String StringDateToRelativeStringDate(String strDate)
    {
        Date dateToReturn = null;
        SimpleDateFormat dateFormat = new SimpleDateFormat(InksellConstants.DATEFORMAT);
        try
        {
            dateToReturn = (Date)dateFormat.parse(strDate);
            return StringDateToRelativeStringDate(dateToReturn);
        }
        catch (ParseException e)
        {
            e.printStackTrace();
        }
        return "";
    }

    public static String StringDateToRelativeStringDate(Date date)
    {
        String dateToReturn = null;

        return getRelativeDateString(date, GetUTCdatetimeAsDate());
    }

    private static String getRelativeDateString(Date startDate, Date endDate){

        //milliseconds
        long different = endDate.getTime() - startDate.getTime();

        int todayDate = Integer.parseInt((String)android.text.format.DateFormat.format("dd",endDate));
        int postDate = Integer.parseInt((String)android.text.format.DateFormat.format("dd",startDate));

        long secondsInMilli = 1000;
        long minutesInMilli = secondsInMilli * 60;
        long hoursInMilli = minutesInMilli * 60;
        long daysInMilli = hoursInMilli * 24;

        long elapsedDays = different / daysInMilli;
        different = different % daysInMilli;

        //More than 2 days
        if(elapsedDays>2)
        {
            return DateToLocalStringDate(startDate);
        }

        //Day before yesterday
        Date yesterdayStart = removeTime((Date)endDate.clone());
        yesterdayStart = addDate(yesterdayStart, Calendar.DATE, -1);
        if(startDate.compareTo(yesterdayStart) < 0)
        {
            return DateToLocalStringDate(startDate);
        }

        //yesterday
        Date todayStart = removeTime((Date)endDate.clone());
        if(startDate.compareTo(todayStart)<0)
        {
            return Utility.GetResourceString(R.string.yesterday);
        }

        //Hours ago
        long elapsedHours = different / hoursInMilli;
        different = different % hoursInMilli;
        if(elapsedHours>1)
        {
            return elapsedHours + " hours ago";
        }

        //minutes ago
        long elapsedMinutes = different / minutesInMilli;
        different = different % minutesInMilli;
        return elapsedMinutes + " min ago";
    }

    private static String DateToLocalStringDate(Date date)
    {
        return android.text.format.DateFormat.getMediumDateFormat(ConfigurationManager.CurrentActivityContext).format(date);

    }

    private static Date removeTime(Date date)
    {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    private static Date addDate(Date date, int field, int value)
    {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(field, value);
        return cal.getTime();
    }

    public static ResponseStatusParser ShouldProcessFurtherAndSucceededWithResponseStatus(ResponseStatus responseStatus)
    {
        int displayMessage = -1;
        boolean isSuccess = false;
        boolean processFurther = false;

        switch (responseStatus)
        {
            case UserNotRegistered:
                displayMessage = R.string.ErrorUserNotExists;
                processFurther = true;
                break;
            case UserAlreadyExist:
                displayMessage = R.string.ErrorUserAlreadyExist;
                processFurther = true;
                break;
            case UserAlreadyRegistered:
                displayMessage = R.string.ErrorUserAlreadyRegistered;
                processFurther = true;
                break;
            case UserAlreadyVerified:
                displayMessage = R.string.ErrorUserAlreadyVerified;
                processFurther = true;
                break;
            case UserSuccessfullyVerified:
                isSuccess = true;
                processFurther = true;
                break;
            case WrongVerificationCode:
                displayMessage = R.string.ErrorWrongVerificationCode;
                processFurther = true;
                break;
            case SomeErrorOccured:
                displayMessage = R.string.ErrorSomeErrorOccured;
                processFurther = true;
                break;
            case PartialUserAddedSuccess:
                isSuccess = true;
                break;
            case PartialUserNotAdded:

                break;
            case ErrorInTransferringDataInVUTable:

                break;
            case UserTableUpdateSuccess:
                isSuccess = true;
                break;
            case UserTableNotUpdated:

                break;
            case UserAddedSuccess:
                displayMessage = R.string.SuccessUserAddedSuccess;
                isSuccess = true;
                processFurther = true;
                break;
            case UserAddedFailed:
                displayMessage = R.string.ErrorUserAddedFailed;
                processFurther = true;
                break;
            case UserNotAuthorized:
                displayMessage = R.string.ErrorUserNotAuthorized;
                processFurther = true;
                break;
            case PostAddedSuccess:
                displayMessage = R.string.SuccessPostAddedSuccess;
                isSuccess = true;
                processFurther = true;
                break;
            case PostSummaryAdded:

                break;
            case PostSummaryNotAdded:

                break;
            case UserNotExists:
                displayMessage = R.string.ErrorUserNotExists;
                processFurther = true;
                break;
            case ErrorInUpdationOfVerifyUser:

                break;
            case PostSummaryDeletedSuccess:
                isSuccess = true;
                break;
            case PostSummaryDeletionFailed:

                break;
            case PostDeletedSuccess:
                displayMessage = R.string.SuccessPostDeletedSuccess;
                isSuccess = true;
                processFurther = true;
                break;
            case PostDeletionFailed:
                displayMessage = R.string.ErrorPostDeletionFailed;
                processFurther = true;
                break;
            case PostUpdationSuccess:
                displayMessage = R.string.SuccessPostUpdationSuccess;
                isSuccess = true;
                processFurther = true;
                break;
            case PostUpdationFailed:
                displayMessage = R.string.ErrorPostUpdationFailed;
                processFurther = true;
                break;
            case PostUpdationDenied:
                processFurther = true;
                break;
            case CompanyRequestAdded:
                displayMessage = R.string.SuccessCompanyRequestAdded;
                processFurther = true;
                isSuccess = true;
                break;
            case CompanyRequestFailed:
                displayMessage = R.string.ErrorCompanyRequestFailed;
                processFurther = true;
                break;
            case EmailIdNotPermitted:
                displayMessage = R.string.ErrorEmailIdNotPermitted;
                processFurther = true;
                break;
            case UserNotPermitted:

                break;
            case SubscriptionAdded:
                isSuccess = true;
                processFurther = true;
                break;
            case SubscriptionFailed:
                displayMessage = R.string.ErrorSubscriptionFailed;
                processFurther = true;
                break;
            case UserUriUpdated:
                isSuccess = true;
                processFurther = true;
                break;
            case UserUriNotUpdated:
                processFurther = true;
                break;
            case UnsubscribeSuccess:
                displayMessage = R.string.SucessUnsubscribeSuccess;
                processFurther = true;
                isSuccess = true;
                break;
            case UnSubscribeFailed:
                displayMessage = R.string.ErrorUnSubscribeFailed;
                processFurther = true;
                break;
            case PostSoldOutUpdated:
                isSuccess = true;
                processFurther = true;
                break;
            case PostSoldOutUpdateFailed:
                processFurther = true;
                displayMessage = R.string.ErrorPostSoldOutUpdateFailed;
                break;
            case PostSummarySoldOutUpdate:

                break;
            case PostSummarySoldOutFailed:

                break;
            default:
                displayMessage = R.string.ErrorUnknownResponseStatus;
        }

        if(displayMessage!=-1) {
            if(isSuccess)
            {
                Utility.ShowToast(displayMessage, R.color.green);
            }
            else {
                Utility.ShowInfoDialog(displayMessage);
            }
        }

        ResponseStatusParser parser = new ResponseStatusParser();
        parser.isSuccess = isSuccess;
        parser.responseStatus = responseStatus;
        parser.processFurther = processFurther;

            return parser;
    }


    public static void setUserPic(ImageView imageView, String UserImageUrl, String PostedBy)
    {
        if(Utility.IsStringNullorEmpty(UserImageUrl))
        {
            ColorGenerator generator = ColorGenerator.MATERIAL;
            int color2 = generator.getColor(PostedBy);

            TextDrawable.IBuilder builder = TextDrawable.builder()
                    .beginConfig()
                    .width(50)  // width in px
                    .height(50) // height in px
                    .endConfig()
                    .rect();

            TextDrawable userImage = builder.build(PostedBy.trim().substring(0,1), color2);

            imageView.setImageDrawable(userImage);
        }
        else {
            Picasso.with(ConfigurationManager.CurrentActivityContext)
                    .load(UserImageUrl)
                    .placeholder(R.drawable.ic_person)
                    .into(imageView);
        }
    }

    public static String GetLocalCurrencySymbol()
    {
        return getCurrency().getSymbol();
    }

    public static void WaitFor(int milliSeconds)
    {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                // Actions to do after 10 seconds
            }
        }, milliSeconds);
    }


    public static void setCallAndEmailButton(final String postTitle, ImageButton btnCall, ImageButton btnEmail, final String contactNumber, final String contactEmail)
    {
        int callBgColor;
        int emailBgColor;
        if(Utility.IsStringNullorEmpty(contactNumber))
        {
            callBgColor = R.color.black_semi_transparent;
        }
        else
        {
            callBgColor = R.color.TitlePrimary;
            btnCall.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_CALL);
                    intent.setData(Uri.parse("tel:" + contactNumber));
                    if (intent.resolveActivity(ConfigurationManager.CurrentActivityContext.getPackageManager()) != null) {
                        ConfigurationManager.CurrentActivityContext.startActivity(intent);
                    }
                }
            });
        }
        if(Utility.IsStringNullorEmpty(contactEmail))
        {
            emailBgColor = R.color.black_semi_transparent;
        }
        else
        {
            emailBgColor = R.color.TitlePrimary;
            btnEmail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_SENDTO);
                    intent.setData(Uri.parse("mailto:")); // only email apps should handle this
                    intent.putExtra(Intent.EXTRA_EMAIL, new String[] { contactEmail });
                    intent.putExtra(Intent.EXTRA_SUBJECT, postTitle);
                    if (intent.resolveActivity(ConfigurationManager.CurrentActivityContext.getPackageManager()) != null) {
                        ConfigurationManager.CurrentActivityContext.startActivity(intent);
                    }
                }
            });
        }
        btnCall.setBackgroundColor(btnCall.getContext().getResources().getColor(callBgColor));
        btnEmail.setBackgroundColor(btnEmail.getContext().getResources().getColor(emailBgColor));
    }

    public static List<CategoryEntity> getCategoryList()
    {
        List<CategoryEntity> categoryEntities = new ArrayList<>();
        categoryEntities.add(new CategoryEntity(CategoryType.AllCategory, Utility.GetResourceString(R.string.category_all)));
        categoryEntities.add(new CategoryEntity(CategoryType.Electronics, Utility.GetResourceString(R.string.category_electronics)));
        categoryEntities.add(new CategoryEntity(CategoryType.Automobile, Utility.GetResourceString(R.string.category_autos)));
        categoryEntities.add(new CategoryEntity(CategoryType.Furniture, Utility.GetResourceString(R.string.category_furniture)));
        categoryEntities.add(new CategoryEntity(CategoryType.RealState, Utility.GetResourceString(R.string.category_realestate)));
        categoryEntities.add(new CategoryEntity(CategoryType.Others, Utility.GetResourceString(R.string.category_other)));

        return categoryEntities;
    }

    public static Bitmap getResizedBitmap(Bitmap bm, int newHeight, int newWidth, boolean maintainAspectRatio) {

        int width = bm.getWidth();
        int height = bm.getHeight();

        double aspectRatio = (double) width/height;

        if(maintainAspectRatio)
        {
            if(newWidth==0)
            {
                newWidth = (int) (newHeight*aspectRatio);
            }
            if(newHeight==0)
            {
                newHeight = (int) (newWidth/aspectRatio);
            }
        }

        float scaleWidth = ((float) newWidth) / width;

        float scaleHeight = ((float) newHeight) / height;

        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);

        Bitmap resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, false);

        return resizedBitmap;

    }

    public static Uri getOutputMediaFileUri() {
        return Uri.fromFile(getOutputMediaFile());
    }

    private static File getOutputMediaFile() {

        // External sdcard location
        File mediaStorageDir = new File(
                Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                InksellConstants.IMAGE_DIR_NAME);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d("", "Oops! Failed create "
                        + InksellConstants.IMAGE_DIR_NAME + " directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                Locale.getDefault()).format(new Date());
        File mediaFile;
        mediaFile = new File(mediaStorageDir.getPath() + File.separator
                    + "IMG_" + timeStamp + ".jpg");

        return mediaFile;
    }

    public static long getTicks()
    {
        long TICKS_AT_EPOCH = 621355968000000000L;
        return System.currentTimeMillis()*10000 + TICKS_AT_EPOCH;
    }

    public static Class getClassFromCategory(CategoryType categoryType)
    {
        switch (categoryType)
        {
            case Electronics:
                return ElectronicEntity.class;
            case Automobile:
                return AutomobileEntity.class;
            case Furniture:
                return FurnitureEntity.class;
            case RealState:
                return RealEstateEntity.class;
            case Others:
                return OtherEntity.class;
            default:
                return null;
        }
    }

    public static String getFurnishedString(FurnishingType type)
    {
        switch (type)
        {
            case UnFurnished:
                return ConfigurationManager.CurrentActivityContext.getString(R.string.unfurnished);
            case SemiFurnished:
                return ConfigurationManager.CurrentActivityContext.getString(R.string.semiFurnished);
            case FullyFurnished:
                return ConfigurationManager.CurrentActivityContext.getString(R.string.fullyFurnished);
        }
        return "";
    }

    public static void setMap(Bundle bundle,MapView mapView, OnMapReadyCallback onMapReadyCallback)
    {
        mapView.onCreate(bundle);
        mapView.onResume();
        MapsInitializer.initialize(ConfigurationManager.CurrentActivityContext);

        mapView.getMapAsync(onMapReadyCallback);
    }

    public static PostSummaryEntity propertyMapToPostSummaryEntity(PropertyMapEntity propertyMapEntity)
    {
        if(propertyMapEntity==null)
            return null;

        PostSummaryEntity postSummaryEntity = new PostSummaryEntity();
        postSummaryEntity.Title = propertyMapEntity.PostTitle;
        postSummaryEntity.PostId = propertyMapEntity.PostId;
        postSummaryEntity.categoryid = CategoryType.RealState.ordinal();

        return postSummaryEntity;
    }

    public static boolean checkPlayServices(Activity activity) {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(activity);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(activity, resultCode, InksellConstants.PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else {
                // Log.i(TAG, "This device is not supported.");
                Utility.ShowToast(R.string.DeviceNotSupported);
            }
            return false;
        }
        return true;
    }


}
