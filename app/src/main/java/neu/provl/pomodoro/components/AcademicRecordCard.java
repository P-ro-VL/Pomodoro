package neu.provl.pomodoro.components;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.google.android.flexbox.AlignItems;
import com.google.android.flexbox.FlexboxLayout;
import com.google.android.flexbox.JustifyContent;

import neu.provl.pomodoro.R;
import neu.provl.pomodoro.data.AcademicRecord;

public class AcademicRecordCard extends FlexboxLayout {
    public AcademicRecordCard(@NonNull Context context, @NonNull AcademicRecord record) {
        super(context);

        int density = (int) getResources().getDisplayMetrics().density;

        setPadding(12 * density, 12 * density, 12 * density, 12 * density);
        setElevation(2 * density);

        GradientDrawable background = new GradientDrawable();
        background.setCornerRadius(14 * density);
        background.setColor(ContextCompat.getColor(context, R.color.white));
        setBackground(background);

        LinearLayout.LayoutParams backgroundLP = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                80 * density
        );
        backgroundLP.topMargin = 12 * density;
        setLayoutParams(backgroundLP);

        LinearLayout infoLayout = new LinearLayout(context);
        infoLayout.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams infoLP = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        infoLP.gravity = Gravity.CENTER;
        infoLayout.setLayoutParams(infoLP);

        TextView title = new TextView(context);
        title.setText(record.getSubject());
        title.setTextSize(14);
        title.setTypeface(getResources().getFont(R.font.medium));

        TextView semester = new TextView(context);
        semester.setText(record.getTerm());
        semester.setTextSize(10);
        semester.setTypeface(getResources().getFont(R.font.thin_italic));

        TextView score = new TextView(context);
        score.setText(record.toFinalScore(true) + " / " + record.toFinalScore(false));
        score.setTextSize(12);
        score.setTextColor(ContextCompat.getColor(context, R.color.primary));
        score.setTypeface(getResources().getFont(R.font.medium));

        infoLayout.addView(title);
        infoLayout.addView(semester);
        infoLayout.addView(score);

        TextView letterScore = new TextView(context);
        letterScore.setText(record.toLetterScore());
        letterScore.setTextSize(24);
        letterScore.setTextColor(record.getColor().toArgb());
        letterScore.setTypeface(getResources().getFont(R.font.medium));

        addView(infoLayout);
        addView(letterScore);

        setJustifyContent(JustifyContent.SPACE_BETWEEN);
        setAlignItems(AlignItems.CENTER);
    }
}
