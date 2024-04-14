package neu.provl.pomodoro.concurrent; // Assuming util package for utility classes

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.ImageView;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;

import neu.provl.pomodoro.MainActivity;

/**
 * An util class used to load network image asynchronously and then inject the result
 * into the given {@link ImageView}.
 *
 * <br>Example:<br>
 * <code>
 *  new NetworkImageInjector(imageView).execute(user.getImageUrl());
 * </code>
 */
public class NetworkImageInjector {

    private final ImageView imageView;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    public NetworkImageInjector(ImageView imageView) {
        this.imageView = imageView;
    }

    public void execute(String imageUrl) {
        executorService.execute(() -> {
            HttpURLConnection connection = null;
            InputStream inputStream = null;

            try {
                URL url = new URL(imageUrl);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                connection.setConnectTimeout(3000);

                inputStream = connection.getInputStream();

                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                if(bitmap != null)
                    MainActivity.instance.runOnUiThread(() -> {
                        imageView.setImageBitmap(bitmap);
                    });
                else
                    Log.d("NetworkImageInjector", "Bitmap is null");

                inputStream.close();
                connection.disconnect();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if(inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (Exception _) {
                    }
                }

                if(connection != null)
                    connection.disconnect();

                System.gc();
            }
        });
    }
}