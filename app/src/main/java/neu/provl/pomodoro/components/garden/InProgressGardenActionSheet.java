package neu.provl.pomodoro.components.garden;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.google.android.flexbox.AlignItems;
import com.google.android.flexbox.FlexboxLayout;
import com.google.android.flexbox.JustifyContent;

import lombok.Getter;
import lombok.Setter;
import neu.provl.pomodoro.R;
import neu.provl.pomodoro.components.ProgressBar;
import neu.provl.pomodoro.concurrent.NetworkImageInjector;
import neu.provl.pomodoro.data.pomodoro.PomodoroPeriod;
import neu.provl.pomodoro.data.pomodoro.PomodoroPhaseType;

public class InProgressGardenActionSheet extends GardenActionSheet {

    private PomodoroPeriod period;

    @Getter
    private ProgressBar timeProgressBar;

    @Getter
    @Setter
    private boolean stop = false;

    public InProgressGardenActionSheet(@NonNull Context context, @NonNull PomodoroPeriod period) {
        super(context);

        this.period = period;
        setOrientation(LinearLayout.VERTICAL);

        update();
    }

    public void update() {
        removeAllViews();
        _setupMusicLayout();
        _setupMainCardLayout();
    }

    private void _setupMusicLayout() {
        FlexboxLayout layout = new FlexboxLayout(getContext());
        layout.setJustifyContent(JustifyContent.SPACE_BETWEEN);
        layout.setAlignItems(AlignItems.CENTER);
        FlexboxLayout.LayoutParams layoutParams = new FlexboxLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        layoutParams.bottomMargin = 12 * getDensity();
        layout.setLayoutParams(layoutParams);

        ImageView thumbnail = new ImageView(getContext());
        LayoutParams thumbnailLP = new LayoutParams(64 * getDensity(), 64 * getDensity());
        thumbnail.setLayoutParams(thumbnailLP);
        GradientDrawable thumbnailCorner = new GradientDrawable();
        thumbnailCorner.setCornerRadius(8 * getDensity());
        thumbnail.setBackground(thumbnailCorner);
        thumbnail.setClipToOutline(true);

        new NetworkImageInjector(thumbnail).execute(period.getBackgroundMusic().getThumbnailUrl());

        LinearLayout nowPlayingCard = new LinearLayout(getContext());
        LinearLayout.LayoutParams nowPlayingCardLP = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                64 * getDensity()
        );
        nowPlayingCardLP.setMarginStart(8 * getDensity());
        nowPlayingCardLP.setMarginEnd(8 * getDensity());
        nowPlayingCard.setLayoutParams(nowPlayingCardLP);
        nowPlayingCard.setOrientation(LinearLayout.VERTICAL);
        nowPlayingCard.setGravity(Gravity.CENTER);
        nowPlayingCard.setPadding(8 * getDensity(), 0, 0, 0);

        GradientDrawable nowPlayingCardBg = new GradientDrawable();
        nowPlayingCardBg.setCornerRadius(8 * getDensity());
        nowPlayingCardBg.setColor(ContextCompat.getColor(getContext(), R.color.white));
        nowPlayingCard.setBackground(nowPlayingCardBg);

        TextView nowPlayingText = new TextView(getContext());
        nowPlayingText.setText(getResources().getString(R.string.now_playing).toUpperCase());
        nowPlayingText.setTextSize(10);
        nowPlayingText.setTypeface(getResources().getFont(R.font.semi_bold));

        TextView songNameText = new TextView(getContext());
        songNameText.setText(period.getBackgroundMusic().getName() + " - " + period.getBackgroundMusic().getAuthor());
        songNameText.setTextSize(14);
        songNameText.setTypeface(getResources().getFont(R.font.thin));
        songNameText.setLines(1);
        songNameText.setEllipsize(TextUtils.TruncateAt.END);

        nowPlayingCard.addView(nowPlayingText);
        nowPlayingCard.addView(songNameText);

        FrameLayout pauseLayout = new FrameLayout(getContext());
        FrameLayout.LayoutParams pauseLayoutParams = new FrameLayout.LayoutParams(
                64 * getDensity(),
                64 * getDensity()
        );
        pauseLayout.setLayoutParams(pauseLayoutParams);

        GradientDrawable pauseIconBg = new GradientDrawable();
        pauseIconBg.setColor(ContextCompat.getColor(getContext(), R.color.white));
        pauseIconBg.setCornerRadius(8 * getDensity());

        pauseLayout.setBackground(pauseIconBg);

        ImageView pauseIcon = new ImageView(getContext());
        FrameLayout.LayoutParams pauseIconLP = new FrameLayout.LayoutParams(
                32 * getDensity(),
                32 * getDensity()
        );
        pauseIconLP.gravity = Gravity.CENTER;
        pauseIcon.setLayoutParams(pauseIconLP);
        pauseIcon.setImageResource(R.drawable.pause);

