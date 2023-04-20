package com.sandhya.youtube.activities;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sandhya.youtube.R;
import com.sandhya.youtube.databinding.ActivityAccountBinding;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;

public class AccountActivity extends AppCompatActivity {

    ActivityAccountBinding binding;
    FirebaseAuth auth;
    FirebaseUser user;
    DatabaseReference reference;



    //profile picture and email
    String profile, email;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAccountBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

//        binding.txtChrash.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                throw new RuntimeException("Test Crash");
//            }
//        });
//        addContentView(binding.txtChrash, new ViewGroup.LayoutParams(
//                ViewGroup.LayoutParams.MATCH_PARENT,
//                ViewGroup.LayoutParams.WRAP_CONTENT));

        //Firebase Google Login
        initAuthUserRefence();
        initUserData();

        // your channel
        initYourAccount();

        //toolbar
        initToolBar();
    }

    //firebase google login step 1
    public void initAuthUserRefence() {
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference();
    }

    //firebase google login step 2
    public void initUserData() {
        reference.child("Users").child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String username = snapshot.child("username").getValue().toString();
                    email = snapshot.child("email").getValue().toString();
                    profile = snapshot.child("profile").getValue().toString();

                    binding.txtUsername.setText(username);
                    binding.txtEmail.setText(email);
                    binding.imgProfile.setImageURI(Uri.parse(profile));
                    Picasso.get().load(profile).placeholder(R.drawable.ic_account).into(binding.imgProfile);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(AccountActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    //toolbar
    public void initToolBar() {
        binding.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    //your account
    public void initYourAccount() {
        binding.txtYourChannel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reference.child("Channels").child(user.getUid()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            //binding.txtYourChannel.setText("Your channel");
                            //Toast.makeText(AccountActivity.this, "User have a channel", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(AccountActivity.this,ChannelActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                           // binding.txtYourChannel.setText("New channel create");
                            Dialog dialog = new Dialog(AccountActivity.this);
                            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                            dialog.setContentView(R.layout.create_channel_item);
                            dialog.setCancelable(true);
                            dialog.setCanceledOnTouchOutside(false);
                            dialog.show();

                            //item_layout
                            EditText edtChannelName, edtDescription;
                            Button btnCreateChannel;
                            edtChannelName = dialog.findViewById(R.id.edtChannelName);
                            edtDescription = dialog.findViewById(R.id.edtDescription);
                            btnCreateChannel = dialog.findViewById(R.id.btnCreateChannel);
                            btnCreateChannel.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    String channelname = edtChannelName.getText().toString();
                                    String description = edtDescription.getText().toString();
                                    if (channelname.isEmpty() || description.isEmpty()) {
                                        Toast.makeText(AccountActivity.this, "Fill required fields", Toast.LENGTH_SHORT).show();
                                    } else {
                                        //create new channel
                                        initNewChannelCreate(channelname, description, dialog);
                                    }
                                }
                            });
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(AccountActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    //create new channel
    public void initNewChannelCreate(String channelname, String description, Dialog dialog) {
        ProgressDialog progressDialog = new ProgressDialog(AccountActivity.this);
        progressDialog.setTitle("New Channel");
        progressDialog.setMessage("Creating...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        //store data
        String date = DateFormat.getDateInstance().format(new Date());
        String time = DateFormat.getTimeInstance().format(new Date());
        HashMap<String, Object> map = new HashMap<>();
        map.put("Email", email);
        map.put("Channel Name", channelname);
        map.put("Description", description);
        map.put("Joining Date", date);
        map.put("Joining Time", time);
        map.put("uid", user.getUid());
        map.put("Profile_Picture", profile);
        reference.child("Channels").child(user.getUid()).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    progressDialog.dismiss();
                    dialog.dismiss();
                    Toast.makeText(AccountActivity.this, channelname + " Channel has been created.", Toast.LENGTH_LONG).show();
                } else {
                    progressDialog.dismiss();
                    dialog.dismiss();
                    Toast.makeText(AccountActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}