package neu.provl.pomodoro.components.navigation;

import android.content.Context;
import android.graphics.Canvas;
import android.media.Image;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.google.android.flexbox.AlignItems;
import com.google.android.flexbox.FlexboxLayout;
import com.google.android.flexbox.JustifyContent;

import neu.provl.pomodoro.R;

public class NavigationBar extends FrameLayout {

    public static NavigationPage currentPage = NavigationPage.HOME;

    public NavigationBar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        update();
    }

    void update() {
        removeAllViews();
        float density = getContext().getResources().getDisplayMetrics().density;

        FlexboxLayout row = new FlexboxLayout(getContext());
        row.setPadding((int)(18*density), 0, (int)(18*density), 0);

        for(NavigationPage page : NavigationPage.values()) {
            row.addView(buildButton(page));
        }

        row.setAlignItems(AlignItems.CENTER);
        row.setJustifyContent(JustifyContent.SPACE_BETWEEN);

        addView(row);
    }

    View buildButton(NavigationPage page) {
        float density = getContext().getResources().getDisplayMetrics().density;
        int innerPadding = (int) (8*density);

        boolean isSelected = page == currentPage;
        int iconId = isSelected ? page.getSelectedIconId() : page.getDefaultIconId();

        FrameLayout frameLayout = new FrameLayout(getContext());
        frameLayout.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        frameLayout.setPadding(innerPadding,innerPadding,innerPadding,innerPadding);
        if(isSelected)
            frameLayout.setBackground(ContextCompat.getDrawable(getContext(),
                    R.drawable.navigation_layout_border));

        ImageView button = new ImageView(getContext());

        button.setLayoutParams(new ViewGroup.LayoutParams(
                (int) (24*density), (int) (24*density)
        ));

        button.setImageDrawable(ContextCompat.getDrawable(getContext(), iconId));

        button.setOnClickListener((e) -> {
            currentPage = page;
            update();

            Log.d("buildButton()", "on Click. Current: " + currentPage);
        });

        frameLayout.addView(button);

        return frameLayout;
    }

}
