package neu.provl.pomodoro.components.navigation;

import android.content.Context;
import android.content.res.Configuration;
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
import android.widget.ScrollView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.flexbox.AlignItems;
import com.google.android.flexbox.FlexboxLayout;
import com.google.android.flexbox.JustifyContent;
import com.google.android.material.snackbar.Snackbar;

import java.util.HashMap;
import java.util.Map;

import neu.provl.pomodoro.MainActivity;
import neu.provl.pomodoro.R;
import neu.provl.pomodoro.components.AppBar;
import neu.provl.pomodoro.fragment.GardenFragment;

public class NavigationBar extends FrameLayout {

    public static NavigationPage currentPage = NavigationPage.HOME;
    public static final Map<NavigationPage, Fragment> cachedFragments = new HashMap<>();

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
            if(currentPage == page) return;

            if(GardenFragment.isStudying) {
                Snackbar.make(this, R.string.cannot_navigate_while_studying, Snackbar.LENGTH_SHORT)
                        .setBackgroundTint(ContextCompat.getColor(getContext(), R.color.destructive))
                        .show();
                return;
            }

            currentPage = page;
            update();

            try {
                Fragment fragment;
                if(cachedFragments.containsKey(currentPage)) {
                    fragment = cachedFragments.get(currentPage);
                } else {
                    fragment = page.getFragmentWrapper().newInstance();
                    cachedFragments.put(currentPage, fragment);
                }

                FragmentManager fragmentManager = MainActivity.getInstance().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                assert fragment != null;
                fragmentTransaction.replace(R.id.fragment_container, fragment);
                fragmentTransaction.commit();

                // This method is used to make sure that the commit action has finished to
                // make the transition among pages smoother.
                fragmentManager.executePendingTransactions();

                boolean isTransparent = currentPage.isTransparentAppBar()
                        && MainActivity.getInstance().getResources()
                        .getConfiguration().orientation != Configuration.ORIENTATION_LANDSCAPE;

                AppBar appBar = MainActivity.getInstance().getRoot().findViewById(R.id.appbar);
                appBar.setTransparent(isTransparent);

                ScrollView scrollView = MainActivity.getInstance().getRoot().findViewById(R.id.main_scroll_view);
                if(isTransparent) {
                    ((LayoutParams) scrollView.getLayoutParams()).topMargin = 0;
                    appBar.setZ(100);
                } else {
                    ((LayoutParams) scrollView.getLayoutParams()).topMargin = (int) (60 * density);
                    appBar.setZ(0);
                }
            } catch (IllegalAccessException | InstantiationException ex) {
                throw new RuntimeException(ex);
            }
        });

        frameLayout.addView(button);

        return frameLayout;
    }

}
