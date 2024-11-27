package com.thufail.project;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.google.gson.Gson;
import com.thufail.project.Model.Cart;
import com.thufail.project.Model.History;
import com.thufail.project.Model.HistoryDetail;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class HistoryFragment extends Fragment {
    private List<History> histories;
    RecyclerView recyclerView;
    HistoryAdapter adapter;

    public HistoryFragment() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_history, container, false);
        new GetHistory().execute();

        recyclerView = (RecyclerView) view.findViewById(R.id.list_history);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        return view;
    }

    public class GetHistory extends AsyncTask<Void, Void, List<History>> {
        @Override
        protected List<History> doInBackground(Void... voids) {
            try{
                SharedPreferences preferences = getActivity().getSharedPreferences("user", Context.MODE_PRIVATE);
                String userId = String.valueOf(preferences.getInt("user_id", 0));

                HttpClient client = new DefaultHttpClient();
                HttpGet get = new HttpGet(new apiConnection().host + "/user/history.php?user_id="+userId);
                get.addHeader("User-Agent", "android");
                HttpResponse response = client.execute(get);
                String entity = EntityUtils.toString(response.getEntity());
                Log.d("creation", entity);
                JSONObject object = new JSONObject(entity);

                Gson gson = new Gson();
                History[] histories1 = gson.fromJson(object.getJSONArray("data").toString(), History[].class);
                Log.d("creation", String.valueOf(histories1[0].getDate()));
                return Arrays.asList(histories1);
            } catch (ClientProtocolException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }

        protected void onPostExecute(List<History> list){
            if(list!=null){
                histories = list;
                adapter = new HistoryAdapter(getContext().getApplicationContext(), histories);
                recyclerView.setAdapter(adapter);
            }

            adapter.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    Log.d("creation", "clicked");
                    Intent intent = new Intent(getActivity().getApplicationContext(), DetailHistoryActivity.class);
                    intent.putExtra("param", histories.get(i).getId());
                    startActivity(intent);
                }
            });
        }
    }
}