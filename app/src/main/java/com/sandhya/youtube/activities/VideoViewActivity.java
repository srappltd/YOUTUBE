package com.sandhya.youtube.activities;

import android.annotation.SuppressLint;
import android.content.pm.ActivityInfo;
import android.icu.text.ListFormatter;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.sandhya.youtube.R;
import com.sandhya.youtube.databinding.ActivityVideoViewBinding;


public class VideoViewActivity extends AppCompatActivity {

    ActivityVideoViewBinding binding;
    // creating a variable for our Database
    // Reference for Firebase.
    DatabaseReference databaseReference;

    //exoplayer 1
    String videoURL;
    boolean flag = false;
    ImageView btFullScreen;
    // creating a variable for exoplayer
    SimpleExoPlayer exoPlayer;
    SimpleExoPlayerView simpleExoPlayerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityVideoViewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        // below line is used to get reference for our database.
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Contents");
//        getVideoUrl();

        //bundle passing
        Bundle bundle = getIntent().getExtras();
        String video = bundle.getString("video");
        String title = bundle.getString("title");
        String date = bundle.getString("date");
        String view = bundle.getString("view");
        String logo = bundle.getString("video");
        String channelName = bundle.getString("channel_name");

        //set text and date
        binding.idExoPlayerView.setPlayer(exoPlayer);
        binding.txtTitle.setText(title);
        binding.txtDate.setText(date);

        //video view url find play 2
        BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        TrackSelector trackSelector = new DefaultTrackSelector(new AdaptiveTrackSelection.Factory(bandwidthMeter));
        exoPlayer = ExoPlayerFactory.newSimpleInstance(VideoViewActivity.this, trackSelector);
        Uri videouri = Uri.parse(video);
        DefaultHttpDataSourceFactory dataSourceFactory = new DefaultHttpDataSourceFactory("exoplayer_video");
        ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();
        MediaSource mediaSource = new ExtractorMediaSource(videouri, dataSourceFactory, extractorsFactory, null, null);
        binding.idExoPlayerView.setPlayer(exoPlayer);
        exoPlayer.prepare(mediaSource);
        binding.idExoPlayerView.setKeepScreenOn(true);
        exoPlayer.setPlayWhenReady(true);

        //simpleExoPlayerView id find 3
        simpleExoPlayerView = findViewById(R.id.idExoPlayerView);

        //fullscreen button 4
        btFullScreen = findViewById(R.id.btFullscreen);
        btFullScreen = simpleExoPlayerView.findViewById(R.id.btFullscreen);
        btFullScreen.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("UseCompatLoadingForDrawables")
            @Override
            public void onClick(View view) {
                if (flag) {
                    flag = false;
                    btFullScreen.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_fullscreen_24));
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                } else {
                    flag = true;
                    btFullScreen.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_fullscreen_exit_24));
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                    getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
                }
            }
        });

        //progressbar landscape mode 5
        exoPlayer.addListener(new Player.EventListener() {
            @Override
            public void onTimelineChanged(Timeline timeline, Object manifest, int reason) {
            }
            @Override
            public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {
            }
            @Override
            public void onLoadingChanged(boolean isLoading) {
            }
            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                if (playbackState == Player.STATE_BUFFERING) {
                    binding.progressBar.setVisibility(View.VISIBLE);
                } else if (playbackState == Player.STATE_READY) {
                    binding.progressBar.setVisibility(View.GONE);
                }
            }
            @Override
            public void onRepeatModeChanged(int repeatMode) {
            }
            @Override
            public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {

            }

            @Override
            public void onPlayerError(ExoPlaybackException error) {

            }

            @Override
            public void onPositionDiscontinuity(int reason) {
            }

            @Override
            public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {
            }

            @Override
            public void onSeekProcessed() {
            }
        });
    }
    //video pause 6
    @Override
    protected void onPause() {
        super.onPause();
        exoPlayer.setPlayWhenReady(false);
//        super.onBackPressed();
    }
    //video back press 7
    @Override
    public void onBackPressed() {
        if (videoURL == null) {
            exoPlayer.setPlayWhenReady(false);
            super.onBackPressed();
        }
    }
    //video restart 8
    @Override
    protected void onRestart() {
        super.onRestart();
        exoPlayer.setPlayWhenReady(true);
    }
}

    //        DatabaseReference db = FirebaseDatabase.getInstance().getReference().child("Channels");
