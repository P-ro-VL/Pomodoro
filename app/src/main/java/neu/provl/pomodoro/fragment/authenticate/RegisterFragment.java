package neu.provl.pomodoro.fragment.authenticate;

import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import neu.provl.pomodoro.LoginScreen;
import neu.provl.pomodoro.R;
import neu.provl.pomodoro.components.FrameButton;

public class RegisterFragment extends Fragment {

    private LoginScreen activity;
    private LinearLayout root;

    public RegisterFragment(LoginScreen activity) {
        this.activity = activity;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.root = (LinearLayout) inflater.inflate(
                R.layout.register_layout, null
        );

        FrameButton registerButton = root.findViewById(R.id.register_button);

        EditText emailInput = root.findViewById(R.id.email_input);
        EditText displaynameInput = root.findViewById(R.id.displayname_input);
        EditText passwordInput = root.findViewById(R.id.password_input);
        EditText repasswordInput = root.findViewById(R.id.repassword_input);

        registerButton.setOnClickListener((e) -> {
            FirebaseAuth auth = FirebaseAuth.getInstance();

            String email = emailInput.getText().toString();
            String displayname = displaynameInput.getText().toString();
            String password = passwordInput.getText().toString();
            String repassword = repasswordInput.getText().toString();

            if (TextUtils.isEmpty(email)) {
                emailInput.setError("Required.");
                return;
            } else {
                emailInput.setError(null);
            }

            if (TextUtils.isEmpty(displayname)) {
                displaynameInput.setError("Required.");
                return;
            } else {
                displaynameInput.setError(null);
            }

            if (TextUtils.isEmpty(password)) {
                passwordInput.setError("Required.");
                return;
            } else {
                passwordInput.setError(null);
            }

            if (TextUtils.isEmpty(repassword)) {
                repasswordInput.setError("Required.");
                return;
            } else {
                repasswordInput.setError(null);
            }

            if(!password.equalsIgnoreCase(repassword)) {
                Snackbar.make(
                        root,
                        R.string.password_not_match,
                        Snackbar.LENGTH_SHORT
                ).show();
                return;
            }

            auth.createUserWithEmailAndPassword(email, password)
                    .addOnSuccessListener((authResult) -> {
                        Snackbar.make(
                                root,
                                R.string.register_success,
                                Snackbar.LENGTH_SHORT
                        ).show();

                        FirebaseUser user = authResult.getUser();

                        assert user != null;
                        user.updateProfile(new UserProfileChangeRequest.Builder()
                                .setDisplayName(displayname)
                                .setPhotoUri(Uri.parse("https://t4.ftcdn.net/jpg/05/49/98/39/360_F_549983970_bRCkYfk0P6PP5fKbMhZMIb07mCJ6esXL.jpg"))
                                .build()).addOnSuccessListener((result) -> {
                            Log.d("INIT",  "INIT DATA");
                                    initData(user);

                        });
                    })
                    .addOnFailureListener((fail) -> {
                        Snackbar.make(
                                root,
                                R.string.register_fail,
                                Snackbar.LENGTH_SHORT
                        ).show();
                    });

        });

        TextView backToSignIn = root.findViewById(R.id.back_to_signin);
        backToSignIn.setOnClickListener((e) -> {
            activity.setMode(LoginScreen.LOGIN_MODE);
        });

        return root;
    }

    public void initData(FirebaseUser user) {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        Map<String, Object> data = new HashMap<>();
        data.put("id", user.getUid());
        data.put("displayname", user.getDisplayName());
        data.put("image-url", user.getPhotoUrl().toString());
        data.put("coin", 0);
        data.put("plants", Arrays.asList("lan_ho_diep"));
        data.put("friends", new ArrayList<String>());
        data.put("friend-requests", new ArrayList<String>());
        data.put("academic-records", new HashMap<String, String>());

        firestore.collection("users")
                .document(user.getUid())
                .set(data);
    }
}
