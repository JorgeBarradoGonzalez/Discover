package jb.dam2.discover.recycler;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import jb.dam2.discover.activities.ArtistProfileActivity;
import jb.dam2.discover.activities.MainActivity;
import jb.dam2.discover.R;
import jb.dam2.discover.activities.ShareActivity;

/*
   JB: RecyclerAdapter Es el adapatador de las listas RecyclerView para las publicaciones. Se encarga de establecer
   el tipo de tarjetas que se mostraran en la lista.

   Dependiendo de si la lista es para la actividad MAIN,SHARE o ARTIST PROFILE, se definen diferentes
   tarjetas para la lista
*/
public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.MyViewHolder> {

    private ArrayList<View> mDataset;
    private MainActivity mainActivity;
    private ShareActivity shareActivity;
    private ArtistProfileActivity artistProfileActivity;

    public RecyclerAdapter(ArrayList<View> myDataset,MainActivity mainActivity) {
        mDataset = myDataset;
        this.mainActivity=mainActivity;
        this.shareActivity=null;
        this.artistProfileActivity = null;
    }

    public RecyclerAdapter(ArrayList<View> myDataset, ShareActivity shareActivity) {
        mDataset = myDataset;
        this.shareActivity=shareActivity;
        this.mainActivity=null;
        this.artistProfileActivity = null;
    }

    public RecyclerAdapter(ArrayList<View> myDataset, ArtistProfileActivity artistProfileActivity) {
        mDataset = myDataset;
        this.artistProfileActivity=artistProfileActivity;
        this.shareActivity=null;
        this.mainActivity=null;
    }

    /*
        JB: Invocado por el layout manager
     */
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent,
                                           int viewType) {
        //JB: Creacion de la vista
        LinearLayout v = null;
        MyViewHolder vh = null;
        if(mainActivity!=null){
            v = (LinearLayout) LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.youtube_card, parent, false);
            vh = new MyViewHolder(v,mainActivity);
        }else if(shareActivity!=null){
            v = (LinearLayout) LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.youtube_share, parent, false);
            vh = new MyViewHolder(v,shareActivity);
        }else if(artistProfileActivity != null){
            v = (LinearLayout) LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.youtube_share, parent, false);
            vh = new MyViewHolder(v,artistProfileActivity);
        }

        return vh;
    }

    /*
        JB: onBindViewHolder() Reemplaza los contenidos de una vista. Invocado por el Layout Manager
     */
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
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
        private View youtubeCard;
        private MainActivity mainActivity;
        private ShareActivity shareActivity;
        private ArtistProfileActivity artistProfileActivity;
        public MyViewHolder(View v,MainActivity mainActivity) {
            super(v);
            youtubeCard = (View) v.findViewById(R.id.youtubeCard);
            this.mainActivity=mainActivity;
            this.shareActivity=null;
            this.artistProfileActivity=null;
        }

        public MyViewHolder(View v,ShareActivity shareActivity) {
            super(v);
            youtubeCard = (View) v.findViewById(R.id.youtubeCardShare);
            this.shareActivity=shareActivity;
            this.mainActivity=null;
            this.artistProfileActivity=null;
        }

        public MyViewHolder(View v,ArtistProfileActivity artistProfileActivity) {
            super(v);
            youtubeCard = (View) v.findViewById(R.id.youtubeCardShare);
            this.artistProfileActivity=artistProfileActivity;
            this.mainActivity=null;
            this.shareActivity=null;
        }

        /*
            JB: assignData() define el comportamiento de cada item
         */
        public void assignData(int position) {
            if(mainActivity!=null){
                UpdateShares updateShares = new UpdateShares(youtubeCard,position,mainActivity);
                updateShares.start();
            }else if(shareActivity!=null){
                UpdateSearchResult updateSearchResult = new UpdateSearchResult(youtubeCard,position,shareActivity);
                updateSearchResult.start();
            }else if(artistProfileActivity!=null){
                UpdateSearchResult updateSearchResult = new UpdateSearchResult(youtubeCard,position,artistProfileActivity);
                updateSearchResult.start();
            }
        }
    }
}