//        db.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                if (snapshot.exists()) {
//                    String name = snapshot.child("Channel Name").getValue().toString();
//                    String description = snapshot.child("Description").getValue().toString();
//                    String profile = snapshot.child("Profile_Picture").getValue().toString();
//
//                    String videos = snapshot.child("video_url").getValue().toString();
//
//                    initializeExoplayerView(videoUrl);
//
////                    binding.txtChannelName.setText(name);
////                    binding.txtChannelName2.setText(name);
////                    binding.txtDescription.setText(description);
//                   // Picasso.get().load(profile).placeholder(R.drawable.ic_account).into(binding.imgProfile);
//
////                    exoPlayer = ExoPlayerFactory.newSimpleInstance(VideoViewActivity.this, trackSelector);
////                    binding.idExoPlayerView.setPlayer(videos);
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//                Toast.makeText(ChannelActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
//            }
//        });



//    private void getVideoUrl() {
//        databaseReference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                // this method is call to get the
//                // realtime updates in the data.
//                // this method is called when the
//                // data is changed in our Firebase console.
//                // below line is for getting the data
//                // from snapshot of our database.
//                String videoUrl = snapshot.child("video_url").getValue(String.class);
//                String title = snapshot.child("video_title").getValue(String.class);
//                binding.txtTitle.setText(title);
//                // after getting the value for our video url
//                // we are passing that value to our
//                // initialize exoplayer method to load our video
//                initializeExoplayerView(videoUrl);
//            }
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//                // calling on cancelled method when we receive
//                // any error or we are not able to get the data.
//                Toast.makeText(VideoViewActivity.this, "Fail to get video url.", Toast.LENGTH_SHORT).show();
//            }
//        });
//
//    }

//    public void getAllVideo(){
//
//        model = new ArrayList<>();
//        databaseReference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                if (snapshot.exists()){
//                    model.clear();
//                    for ( DataSnapshot dataSnapshot : snapshot.getChildren()){
//                        ContentModels models = dataSnapshot.getValue(ContentModels.class);
//                        model.add(models);
//                    }
//                    //shu
//                    Collections.shuffle(model);
//                    contentAdapter.notifyDataSetChanged();
//                }else {
////                    homeProgressBar.setVisibility(View.VISIBLE);
//                    Toast.makeText(getApplicationContext(), "No data found", Toast.LENGTH_SHORT).show();
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//                Toast.makeText(getApplicationContext(), ""+error.getMessage(), Toast.LENGTH_SHORT).show();
//            }
//        });
//    }


//    private void initializeExoplayerView(String videoUrl) {
//        try {
//            // bandwidthmeter is used for getting default bandwidth
//            BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
//            // track selector is used to navigate between video using a default seeker.
//            TrackSelector trackSelector = new DefaultTrackSelector(new AdaptiveTrackSelection.Factory(bandwidthMeter));
//
//            // we are adding our track selector to exoplayer.
//            exoPlayer = ExoPlayerFactory.newSimpleInstance(VideoViewActivity.this, trackSelector);
//
//            // we are parsing a video url and
//            // parsing its video uri.
//            Uri videouri = Uri.parse(videoUrl);
//
//            // we are creating a variable for data source
//            // factory and setting its user agent as 'exoplayer_view'
//            DefaultHttpDataSourceFactory dataSourceFactory = new DefaultHttpDataSourceFactory("exoplayer_video");
//
//            // we are creating a variable for extractor
//            // factory and setting it to default extractor factory.
//            ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();
//
//            // we are creating a media source with above variables
//            // and passing our event handler as null,
//            MediaSource mediaSource = new ExtractorMediaSource(videouri, dataSourceFactory, extractorsFactory, null, null);
//
//            // inside our exoplayer view
//            // we are setting our player
//            binding.idExoPlayerView.setPlayer(exoPlayer);
//
//            // we are preparing our exoplayer
//            // with media source.
//            exoPlayer.prepare(mediaSource);
//
//            // we are setting our exoplayer
//            // when it is ready.
//            exoPlayer.setPlayWhenReady(true);
//        } catch (Exception e) {
//            // below line is used for handling our errors.
//            Log.e("TAG", "Error : " + e.toString());
//        }
//
//    }

