package neu.provl.pomodoro.data;

import neu.provl.pomodoro.R;

public enum PlantType {
    PLANT(R.string.type_plant),
    FLOWER(R.string.type_flower);

    private int displayNameId;

    PlantType(int displayNameId) {
        this.displayNameId = displayNameId;
    }

    public int getDisplayNameId() {
        return displayNameId;
    }
}
