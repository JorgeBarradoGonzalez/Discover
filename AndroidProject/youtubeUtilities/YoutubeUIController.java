package jb.dam2.discover.youtubeUtilities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.YouTubePlayerFullScreenListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;

import jb.dam2.discover.R;
import jb.dam2.discover.activities.FullscreenVideoActivity;

/*
    JB:YoutubeUtilities Es una clase que implemente un Listener, para averiguar cuando se ha pulsado un boton
    del YoutubePlayerview
*/
public class YoutubeUIController implements YouTubePlayerFullScreenListener {

    private Activity mActivity;
    private YouTubePlayerView mPlayer;
    private String mVideoId;

    public YoutubeUIController(Activity activity, YouTubePlayerView mPlayer) {
        this.mActivity = activity;
        this.mPlayer = mPlayer;
        View rootView = (View)mPlayer.getParent().getParent().getParent();
        mVideoId = ((TextView)rootView.findViewById(R.id.hiddenVideoId)).getText().toString();
    }

    /*
    JB:onYouTubePlayerEnterFullScreen() obliga a la pantalla a entrar en modo Landscaper
*/
    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    public void onYouTubePlayerEnterFullScreen() {
        mPlayer.exitFullScreen();
        Intent intent = new Intent(mActivity, FullscreenVideoActivity.class);
        intent.putExtra("videoId",mVideoId);
        mActivity.startActivity(intent);
    }

    /*
    JB:onYouTubePlayerExitFullScreen() se activa al salir del modo pantalla completa
*/
    @Override
    public void onYouTubePlayerExitFullScreen() {

    }
}
