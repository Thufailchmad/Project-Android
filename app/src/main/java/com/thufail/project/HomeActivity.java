package com.thufail.project;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.gson.Gson;
import com.thufail.project.Model.HistoryDetail;
import com.thufail.project.Model.Prepare;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class HomeActivity extends AppCompatActivity implements NavigationBarView.OnItemSelectedListener {

    private BottomNavigationView bottomNavigationView;
    private HomeFragment homeFragment = new HomeFragment();
    private KeranjangFragment keranjangFragment = new KeranjangFragment();
    private HistoryFragment historyFragment = new HistoryFragment();
    private Prepare prepare;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);

        bottomNavigationView =findViewById(R.id.bottomview);
        bottomNavigationView.setOnItemSelectedListener(this);


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        replaceFragment(new HomeFragment());
        new getPrepare().execute();
    }

    private void replaceFragment(Fragment fragment) {

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.flFragment,fragment);
        fragmentTransaction.commit();

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == R.id.Home) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.flFragment, homeFragment)
                    .commit();
            return true;
        } else if (itemId == R.id.Keranjang) {
            if (prepare.getCart_count()<=0){
                AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
                builder.setTitle("Peringatan !");
                builder.setMessage("Keranjang Tidak Boleh Kosong");
                builder.setCancelable(false);
                builder.setPositiveButton("YA", (DialogInterface.OnClickListener)(dialog, shich)->{
                    dialog.cancel();
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
                return false;
            }
            getSupportFragmentManager().beginTransaction()
                        .replace(R.id.flFragment, keranjangFragment)
                        .commit();
            return true;

        } else if (itemId == R.id.history) {
            if (prepare.getHistory_count()<=0){
                AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
                builder.setTitle("Peringatan !");
                builder.setMessage("Tidak Ada History Pembelian");
                builder.setCancelable(false);
                builder.setPositiveButton("YA", (DialogInterface.OnClickListener)(dialog, shich)->{
                    dialog.cancel();
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
                return false;
            }
            getSupportFragmentManager().beginTransaction()
                        .replace(R.id.flFragment, historyFragment)
                        .commit();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    protected class getPrepare extends AsyncTask<Void, Void, Prepare> {

        @Override
        protected Prepare doInBackground(Void... voids) {
            try {
                SharedPreferences preferences = getSharedPreferences("user", MODE_PRIVATE);
                String userId = String.valueOf(preferences.getInt("user_id", 0));
                HttpClient client = new DefaultHttpClient();
                HttpGet get = new HttpGet(new apiConnection().host + "/user/prepare.php?user_id="+userId);
                get.addHeader("User-Agent", "android");

                HttpResponse response = client.execute(get);
                String entity = EntityUtils.toString(response.getEntity());
                JSONObject object = new JSONObject(entity);
                Log.d("creation", entity);

                Gson gson = new Gson();
                Prepare prepare1 = gson.fromJson(object.toString(), Prepare.class);
                Log.d("creation", String.valueOf(prepare1.getHistory_count()));
                return prepare1;
            } catch (JSONException e) {
                throw new RuntimeException(e);
            } catch (ClientProtocolException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        protected void onPostExecute(Prepare prepare1){
            if(prepare1 != null){
                prepare = prepare1;
            }
        }
    }
}