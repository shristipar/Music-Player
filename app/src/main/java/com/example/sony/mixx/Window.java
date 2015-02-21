package com.example.sony.mixx;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.media.session.MediaController;
import android.media.session.MediaSession;
import android.media.session.MediaSessionManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.RemoteException;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.musixmatch.lyrics.MissingPluginException;
import com.musixmatch.lyrics.musiXmatchLyricsConnector;
import java.io.IOException;

public class Window  extends Activity implements SeekBar.OnSeekBarChangeListener {
    public TextView startTimeField,endTimeField;
    private Handler mhandler=new Handler();
    public static int isPlayflag=0;
    MediaMetadataRetriever metaRetriver=new MediaMetadataRetriever();
    public static MediaPlayer mediaPlayer;
    private double startTime = 0;
    private double finalTime;
   // public String path;
    public  byte art[];
    private SeekBar seekbar;
    int position;
    public  int songLength;
    public  static TextView songName;
    public TextView artistName;
    public Bitmap songImage;
    public ImageView album;
    private ImageButton replay,fButton,bButton,playButton,pauseButton;
    Double s2, s3;
    String songlist[] = new String[10000];
    private musiXmatchLyricsConnector mLyricsPlugin = null;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.window);
        startTimeField = (TextView) findViewById(R.id.textView1);
        endTimeField = (TextView) findViewById(R.id.textView2);
        seekbar = (SeekBar) findViewById(R.id.seekBar1);
        seekbar.setOnSeekBarChangeListener(this);
        playButton = (ImageButton) findViewById(R.id.btnPlay);
        replay = (ImageButton) findViewById(R.id.replay);
        replay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer.seekTo(0);
                mediaPlayer.start();
                pauseButton.setEnabled( true);
                pauseButton.setVisibility(View.VISIBLE);
                playButton.setVisibility(View.INVISIBLE);

            }
        });
        fButton = (ImageButton) findViewById(R.id.btnForward);
        bButton = (ImageButton) findViewById(R.id.btnRewind);
        fButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                forward(fButton);
            }
        });
        bButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                rewind(bButton);
            }
        });
        playButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                play(playButton);
            }
        });
        pauseButton = (ImageButton) findViewById(R.id.btnPause);
        pauseButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                pause(pauseButton);
            }
        });
        playButton.setVisibility(View.INVISIBLE);
        pauseButton.setVisibility(View.VISIBLE);
        pauseButton.setEnabled(true);
        album = (ImageView) findViewById(R.id.album);
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mediaPlayer.stop();
                playButton.setVisibility(View.VISIBLE);
                pauseButton.setVisibility(View.INVISIBLE);
                playButton.setEnabled(true);
                pauseButton.setEnabled(false);
                forward(fButton);

            }
        });
        mLyricsPlugin = new musiXmatchLyricsConnector(this);
        mLyricsPlugin.setLoadingMessage("Hang in there!!", "Fetching lyrics");

        findViewById(R.id.showLyrics).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    mLyricsPlugin.startLyricsActivity(artistName.getText().toString(), songName.getText().toString());
                } catch (MissingPluginException e) {
                    mLyricsPlugin.downloadLyricsPlugin();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        });
        updateProgressBar();
        timebar();
    }

    @Override
    public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) {
        timebar();
    }

    @Override
    public void onStartTrackingTouch(SeekBar arg0) {
        mhandler.removeCallbacks(mUpdateTimeTask);
        mhandler.removeCallbacks(mUpdateTimeTask);
        double t1=seekbar.getProgress();
        t1=(t1)*(mediaPlayer.getDuration());
        t1=t1/100;
        if(mediaPlayer.getCurrentPosition() != t1+10000)
        {
            t1=Math.ceil(t1);
            mediaPlayer.seekTo((int)t1);
        }
        timebar();
        updateProgressBar();
    }

    @Override
    public void onStopTrackingTouch(SeekBar arg0) {
        mhandler.removeCallbacks(mUpdateTimeTask);
        double t1=seekbar.getProgress();
        t1=(t1)*(mediaPlayer.getDuration());
        t1=t1/100;
        if(mediaPlayer.getCurrentPosition() != t1+10000)
        {
            t1=Math.ceil(t1);
            mediaPlayer.seekTo((int)t1);
        }
        timebar();
        updateProgressBar();
    }

    public void updateProgressBar()
    {
        mhandler.postDelayed(mUpdateTimeTask, 1);
    }

    private Runnable mUpdateTimeTask = new Runnable()
    {
        public void run()
        {
            if(mediaPlayer.isPlaying())
                timebar();
            int dur=mediaPlayer.getDuration();
            int cur=mediaPlayer.getCurrentPosition();
            int pr=(int)((cur*100)/dur);
            seekbar.setProgress(pr);
            mhandler.postDelayed(this, 1000);
        }
    };

    public void play(View view) {


         if (isPlayflag==2)
         {
            mediaPlayer.reset();
           try {
               mediaPlayer.setDataSource(this,Uri.parse(MainActivity.myPath));
               mediaPlayer.prepare();
               mediaPlayer.start();
               isPlayflag=1;
            } catch (IOException e) {
                e.printStackTrace();
            }
            }
        else if(isPlayflag==1)
         {
                 try{

                 isPlayflag=0;
                 mediaPlayer.reset();

                     mediaPlayer.setDataSource(MainActivity.myPath);
                 mediaPlayer.prepare();

             }
             catch(Exception e)
             {
                 e.printStackTrace();
             }
       }
        if(isPlayflag==0){
            mediaPlayer.start();
            isPlayflag=1;}
        metaRetriver.setDataSource(this,Uri.parse(MainActivity.myPath));
        try {
            art = metaRetriver.getEmbeddedPicture();
            songImage = BitmapFactory.decodeByteArray(art, 0, art.length);
            album.setImageBitmap(songImage);
        }
        catch (Exception e){
            album.setImageResource(R.drawable.album);
        }
        songName.setText(metaRetriver.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE));
        artistName.setText(metaRetriver.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST));
        startTime = 0;
        seekbar.setProgress((int) startTime);
        finalTime = mediaPlayer.getDuration();
        startTime = mediaPlayer.getCurrentPosition();
        playButton.setVisibility(View.INVISIBLE);
        pauseButton.setVisibility(View.VISIBLE);
        pauseButton.setEnabled(true);
        playButton.setEnabled(false);
        timebar();
        updateProgressBar();
        if(metaRetriver.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE)==null){
            int c = MainActivity.myPath.lastIndexOf("/");
            songName.setText(MainActivity.myPath.substring((c+1),(MainActivity.myPath.length()-4)));
        }

        String text=songName.getText().toString();
        MainActivity.sname.setText(text);
        MainActivity.mBuilder.setContentText(songName.getText());
        MainActivity.mNotificationManager.notify(MainActivity.notificationID,MainActivity.mBuilder.build());
    }


    @Override
    public void onBackPressed(){
        super.onBackPressed();
        viewList(replay);
    }

    public void pause(View view){
        mLyricsPlugin.doUnbindService();

        mediaPlayer.pause();
        isPlayflag=0;
        playButton.setVisibility(View.VISIBLE);
        pauseButton.setVisibility(View.INVISIBLE);
        pauseButton.setEnabled(false);
        playButton.setEnabled(true);
    }

    public void forward(View view){
        if (position==songLength-1){
position=-1  ;     }
        MainActivity.myPath=songlist[++position];
        isPlayflag=2;
        play(fButton);
    }
    public void timebar()
    {
        if(mediaPlayer.isPlaying())
        {
            startTime=(mediaPlayer.getCurrentPosition())/1000;
            s2=(double)(mediaPlayer.getCurrentPosition()/1000);
            s3=(double)(mediaPlayer.getDuration()/1000);
            endTimeField.setText((int)(s3/60)+" : "+(int)(s3%60));
            startTimeField.setText((int)(s2/60)+" : "+(int)(s2%60));
        }
    }
    public void rewind(View view){
       if(position==0){
         position=songLength;
        }

            MainActivity.myPath=songlist[--position];
        isPlayflag=2;
        play(bButton);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.action_settings) {
          return true;
        }
        return super.onOptionsItemSelected(item);
    }
    public void onResume()
    {
        mLyricsPlugin.doBindService();
        super.onResume();
        Bundle bundle = getIntent().getExtras();
        songlist=bundle.getStringArray("songlist");
        position=bundle.getInt("Position");
        songLength=bundle.getInt("length");
        metaRetriver.setDataSource(this, Uri.parse(MainActivity.myPath));
        try {
            art = metaRetriver.getEmbeddedPicture();
            songImage = BitmapFactory.decodeByteArray(art, 0, art.length);
            album.setImageBitmap(songImage);
        }
        catch (Exception e){
            album.setImageResource(R.drawable.album);
        }

        songName=(TextView)findViewById(R.id.songName);
        artistName=(TextView)findViewById(R.id.artistName);

        songName.setText(metaRetriver.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE));
        artistName.setText(metaRetriver.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST));
        if(metaRetriver.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE)==null){
            int c = MainActivity.myPath.lastIndexOf("/");
            songName.setText(MainActivity.myPath.substring((c+1),(MainActivity.myPath.length()-4)));
        }
    }

    public void viewList(View view){
        Intent i = new Intent(getApplicationContext(), MainActivity.class);
        i.putExtra("myPath",MainActivity.myPath);
        startActivity(i);
      }

    protected void onStop(){
        super.onStop();
       // MainActivity.myPath=path;
    }
}


