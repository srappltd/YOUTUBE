package com.sandhya.youtube.fragements;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sandhya.youtube.R;
import com.sandhya.youtube.activities.ChannelActivity;
import com.sandhya.youtube.adapter.ChannelTabAdapter;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


public class ChannelDeshboardFragment extends Fragment {



    public ChannelDeshboardFragment() {
        // Required empty public constructor
    }

    public static ChannelDeshboardFragment newInstance(){
        ChannelDeshboardFragment fragment = new ChannelDeshboardFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }


    TextView txtChannelName,txtChannelName2,txtDescription;
    CircleImageView imgProfile;
    TabLayout tabLayout;
    ViewPager viewPager;
    DatabaseReference reference;
    FirebaseAuth auth;
    FirebaseUser user;

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_channel_deshboard, container, false);
        txtChannelName = view.findViewById(R.id.txtChannelName);
        txtChannelName2 = view.findViewById(R.id.txtChannelName2);
        txtDescription = view.findViewById(R.id.txtDescription);
        imgProfile = view.findViewById(R.id.imgProfile);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        reference.child("Channels").child(user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot){
                if (snapshot.exists()) {
                    String name = snapshot.child("Channel Name").getValue().toString();
                    String description = snapshot.child("Description").getValue().toString();
                    String profile = snapshot.child("Profile_Picture").getValue().toString();
                    txtChannelName.setText(name);
                    txtChannelName2.setText(name);
                    txtDescription.setText(description);
                    Picasso.get().load(profile).placeholder(R.drawable.ic_account).into(imgProfile);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        tabLayout.addTab(tabLayout.newTab().setText("Home"));
        tabLayout.addTab(tabLayout.newTab().setText("Videos"));
        tabLayout.addTab(tabLayout.newTab().setText("Playlist"));
        tabLayout.addTab(tabLayout.newTab().setText("Subscription"));
        tabLayout.addTab(tabLayout.newTab().setText("About"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);


        final ChannelTabAdapter adapter = new ChannelTabAdapter(getActivity(), newInstance().getParentFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);

       viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        return view;
    }
}