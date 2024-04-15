package neu.provl.pomodoro.concurrent;

import android.util.Log;

import java.time.Duration;
import java.util.function.Consumer;

import lombok.Getter;
import lombok.Setter;

public class PeriodicThread extends Thread {

    @Getter
    @Setter
    private boolean running;
    private Duration interval;

    @Getter
    @Setter
    private Consumer<PeriodicThread> runnable;

    public PeriodicThread(Consumer<PeriodicThread> runnable, Duration interval) {
        this.runnable = runnable;
        this.interval = interval;
        this.running = true;
    }

    @Override
    public void run() {
        try {
            while(running) {
                runnable.accept(this);

                try {
                    Thread.sleep(interval.toMillis());
                } catch (Exception _) {
                }
            }
        }catch (Exception ex) {
            Log.e("PeriodicThread", "An error has occured while trying to run PeriodicThread");
            Log.e("PeriodicThread", Log.getStackTraceString(ex));
            running = false;
        }
    }
}
