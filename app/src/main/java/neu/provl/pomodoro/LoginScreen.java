package neu.provl.pomodoro;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.auth.api.identity.BeginSignInRequest;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import java.util.HashMap;
import java.util.Map;

import lombok.Getter;
import neu.provl.pomodoro.components.FrameButton;
import neu.provl.pomodoro.data.Subject;
import neu.provl.pomodoro.data.User;
import neu.provl.pomodoro.data.controller.AuthenticationDriver;
import neu.provl.pomodoro.fragment.authenticate.LoginFragment;
import neu.provl.pomodoro.fragment.authenticate.RegisterFragment;

public class LoginScreen extends AppCompatActivity {

    public static final String LOGIN_MODE = "Login", REGISTER_MODE = "Register";

    private ConstraintLayout root;

    @Getter
    private String mode = LOGIN_MODE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.login_screen);
        this.root = findViewById(R.id.login_screen);

        updateLayout();
    }

    public void updateLayout() {
        Fragment fragment = mode.equalsIgnoreCase(LOGIN_MODE) ? new LoginFragment(this)
                : new RegisterFragment(this);

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.authenticate_fragment, fragment);
        fragmentTransaction.commit();

        // This method is used to make sure that the commit action has finished to
        // make the transition among pages smoother.
        fragmentManager.executePendingTransactions();
    }

    public void setMode(String mode) {
        this.mode = mode;
        updateLayout();
    }
}