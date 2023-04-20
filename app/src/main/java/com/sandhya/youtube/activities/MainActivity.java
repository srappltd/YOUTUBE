package com.sandhya.youtube.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.search.SearchBar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.sandhya.youtube.R;
import com.sandhya.youtube.adapter.ContentAdapter;
import com.sandhya.youtube.databinding.ActivityMainBinding;
import com.sandhya.youtube.fragements.ExploreFragment;
import com.sandhya.youtube.fragements.HomeFragement;
import com.sandhya.youtube.fragements.LibraryFragment;
import com.sandhya.youtube.fragements.SubscriberFragment;
import com.sandhya.youtube.models.ContentModels;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    private FirebaseAuth auth;
    FirebaseUser user;
    GoogleSignInClient googleSignInClient;
    GoogleSignInOptions gsc;
    private static final int REQ_CODE = 100;
    private static final int PERMISSION = 101;
    private static final int PICK_GALLERY = 102;

    Uri videoUri;
    Fragment fragment;

    AppBarLayout appBarLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        //permission check
        checkPermission();

        appBarLayout = findViewById(R.id.appbar_Layout);
        //google
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        gsc = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken(getString(R.string
                .default_web_client_id)).requestEmail().build();
        googleSignInClient = GoogleSignIn.getClient(MainActivity.this, gsc);

        //getSupportActionBar().hide();

        binding.toolBar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                switch (item.getItemId()) {
                    case R.id.notification:
                        Toast.makeText(MainActivity.this, "Notification", Toast.LENGTH_SHORT).show();
                        break;

                    case R.id.search:
                        Toast.makeText(MainActivity.this, "Search", Toast.LENGTH_SHORT).show();
                        break;

                    case R.id.account:
                        //Toast.makeText(MainActivity.this, "Account", Toast.LENGTH_SHORT).show();
                        initAccount();
                        break;
                    case R.id.icons:
                        Toast.makeText(MainActivity.this, "Logo", Toast.LENGTH_SHORT).show();
                        break;

//                    case R.id.deshboards:
//                        ChannelDeshboardFragment deshboardFragment = new ChannelDeshboardFragment();
//                        selectedFragment(deshboardFragment);
//                        break;
                }
                return false;
            }
        });

        binding.bottomBar.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                // 1 2 3 4
                switch (item.getItemId()) {

                    case R.id.home:
                        HomeFragement homeFragement = new HomeFragement();
                        selectedFragment(homeFragement);
                        break;

                    case R.id.explore:
                        ExploreFragment exploreFragment = new ExploreFragment();
                        selectedFragment(exploreFragment);
                        break;
                    case R.id.publish:
//                        Toast.makeText(MainActivity.this, "Add A Video", Toast.LENGTH_SHORT).show();
//                        startActivity(new Intent(MainActivity.this,PublishActivity.class));
                        initUpload();
                        break;

                    case R.id.library:
                        LibraryFragment libraryFragment = new LibraryFragment();
                        selectedFragment(libraryFragment);
                        break;

                    case R.id.subscription:
                        SubscriberFragment subscriptionFragment = new SubscriberFragment();
                        selectedFragment(subscriptionFragment);
                        break;
//                    case R.id.deshboard:
//                        ChannelDeshboardFragment deshboardFragment = new ChannelDeshboardFragment();
//                        selectedFragment(deshboardFragment);
//                        break;
                }
                return false;
            }
        });
        binding.bottomBar.setSelectedItemId(R.id.home);
    }

    public void initUpload() {

        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        bottomSheetDialog.setContentView(R.layout.upload_video_item);
        bottomSheetDialog.setCancelable(true);
        bottomSheetDialog.setCanceledOnTouchOutside(false);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            bottomSheetDialog.getWindow().setBackgroundBlurRadius(Window.DECOR_CAPTION_SHADE_DARK);
        }
        bottomSheetDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        bottomSheetDialog.show();

        LinearLayout llUpload, llUploadShort, llUploadPost;
        CircleImageView imgCancel;
        llUpload = bottomSheetDialog.findViewById(R.id.llUpload);
        llUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent iGallery = new Intent(Intent.ACTION_GET_CONTENT);
                iGallery.setType("video/*");
                startActivityForResult(Intent.createChooser(iGallery, "Select video"), PICK_GALLERY);
                bottomSheetDialog.dismiss();
            }
        });
        imgCancel = bottomSheetDialog.findViewById(R.id.imgCancel);
        imgCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetDialog.dismiss();
            }
        });
    }

    private void selectedFragment(Fragment fragment) {
//        setStatusBarColor("#FFFFFF");
        appBarLayout.setVisibility(View.VISIBLE);
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout, fragment);
        fragmentTransaction.commit();
    }

    //Google login 1
    public void initAccount() {
        if (user != null) {
            Toast.makeText(this, "User Already Sign In...", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, AccountActivity.class);
            startActivity(intent);
        } else {
            initGoogleLogin();
        }
    }

    //Google login 2
    private void initGoogleLogin() {
        Intent intent = googleSignInClient.getSignInIntent();
        startActivityForResult(intent, REQ_CODE);
    }

    //Google login 3
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQ_CODE:
                if (resultCode == RESULT_OK && data != null) {
                    Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                    try {
                        GoogleSignInAccount account = task.getResult(ApiException.class);
                        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
                        auth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

                                    //date and time
                                    String date = DateFormat.getDateInstance().format(new Date());
                                    String time = DateFormat.getTimeInstance().format(new Date());
                                    //map data
                                    HashMap<String, Object> map = new HashMap<>();
                                    map.put("username", account.getDisplayName());
                                    map.put("email", account.getEmail());
                                    map.put("profile", String.valueOf(account.getPhotoUrl()));
                                    map.put("uid", firebaseUser.getUid());
                                    map.put("Joining Time", time);
                                    map.put("Joining Date", date);
                                    map.put("search", account.getDisplayName().toLowerCase());

                                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users");
                                    reference.child(firebaseUser.getUid()).setValue(map);
                                    Toast.makeText(MainActivity.this, "Login Successfully", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(getApplicationContext(), AccountActivity.class));
                                } else {
                                    Toast.makeText(MainActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    } catch (Exception e) {
                        Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            case PICK_GALLERY:
                if (resultCode == RESULT_OK && data != null) {
                    videoUri = data.getData();
                    Intent intent = new Intent(MainActivity.this, PublishActivity.class);
                    intent.putExtra("type", "video");
                    intent.setData(videoUri);
                    startActivity(intent);
//                    Toast.makeText(this, "Login", Toast.LENGTH_SHORT).show();

                }

        }

//        binding.searchBar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//            @Override
//            public boolean onQueryTextSubmit(String query) {
//                proccess(query);
//                return false;
//            }
//
//            @Override
//            public boolean onQueryTextChange(String query) {
//                proccess(query);
//                return false;
//            }
//        });

    }

//    private void proccess(String query) {
//        FirebaseRecyclerOptions<ContentModels> options =
//                new FirebaseRecyclerOptions.Builder<ContentModels>().setQuery(FirebaseDatabase.getInstance().getReference().child("Contents").orderByChild("title").startAt(query).endAt(query+"\uf8ff"),ContentModels.class).build();
//        ContentAdapter adapter = new ContentAdapter(getApplicationContext())
//    }

    public void checkPermission() {
        if (ActivityCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION);
        } else {
            Log.d("permission", "checkPermission: Permission granted");
        }
    }

}