        pauseLayout.addView(pauseIcon);

        layout.addView(thumbnail);
        layout.addView(nowPlayingCard);
        layout.addView(pauseLayout);

        addView(layout);

        post(() -> {
           nowPlayingCard.getLayoutParams().width = layout.getMeasuredWidth() - (64 * 2 + 12 * 2) * getDensity();
           nowPlayingCard.setLayoutParams(nowPlayingCard.getLayoutParams());
        });
    }

    private void _setupMainCardLayout() {
        LinearLayout layout = new LinearLayout(getContext());
        layout.setOrientation(LinearLayout.VERTICAL);
        GradientDrawable layoutBackground = new GradientDrawable();
        layoutBackground.setCornerRadius(20 * getDensity());
        layoutBackground.setColor(ContextCompat.getColor(getContext(), R.color.white));
        layout.setBackground(layoutBackground);
        layout.setPadding(18 * getDensity(), 12 * getDensity(),
                18 * getDensity(), 12 * getDensity());

        FlexboxLayout titleSection = new FlexboxLayout(getContext());
        titleSection.setJustifyContent(JustifyContent.SPACE_BETWEEN);
        titleSection.setAlignItems(AlignItems.CENTER);

        TextView title = new TextView(getContext());
        int titleTextId = period.getCurrentPhase().getType() == PomodoroPhaseType.STUDYING ?
                R.string.studying_phase : R.string.break_phase;
        title.setText(titleTextId);
        title.setAllCaps(true);
        title.setTextSize(18);
        title.setTypeface(getResources().getFont(R.font.semi_bold));

        TextView stopStudying = new TextView(getContext());
        stopStudying.setText(R.string.stop_studying);
        stopStudying.setTextColor(ContextCompat.getColor(getContext(), R.color.destructive));
        stopStudying.setTextSize(12);
        stopStudying.setTypeface(getResources().getFont(R.font.regular));
        stopStudying.setOnClickListener((e) -> {
            setStop(true);
        });

        titleSection.addView(title);
        titleSection.addView(stopStudying);

        TextView quoteText = new TextView(getContext());
        quoteText.setText("\"" + period.getCurrentPhase().getQuote() + "\"");
        quoteText.setTextSize(14);
        quoteText.setTypeface(getResources().getFont(R.font.light));
        quoteText.setGravity(Gravity.CENTER);

        LinearLayout coinReceiveLayout = new LinearLayout(getContext());
        LinearLayout.LayoutParams coinLP = new LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        coinLP.bottomMargin = 8 * getDensity();
        coinReceiveLayout.setLayoutParams(coinLP);
        coinReceiveLayout.setGravity(Gravity.CENTER_VERTICAL);

        TextView receivedCoin1 = new TextView(getContext());
        receivedCoin1.setText(R.string.you_can_get_total);
        receivedCoin1.setTextSize(10);
        receivedCoin1.setTextColor(ContextCompat.getColor(getContext(), R.color.secondary));
        receivedCoin1.setTypeface(getResources().getFont(R.font.italic));

        ImageView leafIcon = new ImageView(getContext());
        LayoutParams leafIconLP = new LayoutParams(16 * getDensity(), 16 * getDensity());
        leafIcon.setLayoutParams(leafIconLP);
        leafIcon.setImageResource(R.drawable.leaf);

        TextView coinText = new TextView(getContext());
        coinText.setText(period.getCurrentPhase().getCoinToGet() + "");
        coinText.setTextSize(14);
        coinText.setTextColor(ContextCompat.getColor(getContext(), R.color.secondary));
        coinText.setTypeface(getResources().getFont(R.font.semi_bold));

        TextView receivedCoin2 = new TextView(getContext());
        receivedCoin2.setText(R.string.when_finishing_this_phase);
        receivedCoin2.setTextSize(10);
        receivedCoin2.setTextColor(ContextCompat.getColor(getContext(), R.color.secondary));
        receivedCoin2.setTypeface(getResources().getFont(R.font.italic));

        coinReceiveLayout.addView(receivedCoin1);
        coinReceiveLayout.addView(leafIcon);
        coinReceiveLayout.addView(coinText);
        coinReceiveLayout.addView(receivedCoin2);

        timeProgressBar = new ProgressBar(getContext());
        timeProgressBar.setProgressColorId(R.color.primary);
        timeProgressBar.update();
        timeProgressBar.setProgress(0);

        layout.addView(titleSection);
        layout.addView(quoteText);
        layout.addView(coinReceiveLayout);
        layout.addView(timeProgressBar);

        addView(layout);
    }

    @Override
    public GradientDrawable getBackground() {
        GradientDrawable background = new GradientDrawable();
        background.setColor(Color.TRANSPARENT);
        return background;
    }
}
