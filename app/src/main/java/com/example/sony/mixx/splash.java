package com.example.sony.mixx;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;


public class splash extends Activity {
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);
       ImageView launchr=  (ImageView)findViewById(R.id.splash);
        TextView tap=(TextView)findViewById(R.id.tapToRap);
        Bitmap icon = BitmapFactory.decodeResource(getResources(), R.drawable.launcher);
        fadeAnimation(tap);
        fadeAnimation(launchr);
        zoomAnimation(tap);
        zoomAnimation(launchr);
               Handler x=new Handler();
        x.postDelayed(new splashHandler(),2000);


    }
    class splashHandler implements Runnable{
        @Override
        public void run() {
            startActivity(new Intent(getApplicationContext(),MainActivity.class));
            splash.this.finish();
        }
    }
    private void zoomAnimation(View view) {
        Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.zoom_in);
        view.startAnimation(animation);
    }
    private void fadeAnimation(View view) {
        Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.zoom_out);
        view.startAnimation(animation);
    }

}
