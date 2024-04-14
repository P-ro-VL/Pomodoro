package neu.provl.pomodoro.components;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;

import com.google.android.flexbox.AlignItems;
import com.google.android.flexbox.FlexboxLayout;
import com.google.android.flexbox.JustifyContent;

import java.util.List;

import lombok.Getter;
import neu.provl.pomodoro.MainActivity;
import neu.provl.pomodoro.R;

public class Dropdown extends LinearLayout {

    @Getter
    private List<String> items;
    @Getter
    private int currentItemIndex = 0;

    public Dropdown(@NonNull Context context, @NonNull List<String> items) {
        super(context);

        this.items = items;

        setOnClickListener((e) -> showSelectionDialog());
        setCurrentItemIndex(0);

        setGravity(Gravity.CENTER);
    }

    public String getValue() {
        return getItems().get(getCurrentItemIndex());
    }

    public void setCurrentItemIndex(int currentItemIndex) {
        this.currentItemIndex = currentItemIndex;
        update();
    }

    void update() {
        int density = (int) getResources().getDisplayMetrics().density;
        String item = items.get(currentItemIndex);

        removeAllViews();

        TextView value = new TextView(getContext());
        value.setText(item);
        value.setTextSize(12);
        value.setTypeface(getResources().getFont(R.font.light));

        ImageView arrow = new ImageView(getContext());
        LinearLayout.LayoutParams arrowLP = new LinearLayout.LayoutParams(16 * density, 16 * density);
        arrowLP.leftMargin = 4*density;
        arrow.setLayoutParams(arrowLP);
        arrow.setImageResource(R.drawable.arrow_right);

        addView(value);
        addView(arrow);
    }

    void showSelectionDialog() {
        int density = (int) getResources().getDisplayMetrics().density;

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
        AlertDialog dialog = dialogBuilder.create();

        ScrollView scrollView = new ScrollView(getContext());
        ScrollView.LayoutParams layoutParams = new ScrollView.LayoutParams(
                ScrollView.LayoutParams.MATCH_PARENT,
                250 * density
        );
        layoutParams.setMarginStart(18 * density);
        layoutParams.setMarginEnd(18 * density);

        scrollView.setLayoutParams(layoutParams);
        scrollView.setPadding(12 * density, 12 * density, 12 * density, 12 * density);

        GradientDrawable layoutBg = new GradientDrawable();
        layoutBg.setColor(ContextCompat.getColor(getContext(), R.color.white));
        layoutBg.setCornerRadius(16 * density);

        scrollView.setBackground(layoutBg);

        LinearLayout layout = new LinearLayout(getContext());
        layout.setOrientation(LinearLayout.VERTICAL);

        for(int i = 0; i < getItems().size(); i++) {
            int index = i;
            String item = getItems().get(i);

            FlexboxLayout flexboxLayout = new FlexboxLayout(getContext());
            flexboxLayout.setPadding(0, 12 * density, 0, 12 * density);

            TextView value = new TextView(getContext());
            value.setText(item);
            value.setTextSize(14);
            value.setTypeface(getResources().getFont(R.font.regular));

            flexboxLayout.addView(value);
            if(getCurrentItemIndex() == i) {
                ImageView tick = new ImageView(getContext());
                LinearLayout.LayoutParams tickLP = new LayoutParams(16 * density, 16 * density);
                tick.setImageResource(android.R.drawable.checkbox_on_background);
                tick.setLayoutParams(tickLP);

                flexboxLayout.addView(tick);
            }

            flexboxLayout.setJustifyContent(JustifyContent.SPACE_BETWEEN);
            flexboxLayout.setAlignItems(AlignItems.CENTER);

            layout.addView(flexboxLayout);
            if(i != getItems().size() - 1) {
                View separator = new View(getContext());
                separator.setLayoutParams(new LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        density
                ));
                GradientDrawable background = new GradientDrawable();
                background.setColor(ContextCompat.getColor(getContext(), R.color.icon));
                separator.setBackground(background);

                layout.addView(separator);
            }

            flexboxLayout.setOnClickListener((e) -> {
                setCurrentItemIndex(index);

                dialog.cancel();
            });
        }

        scrollView.addView(layout);

        dialog.setView(scrollView);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }
}
