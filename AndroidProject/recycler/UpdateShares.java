package jb.dam2.discover.recycler;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;

import java.util.ArrayList;

import jb.dam2.discover.discoverUtilities.DiscoverUtilities;
import jb.dam2.discover.youtubeUtilities.YoutubeUIController;
import jb.dam2.discover.activities.MainActivity;
import jb.dam2.discover.activities.UserProfileActivity;
import jb.dam2.discover.pojo.ShareInfo;
import jb.dam2.discover.R;

/*
   JB: UpdateShares es el hilo que se encarga de gestionar todas las vistas en la tarjeta de una publicacion
   Segun la actividad origen de la lista se realizan unos procesos u otros
*/
public class UpdateShares extends Thread{

    private View mYoutubeCard;
    private int mPosition;
    private MainActivity mainActivity;
    private UserProfileActivity userProfileActivity;
    private String action;

    public UpdateShares(View youtubeCard,int position,MainActivity mainActivity){
        mYoutubeCard = youtubeCard;
        mPosition = position;
        this.mainActivity=mainActivity;
        action = "";
    }

    public UpdateShares(View youtubeCard, int position, UserProfileActivity userProfileActivity,String action){
        mYoutubeCard = youtubeCard;
        mPosition = position;
        this.userProfileActivity=userProfileActivity;
        mainActivity=null;
        this.action = action;
    }

    /*
   JB: run() gestiona todas las vistas dentro de la tarjeta de una publicacion
*/
    public void run(){
        YouTubePlayerView playerView = (YouTubePlayerView)mYoutubeCard.findViewById(R.id.youtube_player_view);

        ArrayList<ShareInfo> sharesInfo = null;
        ShareInfo shareInfo = null;
        LinearLayout shareContainer = (LinearLayout) playerView.getParent().getParent().getParent();

        if(mainActivity!=null){

            //JB: Definicion del video
            sharesInfo = mainActivity.getmSharesInfo();
            shareInfo = sharesInfo.get(mPosition);
            String videoId = shareInfo.getMShareVideoId();
            ((TextView)mYoutubeCard.findViewById(R.id.hiddenVideoId)).setText(videoId);

            //JB: Definicion del observador
            mainActivity.getLifecycle().addObserver(playerView);
            playerView.addFullScreenListener(new YoutubeUIController(mainActivity,playerView));
            playerView.getYouTubePlayerWhenReady(youTubePlayer ->youTubePlayer.cueVideo(videoId,0));

            //JB: Se averigua si la publicacion esta marcada como like por el usuario sesion
            String shareId = shareInfo.getMShareId();
            DiscoverUtilities.manageLike(mainActivity.getmSessionId(),shareId,shareContainer,mainActivity);

            //JB: Se establecen los textos de la interfaz de la tarjeta
            ShareInfo finalShareInfo = shareInfo;
            mainActivity.runOnUiThread(() -> {
                ((TextView) mYoutubeCard.findViewById(R.id.shareUsername)).setText(finalShareInfo.getMUsername());
                ((TextView)mYoutubeCard.findViewById(R.id.hiddenShareId)).setText(finalShareInfo.getMShareId());
                ((TextView)mYoutubeCard.findViewById(R.id.youtubeShareTitle)).setText(finalShareInfo.getMShareTitle());
                ((TextView)mYoutubeCard.findViewById(R.id.hiddenUsername)).setText(finalShareInfo.getMUsername());
                ((TextView)mYoutubeCard.findViewById(R.id.youtubeShareComment)).setText(finalShareInfo.getMShareComment());
                ((TextView)mYoutubeCard.findViewById(R.id.shareUsername)).setText(finalShareInfo.getMUsername());
            });
        }else{
            if(action.equals("shares")){
                sharesInfo = userProfileActivity.getmSharesInfo();
            }else{
                sharesInfo = userProfileActivity.getmLikesInfo();
            }

            //JB: Definicion del video
            shareInfo = sharesInfo.get(mPosition);
            String videoId = shareInfo.getMShareVideoId();
            ((TextView)mYoutubeCard.findViewById(R.id.hiddenVideoId)).setText(videoId);
            userProfileActivity.getLifecycle().addObserver(playerView);
            playerView.addFullScreenListener(new YoutubeUIController(userProfileActivity,playerView));
            playerView.getYouTubePlayerWhenReady(youTubePlayer ->youTubePlayer.cueVideo(videoId,0));

            //JB: Se averigua si la publicacion esta marcada como like por el usuario sesion
            String shareId = shareInfo.getMShareId();
            DiscoverUtilities.manageLike(userProfileActivity.getmSessionId(),shareId,shareContainer,userProfileActivity);

            //JB: Se establecen los textos de la interfaz de la tarjeta
            ShareInfo finalShareInfo = shareInfo;
            userProfileActivity.runOnUiThread(() -> {
                ((TextView) mYoutubeCard.findViewById(R.id.shareUsername)).setText(finalShareInfo.getMUsername());
                ((TextView)mYoutubeCard.findViewById(R.id.hiddenShareId)).setText(finalShareInfo.getMShareId());
                ((TextView)mYoutubeCard.findViewById(R.id.youtubeShareTitle)).setText(finalShareInfo.getMShareTitle());
                ((TextView)mYoutubeCard.findViewById(R.id.hiddenUsername)).setText(finalShareInfo.getMUsername());
                ((TextView)mYoutubeCard.findViewById(R.id.youtubeShareComment)).setText(finalShareInfo.getMShareComment());
                ((TextView)mYoutubeCard.findViewById(R.id.shareUsername)).setText(finalShareInfo.getMUsername());
            });
        }
    }
}