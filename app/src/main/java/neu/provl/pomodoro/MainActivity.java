package neu.provl.pomodoro;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import neu.provl.pomodoro.components.FrameButton;
import neu.provl.pomodoro.data.AcademicRecord;
import neu.provl.pomodoro.data.Plant;
import neu.provl.pomodoro.data.PlantType;
import neu.provl.pomodoro.data.Song;
import neu.provl.pomodoro.data.Subject;
import neu.provl.pomodoro.data.User;
import neu.provl.pomodoro.fragment.HomeFragment;

public class MainActivity extends AppCompatActivity {

    public static MainActivity instance;

    public static MainActivity getInstance() {
        return instance;
    }

    private FrameLayout root;

    public FrameLayout getRoot() {
        return root;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        instance = this;

        super.onCreate(savedInstanceState);
        setContentView(R.layout.app_layout);

        this.root = findViewById(R.id.app_layout);

        HomeFragment homeFragment = new HomeFragment();

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, homeFragment);
        fragmentTransaction.commit();
    }

}