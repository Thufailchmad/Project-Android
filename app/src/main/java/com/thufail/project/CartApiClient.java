package com.thufail.project;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

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
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class CartApiClient {

    private static final String API_URL = new apiConnection().host + "user/keranjang.php";

    public static class ApiTask extends AsyncTask<String, Void, String> {
        Context context;
        private String action;
        private String cartId;
        private String quantity;

        public ApiTask(String action, String cartId, String quantity) {
            this.action = action;
            this.cartId = cartId;
            this.quantity = quantity;
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                HttpClient httpClient = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost(API_URL);

                List<NameValuePair> nameValuePairs = new ArrayList<>();
                nameValuePairs.add(new BasicNameValuePair("action", action));
                nameValuePairs.add(new BasicNameValuePair("cartId", cartId));

                if (quantity != null) {
                    nameValuePairs.add(new BasicNameValuePair("quantity", quantity));
                }

                httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                HttpResponse response = httpClient.execute(httpPost);

                BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
                return sb.toString();
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String result) {
            if (result != null) {
                try {
                    JSONObject jsonResponse = new JSONObject(result);
                    boolean success = jsonResponse.getBoolean("success");
                    String message = jsonResponse.optString("message", "Operation completed");

                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                    if (success) {

                    }else {

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(context, "Error parsing response", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    public static void getCartItems() {
        try {
            HttpClient client = new DefaultHttpClient();
            HttpGet get = new HttpGet(new apiConnection().host + "user/keranjang.php");
            get.addHeader("User-Agent", "android");
            HttpResponse response = client.execute(get);
            HttpEntity entity = response.getEntity();
            String json = EntityUtils.toString(entity);
            Log.d("creation", "json" + json);
        } catch (ClientProtocolException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void updateCartItems(String cartId, String quantity) {
        new ApiTask("update", cartId, quantity).execute();
    }

    public static void checkout() {
        new ApiTask("checkout", null, null).execute();
    }

    public static void deleteCartItem(String cartId) {
        new ApiTask("delete", cartId, null).execute();
    }

}
