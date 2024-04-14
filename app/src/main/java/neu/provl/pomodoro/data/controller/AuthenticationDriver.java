package neu.provl.pomodoro.data.controller;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import neu.provl.pomodoro.data.AcademicRecord;
import neu.provl.pomodoro.data.Plant;
import neu.provl.pomodoro.data.Subject;
import neu.provl.pomodoro.data.User;

public class AuthenticationDriver {

    public static User currentUser;

    public static void signIn(FirebaseUser user)  {
        AuthenticationDriver.currentUser = new User(
                user.getUid(),
                user.getDisplayName(),
                user.getPhotoUrl().toString()
        );

        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        firestore.collection("users").document(user.getUid())
                .addSnapshotListener((snapshot, error) -> {
                    Map<String, Object> data = snapshot.getData();

                    int coin = ((Long) data.get("coin")).intValue();
                    List<String> friendIds = (List<String>) data.get("friends");
                    List<String> rawFriendRequests = (List<String>) data.get("friend-requests");
                    List<String> plantIds = (List<String>) data.get("plants");
                    Map<String, Object> rawAcademicRecords = (Map<String, Object>) data.get("academic-records");

                    currentUser.setCoin(coin);

                    currentUser.setFriends(loadFriendList(friendIds));
                    currentUser.setFriendRequests(loadFriendRequest(rawFriendRequests));

                    currentUser.setPlants(loadPlantList(plantIds));

                    currentUser.setAcademicRecords(loadAcademicRecords(rawAcademicRecords));
                });
    }

    private static List<AcademicRecord> loadAcademicRecords(Map<String, Object> data) {
        List<AcademicRecord> academicRecords = new ArrayList<>();

        data.forEach((k, v) -> {
            String rawScore = (String) v;
            String[] args = rawScore.split("@");
            double[] doubles = Arrays.stream(args[1].split("/")).mapToDouble(Double::parseDouble)
                    .toArray();
            AcademicRecord record = new AcademicRecord(k,
                    args[0],
                    new double[] {
                        doubles[0],
                        doubles[1],
                        doubles[2]
                    }
            );

            academicRecords.add(record);
        });

        return academicRecords;
    }

    private static List<Plant> loadPlantList(List<String> plantIds) {
        List<Plant> plants = new ArrayList<>();

        plantIds.forEach((id) -> {
            plants.add(DatabaseDriver.getInstance()
                    .getAvailablePlants().stream().filter(plant -> plant.getId().equalsIgnoreCase(id)).findFirst()
                    .get());
        });

        return plants;
    }

    private static List<User> loadFriendList(List<String> friendIds) {
        List<User> users = new ArrayList<>();

        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        friendIds.forEach((id) -> {
            firestore.collection("users").document(id)
                    .addSnapshotListener((snapshot, error) -> {
                        Map<String, Object> data = snapshot.getData();

                        users.add(new User(
                                id,
                                (String) data.get("displayname"),
                                (String) data.get("image-url")
                        ));
                    });
        });

        return users;
    }

    private static List<User> loadFriendRequest(List<String>  friendIds) {
        List<User> users = new ArrayList<>();

        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        friendIds.forEach((id) -> {
            firestore.collection("users").document(id)
                    .addSnapshotListener((snapshot, error) -> {
                        Map<String, Object> data = snapshot.getData();

                        users.add(new User(
                                id,
                                (String) data.get("displayname"),
                                (String) data.get("image-url")
                        ));
                    });
        });

        return users;
    }
}
