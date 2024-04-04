package neu.provl.pomodoro.fragment;

import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import neu.provl.pomodoro.MainActivity;
import neu.provl.pomodoro.R;
import neu.provl.pomodoro.components.FriendAvatar;
import neu.provl.pomodoro.data.User;

public class HomeFragment extends Fragment {

    private LinearLayout root;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.root = (LinearLayout) inflater.inflate(R.layout.home_layout, null);

        float density = getContext().getResources().getDisplayMetrics().density;

        this.root.post(() -> {
            View heroSection = root.findViewById(R.id.hero_section);
            heroSection.setClipToOutline(false);

            ImageView heroImage = root.findViewById(R.id.hero_image);
            FrameLayout.LayoutParams heroImageLayoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, (int) (heroSection.getMeasuredHeight() - (24 * density)));
            heroImageLayoutParams.gravity = Gravity.END | Gravity.BOTTOM;
            heroImage.setLayoutParams(heroImageLayoutParams);
            heroImage.setImageResource(R.drawable.hero_side_image);
        });

        initFriendList();
        return root;
    }

    void initFriendList() {
        LinearLayout scrollView = root.findViewById(R.id.friend_scroll_view);

        MainActivity.currentUser.getFriends().stream()
                // We're doing a trick here. Compare u2 before u1 to make all online users
                // sorted to the rear
                .sorted((u1, u2) -> Boolean.compare(u2.isOnline(), u1.isOnline()))
                .forEach((friend) -> {
            scrollView.addView(new FriendAvatar(getContext(), friend, 64, 64));
        });
    }

    void initPlantList() {

    }
}
