package neu.provl.pomodoro.data.controller;

import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.LocaleList;

import com.google.android.gms.tasks.Task;
import com.google.mlkit.common.model.DownloadConditions;
import com.google.mlkit.nl.translate.TranslateLanguage;
import com.google.mlkit.nl.translate.Translation;
import com.google.mlkit.nl.translate.Translator;
import com.google.mlkit.nl.translate.TranslatorOptions;

import java.util.Locale;

import neu.provl.pomodoro.R;
import neu.provl.pomodoro.SplashScreen;

public class TranslationDriver {

    public static boolean init() {
        Resources resources = SplashScreen.getInstance().getResources();
        Configuration config = resources.getConfiguration();
        LocaleList locales = config.getLocales();

        for(int i = 0; i < locales.size(); i++)  {
           Locale locale = locales.get(i);

           String language = locale.getLanguage();

            TranslatorOptions options =
                    new TranslatorOptions.Builder()
                            .setSourceLanguage(TranslateLanguage.VIETNAMESE)
                            .setTargetLanguage(language)
                            .build();
            Translator translator = Translation.getClient(options);

            DownloadConditions conditions = new DownloadConditions.Builder()
                    .requireWifi()
                    .build();
            translator.downloadModelIfNeeded(conditions)
                    .addOnSuccessListener((event) -> {
                        SplashScreen.loadingState = resources.getString(R.string.splash_screen_download_lang_packet)
                                .replace("%lang%", language);
                    });

            translator.close();
        }

        return true;
    }

    public static Task<String> translate(String text, String sourceLanguage, String toLanguage) {
        TranslatorOptions options =
                new TranslatorOptions.Builder()
                        .setSourceLanguage(sourceLanguage)
                        .setTargetLanguage(toLanguage)
                        .build();
        Translator translator = Translation.getClient(options);

        return translator.translate(text);
    }

    public static Task<String> translate(String text, String sourceLanguage) {
        Configuration config = SplashScreen.getInstance().getResources().getConfiguration();

        return translate(text, sourceLanguage, config.getLocales().get(0).getLanguage());
    }

}
