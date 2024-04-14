package neu.provl.pomodoro.data.pomodoro;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

import lombok.Data;
import lombok.NoArgsConstructor;
import neu.provl.pomodoro.data.Song;

@Data
@NoArgsConstructor
public class PomodoroPeriod {

    public static final int BASE_COIN_PER_PHASE = 100;
    public static final int BASE_EXP_PER_PHASE = 50;

    private PomodoroMethod method = PomodoroMethod.TWENTY_FIVE_OUT_OF_FIVE;
    private Queue<PomodoroPhase> phases = new LinkedList<>();
    private Song backgroundMusic;
    private int studyingHour = 2, studyingMinute = 0;

    private PomodoroPhase currentPhase;

    private int plantCoinExtra, plantExpExtra;

    public PomodoroPhase nextPhase() {
        if(phases.isEmpty()) return null;
        return currentPhase = phases.poll();
    }

    public int getTotalCoin() {
        return phases.stream().map(PomodoroPhase::getCoinToGet).reduce(Integer::sum).get();
    }

    public void initializePhases() {
        phases.clear();

        int totalMinutes = studyingHour * 60 + studyingMinute;

        boolean isStudying = true;
        while(totalMinutes > 0) {
            int time = isStudying ? getMethod().getStudyingTime() : getMethod().getBreakTime();
            time = Math.min(time, totalMinutes);
            PomodoroPhaseType type = isStudying ? PomodoroPhaseType.STUDYING : PomodoroPhaseType.BREAK;

            PomodoroPhase phase = new PomodoroPhase(time, type, "",
                    BASE_COIN_PER_PHASE + plantCoinExtra, BASE_EXP_PER_PHASE + plantExpExtra);
            getPhases().add(phase);

            totalMinutes -= time;
            isStudying = !isStudying;
        }
    }
}
