package neu.provl.pomodoro.components.garden;

import android.app.TimePickerDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Space;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;

import com.google.android.flexbox.FlexboxLayout;
import com.google.android.flexbox.JustifyContent;

import java.util.function.Consumer;

import lombok.Getter;
import neu.provl.pomodoro.MainActivity;
import neu.provl.pomodoro.R;
import neu.provl.pomodoro.components.FrameButton;
import neu.provl.pomodoro.components.SongCard;
import neu.provl.pomodoro.data.controller.DatabaseDriver;
import neu.provl.pomodoro.data.pomodoro.PomodoroMethod;
import neu.provl.pomodoro.data.Song;
import neu.provl.pomodoro.data.pomodoro.PomodoroPeriod;

public class PrepareGardenActionSheet extends GardenActionSheet {

    @Getter
    private PomodoroPeriod period = new PomodoroPeriod();

    private Consumer<PomodoroPeriod> onStartStudyingRequest;

    public PrepareGardenActionSheet(Context context) {
        super(context);

        setOrientation(LinearLayout.VERTICAL);
        period.setBackgroundMusic(DatabaseDriver.getInstance().getAvailableSongs().get(0));

        period.initializePhases();

        update();
    }

    public void showSelectMethodDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
        AlertDialog dialog = dialogBuilder.create();

        LinearLayout layout = (LinearLayout) MainActivity.getInstance().getLayoutInflater()
                .inflate(R.layout.pomodoro_method_select_layout, null);

        ImageView twentyFiveOutOfFiveMethod = layout.findViewById(R.id.twenty_five_out_of_five_method);
        ImageView fiftyOutOfTenMethod = layout.findViewById(R.id.fifty_out_of_ten_method);

        twentyFiveOutOfFiveMethod.setOnClickListener((e) -> {
            period.setMethod(PomodoroMethod.TWENTY_FIVE_OUT_OF_FIVE);
            _updateSelectMethodDialog(layout);

            update();
        });

        fiftyOutOfTenMethod.setOnClickListener((e) -> {
            period.setMethod(PomodoroMethod.FIFTY_OUT_OF_TEN);
            _updateSelectMethodDialog(layout);

            update();
        });

        _updateSelectMethodDialog(layout);

        FrameButton closeBtn = layout.findViewById(R.id.close_button);
        closeBtn.setOnClickListener((e) -> dialog.cancel());

