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

public class FrameButton extends FrameLayout {
    boolean hasLeading = false;
    int leadingId = -1;
    float leadingWidth, leadingHeight;

    String text = "Button";
    float textSize = 16;

    boolean disabled = false;

    public FrameButton(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        float density = context.getResources().getDisplayMetrics().density;

        setClickable(true);
        setBackground(ContextCompat.getDrawable(context, R.drawable.frame_button_border));

        Objects.requireNonNull(attrs, "Attributes of FrameButton cannot be null");

        TypedArray attributes = context.obtainStyledAttributes(attrs, R.styleable.FrameButton);
        hasLeading = attributes.getBoolean(R.styleable.FrameButton_hasLeading, false);
        leadingId = attributes.getResourceId(R.styleable.FrameButton_leading, R.drawable.logo_green);
        leadingWidth = attributes.getDimension(R.styleable.FrameButton_leadingWidth, 16f);
        leadingHeight = attributes.getDimension(R.styleable.FrameButton_leadingHeight, 16f) ;
        text = getResources().getString(attributes.getResourceId(R.styleable.FrameButton_text, R.string.app_name));
        textSize = attributes.getDimension(R.styleable.FrameButton_textSize, 16) / density;
        attributes.recycle();

        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
        linearLayout.setGravity(Gravity.CENTER);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
        params.weight = 1.0f;
        params.gravity = Gravity.CENTER;

        linearLayout.setLayoutParams(params);

        if(hasLeading) {
            ImageView imageView = new ImageView(context);
            imageView.setImageResource(leadingId);

            LinearLayout.LayoutParams imageViewLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            imageViewLayoutParams.weight = 1.0f;
            imageViewLayoutParams.width = (int) leadingWidth;
            imageViewLayoutParams.height = (int) leadingHeight;

            Log.d("LayoutParams", "" + ((int) leadingWidth));

            imageView.setLayoutParams(imageViewLayoutParams);

            linearLayout.addView(imageView);
        }

        TextView textView = new TextView(context);
        textView.setText(this.text);
        textView.setTextColor(disabled ?
                ContextCompat.getColor(context, R.color.secondary)
                : ContextCompat.getColor(context, R.color.primary));
        textView.setTextSize(textSize);
        textView.setTypeface(ResourcesCompat.getFont(context, R.font.madani));

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        layoutParams.setMargins(hasLeading ? 16 : 0, 0, 0, 0);

        textView.setLayoutParams(layoutParams);

        linearLayout.addView(textView);

        addView(linearLayout);
    }
}
