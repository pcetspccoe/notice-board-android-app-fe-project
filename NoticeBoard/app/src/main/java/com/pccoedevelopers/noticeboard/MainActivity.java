package com.pccoedevelopers.noticeboard;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.UserHandle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Locale;
import java.util.zip.Inflater;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private static final String TAG = "MainActivity";
    private DrawerLayout drawerLayout;
    private RecyclerView recyclerView;
    private StorageReference storageRef;

    private DatabaseReference dref;
    private FirebaseRecyclerOptions<DataInputStream> options;
    private FirebaseRecyclerAdapter<DataInputStream, RecyclerViewHolder> adapter;
    private DatabaseReference position_ref;
    private  String key;
    private String thumbnailpath;

    //UserInfo

    private FirebaseDatabase mUserFirebaseDatabase;
    private FirebaseAuth mUserAuth;
    private FirebaseAuth.AuthStateListener mUserAuthListner;
    private DatabaseReference mUserDbRef;
    private String userId;
    private String userName;
    private String erpId;
    private Fragment fragment = null;
    private String versionName;
    private String updateUri;
    private Long increamentedUpdateCounter;

    private DatabaseReference mUpdaterRef;

    private TextView readMore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MobileAds.initialize(this, "ca-app-pub-6334748056631180~2884028224");

        try {
            PackageInfo pInfo = this.getPackageManager().getPackageInfo(getPackageName(), 0);
            versionName = pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

//Setting fragment
        fragment= new AllFragment();
        if (fragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction ft = fragmentManager.beginTransaction();
            ft.replace(R.id.framelayout_replace, fragment);
            ft.commit();
        }
        Toolbar toolbar = findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu);

        drawerLayout = findViewById(R.id.drawer_layout);

        mUpdaterRef = FirebaseDatabase.getInstance().getReference().child("Flags");

        final ProgressBar progressBar = findViewById(R.id.progressBar_main_rv);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);

        View headerLayout = navigationView.inflateHeaderView(R.layout.nav_header);
        final TextView userNameNavHead = headerLayout.findViewById(R.id.user_name_nav_head);
        final TextView erpIdNavHead = headerLayout.findViewById(R.id.erp_id_nav_head);

        mUpdaterRef.child("is-update-available").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.child("version").getValue(String.class).trim().equalsIgnoreCase(versionName)) {
                    increamentedUpdateCounter = dataSnapshot.child("update-counter").getValue(Long.class)+1;
                    updateUri = dataSnapshot.child("update-uri").getValue(String.class);
                    updateDialog();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(MainActivity.this, "Unable to reach servers!", Toast.LENGTH_SHORT).show();
            }
        });


        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {

                        // set item as selected to persist highlight
                        menuItem.setChecked(true);
                        // close drawer when item is tapped
                        drawerLayout.closeDrawers();

                        // Add code here to update the UI based on the item selected
                        // For example, swap UI fragments here

                        switch (menuItem.getItemId()) {
                            case R.id.nav_all:
                                fragment= new AllFragment();
                                break;
                            case R.id.nav_academic:
                                fragment = new AcadamicsFragment();
                                break;
                            case R.id.nav_scholarship:
                                fragment = new ScholarShipFragment();
                                break;
                            case R.id.nav_other:
                                fragment = new OtherFragment();
                                break;
                            case R.id.nav_update:

                                mUpdaterRef.child("is-update-available").addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        if (!dataSnapshot.child("version").getValue(String.class).trim().equalsIgnoreCase(versionName)) {
                                            mUpdaterRef.child("is-update-available").child("update-counter").setValue(dataSnapshot.child("update-counter").getValue(Long.class)+1);
                                            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(dataSnapshot.child("update-uri").getValue(String.class)));
                                            startActivity(browserIntent);
                                        }
                                        else
                                            Toast.makeText(MainActivity.this, "No Updates Available! " + ("\ud83d\ude03"), Toast.LENGTH_SHORT).show();
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                        Toast.makeText(MainActivity.this, "Unable to reach servers!", Toast.LENGTH_SHORT).show();
                                    }
                                });

                                break;
                            case R.id.nav_info:
                                openInfo();
                                break;
                            case R.id.nav_signout:
                                AuthUI.getInstance()
                                        .signOut(getApplicationContext())
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                                                finish();
                                            }
                                        });
                                break;
                        }
                        if (fragment != null) {
                            FragmentManager fragmentManager = getSupportFragmentManager();
                            FragmentTransaction ft = fragmentManager.beginTransaction();
                            ft.replace(R.id.framelayout_replace, fragment);
                            ft.commit();
                        }

                        return true;
                    }
                });

        if (savedInstanceState == null)
            navigationView.setCheckedItem(R.id.nav_all);

        mUserAuth = FirebaseAuth.getInstance();
        final FirebaseUser user = mUserAuth.getCurrentUser();
        userId = user.getUid();
        mUserFirebaseDatabase = FirebaseDatabase.getInstance();
        mUserDbRef = mUserFirebaseDatabase.getReference().child("Users").child(userId);

        mUserAuthListner = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    //user is signed in

                } else {
                    //user is not signed in
                    Intent gotoSigninIntent = new Intent(MainActivity.this, LoginActivity.class);
                    gotoSigninIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(gotoSigninIntent);
                }
            }
        };

        mUserDbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child("name").getValue() != null && dataSnapshot.child("erp_id").getValue() != null) {
                    userName = dataSnapshot.child("name").getValue().toString();
                    erpId = dataSnapshot.child("erp_id").getValue().toString();
                    userNameNavHead.setText(userName);
                    erpIdNavHead.setText(erpId);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



        //RecyclerView

//        recyclerView = findViewById(R.id.rv_list);
//        recyclerView.setHasFixedSize(true);
//
//        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
//        mLayoutManager.setReverseLayout(true);
//        mLayoutManager.setStackFromEnd(true);

//        recyclerView.setLayoutManager(mLayoutManager);
//
//        dref = FirebaseDatabase.getInstance().getReference().child("Data");
//
//        options = new FirebaseRecyclerOptions.Builder<DataInputStream>()
//                .setQuery(dref, DataInputStream.class).build();
//
//        adapter = new FirebaseRecyclerAdapter<DataInputStream, RecyclerViewHolder>(options) {
//            @Override
//            protected void onBindViewHolder(@NonNull final RecyclerViewHolder holder, final int position, @NonNull final DataInputStream model) {
//                thumbnailpath=model.getThumbnail_path();

//
//                storageRef= FirebaseStorage.getInstance().getReference().child("thumbnail/thumb-"+thumbnailpath);
//                storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
//                    @Override
//                    public void onSuccess(Uri uri) {
//                        Glide.with(getApplicationContext())
//                                .load(uri.toString())
//                                .placeholder(R.drawable.ic_loading)
//                                .into(holder.image);
//
//
//                    }
//                });
//
//
//                holder.description.setText(model.getDescription());
//                holder.id.setText(model.getId());

//                holder.title.setText(model.getTitle());
//
//                progressBar.setVisibility(View.GONE);
//
//               holder.itemView.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        position_ref =adapter.getRef(position);
//                        key =position_ref.getKey();
////                        Toast.makeText(MainActivity.this,key, Toast.LENGTH_SHORT).show();
//                        Intent detailed = new Intent(".DetailedActivity");
//                        detailed.putExtra("key",key);
//                        startActivity(detailed);
//
//                    }
//                });

//            }
//
//            @NonNull
//            @Override
//            public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
//
//                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.rv_list_item, viewGroup, false);
//
//                return new RecyclerViewHolder(view);
//            }
//
//        };
//
//        adapter.startListening();
//        recyclerView.setAdapter(adapter);
//
//    };

//        I know this seems something different but I just copied the code from here to AllFragment so that bugs can be fixed
//        TODO warning do not delete the commented code
    }

    private void updateDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Important Update Available");
        builder.setMessage("Do you want to download the update now?");
        builder.setCancelable(false);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(updateUri));
                startActivity(browserIntent);
                mUpdaterRef.child("is-update-available").child("update-counter").setValue(increamentedUpdateCounter);

            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getApplicationContext(), "You'll be notified next time.", Toast.LENGTH_SHORT).show();
            }
        });

        builder.show();
    }

    public void openSettings(){
        Intent settingsIntent = new Intent(this, SettingsActivity.class);
        startActivity(settingsIntent);
    }

    public void openInfo(){
        Intent infoIntent = new Intent(this, InfoActivity.class);
        startActivity(infoIntent);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        }
        else{
            moveTaskToBack(true);
            finish();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseMessaging.getInstance().subscribeToTopic("news").addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                return;
            }
        });

        mUserAuth.addAuthStateListener(mUserAuthListner);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mUserAuthListner != null){
            mUserAuth.removeAuthStateListener(mUserAuthListner);
        }
    }
}