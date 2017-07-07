package com.example.tylerbwong.awaken.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.example.tylerbwong.awaken.R;

/**
 * @author Tyler Wong
 */
public class StartActivity extends AppCompatActivity {
    private Button mEnterButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        mEnterButton = (Button) findViewById(R.id.enter_button);

        mEnterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent introIntent = new Intent(StartActivity.this, IntroActivity.class);
                startActivity(introIntent);
            }
        });

        boolean logoTransition = getIntent().getBooleanExtra("logo_transition", false);
        if (logoTransition) {
            overridePendingTransition(R.anim.slow_transition, R.anim.slow_transition);
        }
    }

    @Override
    public void onBackPressed() {

    }
}
