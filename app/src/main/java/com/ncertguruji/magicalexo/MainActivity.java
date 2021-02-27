package com.ncertguruji.magicalexo;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.potyvideo.library.AndExoPlayerView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    String chapter = "ch1";
    String post = "";

    //Hardcoding the now but we will get the name from shared preferences
    String name = "Divyam";

    ArrayList<String> names = new ArrayList<String>();
    ArrayList<String> comments = new ArrayList<String>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AndExoPlayerView andExoPlayerView = findViewById(R.id.andExoPlayerView);
        andExoPlayerView.setSource("http://class6sanskrit.ncertguruji.com/vid/ch3.mp4");
        getSupportActionBar().hide();
        getComments();

    }

    public void getComments(){
        ListView lv = (ListView) findViewById(R.id.list);
        List<String> your_array_list = new ArrayList<String>();


        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);
        String url ="http://api.ncertguruji.com/getJson.php?chapter="+chapter;
        JsonObjectRequest request = new JsonObjectRequest(url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        if (null != response) {
                            //handle your response
                            System.out.println("respo is not null");
                            System.out.println(response.toString());
                            for (int i=0; i<response.length(); i++){
                                try {
                                    String name = response.getJSONObject(i+"").getString("createdBy");
                                    String encoded_content = response.getJSONObject(i+"").getString("content");
                                    String content = URLDecoder.decode(encoded_content, "UTF-8");
//                                    System.out.println("My string is"+name);
//                                    your_array_list.add(name);
                                    names.add(name);
                                    comments.add(content);



                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
//                            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
//                                    MainActivity.this,
//                                    android.R.layout.simple_list_item_1,
//                                    your_array_list );
//                            lv.setAdapter(arrayAdapter);
                            customCommentsList customCountryList = new customCommentsList(MainActivity.this, names, comments);
                            lv.setAdapter(customCountryList);
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println(error);
            }
        });
        queue.add(request);
    }

    public void onBtnClick(View view) throws UnsupportedEncodingException {

        // Name to be taken from shared preferences
//        EditText editText = (EditText)findViewById(R.id.name);
//        name = editText.getText().toString();

        EditText editText1 = (EditText)findViewById(R.id.EditText);
        String given = editText1.getText().toString();
        post = URLEncoder.encode(given, "UTF-8");
//        Toast.makeText(this, post, Toast.LENGTH_SHORT).show();

        JSONObject postData = new JSONObject();

        try {
            postData.put("name", name);
            postData.put("post", post);
            postData.put("chapter",chapter);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        System.out.println("Post data is "+postData);

        new SendDeviceDetails().execute("http://api.ncertguruji.com/postjson.php", postData.toString());
    }


    class SendDeviceDetails extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {

            String data = "";

            HttpURLConnection httpURLConnection = null;
            try {

                httpURLConnection = (HttpURLConnection) new URL(params[0]).openConnection();
                httpURLConnection.setRequestMethod("POST");

                httpURLConnection.setDoOutput(true);

                DataOutputStream wr = new DataOutputStream(httpURLConnection.getOutputStream());
                wr.writeBytes(params[1]);
                wr.flush();
                wr.close();

                InputStream in = httpURLConnection.getInputStream();
                InputStreamReader inputStreamReader = new InputStreamReader(in);

                int inputStreamData = inputStreamReader.read();
                while (inputStreamData != -1) {
                    char current = (char) inputStreamData;
                    inputStreamData = inputStreamReader.read();
                    data += current;
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (httpURLConnection != null) {
                    httpURLConnection.disconnect();
                }
            }

            return data;
        }
        private ProgressDialog dialog = new ProgressDialog(MainActivity.this);
        @Override
        protected void onPreExecute() {
            this.dialog.setMessage("Please wait");
            this.dialog.show();
        }
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (result.equals("New record created successfully")){
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
//                Intent intent = new Intent(MainActivity.this, Posts.class);
//                startActivity(intent);
                getComments();
                Toast.makeText(MainActivity.this, "Comment added", Toast.LENGTH_SHORT).show();
            }
            Log.e("TAG", result); // this is expecting a response code to be sent from your server upon receiving the POST data
        }


    }
}