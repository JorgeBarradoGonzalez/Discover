package jb.dam2.discover.recycler;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import jb.dam2.discover.R;
import jb.dam2.discover.activities.UserProfileActivity;

/*
   JB: RecyclerAdapterReplies Es el adapatador de las listas RecyclerView para las publicaciones y likes
   del perfil de un usuario. Se encarga de establecer el tipo de tarjetas que se mostraran en la lista.
*/
public class RecyclerAdapterSelf extends RecyclerView.Adapter<RecyclerAdapterSelf.MyViewHolder> {

    private ArrayList<View> mDataset;
    private UserProfileActivity userProfileActivity;
    private String action;

    public RecyclerAdapterSelf(ArrayList<View> myDataset,UserProfileActivity userProfileActivity,String action) {
        mDataset = myDataset;
        this.userProfileActivity=userProfileActivity;
        this.action=action;
    }


    /*
        JB: Invocado por el layout manager
     */
    @Override
    public RecyclerAdapterSelf.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                               int viewType) {
        // create a new view
        LinearLayout v = (LinearLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.youtube_card, parent, false);
        RecyclerAdapterSelf.MyViewHolder vh = new RecyclerAdapterSelf.MyViewHolder(v,userProfileActivity,action);
        return vh;
    }

    /*
        JB: onBindViewHolder() Reemplaza los contenidos de una vista. Invocado por el Layout Manager
     */
    @Override
    public void onBindViewHolder(RecyclerAdapterSelf.MyViewHolder holder, int position) {
        //JB: Recibe el elemento de la lista de datos en esta posicion
        //JB: Reemplaza los contenidos de la vista por ese elemento
        holder.assignData(position);
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    /*
        //JB: Proporciona una referencia a las vistas de cada item
     */
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        private View youtubeCard;
        private UserProfileActivity userProfileActivity;
        private String action;
        public MyViewHolder(View v,UserProfileActivity userProfileActivity,String action) {
            super(v);
            youtubeCard = (View) v.findViewById(R.id.youtubeCard);
            this.userProfileActivity=userProfileActivity;
            this.action = action;
        }

        /*
            JB: assignData() define el comportamiento de cada item
         */
        public void assignData(int position) {
            UpdateShares updateShares = new UpdateShares(youtubeCard,position,userProfileActivity,action);
            updateShares.start();
            //El thread hace:
            //YouTubePlayerView playerView = (YouTubePlayerView)youtubeCard.findViewById(R.id.youtube_player_view);
            //playerView.getYouTubePlayerWhenReady(youTubePlayer -> youTubePlayer.cueVideo(videos[position],0));
        }
    }

}
