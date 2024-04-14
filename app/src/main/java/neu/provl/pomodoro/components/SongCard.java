package neu.provl.pomodoro.components;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.text.Layout;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.android.flexbox.AlignItems;
import com.google.android.flexbox.FlexboxLayout;
import com.google.android.flexbox.JustifyContent;

import neu.provl.pomodoro.R;
import neu.provl.pomodoro.concurrent.NetworkImageInjector;
import neu.provl.pomodoro.data.Song;

public class SongCard extends FlexboxLayout {

    private Song song;
    private Runnable onSongSelect;

    public SongCard(@NonNull Context context, @NonNull Song song) {
        super(context);

        this.song = song;

        int density = (int) getResources().getDisplayMetrics().density;

        setJustifyContent(JustifyContent.SPACE_BETWEEN);
        setAlignItems(AlignItems.CENTER);

        LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.topMargin = 12 * density;
        setLayoutParams(layoutParams);

        ImageView thumbnail = new ImageView(context);
        LayoutParams thumbnailLP = new LayoutParams(
                32 * density,
                32 * density
        );
        thumbnail.setLayoutParams(thumbnailLP);
        GradientDrawable thumbnailCornerRadius = new GradientDrawable();
        thumbnailCornerRadius.setCornerRadius(4 * density);
        thumbnail.setBackground(thumbnailCornerRadius);
        thumbnail.setClipToOutline(true);

        new NetworkImageInjector(thumbnail).execute(song.getThumbnailUrl());

        TextView songInfo = new TextView(context);
        LayoutParams songInfoLP = new LayoutParams(
                LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT
        );
        songInfoLP.leftMargin = 12 * density;
        songInfo.setLayoutParams(songInfoLP);
        songInfo.setText(song.getAuthor() + " - " + song.getName());
        songInfo.setTextSize(14 );
        songInfo.setTypeface(getResources().getFont(R.font.light));

        LinearLayout infoLayout = new LinearLayout(context);
        infoLayout.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams infoLayoutLP = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        infoLayout.setLayoutParams(infoLayoutLP);

        infoLayout.addView(thumbnail);
        infoLayout.addView(songInfo);

        FrameButton selectButton = new FrameButton(context);
        selectButton.setText(getResources().getString(R.string.select).toUpperCase());
        selectButton.setTextSize(10);
        selectButton.update();
        selectButton.setOnClickListener((e) -> {
            if(onSongSelect != null) onSongSelect.run();
        });

        addView(infoLayout);
        addView(selectButton);
    }

    public Song getSong() {
        return song;
    }

    public Runnable getOnSongSelect() {
        return onSongSelect;
    }

    public void setOnSongSelect(Runnable onSongSelect) {
        this.onSongSelect = onSongSelect;
    }
}
