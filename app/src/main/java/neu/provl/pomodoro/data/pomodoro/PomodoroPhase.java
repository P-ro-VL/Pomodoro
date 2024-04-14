package neu.provl.pomodoro.data.pomodoro;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Data
@Builder
public class PomodoroPhase {

    private int minutes;
    private PomodoroPhaseType type;
    private String quote;

    private int coinToGet;
    private int expToGet;

}
