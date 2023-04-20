package com.sandhya.youtube.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sandhya.youtube.R;
import com.sandhya.youtube.activities.VideoViewActivity;
import com.sandhya.youtube.models.ContentModels;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class ContentAdapter extends RecyclerView.Adapter<ContentAdapter.ViewHolder> {
    Context context;
    ArrayList<ContentModels> models;

    DatabaseReference reference;
    DatabaseReference databaseReference;
    FirebaseUser user;
    SimpleExoPlayer exoPlayer;

    public ContentAdapter(Context context, ArrayList<ContentModels> models) {
        this.context = context;
        this.models = models;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.video_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        ContentModels model = models.get(position);
        if (model != null) {
            Glide.with(context).load(model.getVideo_url()).into(holder.imgThumb);
            holder.txtView.setText(model.getViews());
            holder.txtDate.setText(model.getDate());
            holder.txtTitle.setText(model.getVideo_title());
            setDate(model.getPublisher(),holder.imgProfile,holder.txtChannelName);

            //video recycler video adapter 1
//            getVideoUrl();
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, VideoViewActivity.class);
                intent.putExtra("video",model.getVideo_url());
                intent.putExtra("title",model.getVideo_title());
                intent.putExtra("channel_name",model.getChannel_name());
                intent.putExtra("profile",model.getPublisher());
                intent.putExtra("date",model.getDate());
                intent.putExtra("view",model.getViews());
                context.startActivity(intent);
            }
        });
    }

    //video recycler video adapter 2
    private void getVideoUrl() {
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Contents");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String videoUrl = snapshot.child("video_url").getValue(String.class);
                String title = snapshot.child("video_title").getValue(String.class);
                //video recycler video adapter 3
//                initializeExoplayerView(videoUrl);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(context, "Fail to get video url.", Toast.LENGTH_SHORT).show();
            }
        });

    }

    //video recycler video adapter 3
    private void initializeExoplayerView(String videoUrl) {
        try {
            BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
            TrackSelector trackSelector = new DefaultTrackSelector(new AdaptiveTrackSelection.Factory(bandwidthMeter));
            exoPlayer = ExoPlayerFactory.newSimpleInstance(context, trackSelector);
            Uri videouri = Uri.parse(videoUrl);
            DefaultHttpDataSourceFactory dataSourceFactory = new DefaultHttpDataSourceFactory("exoplayer_video");
            ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();
            MediaSource mediaSource = new ExtractorMediaSource(videouri, dataSourceFactory, extractorsFactory, null, null);
            exoPlayer.prepare(mediaSource);
            exoPlayer.setPlayWhenReady(true);
        } catch (Exception e) {
            // below line is used for handling our errors.
            Log.e("TAG", "Error : " + e.toString());
        }

    }



    private void setDate(String publisher, CircleImageView imgProfile, TextView txtChannelName) {
        reference = FirebaseDatabase.getInstance().getReference().child("Channels");
        reference.child(publisher).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String channelName = snapshot.child("Channel Name").getValue().toString();
                    String logo = snapshot.child("Profile_Picture").getValue().toString();
                    txtChannelName.setText(channelName);
                    Glide.with(context).load(logo).placeholder(R.drawable.ic_account).into(imgProfile);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return models.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgThumb;
        CircleImageView imgProfile;
        TextView txtTitle, txtChannelName, txtView, txtDate;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imgThumb = itemView.findViewById(R.id.imgThumb);
            imgProfile = itemView.findViewById(R.id.imgProfile);
            txtTitle = itemView.findViewById(R.id.txtTitle);
            txtChannelName = itemView.findViewById(R.id.txtChannelName);
            txtView = itemView.findViewById(R.id.txtView);
            txtDate = itemView.findViewById(R.id.txtDate);
        }
    }
}
