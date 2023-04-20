package com.sandhya.youtube.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.sandhya.youtube.R;
import com.sandhya.youtube.models.PlaylistModel;

import java.util.ArrayList;

public class PlaylistAdapter extends RecyclerView.Adapter<PlaylistAdapter.ViewHolder> {
    Context context;
    ArrayList<PlaylistModel> models;

    public PlaylistAdapter(Context context, ArrayList<PlaylistModel> models) {
        this.context = context;
        this.models = models;
    }

    @NonNull
    @Override
    public PlaylistAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.playlist_item,parent,false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull PlaylistAdapter.ViewHolder holder, int position) {
        PlaylistModel model = models.get(position);
        holder.txtPlaylist.setText(model.getPlaylist_name());
        holder.txtVideos.setText(model.getVideos());

    }


    @Override
    public int getItemCount() {
        return models.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtPlaylist,txtVideos;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtPlaylist = itemView.findViewById(R.id.txtPlaylist);
            txtVideos = itemView.findViewById(R.id.txtVideos);

        }
//        public void blind(final PlaylistModel model, final OnItemClickListener listener){
//            txtPlaylist.setText(model.getPlaylist_name());
//            txtVideos.setText("VIdeos "+model.getVideos());
//            itemView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    listener.onItemClick(model);
//                }
//            });
//        }
    }
//    public interface OnItemClickListener extends Adapter {
//        void onItemClick(PlaylistModel model);
//    }
}
