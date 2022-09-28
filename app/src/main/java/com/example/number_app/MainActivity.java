package com.example.number_app;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "";
    // variables
    TextView a,b,check_answer, answer_status;
    TextToSpeech textToSpeech;
    String sentence;
    Button answer_btn;
    int a_value,b_value;
    protected static final int RESULT_SPEECH = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        a = (TextView) findViewById(R.id.a_variable);
        b = (TextView) findViewById(R.id.b_variable);
        answer_btn = (Button) findViewById(R.id.answer_btn);
        check_answer = (TextView) findViewById(R.id.check_answer);
        answer_status = (TextView) findViewById(R.id.answer_status);



        question_setter();
        question_verbal();
        answer_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-US");
                try {
                    startActivityForResult(intent, RESULT_SPEECH);
                    check_answer.setText("");
                }
                catch (ActivityNotFoundException e){
                    Toast.makeText(MainActivity.this, "Your device does not support speech to text", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case RESULT_SPEECH:
                if (resultCode == RESULT_OK && data != null) {
                    ArrayList<String> text = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    String ans = "Your answer is "+ text.get(0);
                    int answer;
                    try {
                        if(Objects.equals(text.get(0), "six")){
                            answer = 6;
                        }
                        else if (Objects.equals(text.get(0), "zero")){
                            answer = 0;
                        }
                        else{
                            answer = Integer.parseInt(text.get(0));
                        }

                    }
                    catch (Exception e){
                        e.printStackTrace();
                        answer = -1;
                    }
                    check_answer.setText(ans);
                    if(answer == (a_value*b_value)){
                            answer_status.setText("Correct Answer");
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                check_answer.setText("");
                                answer_status.setText("");
                                question_setter();
                                question_verbal();

                            }
                        }, 2000);
                    }
                    else {
                        answer_status.setText("Incorrect Answer");
                    }
                }
                break;
        }

    }
    public int random_number_generator(){
        Random random = new Random();
        int i = random.nextInt(10);
        return i;
    }

    public void question_setter(){
        a_value = random_number_generator();
        b_value = random_number_generator();
        String a_str_value = (String) Integer.toString(a_value);
        String b_str_value = (String) Integer.toString(b_value);
        a.setText(a_str_value);
        b.setText(b_str_value);
        sentence = "What is "+ a_str_value + "into" + b_str_value;
    }

    public void question_verbal(){
        textToSpeech = new TextToSpeech(this,
                new TextToSpeech.OnInitListener() {
                    @Override
                    public void onInit(int status) {
                        if (status != TextToSpeech.ERROR){
                            textToSpeech.setLanguage(Locale.ENGLISH);
                        }
                    }
                });

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                AudioManager mAudioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
                mAudioManager.setSpeakerphoneOn(true);
                int loudMax = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_NOTIFICATION);
                mAudioManager.setStreamVolume(AudioManager.STREAM_NOTIFICATION,loudMax, AudioManager.FLAG_PLAY_SOUND);
                textToSpeech.speak(sentence,TextToSpeech.QUEUE_FLUSH, null);
            }
        }, 200);
    }
}