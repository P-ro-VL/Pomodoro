package neu.provl.pomodoro.util;

public class StringUtils {

    public static String toMMssFormat(int seconds) {
        int minutes = seconds / 60;
        int remainingSeconds = seconds - minutes * 60;

        return String.format("%02d", minutes) + ":" + String.format("%02d", remainingSeconds);
    }
}
