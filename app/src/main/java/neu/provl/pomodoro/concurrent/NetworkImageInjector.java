package neu.provl.pomodoro.concurrent;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * An util class used to load network image asynchronously and then inject the result
 * into the given {@link ImageView}.
 *
 * <br>Example:<br>
 * <code>
 *  new NetworkImageInjector(avatar).execute(user.getImageUrl());
 * </code>
 */
public class NetworkImageInjector extends AsyncTask<String, Void, Bitmap> {

    private ImageView imageView;

    public NetworkImageInjector(ImageView imageView) {
        this.imageView = imageView;
    }

    @Override
    protected Bitmap doInBackground(String... urls) {
        String imageUrl = urls[0];
        Bitmap bitmap = null;
        try {
            URL url = new URL(imageUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.connect();
            InputStream inputStream = connection.getInputStream();
            bitmap = BitmapFactory.decodeStream(inputStream);
            inputStream.close();
            connection.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        if (bitmap != null) {
            imageView.setImageBitmap(bitmap);
        } else {
            Log.e("NetworkImageInjector", "An error has occured while trying to set the bitmap. Bitmap is null.");
        }
    }
}
