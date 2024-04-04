package neu.provl.pomodoro.components.navigation;

import neu.provl.pomodoro.R;

public enum NavigationPage {
    HOME(R.drawable.home, R.drawable.home_selected),
    ACADEMIC_RECORD(R.drawable.document, R.drawable.document_selected),
    GARDEN(R.drawable.garden, R.drawable.garden_selected),
    STATISTICS(R.drawable.statistics, R.drawable.statistics_selected),
    SHOP(R.drawable.shop, R.drawable.shop_selected);

    NavigationPage(int defaultIconId, int selectedIconId) {
        this.defaultIconId = defaultIconId;
        this.selectedIconId = selectedIconId;
    }

    private int defaultIconId;
    private int selectedIconId;

    public int getDefaultIconId() {
        return defaultIconId;
    }

    public int getSelectedIconId() {
        return selectedIconId;
    }
}
