package jb.dam2.discover.youtubeUtilities;

import androidx.annotation.NonNull;

import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.utils.YouTubePlayerTracker;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;

/*
    JB:YoutubeUtilities Es una clase abstracta con metodos para acceder a los campos de un YoutubePlayerView
*/
public class YoutubeUtilities extends AbstractYouTubePlayerListener{
    private YouTubePlayerView mYouTubePlayerView;
    private String videoId;

    public YoutubeUtilities(YouTubePlayerView youTubePlayerView){
        this.mYouTubePlayerView=youTubePlayerView;
        videoId = "";
    }

    @Override
    public void onVideoId(@NonNull YouTubePlayer youTubePlayer, String videoId){
        this.videoId = videoId;
    }

    /*
    JB:getVideoId() Devuelve el id del video del YoutubePlayerView
*/
    public String getVideoId(){
        mYouTubePlayerView.getYouTubePlayerWhenReady(youTubePlayer -> {
            youTubePlayer.addListener(this);
            YouTubePlayerTracker tracker = new YouTubePlayerTracker();
            youTubePlayer.addListener(tracker);
            this.videoId = tracker.getVideoId();
        });
       return videoId;
    }
}
