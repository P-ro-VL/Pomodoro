package neu.provl.pomodoro.data.pomodoro;

public enum PomodoroMethod {
    TWENTY_FIVE_OUT_OF_FIVE("25/5", 25, 5),
    FIFTY_OUT_OF_TEN("50/10", 50, 10);

    PomodoroMethod(String displayName, int studyingTime, int breakTime) {
        this.displayName = displayName;
        this.studyingTime = studyingTime;
        this.breakTime = breakTime;
    }

    private String displayName;
    private int studyingTime, breakTime;

    public String getDisplayName() {
        return displayName;
    }

    public int getStudyingTime() {
        return studyingTime;
    }

    public int getBreakTime() {
        return breakTime;
    }
}