        dialog.setView(layout);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }

    private void _updateSelectMethodDialog(LinearLayout layout) {
        ImageView twentyFiveOutOfFiveMethod = layout.findViewById(R.id.twenty_five_out_of_five_method);
        ImageView fiftyOutOfTenMethod = layout.findViewById(R.id.fifty_out_of_ten_method);

        if(period.getMethod() == PomodoroMethod.TWENTY_FIVE_OUT_OF_FIVE) {
            twentyFiveOutOfFiveMethod.setColorFilter(null);

            fiftyOutOfTenMethod.setBackground(null);
            fiftyOutOfTenMethod.setColorFilter(new ColorMatrixColorFilter(new ColorMatrix(){{setSaturation(0f);}}));
        } else {
            fiftyOutOfTenMethod.setColorFilter(null);

            twentyFiveOutOfFiveMethod.setBackground(null);
            twentyFiveOutOfFiveMethod.setColorFilter(new ColorMatrixColorFilter(new ColorMatrix(){{setSaturation(0f);}}));
        }

        period.initializePhases();
    }

    public void showTimeSelectDialog() {
        TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(), (view, hourOfDay, minute) -> {
            period.setStudyingHour(hourOfDay);
            period.setStudyingMinute(minute);

            period.initializePhases();

            update();
        }, 2, 0, true);
        timePickerDialog.show();
    }

    public void showMusicSelectDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
        AlertDialog dialog = dialogBuilder.create();

        LinearLayout layout = (LinearLayout) MainActivity.getInstance().getLayoutInflater()
                .inflate(R.layout.music_select_layout, null);

        LinearLayout listView = layout.findViewById(R.id.music_list_scroll_view);
        DatabaseDriver.getInstance().getAvailableSongs().forEach((song) -> {
            SongCard songCard = new SongCard(getContext(), song);
            songCard.setOnSongSelect(() -> {
                dialog.cancel();
                period.setBackgroundMusic(song);
                update();
            });
            listView.addView(songCard);
        });

        TextView closeBtn = layout.findViewById(R.id.close_button);
        closeBtn.setOnClickListener((e) -> dialog.cancel());

        dialog.setView(layout);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }

    public void update() {
        removeAllViews();

        period.initializePhases();

        Context context = getContext();

        TextView title = new TextView(context);
        title.setText(R.string.pomodoro_study);
        title.setTextSize(18);
        title.setTypeface(getResources().getFont(R.font.semi_bold));

        /* METHOD PREFERENCE */

        Space space1 = new Space(context);
        LayoutParams space1LP = new LayoutParams(0, 8 * getDensity());
        space1.setLayoutParams(space1LP);

        FlexboxLayout methodLayout = new FlexboxLayout(context);
        methodLayout.setJustifyContent(JustifyContent.SPACE_BETWEEN);

        TextView methodTitle = new TextView(context);
        methodTitle.setText(R.string.method);
        methodTitle.setTextSize(12);
        methodTitle.setTypeface(getResources().getFont(R.font.medium));

        LinearLayout methodSelectLayout = new LinearLayout(context);
        methodSelectLayout.setGravity(Gravity.CENTER);
        methodSelectLayout.setOnClickListener((e) -> {
            showSelectMethodDialog();
        });

        TextView methodText = new TextView(context);
        methodText.setText(period.getMethod().getDisplayName());
        methodText.setTextSize(12);
        methodText.setTypeface(getResources().getFont(R.font.light));

        ImageView methodMoreIcon = new ImageView(context);
        LinearLayout.LayoutParams methodMoreIconLP = new LayoutParams(16 * getDensity(),
                16 * getDensity());
        methodMoreIcon.setLayoutParams(methodMoreIconLP);
        methodMoreIcon.setImageResource(R.drawable.arrow_right);

        methodSelectLayout.addView(methodText);
        methodSelectLayout.addView(methodMoreIcon);

        methodLayout.addView(methodTitle);
        methodLayout.addView(methodSelectLayout);

        /* STUDYING TIME PREFERENCE */

        Space space2 = new Space(context);
        LayoutParams space2LP = new LayoutParams(0, 12 * getDensity());
        space2.setLayoutParams(space2LP);

        FlexboxLayout studyingTimeLayout = new FlexboxLayout(context);
        studyingTimeLayout.setJustifyContent(JustifyContent.SPACE_BETWEEN);

        TextView studyingTimeTitle = new TextView(context);
        studyingTimeTitle.setText(R.string.studying_time);
        studyingTimeTitle.setTextSize(12);
        studyingTimeTitle.setTypeface(getResources().getFont(R.font.medium));

        LinearLayout studyingTimeSelectLayout = new LinearLayout(context);
        studyingTimeSelectLayout.setGravity(Gravity.CENTER);

        TextView studyingTimeText = new TextView(context);
        studyingTimeText.setText(getResources().getString(R.string.hours_and_minutes)
                .replace("%hours%", period.getStudyingHour() + "")
                .replace("%minutes%", period.getStudyingMinute() + ""));
        studyingTimeText.setTextSize(12);
        studyingTimeText.setTypeface(getResources().getFont(R.font.light));

        TextView studyingTimeEditText = new TextView(context);
        studyingTimeEditText.setOnClickListener((e) -> showTimeSelectDialog());
        studyingTimeEditText.setPadding(4 * getDensity(), 0, 0, 0);
        studyingTimeEditText.setText(getResources().getString(R.string.edit));
        studyingTimeEditText.setTextSize(12);
        studyingTimeEditText.setTextColor(ContextCompat.getColor(getContext(), R.color.hyperlink));
        studyingTimeEditText.setTypeface(getResources().getFont(R.font.light));

        studyingTimeSelectLayout.addView(studyingTimeText);
        studyingTimeSelectLayout.addView(studyingTimeEditText);

        studyingTimeLayout.addView(studyingTimeTitle);
        studyingTimeLayout.addView(studyingTimeSelectLayout);

        /* BGM PREFERENCE */

        Space space3 = new Space(context);
        LayoutParams space3LP = new LayoutParams(0, 12 * getDensity());
        space3.setLayoutParams(space3LP);

        FlexboxLayout bgmLayout = new FlexboxLayout(context);
        bgmLayout.setJustifyContent(JustifyContent.SPACE_BETWEEN);

        TextView bgmTitle = new TextView(context);
        bgmTitle.setText(R.string.background_music);
        bgmTitle.setTextSize(12);
        bgmTitle.setTypeface(getResources().getFont(R.font.medium));

        LinearLayout bgmSelectLayout = new LinearLayout(context);
        bgmSelectLayout.setGravity(Gravity.CENTER);
        bgmSelectLayout.setOnClickListener((e) -> showMusicSelectDialog());

        TextView bgmText = new TextView(context);
        bgmText.setText(period.getBackgroundMusic().getName());
        bgmText.setTextSize(12);
        bgmText.setTypeface(getResources().getFont(R.font.light));

        ImageView bgmMoreIcon = new ImageView(context);
        LinearLayout.LayoutParams bgmMoreIconLP = new LayoutParams(16 * getDensity(),
                16 * getDensity());
        bgmMoreIcon.setLayoutParams(bgmMoreIconLP);
        bgmMoreIcon.setImageResource(R.drawable.arrow_right);

        bgmSelectLayout.addView(bgmText);
        bgmSelectLayout.addView(bgmMoreIcon);

        bgmLayout.addView(bgmTitle);
        bgmLayout.addView(bgmSelectLayout);

        /* COIN RECEIVE LAYOUT */

        Space space4 = new Space(context);
        LayoutParams space4LP = new LayoutParams(0, 12 * getDensity());
        space4.setLayoutParams(space4LP);

        LinearLayout coinReceiveLayout = new LinearLayout(context);
        coinReceiveLayout.setGravity(Gravity.CENTER_VERTICAL);

        TextView receivedCoin1 = new TextView(context);
        receivedCoin1.setText(R.string.you_can_get_total);
        receivedCoin1.setTextSize(10);
        receivedCoin1.setTypeface(getResources().getFont(R.font.thin_italic));

        ImageView leafIcon = new ImageView(context);
        LayoutParams leafIconLP = new LayoutParams(16 * getDensity(), 16 * getDensity());
        leafIcon.setLayoutParams(leafIconLP);
        leafIcon.setImageResource(R.drawable.leaf);

        TextView coinText = new TextView(context);
        coinText.setText(String.valueOf(period.getTotalCoin()));
        coinText.setTextSize(14);
        coinText.setTextColor(ContextCompat.getColor(getContext(), R.color.secondary));
        coinText.setTypeface(getResources().getFont(R.font.semi_bold));

        TextView receivedCoin2 = new TextView(context);
        receivedCoin2.setText(R.string.when_finishing_this_study_period);
        receivedCoin2.setTextSize(10);
        receivedCoin2.setTypeface(getResources().getFont(R.font.thin_italic));

        coinReceiveLayout.addView(receivedCoin1);
        coinReceiveLayout.addView(leafIcon);
        coinReceiveLayout.addView(coinText);
        coinReceiveLayout.addView(receivedCoin2);

        /* START STUDYING BUTTON */

        Space space5 = new Space(context);
        LayoutParams space5LP = new LayoutParams(0, 12 * getDensity());
        space5.setLayoutParams(space5LP);

        FrameButton startBtn = new FrameButton(context);
        startBtn.setText(getResources().getString(R.string.start_studying).toUpperCase());
        startBtn.setFilledBackground(true);
        startBtn.setHasLeading(true);
        startBtn.setLeadingId(R.drawable.play_circle);
        startBtn.setLeadingWidth(24);
        startBtn.setLeadingHeight(24);
        startBtn.update();
        startBtn.setOnClickListener((e) -> onStartStudyingRequest.accept(period));

        ((LayoutParams) startBtn.getLayoutParams()).gravity = Gravity.CENTER;

        addView(title);
        addView(space1);
        addView(methodLayout);
        addView(space2);
        addView(studyingTimeLayout);
        addView(space3);
        addView(bgmLayout);
        addView(space4);
        addView(coinReceiveLayout);
        addView(space5);
        addView(startBtn);
    }

    public void setOnStartStudyingRequest(Consumer<PomodoroPeriod> onStartStudyingRequest) {
        this.onStartStudyingRequest = onStartStudyingRequest;
    }

    @Override
    public GradientDrawable getBackground() {
        GradientDrawable background = new GradientDrawable();
        background.setColor(ContextCompat.getColor(getContext(), R.color.white));
        background.setCornerRadii(new float[] {20 * getDensity(), 20 * getDensity(),
                20 * getDensity(), 20 * getDensity(),
                0, 0, 0, 0});
        return background;
    }
}
