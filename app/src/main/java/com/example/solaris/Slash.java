package com.example.solaris;

import android.content.Intent;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Timer;
import java.util.TimerTask;

public class Slash extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slash);

        ImageView logoimage, appname;

        TimerTask tarea = new TimerTask() {
            @Override
            public void run() {
                Intent intent = new Intent(Slash.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        };
        Timer tiempo = new Timer();
        tiempo.schedule(tarea, 2000);

        //animaciones

        logoimage = findViewById(R.id.logoimage);
        appname = findViewById(R.id.appname);

        Animation animationUp = AnimationUtils.loadAnimation(this, R.anim.anim_up);
        Animation animationDown = AnimationUtils.loadAnimation(this, R.anim.anim_down);

        logoimage.setAnimation(animationUp);
        appname.setAnimation(animationDown);
    }
}