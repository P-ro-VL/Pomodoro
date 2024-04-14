package neu.provl.pomodoro.components;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import java.util.Objects;

import neu.provl.pomodoro.MainActivity;
import neu.provl.pomodoro.R;
import neu.provl.pomodoro.data.controller.AuthenticationDriver;
import neu.provl.pomodoro.data.controller.DatabaseDriver;

public class CoinChip extends FrameLayout {
    private int coin;
    private int coinTextId;

    public CoinChip(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        int density = (int) context.getResources().getDisplayMetrics().density;

        TypedArray attributes = context.obtainStyledAttributes(attrs, R.styleable.CoinChip);
        coin = AuthenticationDriver.currentUser.getCoin();
        int color = attributes.getColor(R.styleable.CoinChip_color, ContextCompat.getColor(context, R.color.secondary));
        attributes.recycle();

        GradientDrawable background = new GradientDrawable();
        background.setStroke((int) (2 * density), color);
        background.setPadding(16 * density, 12 * density, 16 * density, 12 * density);
        background.setCornerRadius(32 * density);
        setBackground(background);

        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
        linearLayout.setGravity(Gravity.CENTER);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
        params.weight = 1.0f;
        params.gravity = Gravity.CENTER;

        linearLayout.setLayoutParams(params);

        ImageView imageView = new ImageView(context);
        imageView.setImageResource(R.drawable.leaf);

        LinearLayout.LayoutParams imageViewLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        imageViewLayoutParams.weight = 1.0f;
        imageViewLayoutParams.width = (int) ((int) 16 * density);
        imageViewLayoutParams.height = (int) ((int) 16 * density);

        imageView.setLayoutParams(imageViewLayoutParams);

        linearLayout.addView(imageView);

        TextView textView = new TextView(context);
        textView.setId(coinTextId = generateViewId());
        textView.setText(String.valueOf(this.coin));
        textView.setTextColor(color);
        textView.setTextSize(14);
        textView.setTypeface(ResourcesCompat.getFont(context, R.font.semi_bold));

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        layoutParams.setMargins((int) (8*density), 0, 0, 0);

        textView.setLayoutParams(layoutParams);

        linearLayout.addView(textView);

        addView(linearLayout);
    }

    public void update() {
        TextView textView = findViewById(coinTextId);
        textView.setText(String.valueOf(AuthenticationDriver.currentUser.getCoin()));
    }
}
