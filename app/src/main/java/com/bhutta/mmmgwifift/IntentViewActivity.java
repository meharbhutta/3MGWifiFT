package com.bhutta.mmmgwifift;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.view.View;
import android.widget.ImageView;

public class IntentViewActivity extends AppCompatActivity {

    public static final String MAIN = "action_view";

    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intent_view);
        Intent intent = getIntent();
        imageView = ((ImageView)findViewById(R.id.appImageView));
        if (intent.getAction().equals(Intent.ACTION_VIEW)) {
            String path = intent.getData().getPath();
            if (path.equals("/openApp")) {
                findViewById(R.id.startStopServer).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (((SwitchCompat) view).isChecked()) {
                            view.setEnabled(false);
                            wifiIndicator((byte) 1);
                        }
                    }
                });
            } else if (path.equals("/runApp")) {
                ((SwitchCompat) findViewById(R.id.startStopServer)).setChecked(true);
                findViewById(R.id.startStopServer).setEnabled(false);
                wifiIndicator((byte) 1);
            }
        }
    }

    private void wifiIndicator(final byte check){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    IntentViewActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                            imageView.setImageResource(R.drawable.wifi_indicator1);
                        }
                    });
                    Thread.sleep(200);
                    IntentViewActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            imageView.setImageResource(R.drawable.wifi_indicator2);
                        }
                    });
                    Thread.sleep(200);
                    IntentViewActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                           imageView.setImageResource(R.drawable.wifi_indicator3);
                        }
                    });
                    Thread.sleep(200);
                    IntentViewActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            imageView.setImageResource(R.drawable.wifi_indicator4);
                        }
                    });
                    Thread.sleep(200);
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra(MAIN,check);
                    startActivity(intent);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
