package com.pccoedevelopers.noticeboard;

import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.github.chrisbanes.photoview.PhotoView;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

public class DetailedActivity extends AppCompatActivity {

    private DatabaseReference mDatabaseRef;

    private  ProgressBar pbar;
    private String uploader , current_title, notice_title;
    private Long counterValue, increamentedCounterValue;

    private AdView mAdView;


    ProgressBar progressBar;
    WebView webView;
    TextView tv_title,tv_description,tv_uploader;
    private String uri;
    PhotoView img;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed);
        img =(PhotoView) findViewById(R.id.image_view);
         tv_title = findViewById(R.id.tv_title);
        tv_description = findViewById(R.id.tv_description);
        tv_uploader = findViewById(R.id.tv_uploader);
        final String key = getIntent().getStringExtra("key");
        pbar=findViewById(R.id.progressBar);
        Toolbar toolbar = findViewById(R.id.toolbar_detailed);
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_arrow_back);

        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);


//        setting firebase instance


//        mDatabase_image = FirebaseDatabase.getInstance().getReference().child("Data/"+key+"/image");
//        mDatabase_description =FirebaseDatabase.getInstance().getReference().child("Data/"+key+"/description");
//        mDatabase_title =FirebaseDatabase.getInstance().getReference().child("Data/"+key+"/title");
//        mDatabase_uploader =FirebaseDatabase.getInstance().getReference().child("Data/"+key+"/uploader");
        mDatabaseRef = FirebaseDatabase.getInstance().getReference();

        mDatabaseRef.child("Data").child(key).child("image").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Glide.with(getApplicationContext())
                        .load(dataSnapshot.getValue(String.class))
//                        .placeholder(R.mipmap.ic_launcher)
                        .listener(new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {

                                pbar.setVisibility(View.GONE);
                                Toast.makeText(DetailedActivity.this, "Cannot get images", Toast.LENGTH_SHORT).show();
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                pbar.setVisibility(View.GONE);
                                return false;
                            }
                        })
                        .into(img);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });

        mDatabaseRef.child("Data").child(key).child("uploader").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                tv_uploader.setText("- "+dataSnapshot.getValue(String.class));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        mDatabaseRef.child("Data").child(key).child("title").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
            notice_title = dataSnapshot.getValue(String.class);
            tv_title.setText(notice_title);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });



        mDatabaseRef.child("Data").child(key).child("description").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                tv_description.setText(dataSnapshot.getValue(String.class));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });



        mDatabaseRef.child("Analytics").child(key).child("counter").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                counterValue = dataSnapshot.getValue(Long.class);
                increamentedCounterValue = counterValue+1;

                mDatabaseRef.child("Analytics").child(key).child("counter").setValue(increamentedCounterValue);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

//        mDatabase_counter.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                counterValue = dataSnapshot.getValue(Long.class);
//                increamentedCounterValue = counterValue+1;
//                mDatabase_counter.setValue(increamentedCounterValue);
////                mDatabase_counter.child("Analytics/detailed-activity-counter/"+key+"title").setValue(notice_title);
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });


//        progressBar =(ProgressBar)findViewById(R.id.progressBar2);
//        progressBar.setVisibility(View.VISIBLE);
//        webView=(WebView)findViewById(R.id.webview);
//
//        String final_uri="http://drive.google.com/viewerng/viewer?embedded=true&url="+uri;
//
////        Setting webView options
//
//        webView.getSettings().setJavaScriptEnabled(true);
//        webView.getSettings().setBuiltInZoomControls(true);
//        webView.getSettings().setLoadWithOverviewMode(true);
//        webView.getSettings().setUseWideViewPort(true);
//
//        webView.setWebViewClient(new WebViewClient(){
//            @Override
//            public void onPageStarted (WebView view,String url, Bitmap favicon){
//                super.onPageStarted(view,url,favicon);
//                ActionBar actionBar = getSupportActionBar();
//                actionBar.setTitle("Loading");
//            }
//            @Override
//            public void onPageFinished (WebView view,String url){
//                super.onPageFinished(view,url);
//                ActionBar actionBar = getSupportActionBar();
//                actionBar.setTitle("Loaded");
//                progressBar.setVisibility(View.GONE);
//            }
//        });
//        webView.loadUrl("http://ciml.info/dl/v0_8/ciml-v0_8-all.pdf");


        }

    @Override
    protected void onStart() {
        super.onStart();
    }
}

