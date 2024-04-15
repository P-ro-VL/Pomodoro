package neu.provl.pomodoro.fragment;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.flexbox.AlignItems;
import com.google.android.flexbox.FlexboxLayout;
import com.google.android.flexbox.JustifyContent;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import de.hdodenhof.circleimageview.CircleImageView;
import neu.provl.pomodoro.MainActivity;
import neu.provl.pomodoro.R;
import neu.provl.pomodoro.components.FrameButton;
import neu.provl.pomodoro.components.FriendAvatar;
import neu.provl.pomodoro.components.PlantCard;
import neu.provl.pomodoro.components.ProgressBar;
import neu.provl.pomodoro.concurrent.NetworkImageInjector;
import neu.provl.pomodoro.data.Plant;
import neu.provl.pomodoro.data.User;
import neu.provl.pomodoro.data.controller.AuthenticationDriver;
import neu.provl.pomodoro.data.controller.DatabaseDriver;

public class HomeFragment extends Fragment {

    private LinearLayout root;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.root = (LinearLayout) inflater.inflate(R.layout.home_layout, null);

        TextView friendRequestChip = root.findViewById(R.id.friend_request_chip);
        friendRequestChip.setOnClickListener((e) -> {
            showFriendRequestsDialog();
        });

        ImageView addFriendBtn = root.findViewById(R.id.add_friend);
        addFriendBtn.setOnClickListener((e) -> {
            showSendFriendRequestDialog();
        });

        float density = getContext().getResources().getDisplayMetrics().density;

