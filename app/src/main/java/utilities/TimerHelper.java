package utilities;

import android.os.Handler;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Abhinav on 12/10/15.
 */
public class TimerHelper {
    final Handler handler = new Handler();
    Timer t = null;
    int delayInMs = 1000;
    int subsequentDelayinMs = 1000;

    public TimerHelper(int delay)
    {
        delayInMs = delay;
    }

    public TimerHelper(int delay, int subsequentDelay)
    {
        delayInMs = delay;
        subsequentDelayinMs  = subsequentDelay;
    }

    public void startTask(TimerTask timerTask){
        if(t!=null) {
            t.cancel();
        }
        t = new Timer();
        t.schedule(timerTask, delayInMs);
    }

    public void startSubsequentTask(TimerTask timerTask){
        if(t==null)
        {
            t = new Timer();
        }
        t.schedule(timerTask, delayInMs, subsequentDelayinMs);
    }

    public void stopTask(){

        if(t!=null){
            t.cancel();
        }

    }
}
