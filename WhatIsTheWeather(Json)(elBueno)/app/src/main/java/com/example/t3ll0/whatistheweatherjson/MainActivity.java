package com.example.t3ll0.whatistheweatherjson;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.util.Xml;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

public class MainActivity extends AppCompatActivity {
    EditText inputEditText;
    EditText resultEditText;
    Context con=this;
public class DownloadTask extends AsyncTask<String,Void,String> {


    @Override
    protected String doInBackground(String... params) {
        URL url = null;
        HttpURLConnection connection = null;
        try {
            url = new URL(params[0]);
            connection = (HttpURLConnection) url.openConnection();
            InputStream in = connection.getInputStream();
            InputStreamReader reader = new InputStreamReader(in);
            BufferedReader bufferedReader = new BufferedReader(reader);
            StringBuilder builder = new StringBuilder();
            String line = null;
            while ((line = bufferedReader.readLine()) != null) {
                builder.append(line);
            }
            return builder.toString();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        if(s.equals("")) {
            Toast.makeText(con,"no hay informacion",Toast.LENGTH_LONG).show();
        }else{

        Log.d("OnPostExecute", s);
        try{
            JSONObject jsonObject = new JSONObject(s);
            if(!jsonObject.getString("cod").equals("404")) {
String result=formatJson(jsonObject);
                resultEditText.setText(result);
                //--------
            }else{
                String error= jsonObject.getString("message");
                Toast.makeText(con,error,Toast.LENGTH_LONG).show();
            }
        }catch(JSONException e){
            e.printStackTrace();
        }
    }
    }
String formatJson(JSONObject jO){
    try{
        String name= jO.getString("name");

        String country=jO.getJSONObject("sys").getString("country");

        String description="";
         JSONArray weatherArr = jO.getJSONArray("weather");
        for (int i = 0; i < weatherArr.length(); i++) {
        JSONObject weather = weatherArr.getJSONObject(i);
        //Log.d("weather>>", weather.getString(("description")));
        description=weather.getString("description");
    }


        JSONObject main= jO.getJSONObject("main");
        String minTemp= main.getString("temp_min");
        String maxTemp=main.getString("temp_max");
        String humidity=main.getString("humidity");

        JSONObject wind= jO.getJSONObject("wind");
        String windDegrees=wind.getString("deg");
        String windSpeed=wind.getString("speed");;
return"City: "+name+"\n"+
        "Country: "+country+"\n"+
        "Description: "+description+"\n"+
        "Temperature: From-"+minTemp+" To-"+maxTemp+"\n"+
        "Humidity: "+humidity+"\n"+
        "Wind Degrees: "+windDegrees+"\n"+
        "Wind Speed: "+windSpeed+"\n";
    }catch(JSONException e){
        e.printStackTrace();
    }
    return "";
}

}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        inputEditText=(EditText)findViewById(R.id.inputEditText);
        resultEditText=(EditText)findViewById(R.id.resultEditText);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onClick(View view) {
        resultEditText.setText("");
        String country = inputEditText.getText().toString();
        if (country.equals("")) {
            Toast.makeText(this, "Input a Location \n >:(", Toast.LENGTH_LONG).show();
        } else {
            hideKeybord(view);
            //country = TextUtils.htmlEncode(country);
            try {
                country = URLEncoder.encode(country, "utf-8");
            }catch(Exception e){e.printStackTrace();}
            Log.d("html encode",country);
            DownloadTask task = new DownloadTask();
            task.execute("http://api.openweathermap.org/data/2.5/weather?q=" + country + "&appid=44db6a862fba0b067b1930da0d769e98");
        }
    }
    public void hideKeybord(View view) {
        try  {
            InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        } catch (Exception e) {

        }
    }
}
