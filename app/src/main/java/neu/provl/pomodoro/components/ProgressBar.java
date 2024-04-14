package neu.provl.pomodoro.components;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.text.Layout;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import lombok.Getter;
import lombok.Setter;
import neu.provl.pomodoro.MainActivity;
import neu.provl.pomodoro.R;

public class ProgressBar extends FrameLayout {

    /**
     * The progress value of current bar. Must be between 0.0 and 1.0.
     */
    @Getter
    private double progress = 0.5;

    @Getter
    @Setter
    private int progressColorId = R.color.white;

    private int progressComponentId;

    public ProgressBar(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        update();
    }

    public ProgressBar(@NonNull Context context) {
        super(context);
    }

    public void setProgress(double progress) {
        this.progress = progress;

        FrameLayout progressLayout = findViewById(progressComponentId);
        progressLayout.getLayoutParams().width = (int) (getMeasuredWidth() * progress);
        progressLayout.setLayoutParams(progressLayout.getLayoutParams());
        progressLayout.invalidate();
    }

    public void update() {
        removeAllViews();

        Context context = getContext();

        float density = context.getResources().getDisplayMetrics().density;

        GradientDrawable layoutBackground = new GradientDrawable();
        layoutBackground.setColor(context.getColor(R.color.light_gray));
        layoutBackground.setCornerRadius(32);

        GradientDrawable foregroundBackground = new GradientDrawable();
        foregroundBackground.setColor(context.getColor(getProgressColorId()));
        foregroundBackground.setCornerRadius(32);

        FrameLayout layout = new FrameLayout(context);
        layout.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, (int) (10 * density)));
        layout.setBackground(layoutBackground);

        addView(layout);

        FrameLayout progressLayout = new FrameLayout(context);
        progressLayout.setId(progressComponentId = generateViewId());

        addView(progressLayout);

        post(() -> {
            LayoutParams progressLayoutParams = new LayoutParams((int) (LayoutParams.MATCH_PARENT), (int) (10 * density));
            progressLayoutParams.width = (int) (getMeasuredWidth() * progress);
            progressLayout.setLayoutParams(progressLayoutParams);

            progressLayout.setBackground(foregroundBackground);
        });
    }


}
