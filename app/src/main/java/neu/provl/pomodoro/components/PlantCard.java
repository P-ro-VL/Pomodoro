package neu.provl.pomodoro.components;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import neu.provl.pomodoro.R;

public class PlantCard extends FrameLayout {
    public PlantCard(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        int density = (int) context.getResources().getDisplayMetrics().density;

        FrameLayout layout = new FrameLayout(context);
        FrameLayout.LayoutParams layoutParams = new LayoutParams(90 * density, 115 * density);
        layoutParams.gravity = Gravity.BOTTOM;
        layout.setLayoutParams(layoutParams);

        GradientDrawable background = new GradientDrawable();
        background.setColor(ContextCompat.getColor(context, R.color.primary));
        background.setCornerRadius(12 * density);

        layout.setBackground(background);



    }
}
