package com.example.simplepomodorotimer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

public class MainActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    private TextView heading;
    private TextView subText;
    private TextView timerMain;

    private boolean isTimerOn;
    private boolean isPausePause;
    private boolean isPaused;
    private boolean isStopped;
    private boolean isRelaxing;

    private Button buttonPause;
    private Button buttonStop;
    private ImageButton imageButton;
    private Button buttonSettings;

    private CountDownTimer countDownTimer;
    private CountDownTimer countDownTimer1;

    private long timeInMillis = 0;
    private long timeRemaining = 0;
    private long defaultInterval;
    private long defaultPause;
    private long pauseInMillis;
    private long pauseRemaining;

    private MediaPlayer mediaPlayer;
    private Vibrator vibrator;

    SharedPreferences sharedPreferences;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        heading = findViewById(R.id.heading);
        subText = findViewById(R.id.subtext);
        timerMain = findViewById(R.id.time);
        imageButton = findViewById(R.id.image_button);
        buttonSettings = findViewById(R.id.button_settings);

        Typeface w_font = Typeface.createFromAsset(getAssets(), "fonts/wr.ttf");
        Typeface b_font = Typeface.createFromAsset(getAssets(), "fonts/lm.otf");
        Typeface helvetica = Typeface.createFromAsset(getAssets(), "fonts/9202.otf");
        heading.setTypeface(w_font);
        subText.setTypeface(b_font);
        timerMain.setTypeface(helvetica);

        buttonPause = findViewById(R.id.button_pause);
        buttonStop = findViewById(R.id.button_stop);
        buttonPause.setTypeface(b_font);
        buttonStop.setTypeface(b_font);

        buttonPause.setVisibility(View.INVISIBLE);
        buttonPause.setEnabled(false);
        buttonStop.setVisibility(View.INVISIBLE);
        buttonStop.setEnabled(false);

        isTimerOn = false;
        isPausePause = true;
        isStopped = false;
        isPaused = false;
        isRelaxing = false;

        buttonSettings.setEnabled(true);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        sharedPreferences.registerOnSharedPreferenceChangeListener(this);

        setIntervalPauseFromSharedPreferences(sharedPreferences);
        setIntervalPomodoroFromSharedPreferences(sharedPreferences);


    }

    public void onClickButton(View view) {

            if (!isRelaxing) {

                imageButton.setEnabled(false);
                subText.setText("I love you, keep going!");

                buttonPause.setEnabled(true);
                buttonPause.setVisibility(View.VISIBLE);
                buttonStop.setEnabled(true);
                buttonStop.setVisibility(View.VISIBLE);

                isTimerOn = true;
                isStopped = false;

                countDownTimer = new CountDownTimer(timeInMillis, 1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {

                        if (isPaused || isStopped) {

                            cancel();
                        } else {

                            updateTimer(millisUntilFinished);
                            timeRemaining = millisUntilFinished;
                        }
                    }

                    @Override
                    public void onFinish() {

                        MusicAdapter();

                        imageButton.setEnabled(true);
                        isRelaxing = true;
                        subText.setText("Relax! To end pause, press a rabbit again.");
                        countDownTimer1 = new CountDownTimer(pauseInMillis, 1000) {
                            @Override
                            public void onTick(long millisUntilFinished) {

                                updateTimer(millisUntilFinished);
                                pauseRemaining = millisUntilFinished;

                            }

                            @Override
                            public void onFinish() {
                                setIntervalPomodoroFromSharedPreferences(sharedPreferences);
                                updateTimer(timeInMillis);
                                imageButton.setEnabled(true);
                                subText.setText("Press a rabbit to focus on what really matters!");
                                MusicAdapter();
                                isRelaxing = false;

                            }
                        };
                        countDownTimer1.start();

                    /*Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content),
                            "Let me start a pause?", Snackbar.LENGTH_INDEFINITE)
                            .setAction("Yes", new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            Snackbar snackbar1 = Snackbar.make
                                    (findViewById(android.R.id.content), "Nice, relax!", BaseTransientBottomBar.LENGTH_SHORT);
                            snackbar1.show();
                        }
                    });
                    snackbar.show();*/

                        isStopped = true;
                        timeInMillis = 4000;
                        updateTimer(timeInMillis);
                        isPausePause = true;
                        isPaused = false;
                        buttonPause.setText("Pause");


                        buttonPause.setVisibility(View.INVISIBLE);
                        buttonPause.setEnabled(false);
                        buttonStop.setVisibility(View.INVISIBLE);
                        buttonStop.setEnabled(false);


                    }
                };
                countDownTimer.start();

            } else {

                countDownTimer1.cancel();
                isStopped = true;
                timeInMillis = 4000;
                updateTimer(timeInMillis);
                isPausePause = true;
                isPaused = false;
                buttonPause.setText("Pause");

                imageButton.setEnabled(true);
                isRelaxing = false;

                subText.setText("Press a rabbit to focus on what really matters!");


                setIntervalPomodoroFromSharedPreferences(sharedPreferences);
                buttonPause.setVisibility(View.INVISIBLE);
                buttonPause.setEnabled(false);
                buttonStop.setVisibility(View.INVISIBLE);
                buttonStop.setEnabled(false);
            }


    }


    private void updateTimer(long millisUntilFinished) {

        int minutes = (int) millisUntilFinished / 60 / 1000;
        int seconds = (int) millisUntilFinished / 1000 - (minutes * 60);

        String minutesString = "";
        String secondsString = "";

        if (minutes < 10) {

            minutesString = "0" + minutes;
        } else {

            minutesString = String.valueOf(minutes);
        }

        if (seconds < 10) {

            secondsString = "0" + seconds;
        } else {

            secondsString = String.valueOf(seconds);
        }

        timerMain.setText(minutesString + ":" + secondsString);
    }


    public void buttonPauseClick(View view) {

        if (isPausePause) {
            isPaused = true;
            buttonPause.setText("Resume");
            isPausePause = false;

        } else {

            buttonPause.setText("Pause");

            Log.d("MYDEBUG", String.valueOf(timeRemaining/1000));
            long millisInFuture = timeRemaining;
            long countDownInterval = 1000;
            isPaused = false;

            Log.d("MYDEBUG", String.valueOf(millisInFuture/1000));

            countDownTimer = new CountDownTimer(millisInFuture, countDownInterval) {
                @Override
                public void onTick(long millisUntilFinished) {

                    Log.d("MYDEBUG", "Eta vetka");
                    if (isPaused || isStopped) {

                        cancel();

                    }
                    else {

                        updateTimer(millisUntilFinished);
                        timeRemaining = millisUntilFinished;
                    }
                }

                public void onFinish() {

                    MusicAdapter();

                    imageButton.setEnabled(true);
                    isRelaxing = true;
                    subText.setText("Relax! To end pause, press a rabbit again.");
                    countDownTimer1 = new CountDownTimer(pauseInMillis, 1000) {
                        @Override
                        public void onTick(long millisUntilFinished) {

                            updateTimer(millisUntilFinished);
                            pauseRemaining = millisUntilFinished;

                        }

                        @Override
                        public void onFinish() {
                            setIntervalPomodoroFromSharedPreferences(sharedPreferences);
                            updateTimer(timeInMillis);
                            imageButton.setEnabled(true);
                            subText.setText("Press a rabbit to focus on what really matters!");
                            MusicAdapter();
                            isRelaxing = false;

                        }
                    };
                    countDownTimer1.start();

                    /*Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content),
                            "Let me start a pause?", Snackbar.LENGTH_INDEFINITE)
                            .setAction("Yes", new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            Snackbar snackbar1 = Snackbar.make
                                    (findViewById(android.R.id.content), "Nice, relax!", BaseTransientBottomBar.LENGTH_SHORT);
                            snackbar1.show();
                        }
                    });
                    snackbar.show();*/

                    isStopped = true;
                    timeInMillis = 4000;
                    updateTimer(timeInMillis);
                    isPausePause = true;
                    isPaused = false;
                    buttonPause.setText("Pause");


                    buttonPause.setVisibility(View.INVISIBLE);
                    buttonPause.setEnabled(false);
                    buttonStop.setVisibility(View.INVISIBLE);
                    buttonStop.setEnabled(false);


                }
            };
            countDownTimer.start();


            isPausePause = true;

        }
    }


    public void buttonStopClick(View view) {

        isStopped = true;
        timeInMillis = 4000;
        updateTimer(timeInMillis);
        isPausePause = true;
        isPaused = false;
        buttonPause.setText("Pause");

        imageButton.setEnabled(true);

        subText.setText("Press a rabbit to focus on what really matters!");


        setIntervalPomodoroFromSharedPreferences(sharedPreferences);
        buttonPause.setVisibility(View.INVISIBLE);
        buttonPause.setEnabled(false);
        buttonStop.setVisibility(View.INVISIBLE);
        buttonStop.setEnabled(false);
    }

    public void onClickSettings(View view) {


        Intent settingsIntent = new Intent(this, SettingsActivity.class);
        startActivity(settingsIntent);
    }

    public void MusicAdapter() {

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        if(sharedPreferences.getBoolean("enable_sound", true)) {

            mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.beyonddoubt);
            mediaPlayer.setVolume(1.0f, 1.0f);
            mediaPlayer.start();

        }

        if(sharedPreferences.getBoolean("enable_vibrate", true)) {

            vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            long mill = 1000;
            vibrator.vibrate(mill);
        }


    }

    private void setIntervalPomodoroFromSharedPreferences(SharedPreferences sharedPreferences) {

        defaultInterval = Integer.valueOf(sharedPreferences.getString("default_interval", "4"));
        long dIntervalMillis = defaultInterval*1000*60;
        updateTimer(dIntervalMillis);
        timeInMillis = dIntervalMillis;

    }

    private void setIntervalPauseFromSharedPreferences(SharedPreferences sharedPreferences) {

        defaultPause = Integer.valueOf(sharedPreferences.getString("default_pause", "5"));
        long dPauseMillis = defaultPause*1000*60;
        updateTimer(dPauseMillis);
        pauseInMillis = dPauseMillis;
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

        if (key.equals("default_interval")) {

            setIntervalPomodoroFromSharedPreferences(sharedPreferences);
        }

        if (key.equals("default_pause")) {

            setIntervalPauseFromSharedPreferences(sharedPreferences);
        }

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

        sharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
    }
}