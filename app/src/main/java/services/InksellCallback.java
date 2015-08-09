package services;

import inksell.inksell.R;
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
        if(t==null)
        {
            Utility.ShowInfoDialog(R.string.ErrorSomeErrorOccured);
            return;
        }

        if(Utility.isInteger(t.toString()))
        {
            ResponseStatus responseStatus = ResponseStatus.values()[Integer.parseInt(t.toString())];
            if(Utility.ShouldProcessFurtherAndShowResponseError(responseStatus))
            {
                this.onSuccess(t, response);
            }
        }
        else {
            this.onSuccess(t, response);
        }
    }

    @Override
    public void failure(RetrofitError error) {
        //ToDo : Remove below after Development
        Utility.ShowInfoDialog(error.getMessage().toString());

        this.onFailure(error);
    }
}
