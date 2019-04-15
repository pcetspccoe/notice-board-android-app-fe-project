package com.pccoedevelopers.noticeboard;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;


import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class AllFragment  extends Fragment {

    private RecyclerView recyclerView;
    private StorageReference storageRef;
    private DatabaseReference dref;
    private FirebaseRecyclerOptions<DataInputStream> options;
    private FirebaseRecyclerAdapter<DataInputStream, RecyclerViewHolder> adapter;
    private DatabaseReference position_ref;
    private  String key;
    private String thumbnailpath;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        return super.onCreateView(inflater, container, savedInstanceState);

        return  inflater.inflate(R.layout.fragment_all,null);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final ProgressBar progressBar = view.findViewById(R.id.progressBar_main_rv);

        recyclerView = view.findViewById(R.id.rv_list);
        recyclerView.setHasFixedSize(true);

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);

        recyclerView.setLayoutManager(mLayoutManager);

        dref = FirebaseDatabase.getInstance().getReference().child("Data");

        options = new FirebaseRecyclerOptions.Builder<DataInputStream>()
                .setQuery(dref, DataInputStream.class).build();

        adapter = new FirebaseRecyclerAdapter<DataInputStream, RecyclerViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final RecyclerViewHolder holder, final int position, @NonNull final DataInputStream model) {
                thumbnailpath=model.getThumbnail_path();


                storageRef= FirebaseStorage.getInstance().getReference().child("thumbnail/thumb-"+thumbnailpath);
                storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Glide.with(getActivity())
                                .load(uri.toString())
                                .placeholder(R.drawable.ic_loading)
                                .into(holder.image);


                    }
                });
                String catagory="Academics";
                if(model.getId().equals("1"))
                    catagory="Academics";
                if(model.getId().equals("2"))
                    catagory="Scholarship";
                if(model.getId().equals("3"))
                    catagory="Other";

                if (model.getDescription().length() > 120){

                    holder.description.setText(model.getDescription().substring(0,119));
                    holder.readMore.setVisibility(View.VISIBLE);

                }
                else {

                    holder.description.setText(model.getDescription());

                }

                holder.id.setText(catagory);

                holder.title.setText(model.getTitle());

                progressBar.setVisibility(View.GONE);

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        position_ref =adapter.getRef(position);
                        key =position_ref.getKey();
//                        Toast.makeText(MainActivity.this,key, Toast.LENGTH_SHORT).show();
                        Intent detailed = new Intent(".DetailedActivity");
                        detailed.putExtra("key",key);
                        startActivity(detailed);

                    }
                });

            }

            @NonNull
            @Override
            public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.rv_list_item, viewGroup, false);

                return new RecyclerViewHolder(view);
            }

        };

        adapter.startListening();
        recyclerView.setAdapter(adapter);

    }

}
