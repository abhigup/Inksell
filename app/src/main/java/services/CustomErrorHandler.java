package services;

/**
 * Created by Abhinav on 10/05/15.
 */
public class CustomErrorHandler  {
//    private final Context ctx;
//
//    public CustomErrorHandler(Context ctx) {
//        this.ctx = ctx;
//    }
//
//    @Override
//    public Throwable handleError(RetrofitError cause) {
//        String errorDescription;
//
//        if (!Utility.IsNetworkAvailable()) {
//            errorDescription = ctx.getString(R.string.error_network);
//        } else {
//            if (cause.getResponse() == null) {
//                errorDescription = ctx.getString(R.string.error_no_response);
//            } else {
//                    try {
//                        errorDescription = ctx.getString(R.string.error_network_http_error, cause.getResponse().getStatus());
//                    } catch (Exception ex2) {
//                        errorDescription = ctx.getString(R.string.error_unknown);
//                }
//            }
//        }
//
//        Utility.ShowInfoDialog(errorDescription);
//        return new Exception(errorDescription);
//    }
}
