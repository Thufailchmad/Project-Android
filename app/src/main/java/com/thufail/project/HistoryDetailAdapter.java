package com.thufail.project;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.thufail.project.Model.Cart;
import com.thufail.project.Model.History;
import com.thufail.project.Model.HistoryDetail;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.List;

public class HistoryDetailAdapter extends RecyclerView.Adapter<HistoryDetailAdapter.ViewHolder> {

    private static Context context;
    private static List<HistoryDetail> details;

    public HistoryDetailAdapter(Context context, List<HistoryDetail> details) {
        this.context = context;
        this.details = details;
    }


    @NonNull
    @Override
    public HistoryDetailAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_detail_history, parent, false);
        return new ViewHolder(view, this);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryDetailAdapter.ViewHolder holder, int position) {
        NumberFormat numberFormat = NumberFormat.getInstance();

        HistoryDetail historyDetail = details.get(position);
        holder.item_name.setText(historyDetail.getItem_name());
        holder.price.setText("Rp " + numberFormat.format(Integer.parseInt(historyDetail.getPrice())));
        holder.quantity.setText(numberFormat.format(historyDetail.getQuantity()));
        holder.total.setText("Rp " + numberFormat.format(Integer.parseInt(historyDetail.getPrice())*historyDetail.getQuantity()));
    }

    @Override
    public int getItemCount() {return details.size();}

public class ViewHolder extends RecyclerView.ViewHolder {

    public TextView item_name, quantity, price, total;
    public HistoryDetailAdapter historyDetailAdapter;

    public ViewHolder(View itemView, HistoryDetailAdapter historyDetailAdapter) {
        super(itemView);
        this.historyDetailAdapter = historyDetailAdapter;
        item_name = (TextView) itemView.findViewById(R.id.detail_history_item_name);
        quantity = (TextView) itemView.findViewById(R.id.detail_history_quantity);
        price = (TextView) itemView.findViewById(R.id.detail_history_price);
        total = (TextView) itemView.findViewById(R.id.detail_history_total);
    }
}
}
