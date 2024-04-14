package neu.provl.pomodoro.components;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
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

import lombok.Builder;
import lombok.Data;
import neu.provl.pomodoro.R;

@Data
public class FrameButton extends FrameLayout {
    boolean hasLeading = false;
    int leadingId = -1;
    float leadingWidth = 16,  leadingHeight = 16;

    String text = "Button";
    float textSize = 16;
    Typeface textTypeface;

    boolean disabled = false;

    boolean filledBackground = false;

    public FrameButton(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        float density = context.getResources().getDisplayMetrics().density;

        setClickable(true);

        Objects.requireNonNull(attrs, "Attributes of FrameButton cannot be null");

        TypedArray attributes = context.obtainStyledAttributes(attrs, R.styleable.FrameButton);
        hasLeading = attributes.getBoolean(R.styleable.FrameButton_hasLeading, false);
        leadingId = attributes.getResourceId(R.styleable.FrameButton_leading, R.drawable.logo_green);
        leadingWidth = attributes.getDimension(R.styleable.FrameButton_leadingWidth, 16f) / density;
        leadingHeight = attributes.getDimension(R.styleable.FrameButton_leadingHeight, 16f) / density;
        text = getResources().getString(attributes.getResourceId(R.styleable.FrameButton_text, R.string.app_name));
        textSize = attributes.getDimension(R.styleable.FrameButton_textSize, 16) / density;
        attributes.recycle();

        update();
    }

    public FrameButton(@NonNull Context context) {
        super(context);

        setClickable(true);
    }

    public void update() {
        removeAllViews();

        int density = (int) getResources().getDisplayMetrics().density;
        Context context = getContext();

        LinearLayout.LayoutParams LP = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        setLayoutParams(LP);

        setPadding(12 * density, 8 * density, 12 * density, 8 * density);

        if(isFilledBackground()) {
            GradientDrawable background = new GradientDrawable();
            background.setColor(ContextCompat.getColor(getContext(), R.color.primary));
            background.setCornerRadius(1000f); // To make it circular
            setBackground(background);
        } else {
            setBackground(ContextCompat.getDrawable(context, R.drawable.frame_button_border));
        }

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

            LinearLayout.LayoutParams imageViewLayoutParams = new LinearLayout.LayoutParams(
                    (int) (leadingWidth * density),
                    (int) (leadingHeight * density)
            );
            imageViewLayoutParams.weight = 1.0f;

            imageView.setLayoutParams(imageViewLayoutParams);

            linearLayout.addView(imageView);
        }

        TextView textView = new TextView(context);
        textView.setText(this.text);
        textView.setTextColor(disabled ?
                ContextCompat.getColor(context, R.color.secondary)
                : (!isFilledBackground() ? ContextCompat.getColor(context, R.color.primary)
                : ContextCompat.getColor(context, R.color.white)));
        textView.setTextSize(textSize);
        if(getTextTypeface() == null) {
            textView.setTypeface(ResourcesCompat.getFont(context, R.font.medium));
        }else {
            textView.setTypeface(getTextTypeface());
        }

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
