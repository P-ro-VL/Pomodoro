package neu.provl.pomodoro.fragment.authenticate;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.common.base.Charsets;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.nio.charset.Charset;
import java.util.Base64;

import neu.provl.pomodoro.LoginScreen;
import neu.provl.pomodoro.MainActivity;
import neu.provl.pomodoro.R;
import neu.provl.pomodoro.components.FrameButton;
import neu.provl.pomodoro.data.controller.AuthenticationDriver;

public class LoginFragment extends Fragment {

    private LoginScreen activity;
    private LinearLayout root;

    public LoginFragment(LoginScreen activity) {
        this.activity = activity;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.root = (LinearLayout) inflater.inflate(
                R.layout.login_layout, null
        );

        FrameButton loginBtn = root.findViewById(R.id.login_button);

        EditText emailInput = root.findViewById(R.id.email_input);
        EditText passwordInput = root.findViewById(R.id.password_input);

        loginBtn.setOnClickListener((e) -> {
            FirebaseAuth auth = FirebaseAuth.getInstance();

            String email = emailInput.getText().toString();
            String password = passwordInput.getText().toString();

            if (TextUtils.isEmpty(email)) {
                emailInput.setError("Required.");
                return;
            } else {
                emailInput.setError(null);
            }

            if (TextUtils.isEmpty(password)) {
                passwordInput.setError("Required.");
                return;
            } else {
                passwordInput.setError(null);
            }

            auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                FirebaseUser user = auth.getCurrentUser();
                                AuthenticationDriver.signIn(user, email, password);

                                Intent intent = new Intent(activity, MainActivity.class);
                                startActivity(intent);
                                activity.finish();
                            } else {
                                Snackbar.make(
                                        root,
                                        R.string.authentication_failed,
                                        Snackbar.LENGTH_SHORT
                                ).show();
                            }
                        }
                    });

        });

        if(AuthenticationDriver.LAST_LOGIN_USERNAME != null) {
            emailInput.setText(AuthenticationDriver.LAST_LOGIN_USERNAME);

            byte[] decoded = Base64.getDecoder().decode(AuthenticationDriver.LAST_LOGIN_PASSWORD);
            passwordInput.setText(
                    new String(decoded, Charsets.UTF_8)
            );

            loginBtn.performClick();
        }


        TextView registerText = root.findViewById(R.id.create_new_account);
        registerText.setOnClickListener((e) -> {
            activity.setMode(LoginScreen.REGISTER_MODE);
        });

        return root;
    }
}
