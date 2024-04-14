package neu.provl.pomodoro.components.garden;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.widget.LinearLayout;

public abstract class GardenActionSheet extends LinearLayout {

    private int density;

    public int getDensity() {
        return density;
    }

    public GardenActionSheet(Context context) {
        super(context);

        density = (int) getResources().getDisplayMetrics().density;

        setBackground(getBackground());
    }

    public abstract GradientDrawable getBackground();

    public abstract void update();
}
