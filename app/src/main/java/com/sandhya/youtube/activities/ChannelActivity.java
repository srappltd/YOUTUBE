package com.sandhya.youtube.activities;

import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sandhya.youtube.R;
import com.sandhya.youtube.adapter.ChannelTabAdapter;
import com.sandhya.youtube.databinding.ActivityChannelBinding;
import com.squareup.picasso.Picasso;

public class ChannelActivity extends AppCompatActivity {

    ActivityChannelBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChannelBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Channels");
        reference.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String name = snapshot.child("Channel Name").getValue().toString();
                    String description = snapshot.child("Description").getValue().toString();
                    String profile = snapshot.child("Profile_Picture").getValue().toString();
                    binding.txtChannelName.setText(name);
                    binding.txtChannelName2.setText(name);
                    binding.txtDescription.setText(description);
                    Picasso.get().load(profile).placeholder(R.drawable.ic_account).into(binding.imgProfile);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ChannelActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        TabLayout tableLayout;
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Home"));
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Videos"));
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Playlist"));
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Subscription"));
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("About"));
        binding.tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        final ChannelTabAdapter adapter = new ChannelTabAdapter(this, getSupportFragmentManager(), binding.tabLayout.getTabCount());
        binding.viewPager.setAdapter(adapter);

        binding.viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(binding.tabLayout));

        binding.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                binding.viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }
}