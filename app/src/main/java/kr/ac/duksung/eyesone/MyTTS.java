package kr.ac.duksung.eyesone;

import android.content.Context;
import android.speech.tts.TextToSpeech;

import java.util.Locale;

public class MyTTS extends TextToSpeech {

    public MyTTS(Context context, OnInitListener listener) {
        super(context, listener);

        this.setPitch(1.0f);    // 음성의 높낮이 설정
        this.setSpeechRate(1.0f);    // 음성의 속도 설정
        this.setLanguage(Locale.KOREAN);
    }

    public void speak(CharSequence text) {
        this.speak(text, TextToSpeech.QUEUE_FLUSH, null, "id1");
    }

    public void destroy() {
        this.stop();
        this.shutdown();
    }
}
