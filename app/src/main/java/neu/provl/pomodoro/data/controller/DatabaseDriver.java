package neu.provl.pomodoro.data.controller;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;
import neu.provl.pomodoro.MainActivity;
import neu.provl.pomodoro.R;
import neu.provl.pomodoro.SplashScreen;
import neu.provl.pomodoro.data.Plant;
import neu.provl.pomodoro.data.PlantType;
import neu.provl.pomodoro.data.Song;
import neu.provl.pomodoro.data.Subject;

public class DatabaseDriver {

    @Getter
    public static DatabaseDriver instance;

    public static void init() {
        SplashScreen.setLoadingState(R.string.splash_screen_connect_database);

        new DatabaseDriver();

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("studying-users");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<String> studyingUsers = snapshot.getValue(List.class);
                instance.setStudyingUsers(studyingUsers);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private FirebaseFirestore databaseInstance;

    @Getter
    private List<Plant> availablePlants = new ArrayList<>();
    @Getter
    private List<Song> availableSongs = new ArrayList<>();
    @Getter
    @Setter
    private List<String> studyingUsers = new ArrayList<>();

    public DatabaseDriver() {
        if(instance != null) {
            throw new RuntimeException("The database driver cannot be initialized twice.");
        }

        instance = this;

        databaseInstance = FirebaseFirestore.getInstance();
        fetchAllPlants();
        fetchAllSongs();
    }

    public void fetchAllSongs() {
        SplashScreen.setLoadingState(R.string.splash_screen_fetching_song);
        databaseInstance.collection("songs")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Map<String, Object> data = document.getData();

                                Song song = Song.builder()
                                        .id((String) data.get("id"))
                                        .name((String) data.get("name"))
                                        .author((String) data.get("author"))
                                        .thumbnailUrl((String) data.get("thumbnail"))
                                        .build();
                                availableSongs.add(song);
                            }
                        } else {
                            Snackbar.make(
                                            MainActivity.getInstance().getRoot(),
                                            R.string.song_fetch_error,
                                            Snackbar.LENGTH_SHORT
                                    )
                                    .setText(ContextCompat.getString(
                                            MainActivity.getInstance().getApplicationContext(),
                                            R.string.song_fetch_error
                                    ).replace("%error%", task.getException().getMessage()))
                                    .setBackgroundTint(
                                            ContextCompat.getColor(
                                                    MainActivity.getInstance().getApplicationContext(),
                                                    R.color.destructive)
                                    ).show();
                        }
                    }
                });
    }

    public void fetchAllPlants() {
        SplashScreen.setLoadingState(R.string.splash_screen_fetching_plant);
        databaseInstance.collection("plants")
            .get()
            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Map<String, Object> data = document.getData();

                            Plant plant = Plant.builder()
                                    .id((String) data.get("id"))
                                    .name((String) data.get("name"))
                                    .type(PlantType.valueOf(((String) data.get("type")).toUpperCase()))
                                    .description((String) data.get("description"))
                                    .price(((Long) data.get("price")).intValue())
                                    .coinPerPhase(((Long) data.get("coinPerPhase")).intValue())
                                    .xpPerPhase(((Long) data.get("xpPerPhase")).intValue())
                                    .imageUrl("https://raw.githubusercontent.com/P-ro-VL/Pomodoro/main/imgs/" + data.get("id") + ".png")
                                    .build();
                            availablePlants.add(plant);
                        }
                    } else {
                        Snackbar.make(
                            MainActivity.getInstance().getRoot(),
                            R.string.plant_fetch_error,
                            Snackbar.LENGTH_SHORT
                        )
                        .setText(ContextCompat.getString(
                                MainActivity.getInstance().getApplicationContext(),
                                R.string.plant_fetch_error
                        ).replace("%error%", task.getException().getMessage()))
                        .setBackgroundTint(
                            ContextCompat.getColor(
                                    MainActivity.getInstance().getApplicationContext(),
                                    R.color.destructive)
                        ).show();
                    }
                }
            });
    }
}
