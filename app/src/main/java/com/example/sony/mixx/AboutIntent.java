package com.example.sony.mixx;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.sony.mixx.R;

public class AboutIntent extends Activity{
    private ImageView imageViewRound;
    private TextView name;
    private TextView email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.abouttent);
        imageViewRound=(ImageView)findViewById(R.id.imageView_round);
        name=(TextView)findViewById(R.id.name);
        email=(TextView)findViewById(R.id.email);
        Bitmap icon = BitmapFactory.decodeResource(getResources(), R.drawable.abcd);
        imageViewRound.setImageBitmap(icon);
        zoomAnimation(imageViewRound);
        fadeAnimation(name);
        fadeAnimation(email);
    }

    private void zoomAnimation(View view) {
        Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_down);
        view.startAnimation(animation);
    }

    private void fadeAnimation(View view) {
        Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in);
        view.startAnimation(animation);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
