package com.elselse.veritas;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

public class SearchActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        TextView textView = findViewById(R.id.textView);
        textView.setText(MainActivity.query);

        new Thread(() -> {
            try {
                InputStream inputStream = (InputStream) new URL("https://actuallyroy.pythonanywhere.com/verify?query=giraffe").getContent();

                int i = inputStream.read();
                while (i != -1){
                    Log.d("TAG", "run: " + (char) i);
                    i = inputStream.read();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }


}