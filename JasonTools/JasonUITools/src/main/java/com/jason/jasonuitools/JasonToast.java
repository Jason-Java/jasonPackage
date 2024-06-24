package com.jason.jasonuitools;


import android.app.Application;
import android.os.Handler;
import android.os.Looper;
import android.speech.tts.TextToSpeech;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;


public class JasonToast {
    private Application context;
    private View view;
    private TextView textView;
    private Handler handler;

    private JasonToast() {
        handler = new Handler(Looper.getMainLooper());
    }

    private static class SingleJasonToast {
        private static JasonToast jasonToast = new JasonToast();
    }

    public static JasonToast getInstance() {
        return SingleJasonToast.jasonToast;
    }

    public void init(Application context) {
        this.context = context;
        this.view = LayoutInflater.from(context).inflate(R.layout.jason_toast, null, false);
        textView = view.findViewById(R.id.message);
    }

    public void makeError(String message, TextToSpeech textToSpeech) {
        speech(message, textToSpeech);
        makeError(message);
    }

    /**
     * 主线程toast提示框
     *
     * @param message
     * @return
     */
    public void makeError(String message) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                textView.setText(message);
                textView.setBackgroundResource(R.drawable.toast_read_bg);
                Toast toast = new Toast(context);
                toast.setView(view);
                toast.setGravity(Gravity.TOP, 0, 100);
                toast.setDuration(Toast.LENGTH_LONG);
                toast.show();
            }
        });
    }

    public void makeSuccess(String message, TextToSpeech textToSpeech) {
        speech(message, textToSpeech);
        makeSuccess(message);
    }

    /**
     * 主线程 正确提示框
     *
     * @param message
     * @return
     */
    public void makeSuccess(String message) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                textView.setText(message);
                textView.setBackgroundResource(R.drawable.toast_green_bg);
                Toast toast = new Toast(context);
                toast.setView(view);
                toast.setGravity(Gravity.TOP, 0, 100);
                toast.setDuration(Toast.LENGTH_LONG);
                toast.show();
            }
        });
    }

    private void speech(String message, TextToSpeech textToSpeech) {
        if (message == null || message.length() == 0) {
            return;
        }
        if (textToSpeech == null) {
            return;
        }
        textToSpeech.speak(message, 0, null);
    }

}
