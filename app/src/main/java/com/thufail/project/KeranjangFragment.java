package com.thufail.project;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class KeranjangFragment extends Fragment implements CartAdapter.TotalPriceListener {
    RecyclerView recyclerView;
    List<Cart> carts;
    CartAdapter adapter;
    TextView total_harga;
    Button btn_checkout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        View view = inflater.inflate(R.layout.fragment_keranjang, container, false);
        new GetKeranjang().execute();

        recyclerView = (RecyclerView) view.findViewById(R.id.list_cart);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        total_harga = (TextView) view.findViewById(R.id.total_harga);

        btn_checkout = view.findViewById(R.id.btn_checkout);
        btn_checkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    SharedPreferences preferences = getActivity().getSharedPreferences("user", Context.MODE_PRIVATE);

                    HttpClient client = new DefaultHttpClient();
                    HttpPost post = new HttpPost(new apiConnection().host + "/user/keranjang.php?user_id=" + String.valueOf(preferences.getInt("user_id", 0)));
                    post.addHeader("User-Agent", "android");

                    List<NameValuePair> list = new ArrayList<>(1);
                    list.add(new BasicNameValuePair("action", "checkout"));
                    post.setEntity(new UrlEncodedFormEntity(list));

                    HttpResponse response = client.execute(post);
                    String responseString = EntityUtils.toString(response.getEntity());
                    JSONObject object = new JSONObject(responseString);
                    Log.d("creation", "json " + responseString);
                    if(object.has("success")){
                        adapter.notifyDataSetChanged();
                        onTotalPriceChange(0);
                        getActivity().getSupportFragmentManager().beginTransaction()
                                .replace(R.id.flFragment, new HomeFragment())
                                .commit();
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
            }
        });

        return view;
    }

    @Override
    public void onTotalPriceChange(int totalPrice) {
        total_harga.setText(String.valueOf(totalPrice));
        Log.d("creation", String.valueOf(totalPrice));
    }

    public class GetKeranjang extends AsyncTask<Void, Void, List<Cart>>{

        @Override
        protected List<Cart> doInBackground(Void... voids) {
            try {
                SharedPreferences preferences = getActivity().getSharedPreferences("user", Context.MODE_PRIVATE);

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
                Log.d("creation", Arrays.asList(carts).get(0).getPhoto());
                return Arrays.asList(carts);

            } catch (ClientProtocolException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }

        protected void onPostExecute(List<Cart> list){
            if(list!=null){
                carts = list;
                adapter = new CartAdapter(getContext().getApplicationContext(), carts, KeranjangFragment.this);
                recyclerView.setAdapter(adapter);
                total_harga.setText(String.valueOf(adapter.GetPrice()));
                Log.d("creation", String.valueOf(adapter.GetPrice()));
            } else {
                // Handle empty data situation
                recyclerView.setVisibility(View.GONE);
            }
        }
    }
}