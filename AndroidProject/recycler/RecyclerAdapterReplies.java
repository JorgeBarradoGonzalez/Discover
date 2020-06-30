package jb.dam2.discover.recycler;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import jb.dam2.discover.pojo.ReplyInfo;
import jb.dam2.discover.R;
import jb.dam2.discover.activities.ReplyActivity;

/*
   JB: RecyclerAdapterReplies Es el adapatador de las listas RecyclerView para los comentarios. Se encarga de establecer
   el tipo de tarjetas que se mostraran en la lista.
*/
public class RecyclerAdapterReplies extends RecyclerView.Adapter<RecyclerAdapterReplies.MyViewHolder> {

    private ArrayList<View> mDataset;
    private ReplyActivity replyActivity;


    public RecyclerAdapterReplies(ArrayList<View> myDataset,ReplyActivity replyActivity) {
        mDataset = myDataset;
        this.replyActivity=replyActivity;
    }


    /*
        JB: Invocado por el layout manager
     */
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent,
                                           int viewType) {
        //JB: Creacion de la vista
        LinearLayout v = (LinearLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_comment, parent, false);
        MyViewHolder vh = new MyViewHolder(v,replyActivity);
        return vh;
    }

    /*
        JB: onBindViewHolder() Reemplaza los contenidos de una vista. Invocado por el Layout Manager
     */
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
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
        private View commentContainer;
        private ReplyActivity replyActivity;
        public MyViewHolder(View v,ReplyActivity replyActivity) {
            super(v);
            commentContainer = (View) v.findViewById(R.id.commentContainer);
            this.replyActivity=replyActivity;
        }

        /*
            JB: assignData() define el comportamiento de cada item
         */
        public void assignData(int position) {
            ArrayList<ReplyInfo> repliesInfo = replyActivity.getRepliesInfo();
            ReplyInfo reply = repliesInfo.get(position);
            ((TextView) commentContainer.findViewById(R.id.replyUsername)).setText(reply.getMUsername());
            ((TextView) commentContainer.findViewById(R.id.replyText)).setText(reply.getMText());
        }
    }
}