        this.root.post(() -> {
            View heroSection = root.findViewById(R.id.hero_section);
            heroSection.setClipToOutline(false);

            ImageView heroImage = root.findViewById(R.id.hero_image);
            FrameLayout.LayoutParams heroImageLayoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT,
                    (int) (heroSection.getMeasuredHeight() - (24 * density)));
            heroImageLayoutParams.gravity = Gravity.END | Gravity.BOTTOM;
            heroImage.setLayoutParams(heroImageLayoutParams);
            heroImage.setImageResource(R.drawable.hero_side_image);



            User user = AuthenticationDriver.currentUser;
            int levelXp = (int) ((int)1000*(user.getLevel()/2.0));

            ProgressBar progressBar = root.findViewById(R.id.exp_bar);
            progressBar.setProgress(user.getXp()*1.0 / levelXp*1.0);
            progressBar.update();

            TextView xpRemain = root.findViewById(R.id.xp_remain);
            xpRemain.setText(getResources().getString(R.string.level_taken_xp).replace("%taken%",
                    (levelXp - user.getXp())*1.0 + ""));
        });

        refreshFriendStatus();

        initFriendList();
        initFriendRequest();
        initPlantList();

        TextView level = root.findViewById(R.id.level);
        level.setText(AuthenticationDriver.currentUser.getLevel() + "");

        return root;
    }

    void refreshFriendStatus() {
        AuthenticationDriver.currentUser.getFriends()
                .stream()
                .filter(friend -> DatabaseDriver.getInstance().getStudyingUsers().contains(friend.getId()))
                .forEach(user -> user.setStudying(true));
    }

    void showSendFriendRequestDialog() {
        int density = (int) getContext().getResources().getDisplayMetrics().density;

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
        AlertDialog dialog = dialogBuilder.create();
        LayoutInflater layoutInflater = getLayoutInflater();

        View dialogView = layoutInflater.inflate(R.layout.send_friend_request_layout, null);
        GradientDrawable background = new GradientDrawable();
        background.setCornerRadius(16 * density);
        background.setColor(ContextCompat.getColor(getContext(), R.color.white));
        dialogView.setBackground(background);

        FrameButton btn = dialogView.findViewById(R.id.send_friend_request);
        btn.setOnClickListener((e) -> {
            dialog.cancel();
        });

        dialog.setView(dialogView);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }

    void showFriendRequestsDialog() {
        int density = (int) getContext().getResources().getDisplayMetrics().density;

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
        LayoutInflater layoutInflater = getLayoutInflater();

        View dialogView = layoutInflater.inflate(R.layout.friend_requests_layout, null);
        GradientDrawable background = new GradientDrawable();
        background.setCornerRadius(16 * density);
        background.setColor(ContextCompat.getColor(getContext(), R.color.white));
        dialogView.setBackground(background);

        LinearLayout scrollView = dialogView.findViewById(R.id.friend_request_scroll_view);
        initFriendRequests().forEach(scrollView::addView);

        dialogBuilder.setView(dialogView);
        dialogBuilder.show().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }

    List<View> initFriendRequests() {
        int density = (int) getContext().getResources().getDisplayMetrics().density;

        List<View> friendRequestCard = new ArrayList<>();

        AuthenticationDriver.currentUser.getFriendRequests().forEach((user) -> {
            FlexboxLayout layout = new FlexboxLayout(getContext());

            LinearLayout infoLayout = new LinearLayout(getContext());
            infoLayout.setOrientation(LinearLayout.HORIZONTAL);

            CircleImageView avatar = new CircleImageView(getContext());
            avatar.setLayoutParams(new LinearLayout.LayoutParams(48 * density, 48 * density));

            TextView name = new TextView(getContext());
            LinearLayout.LayoutParams nameLP = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
            nameLP.gravity = Gravity.CENTER;
            nameLP.setMarginStart(12 * density);
            name.setLayoutParams(nameLP);
            name.setText(user.getName());
            name.setTextSize(12);
            name.setTypeface(getResources().getFont(R.font.medium));

            infoLayout.addView(avatar);
            infoLayout.addView(name);

            LinearLayout actionsLayout = new LinearLayout(getContext());
            actionsLayout.setOrientation(LinearLayout.HORIZONTAL);

            TextView delete = new TextView(getContext());
            delete.setText(R.string.delete);
            delete.setTextColor(ContextCompat.getColor(getContext(), R.color.destructive));
            delete.setTextSize(12);
            delete.setTypeface(getResources().getFont(R.font.light));

            TextView accept = new TextView(getContext());
            accept.setPadding(8 * density, 0, 0, 0);
            accept.setText(R.string.accept);
            accept.setTextColor(ContextCompat.getColor(getContext(), R.color.primary));
            accept.setTextSize(12);
            accept.setTypeface(getResources().getFont(R.font.semi_bold));

            actionsLayout.addView(delete);
            actionsLayout.addView(accept);

            layout.addView(infoLayout);
            layout.addView(actionsLayout);

            layout.setJustifyContent(JustifyContent.SPACE_BETWEEN);
            layout.setAlignItems(AlignItems.CENTER);

            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
            layoutParams.topMargin = 12 * density;
            layout.setLayoutParams(layoutParams);

            friendRequestCard.add(layout);

            new NetworkImageInjector(avatar).execute(user.getImageUrl());
        });

        return friendRequestCard;
    }

    void initFriendList() {
        LinearLayout scrollView = root.findViewById(R.id.friend_scroll_view);

        List<User> friends = AuthenticationDriver.currentUser.getFriends();

        if(friends == null || friends.isEmpty()) {
            TextView noFriend = new TextView(getContext());
            noFriend.setText("You have no friend. Add more friend!");
            noFriend.setTypeface(getResources().getFont(R.font.medium));
            noFriend.setTextSize(14);
            noFriend.setTextColor(ContextCompat.getColor(getContext(), R.color.typography));
            scrollView.addView(noFriend);
            return;
        }

        friends.stream()
                // We're doing a trick here. Compare u2 before u1 to make all online users
                // sorted to the rear
                .sorted((u1, u2) -> Boolean.compare(u2.isStudying(), u1.isStudying()))
                .forEach((friend) -> {
            scrollView.addView(new FriendAvatar(getContext(), friend, 64, 64));
        });
    }

    void initPlantList() {
        LinearLayout scrollView = root.findViewById(R.id.my_plants_view);

        List<Plant> plants = AuthenticationDriver.currentUser.getPlants();

        // We're doing a trick here. Compare u2 before u1 to make all online users
        // sorted to the rear
        plants.forEach((plant) -> {
            scrollView.addView(new PlantCard(getContext(), plant));
        });
    }

    void initFriendRequest() {
        List<User> friendRequests = AuthenticationDriver.currentUser.getFriendRequests();

        if(friendRequests.isEmpty()) return;

        TextView chip = root.findViewById(R.id.friend_request_chip);
        ((FrameLayout)chip.getParent()).setVisibility(View.VISIBLE);
        chip.setText(getResources().getString(R.string.friend_requests).replace("%amount%",
                friendRequests.size()+""));
    }
}
