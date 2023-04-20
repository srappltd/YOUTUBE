package com.sandhya.youtube.activities;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.hootsuite.nachos.terminator.ChipTerminatorHandler;
import com.sandhya.youtube.R;
import com.sandhya.youtube.databinding.ActivityPublishBinding;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;

public class PublishActivity extends AppCompatActivity {
    ActivityPublishBinding binding;
    private static final int PICK_GALLERY = 102;
    String type;
    Uri videoUri;
    String videoUrl;
    MediaController controller;

    FirebaseUser user;
    DatabaseReference reference;
    int videosCount;
    StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPublishBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initPlaylist();

        initVideoUpload();

        user = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference().child("Contents");
        storageReference = FirebaseStorage.getInstance().getReference().child("Contents");

        binding.nachoTag.addChipTerminator(',', ChipTerminatorHandler.BEHAVIOR_CHIPIFY_ALL);

        binding.videoView.findViewById(R.id.videoView);
        controller = new MediaController(this);
        controller.setBackground(new ColorDrawable(Color.TRANSPARENT));
        Intent intent = getIntent();
        if (intent != null) {
            videoUri = intent.getData();
            binding.videoView.setVideoURI(videoUri);
            binding.videoView.setMediaController(controller);
            binding.videoView.pause();
            // binding.videoView.start();
        }

    }

    public void initVideoUpload() {
        binding.btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String title = binding.edtTitle.getText().toString();
                String description = binding.edtDescription.getText().toString();
                String tags = binding.nachoTag.getAllChips().toString().replace(",", "");

                if (title.isEmpty() || description.isEmpty() || tags.isEmpty()) {
                    Toast.makeText(PublishActivity.this, "fill all fields.", Toast.LENGTH_SHORT).show();
                } else {
                    uploadVideoToStorage(title, description, tags);
                }
            }
        });
    }

    private void uploadVideoToStorage(String title, String description, String tags) {

        final StorageReference sRef = storageReference.child(user.getEmail()).child
                (System.currentTimeMillis() + "," + getFileExtension(videoUri));
        sRef.putFile(videoUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                sRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        videoUrl = uri.toString();
                        //save data firebase
                        saveDataToFirebase(title, description, tags, videoUrl);
                    }
                });
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                double progress = 100.0 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount();
                binding.progressbar.setProgress((int) progress);
                binding.txtProgress.setText("Uploading: " + (int) progress + "%");
            }
        });
    }

    private void saveDataToFirebase(String title, String description, String tags, String videoUrl) {
        String currentDate = DateFormat.getDateInstance().format(new Date());
        String currentTime = DateFormat.getTimeInstance().format(new Date());
        String Id = reference.push().getKey();
        HashMap<String, Object> map = new HashMap<>();
        map.put("ID", Id);
        map.put("video_title", title);
        map.put("video_des", description);
        map.put("video_tag", tags);
        map.put("video_url", videoUrl);
        map.put("publisher", user.getUid());
        map.put("type","video");
        map.put("view",0);
        map.put("date", currentDate);
        map.put("time", currentTime);
        reference.child(Id).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {


                    binding.progressbar.setVisibility(View.GONE);
                    Toast.makeText(PublishActivity.this, "Video Uploaded", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(PublishActivity.this, MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
                    finish();
                    updateCount();
                } else {
                    binding.progressbar.setVisibility(View.GONE);
                    Toast.makeText(PublishActivity.this, "Failed to upload" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void updateCount() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Playlists");
        int update = videosCount + 1;
        HashMap<String, Object> map = new HashMap<>();
        map.put("videos", update);
        reference.child(user.getUid()).updateChildren(map);
    }

    private String getFileExtension(Uri uri) {
        ContentResolver resolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(resolver.getType(uri));

    }

    public void initPlaylist() {
        binding.txtChoosePlaylist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BottomSheetDialog dialog = new BottomSheetDialog(PublishActivity.this);
                dialog.setContentView(R.layout.playlist_dialog_item);
                dialog.setCancelable(true);
                dialog.setCanceledOnTouchOutside(true);
                dialog.show();

                EditText edtPlayList = dialog.findViewById(R.id.edtPlaylist);

//                models.add(new PlaylistModel("Nadasdf","23456","1"));
//                models.add(new PlaylistModel("Nadasdf","2345xss6","1"));
//                models.add(new PlaylistModel("Nadasdf","23sd456","1"));
//                models.add(new PlaylistModel("Nadasdf","2aef3456","1"));
//                models.add(new PlaylistModel("Nadasdf","asd","1"));
//                models.add(new PlaylistModel("Nadasdf","23asd456","1"));
//                models.add(new PlaylistModel("Nadasdf","e","23"));

//                models = new ArrayList<>();
//                adapter = new PlaylistAdapter(PublishActivity.this,models);
//                playlistRecycler = dialog.findViewById(R.id.playlistRecycler);
//                playlistRecycler.setLayoutManager(new LinearLayoutManager(PublishActivity.this));
//                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//                DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Playlists");
//                reference.child(user.getUid()).addValueEventListener(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//                        if (snapshot.exists()) {
//                            for (DataSnapshot snapshot1 : snapshot.getChildren()) {
//                                PlaylistModel model = snapshot1.getValue(PlaylistModel.class);
//                                models.add(model);
//                            }
//                            playlistRecycler.setAdapter(adapter);
//                            adapter.notifyDataSetChanged();
//                            if (models.isEmpty()){
//
//                            }else {
//
//                            }
//                        }
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError error) {
//
//                        Toast.makeText(PublishActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
//                        Log.e("Error", error.getMessage());
//                    }
//                });

                ProgressDialog progressDialog = new ProgressDialog(PublishActivity.this);
                progressDialog.setTitle("New Playlist Create...");
                progressDialog.setCancelable(true);
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.setMessage("Creating...");

                Button btnAddPlaylist = dialog.findViewById(R.id.btnAddPlaylist);
                btnAddPlaylist.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        progressDialog.show();
                        String newplaylist = edtPlayList.getText().toString();
                        if (newplaylist.isEmpty()) {
                            Toast.makeText(PublishActivity.this, "Enter Playlist Name", Toast.LENGTH_SHORT).show();
                        } else {
                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Playlists");
                            HashMap<String, Object> map = new HashMap<>();
                            map.put("playlist", newplaylist);
                            map.put("Email", user.getEmail());
                            map.put("video", videoUrl);
                            map.put("uid", user.getUid());
                            reference.child(user.getUid()).child(newplaylist).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        progressDialog.dismiss();
                                        dialog.dismiss();
                                        Toast.makeText(PublishActivity.this, newplaylist + " New Playlist Created!", Toast.LENGTH_SHORT).show();
                                    } else {
                                        progressDialog.dismiss();
                                        dialog.dismiss();
                                        Toast.makeText(PublishActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }

                    }
                });


            }
        });
    }

    public void checkUserPlaylistAlready(RecyclerView playlistRecycler) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Playlists");
        reference.child(user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    playlistRecycler.setVisibility(View.VISIBLE);

                } else {
                    playlistRecycler.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void initVideoView() {
        binding.videoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BottomSheetDialog dialog = new BottomSheetDialog(PublishActivity.this);
                dialog.setContentView(R.layout.camera_gallery_item);
                dialog.setCancelable(false);
                dialog.setCanceledOnTouchOutside(true);
                dialog.show();

                LinearLayout llGallery, llCamera;
                llCamera = dialog.findViewById(R.id.llCamera);
                llGallery = dialog.findViewById(R.id.llGallery);

                llGallery.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent iGallery = new Intent(Intent.ACTION_GET_CONTENT);
                        iGallery.setType("video/*");
                        startActivityForResult(Intent.createChooser(iGallery, "Select video"), PICK_GALLERY);
                    }
                });
            }
        });
    }
}