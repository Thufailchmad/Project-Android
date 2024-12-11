package com.thufail.project;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.gson.Gson;
import com.thufail.project.Model.Cart;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder>{
    private static Context context;
    private List<Cart> carts;
    TotalPriceListener totalPriceListener;

    public CartAdapter(Context context, List<Cart> carts, TotalPriceListener totalPriceListener) {
        this.context = context;
        this.carts = carts;
        this.totalPriceListener = totalPriceListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_cart, parent, false);
        return new ViewHolder(view, this);
    }

    @Override
    public void onBindViewHolder(@NonNull CartAdapter.ViewHolder holder, int position) {
        NumberFormat numberFormat = NumberFormat.getInstance();
        SharedPreferences preferences = context.getSharedPreferences("user", Context.MODE_PRIVATE);
        String userId = String.valueOf(preferences.getInt("user_id", 0));

        Cart cart = carts.get(position);
        holder.product_name.setText(cart.getName());
        holder.product_price.setText("Rp " + numberFormat.format(Integer.parseInt(cart.getPrice())));
        holder.product_quantity.setText(numberFormat.format(cart.getQuantity()));
        holder.total.setText("Rp " + numberFormat.format((cart.getTotal_price())));
        Log.d("creation", cart.getPhoto());
        Glide.with(context).load(new apiConnection().host + "/assets/" + cart.getPhoto()).listener(new RequestListener<Drawable>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, @Nullable Object model, @NonNull Target<Drawable> target, boolean isFirstResource) {
                Log.d("Glide", "Load image failed");
                return false;
            }

            @Override
            public boolean onResourceReady(@NonNull Drawable resource, @NonNull Object model, Target<Drawable> target, @NonNull DataSource dataSource, boolean isFirstResource) {
                return false;
            }
        }).into(holder.imageView);

        holder.button_minus.setOnClickListener(view -> {
            try {
                HttpClient client = new DefaultHttpClient();
                HttpPost post = new HttpPost(new apiConnection().host + "/user/keranjang.php?user_id=" + userId);
                List<NameValuePair> list = new ArrayList<>(3);

                list.add(new BasicNameValuePair("action", "update"));
                list.add(new BasicNameValuePair("quantity", String.valueOf(cart.getQuantity() - 1)));
                list.add(new BasicNameValuePair("cartId", String.valueOf(cart.getCart_id())));
                post.setEntity(new UrlEncodedFormEntity(list));
                post.addHeader("User-Agent", "android");

                HttpResponse response = client.execute(post);
                String entityUtils = EntityUtils.toString(response.getEntity());
                JSONObject object = new JSONObject(entityUtils);
                Log.d("creation", entityUtils);
                if(object.has("success")){
                    Log.d("creation", "success");
                    this.carts = getData();
                    notifyDataSetChanged();
                    totalPriceListener.onTotalPriceChange(GetPrice());
                }
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            } catch (ClientProtocolException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        });

        holder.button_plus.setOnClickListener(view -> {
            try {
                HttpClient client = new DefaultHttpClient();
                HttpPost post = new HttpPost(new apiConnection().host + "/user/keranjang.php?user_id=" + userId);
                List<NameValuePair> list = new ArrayList<>(3);

                list.add(new BasicNameValuePair("action", "update"));
                list.add(new BasicNameValuePair("quantity", String.valueOf(cart.getQuantity() + 1)));
                list.add(new BasicNameValuePair("cartId", String.valueOf(cart.getCart_id())));
                post.setEntity(new UrlEncodedFormEntity(list));
                post.addHeader("User-Agent", "android");

                HttpResponse response = client.execute(post);
                String entityUtils = EntityUtils.toString(response.getEntity());
                JSONObject object = new JSONObject(entityUtils);
                Log.d("creation", entityUtils);
                if(object.has("success")){
                    Log.d("creation", "success");
                    this.carts = getData();
                    notifyDataSetChanged();
                    totalPriceListener.onTotalPriceChange(GetPrice());
                }
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            } catch (ClientProtocolException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        });

        holder.button_hapus.setOnClickListener(view -> {
            try {
                HttpClient client = new DefaultHttpClient();
                HttpPost post = new HttpPost(new apiConnection().host + "/user/keranjang.php?user_id=" + userId);
                List<NameValuePair> list = new ArrayList<>(2);

                list.add(new BasicNameValuePair("action", "delete"));
                list.add(new BasicNameValuePair("cartId", String.valueOf(cart.getCart_id())));
                post.setEntity(new UrlEncodedFormEntity(list));
                post.addHeader("User-Agent", "android");

                HttpResponse response = client.execute(post);
                String entityUtils = EntityUtils.toString(response.getEntity());
                JSONObject object = new JSONObject(entityUtils);
                Log.d("creation", entityUtils);
                if(object.has("success")){
                    Log.d("creation", "success");
                    if (getItemCount() == 1){
                        Intent intent = new Intent(context, HomeActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                        return;
                    }
                    this.carts = getData();
                    notifyDataSetChanged();
                    totalPriceListener.onTotalPriceChange(GetPrice());
                }
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            } catch (ClientProtocolException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public int getItemCount() {
        return carts != null ? carts.size():0;
    }

    public static List<Cart> getData(){
        try {
            SharedPreferences preferences = context.getSharedPreferences("user", Context.MODE_PRIVATE);

            HttpClient client = new DefaultHttpClient();
            HttpGet get = new HttpGet(new apiConnection().host + "/user/keranjang.php?user_id=" + String.valueOf(preferences.getInt("user_id", 0)));
            get.addHeader("User-Agent", "android");
            HttpResponse response = client.execute(get);
            HttpEntity entity = response.getEntity();
            String json = EntityUtils.toString(entity);
            Log.d("creation", "json" + json);
            JSONObject object = new JSONObject(json);

            Gson gson = new Gson();
            Cart[] carts = gson.fromJson(object.getJSONArray("cartItems").toString(), Cart[].class);
            Log.d("creation", Arrays.asList(carts).get(0).getName());
            return Arrays.asList(carts);

        } catch (ClientProtocolException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    public int GetPrice(){
        int total = 0;
        for(Cart cart : carts){
            total += cart.getTotal_price();
        }

        return total;
    }

    class  ViewHolder extends RecyclerView.ViewHolder {

        public TextView product_name, product_price, product_quantity, total;
        public ImageView imageView;
        public CartAdapter cartAdapter;
        public Button button_minus, button_plus, button_hapus;


        public ViewHolder(View itemView, CartAdapter cartAdapter) {
            super(itemView);
            this.cartAdapter = cartAdapter;
            product_name = (TextView) itemView.findViewById(R.id.product_name);
            product_price = (TextView) itemView.findViewById(R.id.product_price);
            product_quantity = (TextView) itemView.findViewById(R.id.product_quantity);
            total =  (TextView) itemView.findViewById(R.id.total);
            imageView = (ImageView) itemView.findViewById(R.id.produk_image);
            button_minus = (Button) itemView.findViewById(R.id.button_minus);
            button_plus = (Button) itemView.findViewById(R.id.button_plus);
            button_hapus = (Button) itemView.findViewById(R.id.button_hapus);
        }
    }

    public interface TotalPriceListener {
        void onTotalPriceChange(int totalPrice);
    }
}
