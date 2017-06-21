package com.example.liu.helloworld;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.os.UpdateEngine;
import android.os.UpdateEngineCallback;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
//import android.widget.Toast;
import android.os.Handler;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = "Hello";
    private static final String UPDATE_FILE = "0614ota.zip";
    private static final String PAYLOAD_FILE = "payload.bin";
    private static final String PROPERTY_FILE = "payload_properties.txt";
    private Handler mainHandler = new Handler();
    private String[] strArray = new String[4];
    public  Button button_start = null;
    public  Button button_cancel = null;
    public ProgressBar progressBar = null;
    public UpdateEngine engine = new UpdateEngine();;
    public TextView tvShow = null;
    public boolean start  = true;
    public boolean cancel = false;
    public UpdateEngineCallback CallbackImplement = new UpdateEngineCallback() {
        @Override
        public void onStatusUpdate(int i, float v) {
            // Toast.makeText(MainActivity.this,"status: "+ i, Toast.LENGTH_SHORT).show();
            //int progress = progressBar.getProgress();
            Log.d(TAG,"status:"+i);
            Log.d(TAG,"percent:"+v);
            progressBar.setProgress((int)(v*100));
        }

        @Override
        public void onPayloadApplicationComplete(int i) {
            // Toast.makeText(MainActivity.this,"done! : "+ i, Toast.LENGTH_SHORT).show();
            Log.d(TAG,"complete:"+i);
        }
    };



    class ImplentOps extends Thread{
        @Override
        public void run() {
            Log.d(TAG,"inside run");
            try {
                //ota.zip resides on /data manually
                Zip zip = new Zip(MainActivity.this);
                zip.unZipToFolder(UPDATE_FILE);
                Log.d(TAG, "upzip done");

                //read file
                FileRead.readProperty(PROPERTY_FILE , MainActivity.this , strArray);
                Log.d(TAG, "read property done");

                //do perform
                //engine.applyPayload("file:///data/payload.bin", 0, 0, strArray);
               engine.applyPayload("file://"+MainActivity.this.getFilesDir()+File.separator+PAYLOAD_FILE , 0, 0, strArray);
                Log.d(TAG, "apply done");

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvShow = (TextView)findViewById(R.id.show_text);
        button_start =  (Button) findViewById(R.id.button_start);
        button_cancel = (Button) findViewById(R.id.button_cancel);
        button_cancel.setEnabled(false);
        progressBar = (ProgressBar)findViewById(R.id.progrss_bar);

        button_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              ImplentOps ops = new ImplentOps();
                ops.start();

                if(start){
                    button_start.setEnabled(false);
                    button_cancel.setEnabled(true);
                    start= !start;
                    cancel= !cancel;
                }
                Log.d(TAG,"press start");
            }
        });

        button_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cancel) {
                    button_cancel.setEnabled(false);
                    button_start.setEnabled(true);
                    start = !start;
                    cancel = !cancel;
                }
                Log.d(TAG,"press cancel");
            }
        });

        if(engine.bind(CallbackImplement, mainHandler)){
            Log.d(TAG,"bind to callback success");
        }
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
}
