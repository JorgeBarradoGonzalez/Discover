package jb.dam2.discover.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;

import jb.dam2.discover.R;

/*
    //JB: FullscreenVideoActivity es la actividad que obliga a la pantalla a estar en modo Landscape. Mostrando el video correspondiente
    a pantalla completa
*/
public class FullscreenVideoActivity extends AppCompatActivity {
    
    private String mVideoId;

    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullscreen_video);
        //JB: Se recoge el id del video para el que se ha creado la aplicacion
        Intent intent = getIntent();
        mVideoId = intent.getStringExtra("videoId");

        //JB: Se definen vista y se procede a cargar el video mediante YoutubePlayerView
        LinearLayout fullscreenLayout = findViewById(R.id.fullscreenLayout);
        YouTubePlayerView playerView = (YouTubePlayerView)findViewById(R.id.youtube_player_view_fullscreen);
        playerView.getYouTubePlayerWhenReady(y->y.loadVideo(mVideoId,0));

        //JB: Se define la actividad como getLifeCycleObserver para eliminar lo respectivo a la misma al eliminarse
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        //JB: Se define la actividad como getLifeCycleObserver para eliminar lo respectivo a la misma al eliminarse
        this.getLifecycle().addObserver(playerView);
    }

    /*
    //JB: onSupportNavigateUp() Vuelve a la actividad anterior y elimina esta
*/
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        finish();
        return true;
    }
}
