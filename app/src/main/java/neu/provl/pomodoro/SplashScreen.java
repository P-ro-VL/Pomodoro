package neu.provl.pomodoro;

import android.content.Intent;
import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import lombok.Getter;
import neu.provl.pomodoro.data.controller.DatabaseDriver;
import neu.provl.pomodoro.data.controller.TranslationDriver;

public class SplashScreen extends AppCompatActivity {

    @Getter
    public static SplashScreen instance;

    public static String loadingState = "Preparing the application ...";

    public static void setLoadingState(int stringId) {
        loadingState = getInstance().getResources().getString(stringId);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        instance = this;

        setContentView(R.layout.activity_splash_screen);
        FrameLayout root = findViewById(R.id.splash_screen);

        TextView loadingStatus = root.findViewById(R.id.loading_status);

        new Thread(() -> {
           while(!loadingState.equalsIgnoreCase("finished")) {
               runOnUiThread(() -> {
                   loadingStatus.setText(loadingState);
               });
           }

           runOnUiThread(() -> {
               Intent intent = new Intent(this, LoginScreen.class);
               startActivity(intent);
               finish();
           });
        }).start();

        TranslationDriver.init();
        DatabaseDriver.init();

        loadingState = "finished";
    }
}