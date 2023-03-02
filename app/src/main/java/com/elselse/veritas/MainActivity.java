package com.elselse.veritas;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

public class MainActivity extends Activity {
    boolean b = false;
    public static String query = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ImageView mask = findViewById(R.id.mask);
        ImageButton cameraBtn = findViewById(R.id.cameraBtn);
        MaterialButton searchBtn = findViewById(R.id.searchBtn),
                castBtn = findViewById(R.id.castBtn),
                imageBtn = findViewById(R.id.imageBtn),
                notice_ok = findViewById(R.id.notice_ok),
                queryBtn = findViewById(R.id.queryBtn),
                clipboardBtn = findViewById(R.id.clipboardBtn);
        TextInputLayout searchField = findViewById(R.id.searchField);
        FrameLayout notice = findViewById(R.id.notice);

        SharedPreferences prefs = getSharedPreferences("com.elselse.veritas", MODE_PRIVATE);
        if (prefs.getBoolean("firstRun", true)) {
            notice.setVisibility(View.VISIBLE);
            prefs.edit().putBoolean("firstRun", false).apply();
        }

        notice_ok.setOnClickListener(v -> notice.setVisibility(View.GONE));




        searchBtn.setOnClickListener(v -> {
            b = true;
            searchField.animate().y(10).setDuration(500).start();
            searchField.requestFocus();
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);
            new Handler().postDelayed(() -> mask.setVisibility(View.VISIBLE), 300);

        });

        castBtn.setOnClickListener(v -> {
            super.onBackPressed();
            Log.d("MainActivity", "Cast button clicked");
            new Handler().postDelayed(this::takeScreenshot, 5000);
        });

        imageBtn.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            startActivityForResult(intent, 1);
        });

        queryBtn.setOnClickListener(v -> {
            b = true;
            searchField.animate().y(10).setDuration(500).start();
            searchField.requestFocus();
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);
            new Handler().postDelayed(() -> mask.setVisibility(View.VISIBLE), 300);
        });

        clipboardBtn.setOnClickListener(v -> {
            ClipboardManager myCP;
            myCP = (ClipboardManager)getSystemService(CLIPBOARD_SERVICE);
            ClipData mydata= myCP.getPrimaryClip();
            ClipData.Item item= mydata.getItemAt(0);
            String mytext = String.valueOf(item.getText());
            Log.d("MainActivity", "Clipboard text: " + mytext);
            query = mytext;
            Intent intent = new Intent(MainActivity.this, SearchActivity.class);
            startActivity(intent);
        });

        searchField.setEndIconOnClickListener(v -> {
            b = false;
            searchField.animate().y(1000).setDuration(500).start();
            mask.setVisibility(View.GONE);
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(searchField.getWindowToken(), 0);
            query = searchField.getEditText().getText().toString();
            Log.d("MainActivity", "Search button clicked");
            Intent intent = new Intent(getApplicationContext(), SearchActivity.class);
            startActivity(intent);
        });

        cameraBtn.setOnClickListener(v -> {
            Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
            startActivityForResult(intent, 2);
        });

    }

    @Override
    public void onBackPressed() {
        if(b){
            b = false;
            TextInputLayout searchField = findViewById(R.id.searchField);
            searchField.animate().y(10000).setDuration(500).start();
            searchField.clearFocus();
            ImageView mask = findViewById(R.id.mask);
            new Handler().postDelayed(() -> mask.setVisibility(View.GONE), 300);

        }else{
            super.onBackPressed();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            Uri uri = data.getData();
            InputImage image = null;
            try {
                image = InputImage.fromFilePath(this, uri);
            } catch (IOException e) {
                e.printStackTrace();
            }
            TextRecognizer recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);
            recognizer.process(image)
                    .addOnSuccessListener(visionText -> {
                        final int[] max = {0};
                        final String[] text = {""};
                        visionText.getTextBlocks().forEach(block -> {
                            int p = (block.getBoundingBox().height() * block.getBoundingBox().width())/block.getText().length();
                            if (p > max[0]) {
                                max[0] = p;
                                text[0] = block.getText();
                            }
                        });
                        Log.d("MainActivity", text[0]);
                        query = text[0];
                        Intent intent = new Intent(getApplicationContext(), SearchActivity.class);
                        startActivity(intent);
                    })
                    .addOnFailureListener(e -> {
                        Log.d("MainActivity", e.getMessage());
                    });
        }

        if(requestCode == 2 && resultCode == RESULT_OK){
            Bitmap bitmap = (Bitmap) data.getExtras().get("data");
            InputImage image = InputImage.fromBitmap(bitmap, 0);
            TextRecognizer recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);
            recognizer.process(image)
                    .addOnSuccessListener(visionText -> {
                        final int[] max = {0};
                        final String[] text = {""};
                        visionText.getTextBlocks().forEach(block -> {
                            int p = (block.getBoundingBox().height() * block.getBoundingBox().width())/block.getText().length();
                            if (p > max[0]) {
                                max[0] = p;
                                text[0] = block.getText();
                            }
                        });
                        Log.d("MainActivity", text[0]);
                        query = text[0];
                        Intent intent = new Intent(getApplicationContext(), SearchActivity.class);
                        startActivity(intent);
                    })
                    .addOnFailureListener(e -> {
                        Log.d("MainActivity", e.getMessage());
                    });
        }
    }

    private void takeScreenshot() {
        Date now = new Date();
        android.text.format.DateFormat.format("yyyy-MM-dd_hh:mm:ss", now);

        try {
            // image naming and path  to include sd card  appending name you choose for file
            new File(Environment.getExternalStorageDirectory() + "/Veritas").mkdirs();
            String mPath = Environment.getExternalStorageDirectory().toString() + "/Veritas/" + now.getTime() + ".jpg";

            // create bitmap screen capture
            View v1 = getWindow().getDecorView();
            v1.setDrawingCacheEnabled(true);
            Bitmap bitmap = Bitmap.createBitmap(v1.getDrawingCache());
            v1.setDrawingCacheEnabled(false);

            File imageFile = new File(mPath);

            FileOutputStream outputStream = new FileOutputStream(imageFile);
            int quality = 100;
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream);
            outputStream.flush();
            outputStream.close();

        } catch (Throwable e) {
            // Several error may come out with file handling or DOM
            e.printStackTrace();
        }
    }

    private void openScreenshot(File imageFile) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        Uri uri = Uri.fromFile(imageFile);
        intent.setDataAndType(uri, "image/*");
        startActivity(intent);
    }
}