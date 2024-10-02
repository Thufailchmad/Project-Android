package com.thufail.project;

import android.content.Intent;
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
import java.util.ArrayList;
import java.util.List;

public class RegisterActivity extends AppCompatActivity {

    Button button;
    EditText fullname, email, alamat, password, ulangipassword;
    TextView errorEmail, errorFullname, errorAlamat, errorPassword, errorUlangiPassword;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);

        HttpClient client = new DefaultHttpClient();
        HttpPost post = new HttpPost(new apiConnection().host+"/register/index.php");

        StrictMode.ThreadPolicy policy =  new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        button = (Button) findViewById(R.id.signupbtn);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    fullname = findViewById(R.id.name);
                    email = findViewById(R.id.email);
                    alamat = findViewById(R.id.alamat);
                    password = findViewById(R.id.password);
                    ulangipassword = findViewById(R.id.ulangipassword);

                    List<NameValuePair> pairList = new ArrayList<NameValuePair>(5);
                    pairList.add(new BasicNameValuePair("email", email.getText().toString()));
                    pairList.add(new BasicNameValuePair("name", fullname.getText().toString()));
                    pairList.add(new BasicNameValuePair("address", alamat.getText().toString()));
                    pairList.add(new BasicNameValuePair("password", password.getText().toString()));
                    pairList.add(new BasicNameValuePair("rePassword", ulangipassword.getText().toString()));

                    post.addHeader("User-Agent", "android");
                    post.setEntity(new UrlEncodedFormEntity(pairList));

                    HttpResponse response = client.execute(post);
                    JSONObject object = new JSONObject(EntityUtils.toString(response.getEntity()));
                    if(object.getString("status").equals("error")){
                        Toast.makeText(RegisterActivity.this, object.getString("status"), Toast.LENGTH_LONG).show();
                        JSONObject errorObject = object.getJSONObject("error");
                        if(!errorObject.getString("email").isEmpty()){
                            errorEmail = findViewById(R.id.erroremail);
                            errorEmail.setText(errorObject.getString("email"));
                            errorEmail.setVisibility(View.VISIBLE);
                        }
                        if(errorObject.has("name")){
                            errorFullname = findViewById(R.id.errorfullname);
                            errorFullname.setText(errorObject.getString("name"));
                            errorFullname.setVisibility(View.VISIBLE);
                        }
                        if(errorObject.has("address")){
                            errorAlamat = findViewById(R.id.erroralamat);
                            errorAlamat.setText(errorObject.getString("address"));
                            errorAlamat.setVisibility(View.VISIBLE);
                        }
                        if(errorObject.has("password")){
                            errorPassword = findViewById(R.id.errorpassword);
                            errorPassword.setText(errorObject.getString("password"));
                            errorPassword.setVisibility(View.VISIBLE);
                        }
                        if(errorObject.has("rePassword")
                        ){
                            errorUlangiPassword = findViewById(R.id.errorulangipassword);
                            errorUlangiPassword.setText(errorObject.getString("rePassword"));
                            errorUlangiPassword.setVisibility(View.VISIBLE);
                        }

                    }else if(object.getString("status").equals("registered")){
                        Toast.makeText(RegisterActivity.this, object.getString("message"), Toast.LENGTH_LONG).show();
                    }else{
                        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                        startActivity(intent);
                    }
                }catch (ClientProtocolException e){

                }catch (IOException e){

                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}