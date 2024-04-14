package neu.provl.pomodoro.data;

import android.graphics.Color;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class AcademicRecord {

    private String subject;
    private String term;

    private double[] scores;

    public double toFinalScore(boolean isTen) {
        if(scores.length != 3) throw new IllegalStateException("The length of scores array must be 3");
        double ten = 0.1 * scores[0] + 0.4 * scores[1] + 0.5 * scores[2];
        return isTen ? Math.round(ten * 100) / 100.0 : from10to4();
    }

    public Color getColor() {
        double score = toFinalScore(true);
        if (score >= 8.5) {
            return Color.valueOf(0xffC51D1D);
        } else if (score >= 7.0) {
            return Color.valueOf(0xff1D56C5);
        } else if (score >= 5.5) {
            return Color.valueOf(0xffC5821D);
        } else if (score >= 4.5) {
            return Color.valueOf(0xff6AB457);
        } else {
            return Color.valueOf(0xffABABAB);
        }
    }

    public String toLetterScore() {
        double score = toFinalScore(true);
        if (score >= 9.0) {
            return "A+";
        } else if (score >= 8.5) {
            return "A";
        } else if (score >= 8.0) {
            return "B+";
        } else if (score >= 7.0) {
            return "B";
        } else if (score >= 6.5) {
            return "C+";
        } else if (score >= 5.5) {
            return "C";
        } else if (score >= 5.0) {
            return "D+";
        } else if (score >= 4.5) {
            return "D";
        } else if (score == -1.0) {
            return "";
        } else {
            return "F";
        }
    }

    private double from10to4() {
        switch (toLetterScore()) {
            case "A+":
            case "A":
                return 4.0;
            case "B+":
                return 3.5;
            case "B":
                return 3.0;
            case "C+":
                return 2.5;
            case "C":
                return 2.0;
            case "D+":
                return 1.5;
            case "D":
                return 1.0;
            default:
                return 0.0;
        }
    }
}
