package com.thufail.project;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.thufail.project.Model.Item;

import java.lang.annotation.Target;
import java.sql.Connection;
import java.text.NumberFormat;
import java.util.List;

import javax.sql.DataSource;

public class CardAdapter extends RecyclerView.Adapter<CardAdapter.GridItemViewHolder>{

    private List<Item> itemModels;

    private Context context;

    private AdapterView.OnItemClickListener OnItemClickListener;

    public CardAdapter(Context context, List<Item> itemModels) {
        this.context = context;
        this.itemModels = itemModels;
    }

    @NonNull
    @Override
    public CardAdapter.GridItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_layout, parent, false);
        return new GridItemViewHolder(view, this);
    }

    @Override
    public void onBindViewHolder(@NonNull CardAdapter.GridItemViewHolder holder, int position) {
        NumberFormat format = NumberFormat.getInstance();

        Item item = itemModels.get(position);
        holder.itemName.setText(item.getName());
        holder.itemPrice.setText("Rp" + format.format(item.getPrice()));
        Glide.with(context).load(new apiConnection().host + "/" + item.getPhoto()).listener(new RequestListener<Drawable>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, @Nullable Object model, @NonNull com.bumptech.glide.request.target.Target<Drawable> target, boolean isFirstResource) {
                Log.e("Glide", "Load Failed", e);
                return false;
            }

            @Override
            public boolean onResourceReady(@NonNull Drawable resource, @NonNull Object model, com.bumptech.glide.request.target.Target<Drawable> target, @NonNull com.bumptech.glide.load.DataSource dataSource, boolean isFirstResource) {
                return false;
            }
        }).into(holder.imageView);

    }


    @Override
    public int getItemCount() {
        return itemModels.size();
    }

    public void  setOnItemClickListener(AdapterView.OnItemClickListener onItemClickListener) {
        this.OnItemClickListener = onItemClickListener;
    }

    private void OnItemHolderClick(GridItemViewHolder itemHolder) {
        if (OnItemClickListener != null) {
            OnItemClickListener.onItemClick(null, itemHolder.itemView,
                    itemHolder.getAdapterPosition(), itemHolder.getItemId());
        }
    }

    class  GridItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public TextView itemName, itemPrice;

        public ImageView imageView;

        public CardAdapter cardAdapter;


        public GridItemViewHolder(View itemView, CardAdapter cardAdapter) {
            super(itemView);
            this.cardAdapter = cardAdapter;
            itemName = (TextView) itemView.findViewById(R.id.tvTitle);
            itemPrice = (TextView) itemView.findViewById(R.id.item_price);
            imageView = (ImageView) itemView.findViewById(R.id.img_lampu);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            cardAdapter.OnItemHolderClick(this);
        }
    }
}