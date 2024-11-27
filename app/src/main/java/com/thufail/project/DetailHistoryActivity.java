package com.thufail.project;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
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
import java.util.Collections;
import java.util.List;

public class DetailHistoryActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private HistoryDetailAdapter adapter;
    private List<HistoryDetail> details;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_detail_history);

        recyclerView = (RecyclerView) findViewById(R.id.list_detail_history);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        new GetDetailHistory().execute();
    }

    protected class GetDetailHistory extends AsyncTask<Void, Void, List<HistoryDetail>> {

        @Override
        protected List<HistoryDetail> doInBackground(Void... voids) {
            try {
                SharedPreferences preferences = getSharedPreferences("user", MODE_PRIVATE);
                String userId = String.valueOf(preferences.getInt("user_id", 0));
                String historyId = String.valueOf(getIntent().getIntExtra("param",0));
                Log.d("creation", historyId);

                HttpClient client = new DefaultHttpClient();
                HttpGet get = new HttpGet(new apiConnection().host + "/user/history.php?user_id="+userId+"&history_id="+historyId);
                get.addHeader("User-Agent", "android");

                HttpResponse response = client.execute(get);
                String entity = EntityUtils.toString(response.getEntity());
                JSONObject object = new JSONObject(entity);
                Log.d("creation", entity);

                Gson gson = new Gson();
                HistoryDetail[] historyDetails = gson.fromJson(object.getJSONArray("data").toString(), HistoryDetail[].class);

                return Arrays.asList(historyDetails);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            } catch (ClientProtocolException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        protected void onPostExecute(List<HistoryDetail> list){
            if(list!=null){
                details = list;
                adapter = new HistoryDetailAdapter(DetailHistoryActivity.this, details);
                recyclerView.setAdapter(adapter);
            }
        }
    }
}