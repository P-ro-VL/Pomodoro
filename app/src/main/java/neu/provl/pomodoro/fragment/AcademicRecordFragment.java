package neu.provl.pomodoro.fragment;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.google.android.flexbox.FlexboxLayout;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.mlkit.nl.translate.TranslateLanguage;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import neu.provl.pomodoro.R;
import neu.provl.pomodoro.components.AcademicRecordCard;
import neu.provl.pomodoro.components.Dropdown;
import neu.provl.pomodoro.components.FrameButton;
import neu.provl.pomodoro.components.navigation.NavigationBar;
import neu.provl.pomodoro.data.AcademicRecord;
import neu.provl.pomodoro.data.controller.AuthenticationDriver;
import neu.provl.pomodoro.data.controller.TranslationDriver;

public class AcademicRecordFragment extends Fragment {

    private LinearLayout root;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.root = (LinearLayout) inflater.inflate(
                R.layout.academic_record_layout, null
        );

        update();

        FrameButton addRecordBtn = root.findViewById(R.id.add_record);
        addRecordBtn.setOnClickListener((e) -> showAddRecordDialog());

        return root;
    }

    @SuppressLint("SetTextI18n")
    public void update() {
        LinearLayout scrollView = root.findViewById(R.id.record_scroll_view);
        scrollView.removeAllViews();
        initAcademicRecords().forEach((record) -> scrollView.addView(record));

        double _gpa = AuthenticationDriver.currentUser.getAcademicRecords()
                .stream().map(r -> r.toFinalScore(false))
                .collect(Collectors.averagingDouble(x -> (double) x));

        TextView gpa = root.findViewById(R.id.GPA);
        gpa.setText(Double.toString(_gpa));

        TextView degreeRank = root.findViewById(R.id.degree_rank);
        degreeRank.setText(getDegreeRank(_gpa));

        initPerformanceChart();
    }

    private String getDegreeRank(double gpa) {
        if(gpa >= 3.6) {
            return getResources().getString(R.string.excellent);
        } else if(gpa >= 3.2) {
            return getResources().getString(R.string.very_good);
        } else if(gpa >= 2.5) {
            return getResources().getString(R.string.good);
        } else {
            return getResources().getString(R.string.ordinary);
        }
    }

    void showAddRecordDialog() {
        int density = (int) getContext().getResources().getDisplayMetrics().density;

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
        AlertDialog dialog = dialogBuilder.create();
        LayoutInflater layoutInflater = getLayoutInflater();

        LinearLayout dialogView = (LinearLayout) layoutInflater.inflate(R.layout.add_record_layout, null);
        GradientDrawable background = new GradientDrawable();
        background.setCornerRadius(16 * density);
        background.setColor(ContextCompat.getColor(getContext(), R.color.white));
        dialogView.setBackground(background);

        FlexboxLayout semesterLayout = dialogView.findViewById(R.id.semester_select);

        String[] rawSemesters = {
                "1st term - 1st year",
                "2nd term - 1st year",
                "3rd term - 1st year",
                "1st term - 2st year",
                "2nd term - 2st year",
                "3rd term - 2st year",
                "1st term - 3st year",
                "2nd term - 3st year",
                "3rd term - 3st year",
                "1st term - 4st year",
                "2nd term - 4st year",
                "3rd term - 4st year"
        };

        List<Task<String>> translateTasks = new ArrayList<>();
        for(int i = 0; i < rawSemesters.length; i++) {
            translateTasks.add(TranslationDriver.translate(rawSemesters[i], TranslateLanguage.ENGLISH));
        }

        AtomicReference<Dropdown> dropdown = new AtomicReference<>();

        Tasks.whenAllSuccess(translateTasks)
                        .addOnSuccessListener((result) -> {
                            dropdown.set(new Dropdown(getContext(),
                                    result.stream().map(Object::toString).collect(Collectors.toList())));
                            semesterLayout.addView(dropdown.get());
                            semesterLayout.invalidate();
                        });

        FrameButton addBtn = dialogView.findViewById(R.id.add_record);
        addBtn.setOnClickListener((e) -> {
            EditText subjectNameInput = dialogView.findViewById(R.id.subject_name_input);
            EditText tenPercentInput = dialogView.findViewById(R.id.ten_percent_score_input);
            EditText fourtyPercentInput = dialogView.findViewById(R.id.forty_percent_score_input);
            EditText fiftyPercentInput = dialogView.findViewById(R.id.fifty_percent_score_input);

            String subject = subjectNameInput.getText().toString();
            double t = Double.parseDouble(tenPercentInput.getText().toString());
            double fo = Double.parseDouble(fourtyPercentInput.getText().toString());
            double fi = Double.parseDouble(fiftyPercentInput.getText().toString());

            AcademicRecord record = new AcademicRecord(subject, dropdown.get().getValue(),  new double[] {t, fo, fi});
            AuthenticationDriver.currentUser.getAcademicRecords().add(record);

            update();

            dialog.cancel();
        });

        dialog.setView(dialogView);
        dialog.show();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }

    List<View> initAcademicRecords() {
        List<View> records = new ArrayList<>();
        AuthenticationDriver.currentUser.getAcademicRecords().forEach((record) -> {
            records.add(new AcademicRecordCard(getContext(), record));
        });
        return records;
    }

    void initPerformanceChart() {
        LineChart lineChart = root.findViewById(R.id.performance_chart);

        YAxis rightYAxis = lineChart.getAxisRight();
        rightYAxis.setEnabled(false);

        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

        lineChart.setDescription(null);

        LineDataSet lineDataSet = new LineDataSet(initLineData(), getResources().getString(R.string.your_performance));
        lineDataSet.setColor(ContextCompat.getColor(getContext(), R.color.primary));

        lineChart.setData(new LineData(lineDataSet));
        lineChart.invalidate();
    }

    List<Entry> initLineData() {
        List<Entry> data = new ArrayList<>();
        Map<String, Double> gpa = AuthenticationDriver
                .currentUser
                .getAcademicRecords()
                .stream().collect(Collectors.groupingBy(AcademicRecord::getTerm, Collectors.averagingDouble(record -> record.toFinalScore(false))));
        List<Map.Entry<String, Double>> entries = new ArrayList<>(gpa.entrySet());
        int index = 0;
        for(int i = 0; i < gpa.entrySet().size(); i++){
            Map.Entry<String, Double> entry = entries.get(i);
            data.add(new Entry(index, entry.getValue().floatValue()));
            index++;
        }

        return data;
    }

}
