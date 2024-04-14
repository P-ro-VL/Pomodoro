package neu.provl.pomodoro.components.garden;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.google.android.flexbox.AlignItems;
import com.google.android.flexbox.FlexboxLayout;
import com.google.android.flexbox.JustifyContent;

import neu.provl.pomodoro.R;
import neu.provl.pomodoro.components.FrameButton;

public class CollapsedGardenActionSheet extends GardenActionSheet {

    private Runnable onRequestExpand;

    public CollapsedGardenActionSheet(Context context) {
        super(context);

        setOrientation(LinearLayout.HORIZONTAL);

        FlexboxLayout layout = new FlexboxLayout(context);
        FlexboxLayout.LayoutParams layoutParams = new FlexboxLayout.LayoutParams(
                FlexboxLayout.LayoutParams.MATCH_PARENT,
                FlexboxLayout.LayoutParams.WRAP_CONTENT
        );
        layout.setLayoutParams(layoutParams);
        layout.setJustifyContent(JustifyContent.SPACE_BETWEEN);
        layout.setAlignItems(AlignItems.CENTER);

        TextView title = new TextView(context);
        title.setText(R.string.pomodoro_study);
        title.setTextSize(18);
        title.setTypeface(getResources().getFont(R.font.semi_bold));

        FrameButton showBtn = new FrameButton(context);
        showBtn.setText(getResources().getString(R.string.show).toUpperCase());
        showBtn.setTextSize(10);
        showBtn.setOnClickListener((e) -> onRequestExpand.run());
        showBtn.update();

        layout.addView(title);
        layout.addView(showBtn);

        addView(layout);
    }

    public void setOnRequestExpand(Runnable onRequestExpand) {
        this.onRequestExpand = onRequestExpand;
    }

    @Override
    public GradientDrawable getBackground() {
        GradientDrawable background = new GradientDrawable();
        background.setColor(ContextCompat.getColor(getContext(), R.color.white));
        background.setCornerRadius(20 * getDensity());
        return background;
    }

    @Override
    public void update() {
    }
}
