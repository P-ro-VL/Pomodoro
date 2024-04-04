package neu.provl.pomodoro;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import neu.provl.pomodoro.data.User;
import neu.provl.pomodoro.fragment.HomeFragment;

public class MainActivity extends AppCompatActivity {

    public static User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.app_layout);

        initTestUser();

        HomeFragment homeFragment = new HomeFragment();

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, homeFragment);
        fragmentTransaction.commit();
    }

    void initTestUser() {
        List<User> friends = new ArrayList<>();
        for(int i = 0; i < 10; i++) {
            friends.add(User.builder()
                    .id("" + i)
                    .name("Friend " + i)
                    .imageUrl("https://image.kpopmap.com/2019/04/lee-taevin-profile.jpg")
                    .isOnline(ThreadLocalRandom.current().nextBoolean())
                    .build());
        }

        MainActivity.currentUser = new User("user", "Pham Van Linh", "", true, friends, new ArrayList<>());
    }

}