package com.github.rahatarmanahmed.cpv.example;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.github.rahatarmanahmed.cpv.CircularProgressView;
import com.github.rahatarmanahmed.cpv.CircularProgressViewAdapter;


public class MainActivity extends Activity {

    CircularProgressView progressView;
    Thread updateThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        progressView = (CircularProgressView) findViewById(R.id.progress_view);

        // Test the listener with logcat messages
        progressView.addListener(new CircularProgressViewAdapter() {
            @Override
            public void onProgressUpdate(float currentProgress) {
                Log.d("CPV", "onProgressUpdate: " + currentProgress);
            }

            @Override
            public void onProgressUpdateEnd(float currentProgress) {
                Log.d("CPV", "onProgressUpdateEnd: " + currentProgress);
            }

            @Override
            public void onAnimationReset() {
                Log.d("CPV", "onAnimationReset");
            }

            @Override
            public void onModeChanged(boolean isIndeterminate) {
                Log.d("CPV", "onModeChanged: " + (isIndeterminate ? "indeterminate" : "determinate"));
            }
        });

        // Test loading animation
        startAnimationThreadStuff(1000);
        final Button button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(progressView.isIndeterminate())
                {
                    progressView.setIndeterminate(false);
                    button.setText("Switch to indeterminate");
                }
                else
                {
                    progressView.setIndeterminate(true);
                    button.setText("Switch to determinate");
                }
                startAnimationThreadStuff(0);
            }
        });
    }

    private void startAnimationThreadStuff(long delay) {
        if (updateThread != null && updateThread.isAlive())
            updateThread.interrupt();
        // Start animation after a delay so there's no missed frames while the app loads up
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(!progressView.isIndeterminate()) {
                    progressView.setProgress(0f);
                    // Run thread to update progress every quarter second until full
                    updateThread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            while (progressView.getProgress() < progressView.getMaxProgress() && !Thread.interrupted()) {
                                // Must set progress in UI thread
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        progressView.setProgress(progressView.getProgress() + 10);
                                    }
                                });
                                SystemClock.sleep(250);
                            }
                        }
                    });
                    updateThread.start();
                }
                // Alias for resetAnimation, it's all the same
                progressView.startAnimation();
            }
        }, delay);
    }
}
