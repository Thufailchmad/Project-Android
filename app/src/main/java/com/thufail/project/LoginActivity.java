package com.thufail.project;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
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

public class LoginActivity extends AppCompatActivity {
    TextView reglink, salah, errorpassword, erroremail;
    EditText email, password;
    Button login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        StrictMode.ThreadPolicy policy =  new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        reglink = findViewById(R.id.register_link);
        reglink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
                startActivity(intent);
            }
        });

        login = findViewById(R.id.login_btn);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HttpClient client = new DefaultHttpClient();
                HttpPost post = new HttpPost(new apiConnection().host+"/auth/login.php");

                email = findViewById(R.id.email_input);
                password = findViewById(R.id.password_input);
                salah = findViewById(R.id.salah);
                erroremail = findViewById(R.id.erroremail);
                errorpassword = findViewById(R.id.errorpassword);

                try {
                    List<NameValuePair> list = new ArrayList<NameValuePair>(2);
                    list.add(new BasicNameValuePair("email", email.getText().toString()));
                    list.add(new BasicNameValuePair("password", password.getText().toString()));

                    post.addHeader("User-Agent", "android");
                    post.setEntity(new UrlEncodedFormEntity(list));

                    HttpResponse response = client.execute(post);
                    JSONObject object = new JSONObject(EntityUtils.toString(response.getEntity()));
                    Toast.makeText(LoginActivity.this, object.getString("status"), Toast.LENGTH_LONG).show();

                    if (object.getString("status").equals("Login Gagal")){
                        if (object.has("error")){
                            JSONObject errorObject = object.getJSONObject("error");
                            if (!errorObject.getString("email").isEmpty()){
                                erroremail.setText(errorObject.getString("email"));
                                erroremail.setVisibility(View.VISIBLE);
                            }

                            if (!errorObject.getString("password").isEmpty()){
                                errorpassword.setText(errorObject.getString("password"));
                                errorpassword.setVisibility(View.VISIBLE);
                            }
                        } else if (object.has("message")) {
                            salah.setText(object.getString("message"));
                            salah.setVisibility(View.VISIBLE);
                        }
                    }else {
                        SharedPreferences preferences = getSharedPreferences("user", MODE_PRIVATE);
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putInt("user_id", object.getInt("user_id"));
                        editor.apply();

                        Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                        startActivity(intent);
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
    }
}