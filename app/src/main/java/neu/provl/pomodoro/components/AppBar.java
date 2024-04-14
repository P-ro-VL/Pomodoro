package neu.provl.pomodoro.components;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.util.LayoutDirection;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import neu.provl.pomodoro.R;

public class AppBar extends FrameLayout {

    private AttributeSet attrs;

    private boolean transparent = false;

    public AppBar(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        this.attrs = attrs;

        update();
    }

    public void setTransparent(boolean transparent) {
        this.transparent = transparent;
        removeAllViews();
        update();
    }

    public void update() {
        Context context = getContext();

        setPadding((int) getResources().getDimension(R.dimen.standard_padding),0, (int) getResources().getDimension(R.dimen.standard_padding),0);

        TypedArray attributes = context.obtainStyledAttributes(attrs, R.styleable.AppBar);
        boolean isTransparent = attributes.getBoolean(R.styleable.AppBar_isTransparent, transparent);
        attributes.recycle();

        float density = context.getResources().getDisplayMetrics().density;

        int iconId = isTransparent ? R.drawable.logo_white : R.drawable.logo_green;
        int settingsIconId = isTransparent ? R.drawable.settings_white : R.drawable.settings;

        GradientDrawable background = new GradientDrawable();
        if(transparent) {
            setBackground(null);
        } else {
            background.setColor(ContextCompat.getColor(getContext(), R.color.white));
            setBackground(background);
        }

        ImageView icon = new ImageView(context);
        LayoutParams iconLayoutParams = new LayoutParams((int) (32*density), (int) (32*density));
        iconLayoutParams.gravity = Gravity.CENTER;
        icon.setLayoutParams(iconLayoutParams);
        icon.setImageResource(iconId);

        ImageView settings = new ImageView(context);
        LayoutParams settingsLayoutParams = new LayoutParams((int) (24*density), (int) (24*density));
        settingsLayoutParams.gravity = Gravity.RIGHT | Gravity.CENTER;
        settings.setLayoutParams(settingsLayoutParams);
        settings.setImageResource(settingsIconId);

        addView(icon);
        addView(settings);
    }

}
