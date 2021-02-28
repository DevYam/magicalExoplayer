package com.ncertguruji.magicalexo;

import androidx.appcompat.app.AppCompatActivity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
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

import com.google.android.gms.ads.MobileAds;

public class MainActivity extends AppCompatActivity {

    String chapter = "ch1";
    String post = "";
    ListView lv;

    //Hardcoding the now but we will get the name from shared preferences
    String name = "Divyam";

    ArrayList<String> names = new ArrayList<String>();
    ArrayList<String> comments = new ArrayList<String>();

    private FrameLayout adContainerView;
    private AdView adView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AndExoPlayerView andExoPlayerView = findViewById(R.id.andExoPlayerView);
        andExoPlayerView.setSource("http://class6sanskrit.ncertguruji.com/vid/ch3.mp4");
        getSupportActionBar().hide();
        lv = (ListView) findViewById(R.id.list);
        getComments();

        //Call the function to initialize AdMob SDK
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        //get the reference to your FrameLayout
        adContainerView = findViewById(R.id.adView_container);

        //Create an AdView and put it into your FrameLayout
        adView = new AdView(this);
        adContainerView.addView(adView);

        //Create an AdView and put it into your FrameLayout
        adView = new AdView(this);
        adContainerView.addView(adView);
        adView.setAdUnitId("ca-app-pub-3940256099942544/6300978111");

        //start requesting banner ads
        loadBanner();
    }

    public void getComments(){


        // This is being done so as to not repeat the comments
        names.clear();
        comments.clear();
        customCommentsList customCountryList = new customCommentsList(MainActivity.this, names, comments);
        lv.setAdapter(customCountryList);

        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);
        String url ="http://api.ncertguruji.com/getJson.php?chapter="+chapter;
        JsonObjectRequest request = new JsonObjectRequest(url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        if (null != response) {
                            //handle your response
                            for (int i=0; i<response.length(); i++){
                                try {
                                    String name = response.getJSONObject(i+"").getString("createdBy");
                                    String encoded_content = response.getJSONObject(i+"").getString("content");
                                    String content = URLDecoder.decode(encoded_content, "UTF-8");
                                    names.add(name);
                                    comments.add(content);

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        customCountryList.notifyDataSetChanged();
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

        EditText editText1 = (EditText)findViewById(R.id.EditText);
        String given = editText1.getText().toString();
        post = URLEncoder.encode(given, "UTF-8");
        JSONObject postData = new JSONObject();
        try {
            postData.put("name", name);
            postData.put("post", post);
            postData.put("chapter",chapter);
        } catch (JSONException e) {
            e.printStackTrace();
        }
//        System.out.println("Post data is "+postData);

        new SendDeviceDetails().execute("http://api.ncertguruji.com/postjson.php", postData.toString());
    }
    private AdSize getAdSize() {
        //Determine the screen width to use for the ad width.
        Display display = getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);
        float widthPixels = outMetrics.widthPixels;
        float density = outMetrics.density;
        //you can also pass your selected width here in dp
        int adWidth = (int) (widthPixels / density);

        //return the optimal size depends on your orientation (landscape or portrait)
        return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(this, adWidth);
    }

    private void loadBanner() {
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .build();

        AdSize adSize = getAdSize();
        // Set the adaptive ad size to the ad view.
        adView.setAdSize(adSize);

        // Start loading the ad in the background.
        adView.loadAd(adRequest);
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
                Toast.makeText(MainActivity.this, "Comment added", Toast.LENGTH_SHORT).show();
                getComments();
            }
            Log.e("TAG", result);
        }
    }
}