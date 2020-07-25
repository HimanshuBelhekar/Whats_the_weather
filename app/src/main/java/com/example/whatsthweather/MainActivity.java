package com.example.whatsthweather;

import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.content.AsyncTaskLoader;

import android.hardware.input.InputManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class MainActivity extends AppCompatActivity {

    TextView descriptionTextView;
    EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        descriptionTextView = findViewById(R.id.descriptionTextView);
        editText = findViewById(R.id.editText);
    }

    public void getWeather(View view){
        try {
            DownloadTask task = new DownloadTask();
            String encodedCityName = URLEncoder.encode(editText.getText().toString(),"UTF-8");

            task.execute("https://openweathermap.org/data/2.5/weather?q=" + encodedCityName + "&appid=439d4b804bc8187953eb36d2a8c26a02");

            InputMethodManager mgr = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            mgr.hideSoftInputFromWindow(editText.getWindowToken(),0);

        } catch(Exception e){
            e.printStackTrace();
            Toast.makeText(MainActivity.this, "Couldn't get the weather :(", Toast.LENGTH_SHORT).show();
        }

    }

    public class DownloadTask extends AsyncTask<String, Void, String>{

        @Override
        protected String doInBackground(String... urls) {
            String result = "";
            URL url;
            HttpURLConnection urlConnection = null;

            try{
                url = new URL(urls[0]);
                urlConnection = (HttpURLConnection)url.openConnection();
                InputStream in = urlConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);
                int data = reader.read();

                while(data != -1){
                    char current = (char)data;
                    result += current;
                    data = reader.read();
                }

                return result;

            }catch(Exception e){
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            try {
                JSONObject jsonObject = new JSONObject(s);
                String weatherInfo = jsonObject.getString("weather");
                JSONArray arr = new JSONArray(weatherInfo);

                String message = null;
                for(int i=0; i<arr.length(); i++){
                    JSONObject jsonPart = arr.getJSONObject(i);

                    String main = jsonPart.getString("main");
                    String desc = jsonPart.getString("description");

                    if(!main.equals("") && !desc.equals("")) {
                        message = main + " : " + desc + "\n";
                    }
                }

                if (!message.equals("")) {
                    descriptionTextView.setText(message);
                }else{
                    Toast.makeText(MainActivity.this, "Couldn't get the weather :(", Toast.LENGTH_SHORT).show();
                }
            }catch(Exception e){
                e.printStackTrace();
                Toast.makeText(MainActivity.this, "Couldn't get the weather :(", Toast.LENGTH_SHORT).show();
            }
        }
    }

}