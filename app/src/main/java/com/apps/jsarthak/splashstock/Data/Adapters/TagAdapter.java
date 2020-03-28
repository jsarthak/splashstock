package com.apps.jsarthak.splashstock.Data.Adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.apps.jsarthak.splashstock.R;
import com.apps.jsarthak.splashstock.UI.Search.SearchActivity;

import java.util.ArrayList;

public class TagAdapter extends RecyclerView.Adapter<TagAdapter.TagViewHolder>{


        Context mContext;
        ArrayList<String> stringArrayList;

        public TagAdapter(Context mContext, ArrayList<String> stringArrayList) {
            this.mContext = mContext;
            this.stringArrayList = stringArrayList;
        }

        @NonNull
        @Override
        public TagViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.tag_list_item, parent, false);
            return new TagViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull TagViewHolder holder, int position) {
            final String tag = stringArrayList.get(position);
            holder.textView.setText(tag);
            holder.container.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent tagIntent = new Intent(mContext, SearchActivity.class);
                    tagIntent.putExtra("query", tag);
                    mContext.startActivity(tagIntent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return stringArrayList.size();
        }

        class TagViewHolder extends RecyclerView.ViewHolder{

            CardView container;
            TextView textView;

            public TagViewHolder(View itemView) {
                super(itemView);
                textView = itemView.findViewById(R.id.tv_tag_item_title);
                container = itemView.findViewById(R.id.tag_item_container);
            }
        }
}
