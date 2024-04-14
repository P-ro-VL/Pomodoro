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

@Data
@RequiredArgsConstructor
public class User {

    @Setter(value = AccessLevel.NONE)
    private final String id;
    private final String name;
    private final String imageUrl;
    private boolean isStudying = false;

    private int coin = 0;

    private List<User> friends = new ArrayList<>();
    private List<User> friendRequests = new ArrayList<>();

    private List<Plant> plants = new ArrayList<>();

    private List<AcademicRecord> academicRecords = new ArrayList<>();

    private Map<String, Integer> studyingSeconds = new HashMap<>();

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
}
