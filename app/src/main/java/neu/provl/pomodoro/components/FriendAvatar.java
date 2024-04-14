package neu.provl.pomodoro.components;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ColorFilter;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import de.hdodenhof.circleimageview.CircleImageView;
import neu.provl.pomodoro.R;
import neu.provl.pomodoro.concurrent.NetworkImageInjector;
import neu.provl.pomodoro.data.User;

public class FriendAvatar extends FrameLayout {

    public FriendAvatar(@NonNull Context context, @NonNull User user, int width, int height) {
        super(context);

        assert width == height;

        int density = (int) context.getResources().getDisplayMetrics().density;

        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams layoutLP = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutLP.gravity = Gravity.CENTER;
        layoutLP.rightMargin = 12 * density;
        layout.setLayoutParams(layoutLP);

        FrameLayout avatarLayout = new FrameLayout(context);

        LayoutParams layoutParams = new LayoutParams(width * density, height * density);
        layoutParams.gravity = Gravity.CENTER;
        avatarLayout.setLayoutParams(layoutParams);

        GradientDrawable statusRing = new GradientDrawable();
        statusRing.setCornerRadius(1000f); // To make it circular
        statusRing.setColor(ContextCompat.getColor(context, user.isStudying() ? R.color.primary : R.color.secondary));

        avatarLayout.setBackground(statusRing);

        GradientDrawable blankSpaceBgr = new GradientDrawable();
        blankSpaceBgr.setCornerRadius(1000f);
        blankSpaceBgr.setColor(ContextCompat.getColor(context, R.color.white));

        FrameLayout blankSpace = new FrameLayout(context);
        LayoutParams blankSpaceLP = new LayoutParams(density*(width - 8), density*(height - 8));
        blankSpaceLP.gravity = Gravity.CENTER;
        blankSpace.setLayoutParams(blankSpaceLP);
        blankSpace.setBackground(blankSpaceBgr);

        CircleImageView avatar = new CircleImageView(context);
        LayoutParams avatarLP = new LayoutParams(density*(width - 16) , density*(height - 16) );
        avatarLP.gravity = Gravity.CENTER;
        avatar.setLayoutParams(avatarLP);
        if(!user.isStudying()) {
            avatar.setColorFilter(new ColorMatrixColorFilter(new ColorMatrix(){{setSaturation(0f);}}));
        }

        avatarLayout.addView(blankSpace);
        avatarLayout.addView(avatar);

        layout.addView(avatarLayout);

        TextView userName = new TextView(context);
        userName.setText(user.getName());
        userName.setTextColor(ContextCompat.getColor(context, R.color.typography));
        userName.setTextSize(12);
        userName.setTypeface(ResourcesCompat.getFont(context, R.font.light));
        LinearLayout.LayoutParams userNameLP = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        userNameLP.gravity = Gravity.CENTER;
        userName.setLayoutParams(userNameLP);

        layout.addView(userName);

        addView(layout);

        new NetworkImageInjector(avatar).execute(user.getImageUrl());
    }

}
