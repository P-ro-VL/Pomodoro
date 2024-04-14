package neu.provl.pomodoro.components.navigation;

import androidx.fragment.app.Fragment;

import neu.provl.pomodoro.R;
import neu.provl.pomodoro.fragment.AcademicRecordFragment;
import neu.provl.pomodoro.fragment.GardenFragment;
import neu.provl.pomodoro.fragment.HomeFragment;
import neu.provl.pomodoro.fragment.ShopFragment;
import neu.provl.pomodoro.fragment.StatisticsFragment;

public enum NavigationPage {
    HOME(R.drawable.home, R.drawable.home_selected, false, HomeFragment.class),
    ACADEMIC_RECORD(R.drawable.document, R.drawable.document_selected,false, AcademicRecordFragment.class),
    GARDEN(R.drawable.garden, R.drawable.garden_selected,true, GardenFragment.class),
    STATISTICS(R.drawable.statistics, R.drawable.statistics_selected,false, StatisticsFragment.class),
    SHOP(R.drawable.shop, R.drawable.shop_selected,false, ShopFragment.class);


    NavigationPage(int defaultIconId, int selectedIconId, boolean transparentAppBar, Class<? extends Fragment> fragmentWrapper) {
        this.defaultIconId = defaultIconId;
        this.selectedIconId = selectedIconId;
        this.fragmentWrapper = fragmentWrapper;
        this.transparentAppBar = transparentAppBar;
    }

    private int defaultIconId;
    private int selectedIconId;
    private boolean transparentAppBar;
    private final Class<? extends Fragment> fragmentWrapper;

    public int getDefaultIconId() {
        return defaultIconId;
    }

    public int getSelectedIconId() {
        return selectedIconId;
    }

    public Class<? extends Fragment> getFragmentWrapper() {
        return fragmentWrapper;
    }

    public boolean isTransparentAppBar() {
        return transparentAppBar;
    }
}
