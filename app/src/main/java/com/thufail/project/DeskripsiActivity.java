package com.thufail.project;

import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.google.gson.Gson;
import com.thufail.project.Model.Item;

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
import java.util.ArrayList;
import java.util.List;

public class DeskripsiActivity extends AppCompatActivity {

    TextView product_title, kategori_produk, harga_produk, detail_deskripsi;
    ImageView imageView;
    Item myItem;
    Button tambah_keranjang;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_deskripsi);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        tambah_keranjang = (Button) findViewById(R.id.tambah_keranjang);
        tambah_keranjang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    HttpClient client = new DefaultHttpClient();
                    HttpPost post = new HttpPost(new apiConnection().host + "/user/desk_item.php?id=" + myItem.getId());
                    post.addHeader("User-Agent", "android");
                    List<NameValuePair> list = new ArrayList<NameValuePair>(3);
                    SharedPreferences preferences = getSharedPreferences("user", MODE_PRIVATE);

                    list.add(new BasicNameValuePair("item_id", String.valueOf(myItem.getId())));
                    list.add(new BasicNameValuePair("quantity", "1"));
                    list.add(new BasicNameValuePair("user_id", String.valueOf(preferences.getInt("user_id", 0))));

                    post.setEntity(new UrlEncodedFormEntity(list));

                    HttpResponse response = client.execute(post);
                    String entityUtils = EntityUtils.toString(response.getEntity());
                    Log.d("creation", entityUtils);
                    JSONObject object = new JSONObject(entityUtils);

                    Toast.makeText(DeskripsiActivity.this, object.getString("message"), Toast.LENGTH_LONG).show();
                } catch (UnsupportedEncodingException e) {
                    throw new RuntimeException(e);
                } catch (ClientProtocolException e) {
                    throw new RuntimeException(e);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        });


        new FetchItem().execute();
    }

    protected class FetchItem extends AsyncTask<Void, Void, Item>{

        @Override
        protected Item doInBackground(Void... voids) {
            try {
                String param = String.valueOf(getIntent().getIntExtra("param", 0));
                Log.d("creation", param);
                HttpClient client = new DefaultHttpClient();
                HttpGet get = new HttpGet(new apiConnection().host + "/user/desk_item.php?id=" + param);
                get.addHeader("User-Agent", "android");
                HttpResponse response = client.execute(get);
                HttpEntity entity = response.getEntity();
                String json = EntityUtils.toString(entity);
                Log.d("creation", json+json);
                JSONObject object = new JSONObject(json);

                Gson gson = new Gson();
                Item item = gson.fromJson(String.valueOf(object.getJSONObject("item")), Item.class);
                Log.d("creation", item.getName());
                return item;
            } catch (ClientProtocolException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        protected void onPostExecute(Item item) {
            if(item != null){
                myItem = item;

                product_title = (TextView) findViewById(R.id.product_title);
                product_title.setText(myItem.getName());
                kategori_produk = (TextView) findViewById(R.id.kategori_produk);
                kategori_produk.setText(myItem.getCategory());
                harga_produk = (TextView) findViewById(R.id.harga_produk);
                harga_produk.setText("Rp " + String.valueOf(myItem.getPrice()));
                detail_deskripsi = (TextView) findViewById(R.id.detail_deskripsi);
                detail_deskripsi.setText(myItem.getDescription());

                imageView = (ImageView) findViewById(R.id.product_image);
                Glide.with(DeskripsiActivity.this).load(new apiConnection().host+"/assets/" + myItem.getPhoto()).listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, @Nullable Object model, @NonNull com.bumptech.glide.request.target.Target<Drawable> target, boolean isFirstResource) {
                        Log.e("Glide", "Load Failed", e);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(@NonNull Drawable resource, @NonNull Object model, com.bumptech.glide.request.target.Target<Drawable> target, @NonNull com.bumptech.glide.load.DataSource dataSource, boolean isFirstResource) {
                        return false;
                    }
                }).into(imageView);
            }
        }
    }
}