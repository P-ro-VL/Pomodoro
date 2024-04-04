package neu.provl.pomodoro.components;

import android.content.Context;
import android.content.res.TypedArray;
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

import neu.provl.pomodoro.R;

public class CoinChip extends FrameLayout {
    private int coin;

    public CoinChip(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        float density = context.getResources().getDisplayMetrics().density;

        setBackground(ContextCompat.getDrawable(getContext(), R.drawable.coin_chip_border));

        TypedArray attributes = context.obtainStyledAttributes(attrs, R.styleable.CoinChip);
        coin = attributes.getInt(R.styleable.CoinChip_coinAmount, 0);
        attributes.recycle();

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
        textView.setText(String.valueOf(this.coin));
        textView.setTextColor(ContextCompat.getColor(context, R.color.secondary));
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
}
