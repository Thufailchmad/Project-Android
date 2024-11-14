package com.thufail.project;

import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

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
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class DeskripsiActivity extends AppCompatActivity {

    TextView product_title, kategori_produk, harga_produk, detail_deskripsi;
    ImageView imageView;
    Item myItem;

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

        new FetchItem().execute();
    }

    protected class FetchItem extends AsyncTask<Void, Void, Item>{

        @Override
        protected Item doInBackground(Void... voids) {
            try {
                String param = String.valueOf(getIntent().getIntExtra("param", 0));
                Log.d("creation", param);
                HttpClient client = new DefaultHttpClient();
                HttpGet get = new HttpGet(new apiConnection().host + "/item/detail.php?id=" + param);
                HttpResponse response = client.execute(get);
                HttpEntity entity = response.getEntity();
                String json = EntityUtils.toString(entity);
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
                Glide.with(DeskripsiActivity.this).load(new apiConnection().host+"/" + myItem.getPhoto()).listener(new RequestListener<Drawable>() {
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