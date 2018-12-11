package Server;

import java.util.Date;
import java.util.TimerTask;

import static Common.Commons.TIMEOUT_PERIOD;

public class TimeOutTask extends TimerTask {
    private RequestHandler requestHandler;

    TimeOutTask(RequestHandler requestHandler) {
        this.requestHandler = requestHandler;
    }

    @Override
    public void run() {
        Date currentTime = new Date();
        if ((currentTime.getTime() - requestHandler.getLastActingDate().getTime()) > TIMEOUT_PERIOD){
            System.out.println("Timer work");
            requestHandler.TimeoutLogout();
        }
    }
}