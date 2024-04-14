package neu.provl.pomodoro.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

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
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import neu.provl.pomodoro.R;

public class StatisticsFragment extends Fragment {
    private LinearLayout root;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.root = (LinearLayout) inflater.inflate(R.layout.statistics_layout, null);

        BarChart studyingHourChart = root.findViewById(R.id.studying_hour_chart);
        BarDataSet barDataSet = new BarDataSet(initTestChartData(), getResources().getString(R.string.studying_hour));
        barDataSet.setForm(Legend.LegendForm.CIRCLE);
        barDataSet.setColor(ContextCompat.getColor(getContext(), R.color.primary));

        YAxis rightYAxis = studyingHourChart.getAxisRight();
        rightYAxis.setEnabled(false);

        XAxis xAxis = studyingHourChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

        studyingHourChart.getDescription().setEnabled(false);

        studyingHourChart.setData(new BarData(barDataSet));
        studyingHourChart.invalidate();

        return root;
    }

    List<BarEntry> initTestChartData() {
        List<BarEntry> dataset = new ArrayList<>();

        for(int i = 0; i < 5; i++) {
            dataset.add(new BarEntry(i, ThreadLocalRandom.current().nextInt(1, 10)));
        }

        return dataset;
    }
}
