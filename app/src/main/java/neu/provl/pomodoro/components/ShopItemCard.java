package neu.provl.pomodoro.components;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.google.android.flexbox.AlignItems;
import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexboxLayout;
import com.google.android.flexbox.JustifyContent;

import org.w3c.dom.Text;

import de.hdodenhof.circleimageview.CircleImageView;
import neu.provl.pomodoro.R;
import neu.provl.pomodoro.concurrent.NetworkImageInjector;
import neu.provl.pomodoro.data.Plant;

public class ShopItemCard extends FlexboxLayout {
    public ShopItemCard(@NonNull Context context, @NonNull Plant plant) {
        super(context);

        int density = (int) getResources().getDisplayMetrics().density;

        FlexboxLayout.LayoutParams layoutParams = new FlexboxLayout.LayoutParams(
                FlexboxLayout.LayoutParams.MATCH_PARENT,
               80 * density
        );
        layoutParams.bottomMargin = 12 * density;
        setLayoutParams(layoutParams);

        setPadding(18 * density, 0, 18 * density, 0);
        setJustifyContent(JustifyContent.SPACE_BETWEEN);
        setAlignItems(AlignItems.CENTER);

        GradientDrawable background = new GradientDrawable();
        background.setColor(ContextCompat.getColor(context, R.color.white));
        background.setCornerRadius(14 * density);
        setBackground(background);

        setElevation(2f);

        CircleImageView avatar = new CircleImageView(context);
        LinearLayout.LayoutParams avatarLP = new LinearLayout.LayoutParams(
                65*density,
                65*density
        );
        avatar.setLayoutParams(avatarLP);
        GradientDrawable avatarBg = new GradientDrawable();
        avatarBg.setColor(ContextCompat.getColor(context, R.color.primary));
        avatarBg.setCornerRadius(1000f); // To make it circular
        avatar.setBackground(avatarBg);

        new NetworkImageInjector(avatar).execute(plant.getImageUrl());

        FlexboxLayout info = new FlexboxLayout(context);
        info.setPadding(12 * density, 0, 0, 0);
        info.setFlexDirection(FlexDirection.COLUMN);
        info.setJustifyContent(JustifyContent.SPACE_BETWEEN);

        TextView name = new TextView(context);
        name.setText(plant.getName());
        name.setTextSize(16);
        name.setTypeface(getResources().getFont(R.font.medium));

        LinearLayout priceLayout = new LinearLayout(context);

        TextView price = new TextView(context);
        price.setText(plant.getPrice() + "");
        price.setTextSize(16);
        price.setTextColor(ContextCompat.getColor(context, R.color.secondary));
        price.setTypeface(getResources().getFont(R.font.medium));

        ImageView leafIcon = new ImageView(context);
        LayoutParams leafIconLP = new LayoutParams(
                16 * density,
                16 * density
        );
        leafIconLP.leftMargin = 4 * density;
        leafIcon.setLayoutParams(leafIconLP);
        leafIcon.setImageResource(R.drawable.leaf);

        priceLayout.setGravity(Gravity.CENTER);

        priceLayout.addView(price);
        priceLayout.addView(leafIcon);

        info.addView(name);
        info.addView(priceLayout);

        FrameButton actionBtn = new FrameButton(context);
        actionBtn.setText(getResources().getString(R.string.buy));
        actionBtn.setTextSize(12);
        actionBtn.update();

        LinearLayout leadingLayout = new LinearLayout(context);
        leadingLayout.addView(avatar);
        leadingLayout.addView(info);

        addView(leadingLayout);
        addView(actionBtn);
    }
}
