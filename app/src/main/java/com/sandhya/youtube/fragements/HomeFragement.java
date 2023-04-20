package com.sandhya.youtube.fragements;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sandhya.youtube.R;
import com.sandhya.youtube.adapter.ContentAdapter;
import com.sandhya.youtube.models.ContentModels;

import java.util.ArrayList;
import java.util.Collections;

public class HomeFragement extends Fragment {

    public HomeFragement(){

    }
    SwipeRefreshLayout swipeRefreshLayout;
    RecyclerView homeRecycler;
    ArrayList<ContentModels> models;
    ContentAdapter contentAdapter;
    DatabaseReference reference;
    ProgressBar homeProgressBar;
    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home_fragement, container, false);
        homeRecycler = view.findViewById(R.id.homeRecycler);
        homeRecycler.setHasFixedSize(true);
        homeProgressBar = view.findViewById(R.id.homeProgressBar);
        reference = FirebaseDatabase.getInstance().getReference().child("Contents");
        getAllVideo();
        swipeRefreshLayout = view.findViewById(R.id.swiper);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(false);
                models = new ArrayList<>();
                reference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()){
                            homeProgressBar.setVisibility(View.GONE);
                            models.clear();
                            for ( DataSnapshot dataSnapshot : snapshot.getChildren()){
                                ContentModels model = dataSnapshot.getValue(ContentModels.class);
                                models.add(model);
                            }
                            //shu
                            Collections.shuffle(models);
                            contentAdapter = new ContentAdapter(getActivity(),models);
                            homeRecycler.setAdapter(contentAdapter);
                            contentAdapter.notifyDataSetChanged();
                        }else {
                            homeProgressBar.setVisibility(View.VISIBLE);
                            Toast.makeText(getActivity(), "No data found", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(getActivity(), ""+error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });


        return view;
    }


    public void getAllVideo(){

        models = new ArrayList<>();
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    homeProgressBar.setVisibility(View.GONE);
                    models.clear();
                    for ( DataSnapshot dataSnapshot : snapshot.getChildren()){
                        ContentModels model = dataSnapshot.getValue(ContentModels.class);
                        models.add(model);
                    }
                    //shu
                    Collections.shuffle(models);
                    contentAdapter = new ContentAdapter(getActivity(),models);
                    homeRecycler.setAdapter(contentAdapter);
                    contentAdapter.notifyDataSetChanged();
                }else {
                    homeProgressBar.setVisibility(View.VISIBLE);
                    Toast.makeText(getActivity(), "No data found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getActivity(), ""+error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}