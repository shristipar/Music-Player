

package com.example.sony.mixx;

import android.app.ListActivity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.media.session.MediaController;
import android.media.session.MediaSession;
import android.media.session.MediaSessionManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.v4.app.NotificationCompat;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;

import com.musixmatch.lyrics.MissingPluginException;
import com.musixmatch.lyrics.musiXmatchLyricsConnector;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends ListActivity {
    public List<String> item2 = null;
    private String root = "/storage/";
    public static String myPath;
    String songlist[] = new String[10000];
    File fpb[];
    int songlength =0;
    ImageButton sPlay;
    ImageButton sPause;
    String songname;
    static TextView sname;
    int pos;
    public int f_des=0;
    static NotificationManager mNotificationManager;
    Notification notification;
    static    int notificationID = 100;
    static NotificationCompat.Builder mBuilder;
    ImageButton imgbtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imgbtn = (ImageButton) findViewById(R.id.about1);
        imgbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), AboutIntent.class);
                startActivity(i);
            }

        });
        sPlay = (ImageButton) findViewById(R.id.btnPlay1);
        sname = (TextView) findViewById(R.id.songName);
        View panelView = findViewById(R.id.topPanel);
        sPause = (ImageButton) findViewById(R.id.btnPause1);
        sPause.setVisibility(View.INVISIBLE);

        SharedPreferences prefs = this.getSharedPreferences("myPrefsKey", Context.MODE_PRIVATE);
        String song = prefs.getString("key", null);
        pos = prefs.getInt("pos", 0);
        sPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Window.mediaPlayer.pause();
                Window.isPlayflag = 0;
                sPlay.setEnabled(true);
                sPause.setVisibility(View.INVISIBLE);
                sPlay.setVisibility(View.VISIBLE);
                sPause.setEnabled(false);
            }
        });
        myPath = song;
        sPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Window.mediaPlayer = MediaPlayer.create(getApplicationContext(), Uri.parse(myPath));
                Window.mediaPlayer.start();
                Window.isPlayflag = 1;
                sPlay.setVisibility(View.INVISIBLE);
                sPause.setVisibility(View.VISIBLE);
                sPause.setEnabled(true);
                sPlay.setEnabled(false);
            }
        });
        panelView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Window.isPlayflag == 0) {
                    Window.mediaPlayer = MediaPlayer.create(getApplicationContext(), Uri.parse(myPath));
                    Window.mediaPlayer.start();
                    Window.isPlayflag = 1;
                }
                Bundle bundle = new Bundle();
                bundle.putInt("Position", pos);
                bundle.putStringArray("songlist", songlist);
                bundle.putString("path", myPath);
                bundle.putInt("length",songlength);
                Intent i = new Intent(getApplicationContext(), Window.class);
                i.putExtras(bundle);
                startActivity(i);
            }
        });
        // Recursive listing of all ".mp3" files
        File file1 = null;
        File fp;
        File fpa[];
        fp = new File(root);
        fpa = fp.listFiles();
        for (int i = 0; i < fpa.length; i++) {
            file1 = fpa[i];
            if (file1.isDirectory() && file1.canRead()) {
                dir(file1.getPath());
            }
        }
        item2 = new ArrayList<String>();
        for (int i = 0; i < songlength; i++) {
            songname = songlist[i];
            int c = songname.lastIndexOf("/");
            songname = songname.substring(c + 1);
            item2.add(songname);
        }
        ArrayAdapter<String> fileList =
                new ArrayAdapter<String>(this, R.layout.row, item2);
        setListAdapter(fileList);

    }

    void dir(String k) {
        File file1 = null;
        File fp, fp1;
        int i, j, p;
        File fpa[];
        fp = new File(k);
        fpa = fp.listFiles();
        String fbc[] = new String[10000];
        int x = 0;
        for (i = 0; i < fpa.length; i++) {
            file1 = fpa[i];
            if (file1.isDirectory() && file1.canRead()) {
                fbc[x++] = file1.getPath();
            } else if (file1.getName().endsWith(".mp3")) {
                songlist[songlength++] = file1.getPath();
            }
        }
        for (p = 0; p < x; p++) {
            fp1 = new File(fbc[p]);
            fpb = fp1.listFiles();
            for (j = 0; j < fpb.length; j++) {
                file1 = fpb[j];
                if (file1.getName().endsWith(".mp3")) {
                    songlist[songlength++] = file1.getPath();
                }
                if (file1.isDirectory() && file1.canRead()) {
                    File fp2, fpd[];
                    fp2 = new File(file1.getPath());
                    fpd = fp2.listFiles();
                    for (int y = 0; y < fpd.length; y++)
                        if (fpd[y].canRead())
                            if (fpd[y].getName().endsWith(".mp3")) {
                                songlist[songlength++] = fpd[y].getPath();
                            }

                }
            }
        }

    }



    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        myPath = songlist[position];
        pos=position;
        if(Window.isPlayflag==1){
            Window.mediaPlayer.stop();
        }
        Window.mediaPlayer= MediaPlayer.create(this,Uri.parse(myPath));
        if(Window.isPlayflag==1)
        {
            try{
                Window.isPlayflag=0;
                Window. mediaPlayer.reset();
                Window.mediaPlayer.setDataSource(myPath);
                Window.mediaPlayer.prepare();
                int s = myPath.lastIndexOf("/");
             sname.setText(myPath.substring((s + 1), (myPath.length() - 4))); } catch(Exception e)
            {
                e.printStackTrace();
            }

        }
        if(Window.isPlayflag==0)
        {
            Window.mediaPlayer.start();
            Window.isPlayflag=1;
            sPlay.setVisibility(View.INVISIBLE);
            sPause.setVisibility(View.VISIBLE);
            sPause.setEnabled(true);
        }
        int s = myPath.lastIndexOf("/");
        sname.setText(myPath.substring((s + 1), (myPath.length() - 4)));
        Window.mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

            @Override
            public void onCompletion(MediaPlayer mp) {
                Window.mediaPlayer.stop();
                sPlay.setVisibility(View.VISIBLE);
                sPause.setVisibility(View.INVISIBLE);
                sPlay.setEnabled(true);
                sPause.setEnabled(false);
            }

        });
        SharedPreferences prefs = this.getSharedPreferences("myPrefsKey", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("key", myPath);
        editor.putInt("pos",pos);
        editor.commit();
        mBuilder.setContentText(sname.getText());
        mNotificationManager.notify(notificationID,mBuilder.build());

    }

    public void onResume() {
        super.onResume();
        f_des=0;
        try{
            Intent i=getIntent();
            Bundle bundle=i.getExtras();
            myPath=bundle.getString("myPath");
          }
        catch (Exception e){

        }
        if (Window.isPlayflag == 1){
            sPause.setEnabled(true);
            sPlay.setVisibility(View.INVISIBLE);
            sPause.setVisibility(View.VISIBLE);
            int s = myPath.lastIndexOf("/");
            sname.setText(myPath.substring((s + 1), (myPath.length() - 4)));
        }

        if(Window.isPlayflag==0){
            sPlay.setEnabled(true);
            sPlay.setVisibility(View.VISIBLE);
            sPause.setVisibility(View.INVISIBLE);
            try {
                int s = myPath.lastIndexOf("/");
                sname.setText(myPath.substring((s + 1), (myPath.length() - 4)));

            } catch (Exception e) {
                myPath = songlist[0];
                int s = myPath.lastIndexOf("/");
                sname.setText(myPath.substring((s + 1), (myPath.length() - 4)));
            }
            mNotificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            mBuilder =new NotificationCompat.Builder(this);
            mBuilder.setSmallIcon(R.drawable.launcher_notify);
            mBuilder.setContentTitle("#TapToRap");
            mBuilder.setContentText(sname.getText());
            mBuilder.setOngoing(true);
            Intent resultIntent = new Intent(this, MainActivity.class);
           // resultIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            //resultIntent.putExtra("myPath",myPath);
            TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
            stackBuilder.addParentStack(MainActivity.class);
            stackBuilder.addNextIntent(resultIntent);
            PendingIntent resultPendingIntent = PendingIntent.getActivity(this, notificationID,resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            mBuilder.setContentIntent(resultPendingIntent);
            notification= mBuilder.build();
            mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.notify(notificationID, notification);
        }
    }

    @Override
    protected void onStop(){
        super.onStop();
        SharedPreferences prefs = this.getSharedPreferences("myPrefsKey", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("key", myPath);
        editor.putInt("pos",pos);
        editor.commit();
    }

   @Override
    protected void onDestroy(){
        super.onDestroy();
        if(f_des==0)
            mNotificationManager.cancel(notificationID);
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
        f_des=1;
    }
}