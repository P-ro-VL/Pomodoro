package neu.provl.pomodoro.components;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import neu.provl.pomodoro.R;
import neu.provl.pomodoro.concurrent.NetworkImageInjector;
import neu.provl.pomodoro.data.Plant;

public class PlantCard extends FrameLayout {
    public PlantCard(@NonNull Context context, @NonNull Plant plant) {
        super(context);

        int density = (int) context.getResources().getDisplayMetrics().density;

        FrameLayout.LayoutParams LP = new LayoutParams(90 * density, 115 * density);
        LP.rightMargin = 12 * density;
        setLayoutParams(LP);

        FrameLayout layout = new FrameLayout(context);
        FrameLayout.LayoutParams layoutParams = new LayoutParams(90 * density, 100 * density);
        layoutParams.gravity = Gravity.BOTTOM;
        layout.setLayoutParams(layoutParams);

        GradientDrawable background = new GradientDrawable();
        background.setColor(ContextCompat.getColor(context, R.color.primary));
        background.setCornerRadius(12 * density);

        layout.setBackground(background);

        ImageView shadow = new ImageView(context);
        LayoutParams shadowLP = new LayoutParams(54 * density, 22 * density);
        shadowLP.gravity = Gravity.CENTER_VERTICAL | Gravity.END;
        shadow.setLayoutParams(shadowLP);
        shadow.setImageResource(R.drawable.plant_shadow);

        ImageView plantImg = new ImageView(context);
        LayoutParams plantLP = new LayoutParams(65 * density, 65 * density);
        plantLP.gravity = Gravity.END;
        plantImg.setLayoutParams(plantLP);

        LinearLayout textInfo = new LinearLayout(context);
        textInfo.setPadding(8 * density,8 * density,8 * density,8 * density);
        textInfo.setOrientation(LinearLayout.VERTICAL);
        FrameLayout.LayoutParams textInfoLP = new FrameLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        textInfoLP.gravity = Gravity.BOTTOM;
        textInfo.setLayoutParams(textInfoLP);

        TextView plantName = new TextView(context);
        plantName.setText(plant.getName());
        plantName.setTextColor(ContextCompat.getColor(context, R.color.white));
        plantName.setTextSize(12f);
        plantName.setTypeface(getResources().getFont(R.font.medium));

        TextView plantType = new TextView(context);
        plantType.setText(getResources().getString(plant.getType().getDisplayNameId()));
        plantType.setTextColor(ContextCompat.getColor(context, R.color.white));
        plantType.setTextSize(10f);
        plantType.setTypeface(getResources().getFont(R.font.light));

        textInfo.addView(plantName);
        textInfo.addView(plantType);

        addView(layout);
        addView(shadow);
        addView(textInfo);
        addView(plantImg);

        new NetworkImageInjector(plantImg).execute(plant.getImageUrl());
    }
}
