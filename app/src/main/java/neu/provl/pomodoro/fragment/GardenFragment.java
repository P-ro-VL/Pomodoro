package neu.provl.pomodoro.fragment;

import android.content.res.Configuration;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.flexbox.FlexboxLayout;

import java.time.Duration;

import neu.provl.pomodoro.MainActivity;
import neu.provl.pomodoro.R;
import neu.provl.pomodoro.components.CoinChip;
import neu.provl.pomodoro.components.garden.CollapsedGardenActionSheet;
import neu.provl.pomodoro.components.garden.GardenActionSheet;
import neu.provl.pomodoro.components.garden.InProgressGardenActionSheet;
import neu.provl.pomodoro.components.garden.PlantRegion;
import neu.provl.pomodoro.components.garden.PrepareGardenActionSheet;
import neu.provl.pomodoro.concurrent.PeriodicThread;
import neu.provl.pomodoro.data.controller.AuthenticationDriver;
import neu.provl.pomodoro.data.controller.NotificationDriver;
import neu.provl.pomodoro.data.pomodoro.PomodoroMethod;
import neu.provl.pomodoro.data.pomodoro.PomodoroPeriod;
import neu.provl.pomodoro.data.pomodoro.PomodoroPhase;
import neu.provl.pomodoro.data.pomodoro.PomodoroPhaseType;
import neu.provl.pomodoro.util.StringUtils;

public class GardenFragment extends Fragment {

    public static boolean isStudying = false;

    public static PomodoroPeriod LAST_PERIOD_DATA = null;

    private ViewGroup root;

    private GardenActionSheet actionSheet;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.root = (ViewGroup) inflater.inflate(R.layout.garden_layout, null);

        PlantRegion plantRegion = new PlantRegion(getContext(), null);
        plantRegion.setOnPlantSelect((plant) -> {
            if(actionSheet instanceof PrepareGardenActionSheet) {
                PomodoroPeriod period = ((PrepareGardenActionSheet) actionSheet).getPeriod();
                period.setPlantCoinExtra(plant.getCoinPerPhase());
                period.setPlantExpExtra(plant.getXpPerPhase());
                actionSheet.update();
            }
        });
        FrameLayout plantRegionLayout = this.root.findViewById(R.id.plant_region);
        plantRegionLayout.addView(plantRegion);

        if(MainActivity.getInstance().getResources().getConfiguration().orientation
         != Configuration.ORIENTATION_LANDSCAPE) {
            actionSheet = new CollapsedGardenActionSheet(getContext());
            ((CollapsedGardenActionSheet) actionSheet).setOnRequestExpand(() -> {
                this.actionSheet = new PrepareGardenActionSheet(getContext(), LAST_PERIOD_DATA);
                ((PrepareGardenActionSheet) this.actionSheet).setOnStartStudyingRequest(this::startStudying);

                updateActionSheet();
            });
        } else {
            this.actionSheet = new PrepareGardenActionSheet(getContext(), LAST_PERIOD_DATA);
            ((PrepareGardenActionSheet) this.actionSheet).setOnStartStudyingRequest(this::startStudying);
        }

        updateActionSheet();

        return root;
    }

    public void updateActionSheet() {
        FrameLayout actionSheetLayout = root.findViewById(R.id.garden_action_sheet);
        actionSheetLayout.removeAllViews();
        actionSheetLayout.addView(actionSheet);
    }

    private int clock = 0;
    public void startStudying(PomodoroPeriod period) {
        AuthenticationDriver.currentUser.increaseMethod(period.getMethod());

        period.nextPhase();
        clock = period.getCurrentPhase().getMinutes() * 60;

        this.actionSheet = new InProgressGardenActionSheet(getContext(), period);
        updateActionSheet();

        /* Start Adjust Components */
        TextView countingText = root.findViewById(R.id.countdown_clock);
        countingText.setVisibility(View.VISIBLE);

        if(MainActivity.getInstance().getResources().getConfiguration().orientation
                != Configuration.ORIENTATION_LANDSCAPE) {
            FlexboxLayout titleSection = root.findViewById(R.id.garden_title_section);
            titleSection.setVisibility(View.INVISIBLE);
        }

        FrameLayout actionSheetLayout = root.findViewById(R.id.garden_action_sheet);
        actionSheetLayout.setBackground(null);
        /* End Adjust Components */

        PeriodicThread periodicThread = new PeriodicThread(
        (thread) -> {
            AuthenticationDriver.currentUser.accumulateStudyingHour(1);

            InProgressGardenActionSheet as = (InProgressGardenActionSheet) actionSheet;

            if(as.isStop()) {
                _stopStudying();

                thread.setRunning(false);
                return;
            }

            MainActivity.getInstance().runOnUiThread(() -> {
                countingText.setText(StringUtils.toMMssFormat(clock));
            });

            if(clock == 0) {
                int coinReceive = period.getCurrentPhase().getCoinToGet();
                int expReceive = period.getCurrentPhase().getExpToGet();

                AuthenticationDriver.currentUser.addCoin(coinReceive);
                AuthenticationDriver.currentUser.addExperience(expReceive);
                AuthenticationDriver.currentUser.accumulateCoins(coinReceive);

                MainActivity.getInstance().runOnUiThread(() -> {
                    as.update();
                    CoinChip coinChip = root.findViewById(R.id.coin_chip);
                    coinChip.update();

                    NotificationDriver.pushNotification(MainActivity.getInstance(),
                            getString(R.string.finish_phase_noti_title),
                            getString(R.string.finish_phase_noti_content)
                                    .replace("%phase%", period.getCurrentPhase().getType()
                                            == PomodoroPhaseType.STUDYING ? getResources().getString(R.string.studying_phase)
                                            : getResources().getString(R.string.break_phase))
                                    .replace("%coin%", coinReceive + "")
                                    .replace("%exp%", expReceive + ""));
                });

                PomodoroPhase phase = period.nextPhase();
                if(phase == null) {
                    thread.setRunning(false);
                    return;
                }

                clock = phase.getMinutes() * 60;
            } else {
                clock--;

                double progress = (clock * 1.0) / (period.getCurrentPhase().getMinutes() * 60.0);
                MainActivity.getInstance().runOnUiThread(() -> {
                    as.getTimeProgressBar().setProgress(progress);
                });
            }
        }, Duration.ofSeconds(1));

        periodicThread.start();

        isStudying = true;
    }

    private void _stopStudying() {
        TextView countingText = root.findViewById(R.id.countdown_clock);
        FlexboxLayout titleSection = root.findViewById(R.id.garden_title_section);
        FrameLayout actionSheetLayout = root.findViewById(R.id.garden_action_sheet);

        MainActivity.getInstance().runOnUiThread(() -> {
            this.actionSheet = new PrepareGardenActionSheet(getContext(), LAST_PERIOD_DATA);
            ((PrepareGardenActionSheet) this.actionSheet).setOnStartStudyingRequest(this::startStudying);

            updateActionSheet();

            if(MainActivity.getInstance().getResources().getConfiguration().orientation
                    != Configuration.ORIENTATION_LANDSCAPE)
                countingText.setVisibility(View.INVISIBLE);
            titleSection.setVisibility(View.VISIBLE);

            actionSheetLayout.setBackground(ContextCompat.getDrawable(getContext(),
                    R.drawable.garden_action_sheet_background));
        });

        isStudying = false;
    }

}
