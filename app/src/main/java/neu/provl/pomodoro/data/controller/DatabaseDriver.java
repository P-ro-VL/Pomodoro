package neu.provl.pomodoro.data.controller;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.common.base.Charsets;
import com.google.common.io.Files;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;
import neu.provl.pomodoro.MainActivity;
import neu.provl.pomodoro.R;
import neu.provl.pomodoro.SplashScreen;
import neu.provl.pomodoro.concurrent.LocalStorageThread;
import neu.provl.pomodoro.data.AcademicRecord;
import neu.provl.pomodoro.data.Plant;
import neu.provl.pomodoro.data.PlantType;
import neu.provl.pomodoro.data.Song;
import neu.provl.pomodoro.data.Subject;
import neu.provl.pomodoro.data.pomodoro.PomodoroMethod;
import neu.provl.pomodoro.data.pomodoro.PomodoroPeriod;
import neu.provl.pomodoro.fragment.GardenFragment;

public class DatabaseDriver {

    @Getter
    public static DatabaseDriver instance;

    public static void init() {
        SplashScreen.setLoadingState(R.string.splash_screen_connect_database);

        new DatabaseDriver();

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("studying-users");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<String> studyingUsers = snapshot.getValue(List.class);
                instance.setStudyingUsers(studyingUsers);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private FirebaseFirestore databaseInstance;

    @Getter
    private List<Plant> availablePlants = new ArrayList<>();
    @Getter
    private List<Song> availableSongs = new ArrayList<>();
    @Getter
    @Setter
    private List<String> studyingUsers = new ArrayList<>();

    public DatabaseDriver() {
        if(instance != null) {
            throw new RuntimeException("The database driver cannot be initialized twice.");
        }

        instance = this;

        databaseInstance = FirebaseFirestore.getInstance();
        fetchAllPlants();
        fetchAllSongs();
    }

    public void fetchAllSongs() {
        SplashScreen.setLoadingState(R.string.splash_screen_fetching_song);
        databaseInstance.collection("songs")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Map<String, Object> data = document.getData();

                                Song song = Song.builder()
                                        .id((String) data.get("id"))
                                        .name((String) data.get("name"))
                                        .author((String) data.get("author"))
                                        .thumbnailUrl((String) data.get("thumbnail"))
                                        .build();
                                availableSongs.add(song);
                            }
                        } else {
                            Snackbar.make(
                                            MainActivity.getInstance().getRoot(),
                                            R.string.song_fetch_error,
                                            Snackbar.LENGTH_SHORT
                                    )
                                    .setText(ContextCompat.getString(
                                            MainActivity.getInstance().getApplicationContext(),
                                            R.string.song_fetch_error
                                    ).replace("%error%", task.getException().getMessage()))
                                    .setBackgroundTint(
                                            ContextCompat.getColor(
                                                    MainActivity.getInstance().getApplicationContext(),
                                                    R.color.destructive)
                                    ).show();
                        }
                    }
                });
    }

    public void fetchAllPlants() {
        SplashScreen.setLoadingState(R.string.splash_screen_fetching_plant);
        databaseInstance.collection("plants")
            .get()
            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Map<String, Object> data = document.getData();

                            Plant plant = Plant.builder()
                                    .id((String) data.get("id"))
                                    .name((String) data.get("name"))
                                    .type(PlantType.valueOf(((String) data.get("type")).toUpperCase()))
                                    .description((String) data.get("description"))
                                    .price(((Long) data.get("price")).intValue())
                                    .coinPerPhase(((Long) data.get("coinPerPhase")).intValue())
                                    .xpPerPhase(((Long) data.get("xpPerPhase")).intValue())
                                    .imageUrl("https://raw.githubusercontent.com/P-ro-VL/Pomodoro/main/imgs/" + data.get("id") + ".png")
                                    .build();
                            availablePlants.add(plant);
                        }
                    } else {
                        Snackbar.make(
                            MainActivity.getInstance().getRoot(),
                            R.string.plant_fetch_error,
                            Snackbar.LENGTH_SHORT
                        )
                        .setText(ContextCompat.getString(
                                MainActivity.getInstance().getApplicationContext(),
                                R.string.plant_fetch_error
                        ).replace("%error%", task.getException().getMessage()))
                        .setBackgroundTint(
                            ContextCompat.getColor(
                                    MainActivity.getInstance().getApplicationContext(),
                                    R.color.destructive)
                        ).show();
                    }
                }
            });
    }

    public void loadConfig() {
        try {
            String str = Files.asCharSource(LocalStorageThread.CONFIG_FILE, Charsets.UTF_8).read();
            if(str.isEmpty()) return;
            JsonObject jsonObject = JsonParser.parseString(str).getAsJsonObject();

            if(jsonObject.has("last-login")) {
                JsonObject lastLoginObj = jsonObject.getAsJsonObject("last-login");
                AuthenticationDriver.LAST_LOGIN_USERNAME = lastLoginObj.get("username").getAsString();
                AuthenticationDriver.LAST_LOGIN_PASSWORD = lastLoginObj.get("password").getAsString();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void loadUserData() {
        try {
            String str = Files.asCharSource(LocalStorageThread.USER_DATA_FILE, Charsets.UTF_8).read();
            if(str.isEmpty()) return;
            JsonObject jsonObject = JsonParser.parseString(str).getAsJsonObject();

            if(jsonObject.has("academic-records")) {
                JsonObject recordObj = jsonObject.getAsJsonObject("academic-records");

                for(Map.Entry<String, JsonElement> obj : recordObj.asMap().entrySet()) {
                    JsonObject e = obj.getValue().getAsJsonObject();

                    String name = e.get("subject-name").getAsString();
                    String term = e.get("term").getAsString();
                    double t = Double.parseDouble(e.get("10%").getAsString());
                    double fo = Double.parseDouble(e.get("40%").getAsString());
                    double fi = Double.parseDouble(e.get("50%").getAsString());

                    AcademicRecord academicRecord = new AcademicRecord(
                      name,
                      term,
                      new double[] {t, fo, fi}
                    );

                    AuthenticationDriver.currentUser.getAcademicRecords().add(academicRecord);
                }
            }

            if(jsonObject.has("last-period-data"))  {
                JsonObject lastPeriodData = jsonObject.get("last-period-data").getAsJsonObject();

                String plantId = lastPeriodData.get("selected-plant").getAsString();
                PomodoroMethod method = PomodoroMethod.valueOf(
                        lastPeriodData.get("selected-method").getAsString().toUpperCase()
                );
                int studyingHour = Integer.parseInt(lastPeriodData.get("studying-hours").getAsString());
                int studyingMinute = Integer.parseInt(lastPeriodData.get("studying-minutes").getAsString());
                String bgmId = lastPeriodData.get("bgm-id").getAsString();
                int coinExtra = Integer.parseInt(lastPeriodData.get("coin-extra").getAsString());
                int expExtra = Integer.parseInt(lastPeriodData.get("exp-extra").getAsString());

                PomodoroPeriod period = new PomodoroPeriod();
                period.setPlantId(plantId);
                period.setMethod(method);
                period.setStudyingHour(studyingHour);
                period.setStudyingMinute(studyingMinute);
                period.setPlantCoinExtra(coinExtra);
                period.setPlantExpExtra(expExtra);
                period.setBackgroundMusic(getAvailableSongs().stream().filter(
                        song -> song.getId().equalsIgnoreCase(bgmId)
                ).findFirst().get());

                GardenFragment.LAST_PERIOD_DATA = period;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void loadStatistics() {
        try {
            String str = Files.asCharSource(LocalStorageThread.STATISTIC_FILE, Charsets.UTF_8).read();
            if(str.isEmpty()) return;
            JsonObject jsonObject = JsonParser.parseString(str).getAsJsonObject();

            Map<PomodoroMethod, Integer> counterMap = new HashMap<>();
            JsonObject counter = jsonObject.get("counter").getAsJsonObject();
            for(Map.Entry<String, JsonElement> entry : counter.asMap().entrySet()) {
                counterMap.put(PomodoroMethod.valueOf(entry.getKey()),
                        Integer.parseInt(entry.getValue().getAsString()));
            }

            AuthenticationDriver.currentUser.setMethodCounter(counterMap);

            Map<String, Integer> studyingTimeMap = new HashMap<>();
            JsonObject studyingTime = jsonObject.get("studying-time").getAsJsonObject();
            for(Map.Entry<String, JsonElement> entry : studyingTime.asMap().entrySet()) {
                studyingTimeMap.put(
                        entry.getKey(),
                        Integer.parseInt(entry.getValue().getAsString())
                );
            }
            AuthenticationDriver.currentUser.setStudyingSeconds(studyingTimeMap);

            Map<String, Integer> coinMap = new HashMap<>();
            JsonObject coin = jsonObject.get("coin-received").getAsJsonObject();
            for(Map.Entry<String, JsonElement> entry : coin.asMap().entrySet()) {
                coinMap.put(
                        entry.getKey(),
                        Integer.parseInt(entry.getValue().getAsString())
                );
            }
            AuthenticationDriver.currentUser.setAccumulatedCoins(coinMap);

        }catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
