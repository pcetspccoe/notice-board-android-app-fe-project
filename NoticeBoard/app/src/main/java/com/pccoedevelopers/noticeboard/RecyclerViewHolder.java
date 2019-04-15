package com.pccoedevelopers.noticeboard;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class RecyclerViewHolder extends RecyclerView.ViewHolder {

    public TextView description, id, title, readMore;
    public ImageView image;

    public RecyclerViewHolder(@NonNull View itemView) {
        super(itemView);

        description = itemView.findViewById(R.id.notice_description);
        id = itemView.findViewById(R.id.notice_id);
        title = itemView.findViewById(R.id.notice_title);
        image = itemView.findViewById(R.id.notice_image);
        readMore = itemView.findViewById(R.id.read_more);
    }
}
