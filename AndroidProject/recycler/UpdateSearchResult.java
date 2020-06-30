package jb.dam2.discover.recycler;

import android.os.AsyncTask;
import android.view.View;
import android.widget.TextView;

import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;

import java.util.ArrayList;

import jb.dam2.discover.youtubeUtilities.GetSearchVideos;
import jb.dam2.discover.R;
import jb.dam2.discover.youtubeUtilities.YoutubeUIController;
import jb.dam2.discover.activities.ArtistProfileActivity;
import jb.dam2.discover.activities.ShareActivity;

/*
   JB: UpdateSearchResult es el hilo que se encarga de gestionar todas las vistas en la tarjeta de una publicacion si se ha realizado una busqueda
   Segun la actividad origen de la lista se realizan unos procesos u otros
*/
public class UpdateSearchResult extends Thread{
    private View mYoutubeCard;
    private int mPosition;
    private ShareActivity shareActivity;
    private ArtistProfileActivity artistProfileActivity;

    public UpdateSearchResult(View youtubeCard,int position,ShareActivity shareActivity){
        mYoutubeCard = youtubeCard;
        mPosition = position;
        this.shareActivity=shareActivity;
        this.artistProfileActivity=null;
    }

    public UpdateSearchResult(View youtubeCard,int position,ArtistProfileActivity artistProfileActivity){
        mYoutubeCard = youtubeCard;
        mPosition = position;
        this.shareActivity=null;
        this.artistProfileActivity=artistProfileActivity;
    }

    /*
   JB: run() gestiona todas las vistas dentro de la tarjeta de una publicacion
*/
    public void run(){
        YouTubePlayerView playerView = (YouTubePlayerView)mYoutubeCard.findViewById(R.id.youtube_player_view);

        if(shareActivity!=null){
            //JB: Definicion del video
            ArrayList<String> videos = shareActivity.getVideoSearchResult();
            String videoId = videos.get(mPosition);
            ((TextView)mYoutubeCard.findViewById(R.id.hiddenVideoId)).setText(videoId);
            playerView.getYouTubePlayerWhenReady(youTubePlayer -> youTubePlayer.cueVideo(videoId,0));

            //JB: Marcado como observer, para controlar las actividades en ejecucion en caso de que se cierre la actividad
            shareActivity.getLifecycle().addObserver(playerView);

            //JB: Se indica un escuchador parra el boton full screen
            playerView.addFullScreenListener(new YoutubeUIController(shareActivity,playerView));

            //JB: Se llama un hilo para establecer el titulo de la publicacion
            callUpdateTitle(videoId);

        }else if(artistProfileActivity!=null){

            //JB: Definicion del video
            ArrayList<String> videos = artistProfileActivity.getVideoSearchResult();
            String videoId = videos.get(mPosition);
            ((TextView)mYoutubeCard.findViewById(R.id.hiddenVideoId)).setText(videoId);
            playerView.getYouTubePlayerWhenReady(youTubePlayer -> youTubePlayer.cueVideo(videoId,0));

            //JB: Marcado como observer, para controlar las actividades en ejecucion en caso de que se cierre la actividad
            artistProfileActivity.getLifecycle().addObserver(playerView);

            //JB: Se indica un escuchador parra el boton full screen
            playerView.addFullScreenListener(new YoutubeUIController(artistProfileActivity,playerView));
            mYoutubeCard.findViewById(R.id.youtubeCardBtnAcciones).setVisibility(View.GONE);

            //JB: Se llama un hilo para establecer el titulo de la publicacion mediante el id del video
            callUpdateTitle(videoId);
        }

    }

    /*
   JB: callUpdateTitle() realiza una busqueda del titulo de un video por su id
*/
    private void callUpdateTitle(String videoId){
        new GetTitle(videoId,(TextView)mYoutubeCard.findViewById(R.id.youtubeShareTitle)).execute();
        TextView hiddenId = mYoutubeCard.findViewById(R.id.hiddenVideoId);
        hiddenId.setText(videoId);
    }

    /*
   JB: GetTitle es una clase asincrona que realiza una busqueda del titulo de un video por su id y lo plasma en la interfaz
*/
    public class GetTitle extends AsyncTask<String,Integer,String> {
        private String videoId;
        private TextView textView;

        public GetTitle(String videoId, TextView textView){
            this.videoId=videoId;
            this.textView=textView;
        }

        @Override
        protected String doInBackground(String... videoIds) {
            GetSearchVideos getSearchVideos = new GetSearchVideos(videoId);
            String titulo = getSearchVideos.getTitleQuery().replace("&quot;","").replace("\"","").replace("\'","");
            if(shareActivity!=null){
                shareActivity.runOnUiThread(() -> textView.setText(titulo));
            }else if(artistProfileActivity!=null){
                artistProfileActivity.runOnUiThread(() -> textView.setText(titulo));
            }
            return "";
        }

    }
}
