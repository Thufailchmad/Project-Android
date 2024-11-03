package com.thufail.project;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CardAdapter extends RecyclerView.Adapter<CardAdapter.HolderData> {

    List<String> listData;
    LayoutInflater inflater;
    private ViewGroup parent;

    public CardAdapter(Context context, List<String> listData) {
        this.listData = listData;
        this.inflater = LayoutInflater.from(context);
    }

    @androidx.annotation.NonNull
    @Override
    public CardAdapter.HolderData onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = inflater.inflate(R.layout.card_view, parent, false);
        return new HolderData(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CardAdapter.HolderData holderData, int position) {
        holderData.txtData.setText(listData.get(position));
    }

    @Override
    public int getItemCount() {
        return listData.size();
    }
    public class HolderData extends RecyclerView.ViewHolder{

        TextView txtData;

            public HolderData(@NonNull View itemView) {
            super(itemView);

            txtData = itemView.findViewById(R.id.data_text);
        }
    }
}
