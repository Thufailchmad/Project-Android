package com.thufail.project;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.thufail.project.Model.History;

import java.text.NumberFormat;
import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder>{

    private static Context context;
    private static List<History> histories;
    private AdapterView.OnItemClickListener onItemClickListener;

    public HistoryAdapter(Context context, List<History> histories) {
        this.context = context;
        this.histories = histories;
    }

    @NonNull
    @Override
    public HistoryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_history, parent, false);
        return new ViewHolder(view, this);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryAdapter.ViewHolder holder, int position) {
        NumberFormat numberFormat = NumberFormat.getInstance();

        History history = histories.get(position);
        holder.user_name.setText(history.getUser_name());
        holder.total.setText("Rp " + numberFormat.format(history.getTotal()));
        holder.date.setText(history.getDate());
    }

    @Override
    public int getItemCount() {return histories.size(); }

    public void  setOnItemClickListener(AdapterView.OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    private void OnItemHolderClick(ViewHolder itemHolder) {
        if (onItemClickListener != null) {
            onItemClickListener.onItemClick(null, itemHolder.itemView,
                    itemHolder.getAdapterPosition(), itemHolder.getItemId());
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public TextView user_name, date, total;
        public HistoryAdapter historyAdapter;


        public ViewHolder(View itemView, HistoryAdapter historyAdapter) {
            super(itemView);
            this.historyAdapter = historyAdapter;
            user_name = (TextView) itemView.findViewById(R.id.user_name);
            date = (TextView) itemView.findViewById(R.id.date);
            total =  (TextView) itemView.findViewById(R.id.total_history);
            itemView.setOnClickListener(this);
    }

        @Override
        public void onClick(View view) {
            historyAdapter.OnItemHolderClick(this);
        }
    }}


