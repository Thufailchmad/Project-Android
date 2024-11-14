package com.thufail.project;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Toast;

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
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;


public class HomeFragment extends Fragment {

    private RecyclerView recyclerView;

    private List<Item> itemList;

    private CardAdapter cardAdapter;

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";


    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    Button button;

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_home, container, false);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        recyclerView = (RecyclerView) view.findViewById(R.id.list_item);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext().getApplicationContext(), 2));
        recyclerView.addItemDecoration(new CardSpacingItemDecoration(2, 60, true));

        new FetchItemTasks().execute();

        return view;
    };

    private class FetchItemTasks extends AsyncTask<Void, Void, List<Item>>{

        @Override
        protected List<Item> doInBackground(Void... voids) {
            try {
                HttpClient client = new DefaultHttpClient();
                HttpGet get = new HttpGet(new apiConnection().host + "/item");
                HttpResponse response = client.execute(get);
                HttpEntity entity = response.getEntity();
                String json = EntityUtils.toString(entity);
                Log.d("creation", "json "+json);
                JSONObject object = new JSONObject(json);

                Gson gson = new Gson();
                Item[] items = gson.fromJson(object.getJSONArray("item").toString(), Item[].class);
                Log.d("creation", Arrays.asList(items).get(0).getPhoto());
                return Arrays.asList(items);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            } catch (ClientProtocolException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        protected void onPostExecute(List<Item> items){
            Log.d("creation", "model set");
            if (items != null){
                itemList = items;
                cardAdapter = new CardAdapter(getActivity().getApplicationContext(), itemList);
                recyclerView.setAdapter(cardAdapter);
            }

            cardAdapter.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    Toast.makeText(getActivity(), itemList.get(i).getName(), Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(getActivity().getApplicationContext(), DeskripsiActivity.class);
                    intent.putExtra("param", itemList.get(i).getId());
                    startActivity(intent);
                    Log.d("creation", "clicked");
                }
            });
        }
    }
}