package neu.provl.pomodoro.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.renderer.YAxisRenderer;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import neu.provl.pomodoro.R;
import neu.provl.pomodoro.data.controller.AuthenticationDriver;
import neu.provl.pomodoro.data.pomodoro.PomodoroMethod;

public class StatisticsFragment extends Fragment {
    private LinearLayout root;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.root = (LinearLayout) inflater.inflate(R.layout.statistics_layout, null);

        BarChart studyingHourChart = root.findViewById(R.id.studying_hour_chart);
        BarDataSet barDataSet = new BarDataSet(initChartData(), getResources().getString(R.string.studying_hour));
        barDataSet.setForm(Legend.LegendForm.CIRCLE);
        barDataSet.setColor(ContextCompat.getColor(getContext(), R.color.primary));

        YAxis rightYAxis = studyingHourChart.getAxisRight();
        rightYAxis.setEnabled(false);

        XAxis xAxis = studyingHourChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

        studyingHourChart.getDescription().setEnabled(false);

        studyingHourChart.setData(new BarData(barDataSet));
        studyingHourChart.invalidate();

        initStatistics();

        return root;
    }

    void initStatistics() {
        TextView avgStudyingHour = root.findViewById(R.id.avg_studying_time);
        TextView avgReceivedCoin = root.findViewById(R.id.avg_received_coin);
        TextView favouriteMethod = root.findViewById(R.id.favourite_method);

        float avgSH = Math.round((
                AuthenticationDriver.currentUser.getStudyingSeconds().values().stream().collect(Collectors.averagingInt(x -> x))
                .floatValue() % 60)*10)/10f;
        int avgRC = AuthenticationDriver.currentUser.getAccumulatedCoins()
                .values().stream().collect(Collectors.averagingInt(x -> x))
                .intValue();
        Optional<Map.Entry<PomodoroMethod, Integer>> optional = AuthenticationDriver.currentUser.getMethodCounter()
                .entrySet().stream().max(Comparator.comparingInt(Map.Entry::getValue));
        PomodoroMethod pomodoroMethod = PomodoroMethod.TWENTY_FIVE_OUT_OF_FIVE;
        if(optional.isPresent()) {
            pomodoroMethod = optional.get().getKey();
        }

        avgStudyingHour.setText(avgSH + "");
        avgReceivedCoin.setText(avgRC + "");
        favouriteMethod.setText(pomodoroMethod.getDisplayName());
    }

    List<BarEntry> initChartData() {
        List<BarEntry> data = new ArrayList<>();
        List<Map .Entry<String, Integer>> entries = new ArrayList<>(
                AuthenticationDriver.currentUser.getStudyingSeconds().entrySet()
        );

        for(int i = 0; i < entries.size(); i++) {
            data.add(new BarEntry(i, Math.round((entries.get(i).getValue().floatValue() % 60)*10)/10f));
        }

        return data;
    }

}
