package services;

import inksell.inksell.R;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;
import utilities.ResponseStatus;
import utilities.ResponseStatusParser;
import utilities.Utility;

/**
 * Created by Abhinav on 20/04/15.
 */
public abstract class InksellCallback<T> implements Callback<T> {

    public abstract void onSuccess(T t);

    public void onError(ResponseStatus responseStatus) {

    }

    @Override
    public void onResponse(Response<T> response, Retrofit retrofit)
    {
        if(response.isSuccess()) {
            T data = response.body();
            if (data == null) {
                Utility.ShowInfoDialog(R.string.ErrorSomeErrorOccured);
                return;
            }

            if (Utility.isInteger(data.toString()) && Integer.parseInt(data.toString()) < 1000) {
                ResponseStatus responseStatus = ResponseStatus.values()[Integer.parseInt(data.toString())];
                ResponseStatusParser parser = Utility.ShouldProcessFurtherAndSucceededWithResponseStatus(responseStatus);

                if(parser.processFurther) {
                    if (parser.isSuccess) {
                        this.onSuccess(data);
                    }
                    else
                    {
                        this.onError(parser.responseStatus);
                    }
                }
            } else {
                this.onSuccess(data);
            }
        }
        else
        {
            Utility.ShowToast(response.message());
        }
    }

    @Override
    public void onFailure(Throwable error) {
        //ToDo : Remove below after Development
        Utility.ShowInfoDialog(error.getMessage());

        this.onError(ResponseStatus.SomeErrorOccured);
    }
}
