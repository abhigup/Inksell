package services;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import utilities.ResponseStatus;
import utilities.Utility;

/**
 * Created by Abhinav on 20/04/15.
 */
public abstract class InksellCallback<T> implements Callback<T> {

    public abstract void onSuccess(T t, Response response);

    public void onFailure(RetrofitError error) {

    }

    @Override
    public void success(T t, Response response)
    {
        if(Utility.isInteger(t.toString()))
        {
            ResponseStatus responseStatus = ResponseStatus.values()[Integer.parseInt(t.toString())];
            Utility.ShowInfoDialog(Utility.GetResponseError(responseStatus));
        }
        else {
            this.onSuccess(t, response);
        }
    }

    @Override
    public void failure(RetrofitError error) {
        Utility.ShowInfoDialog(error.getMessage().toString());
        this.onFailure(error);
    }
}
