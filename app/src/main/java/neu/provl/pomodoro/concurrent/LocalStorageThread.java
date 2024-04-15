package neu.provl.pomodoro.concurrent;

import android.util.Log;

import com.google.gson.JsonObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Duration;
import java.util.Map;

import neu.provl.pomodoro.SplashScreen;
import neu.provl.pomodoro.data.AcademicRecord;
import neu.provl.pomodoro.data.User;
import neu.provl.pomodoro.data.controller.AuthenticationDriver;
import neu.provl.pomodoro.data.pomodoro.PomodoroMethod;
import neu.provl.pomodoro.data.pomodoro.PomodoroPeriod;
import neu.provl.pomodoro.fragment.GardenFragment;

public class LocalStorageThread extends PeriodicThread {

    public static final int LOCAL_SAVING_INTERVAL = 5; // in seconds
    public static File CONFIG_FILE = new File("config.json");
    public static File STATISTIC_FILE = new File("statistics.json");
    public static File USER_DATA_FILE = new File("user_data.json");

    public static void init() throws IOException {
        String folder = SplashScreen.getInstance().getApplicationContext().getFilesDir().getPath();

        System.out.println(folder);

        CONFIG_FILE = new File(folder + "/config.json");
        STATISTIC_FILE = new File(folder + "/statistics.json");
        USER_DATA_FILE = new File(folder + "/user_data.json");

        if(!CONFIG_FILE.exists()) {
            CONFIG_FILE.createNewFile();
        }

        if(!STATISTIC_FILE.exists()) {
            STATISTIC_FILE.createNewFile();
        }

        if(!USER_DATA_FILE.exists()) {
            USER_DATA_FILE.createNewFile();
        }
    }

    public static void clear() {
        CONFIG_FILE.delete();
        STATISTIC_FILE.delete();
        USER_DATA_FILE.delete();
    }

    public LocalStorageThread() {
        super(null, Duration.ofSeconds(LOCAL_SAVING_INTERVAL));

        setRunnable((thread) -> {
            try {
                saveConfig();
                saveUserData();
                saveStatistics();
            }catch (Exception ex) {
                Log.d("LOCAL STORAGE THREAD", "An unexpected error has occurred");
                ex.printStackTrace();
                thread.setRunning(false);
            }
        });
    }

    private void saveConfig() {
        JsonObject object = new JsonObject();

        if(AuthenticationDriver.currentUser != null) {
            User user = AuthenticationDriver.currentUser;

            JsonObject lastLoginObject = new JsonObject();
            lastLoginObject.addProperty("username", user.getUsername());
            lastLoginObject.addProperty("password", user.getEncryptedPassword());

            object.add("last-login", lastLoginObject);
        }

        try {
            FileWriter fileWriter = new FileWriter(CONFIG_FILE);
            fileWriter.write(object.toString());
            fileWriter.flush();
            fileWriter.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void saveUserData() {
        if(AuthenticationDriver.currentUser == null) return;

        User user = AuthenticationDriver.currentUser;

        JsonObject userData = new JsonObject();
        /*
        ACADEMIC RECORDS
         */
        JsonObject academicRecordObject = new JsonObject();

        for(AcademicRecord academicRecord : user.getAcademicRecords()) {
            JsonObject record = new JsonObject();
            record.addProperty("subject-name", academicRecord.getSubject());
            record.addProperty("term", academicRecord.getTerm());
            record.addProperty("10%", academicRecord.getScores()[0]);
            record.addProperty("40%", academicRecord.getScores()[1]);
            record.addProperty("50%", academicRecord.getScores()[2]);

            academicRecordObject.add(academicRecord.getSubject(), record);
        }

        userData.add("academic-records", academicRecordObject);

        /*
        STUDY GARDEN
         */
        JsonObject studyGardenObject = new JsonObject();
        PomodoroPeriod period = GardenFragment.LAST_PERIOD_DATA;
        if(period != null) {
            studyGardenObject.addProperty("selected-plant", period.getPlantId());
            studyGardenObject.addProperty("selected-method", period.getMethod().toString());
            studyGardenObject.addProperty("studying-hours", period.getStudyingHour());
            studyGardenObject.addProperty("studying-minutes", period.getStudyingMinute());
            studyGardenObject.addProperty("coin-extra", period.getPlantCoinExtra());
            studyGardenObject.addProperty("exp-extra", period.getPlantExpExtra());
            studyGardenObject.addProperty("studying-minutes", period.getStudyingMinute());
            studyGardenObject.addProperty("bgm-id", period.getBackgroundMusic().getId());

            userData.add("last-period-data", studyGardenObject);
        }

        try {
            FileWriter fileWriter = new FileWriter(USER_DATA_FILE);
            fileWriter.write(userData.toString());
            fileWriter.flush();
            fileWriter.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void saveStatistics() {
        if(AuthenticationDriver.currentUser == null) return;
        User user = AuthenticationDriver.currentUser;

        JsonObject statisticObject = new JsonObject();

        /*
        METHOD COUNTER
         */
        JsonObject methodCounter = new JsonObject();
        Map<PomodoroMethod, Integer> counter = user.getMethodCounter();
        for(PomodoroMethod method : PomodoroMethod.values()) {
            methodCounter.addProperty(method.toString(), counter.getOrDefault(method, 0));
        }
        statisticObject.add("counter", methodCounter);

        /*
        STUDY TIME
         */
        JsonObject studyTime = new JsonObject();
        for(Map.Entry<String, Integer> entry : user.getStudyingSeconds().entrySet()) {
            studyTime.addProperty(entry.getKey(), entry.getValue());
        }
        statisticObject.add("studying-time", studyTime);

        /*
        COIN RECEIVED
         */
        JsonObject coinReceived = new JsonObject();
        for(Map.Entry<String, Integer> entry : user.getAccumulatedCoins().entrySet()) {
            coinReceived.addProperty(entry.getKey(), entry.getValue());
        }
        statisticObject.add("coin-received", coinReceived);

        try {
            FileWriter fileWriter = new FileWriter(STATISTIC_FILE);
            fileWriter.write(statisticObject.toString());
            fileWriter.flush();
            fileWriter.close();
        }catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
