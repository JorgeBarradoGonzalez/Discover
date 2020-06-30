package jb.dam2.discover.recycler;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import jb.dam2.discover.R;
import jb.dam2.discover.activities.AlertActivity;
import jb.dam2.discover.pojo.AlertInfo;

/*
   JB: RecyclerAdapterAlerts Es el adapatador de las listas RecyclerView para las alertas que se encarga de establecer
   el tipo de tarjetas que se mostraran en la lista.
*/
public class RecyclerAdapterAlerts extends RecyclerView.Adapter<RecyclerAdapterAlerts.MyViewHolder> {
    private ArrayList<View> mDataset;
    private AlertActivity alertActivity;

    public RecyclerAdapterAlerts(ArrayList<View> myDataset, AlertActivity alertActivity) {
        mDataset = myDataset;
        this.alertActivity = alertActivity;
    }


    /*
        JB: Invocado por el layout manager
     */
    @Override
    public RecyclerAdapterAlerts.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                                  int viewType) {
        //JB: Creacion de la vista
        LinearLayout v = (LinearLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_alert, parent, false);
        RecyclerAdapterAlerts.MyViewHolder vh = new RecyclerAdapterAlerts.MyViewHolder(v, alertActivity);
        return vh;
    }

    /*
        JB: onBindViewHolder() Reemplaza los contenidos de una vista. Invocado por el Layout Manager
     */
    @Override
    public void onBindViewHolder(RecyclerAdapterAlerts.MyViewHolder holder, int position) {
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
        private View alertContainer;
        AlertActivity alertActivity;
        public MyViewHolder(View v, AlertActivity alertActivity) {
            super(v);
            alertContainer = (View) v.findViewById(R.id.alertCardLayoutParent);
            this.alertActivity = alertActivity;
        }

        /*
            JB: assignData() define el comportamiento de cada item
         */
        public void assignData(int position) {
            ArrayList<AlertInfo> alertsInfo = alertActivity.getAlertsInfoArray();
            AlertInfo alertInfo = alertsInfo.get(position);
            ((TextView) alertContainer.findViewById(R.id.alertCardUserReplied)).setText(alertInfo.getMUsernameReplied());
            ((TextView) alertContainer.findViewById(R.id.alertCardUserMessage)).setText(R.string.userReplied);
            ((TextView) alertContainer.findViewById(R.id.alertCardShareTitle)).setText(alertInfo.getMShareTitle());
            ((TextView) alertContainer.findViewById(R.id.alertHiddenId)).setText(alertInfo.getMId());
            ((TextView) alertContainer.findViewById(R.id.alertHiddenShareId)).setText(alertInfo.getMShareId());
            ((TextView) alertContainer.findViewById(R.id.alertCardShareReplyText)).setText("\""+alertInfo.getMRepliedText()+"\"");
        }
    }
}
