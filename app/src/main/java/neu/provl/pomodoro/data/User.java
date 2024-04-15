package neu.provl.pomodoro.data;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import neu.provl.pomodoro.data.pomodoro.PomodoroMethod;

@Data
@RequiredArgsConstructor
public class User {

    private String username = "";
    private String encryptedPassword = "";

    @Setter(value = AccessLevel.NONE)
    private final String id;
    private final String name;
    private final String imageUrl;
    private boolean isStudying = false;

    private int coin = 0;
    private int xp = 0;
    private int level = 1;

    private List<User> friends = new ArrayList<>();
    private List<User> friendRequests = new ArrayList<>();

    private List<Plant> plants = new ArrayList<>();

    private List<AcademicRecord> academicRecords = new ArrayList<>();

    /*
    LOCAL STORAGE
     */
    private Map<String, Integer> studyingSeconds = new HashMap<>();
    private Map<String, Integer> accumulatedCoins = new HashMap<>();
    private Map<PomodoroMethod, Integer> methodCounter = new HashMap<>();

    public void addCoin(int amount) {
        this.coin += amount;
    }

    public void accumulateStudyingHour(int seconds) {
        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        String key = dateFormat.format(date);

        if(studyingSeconds.containsKey(key)) {
            studyingSeconds.put(key, studyingSeconds.get(key) + seconds);
        } else {
            studyingSeconds.put(key, seconds);
        }
    }

    public void accumulateCoins(int coin) {
        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        String key = dateFormat.format(date);

        if(accumulatedCoins.containsKey(key)) {
            accumulatedCoins.put(key, accumulatedCoins.get(key) + coin);
        } else {
            accumulatedCoins.put(key, coin);
        }
    }

    public void increaseMethod(PomodoroMethod method) {
        if(methodCounter.containsKey(method)) {
            methodCounter.put(method, methodCounter.get(method) + 1);
        } else {
            methodCounter.put(method, 1);
        }
    }

    public void addExperience(int xp) {
        if(getXp() + xp > ((int)1000*(getLevel()/2.0))) {
            setLevel(getLevel() + 1);
            int remainXp = (getXp() + xp) - 1000;
            addCoin(remainXp);
        } else  {
            setXp(getXp() + xp);
        }
    }
}
