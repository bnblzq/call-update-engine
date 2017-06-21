package com.example.liu.helloworld;

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
import android.widget.Toast;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = "Hello";
    Button button_start = null;
    Button button_cancel = null;
    ProgressBar progressBar = null;
    UpdateEngineCallback CallbackImplement = null;
    UpdateEngine engine = new UpdateEngine();;
    TextView textView = null;
    boolean start  = true;
    boolean cancel = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = (TextView)findViewById(R.id.show_text);
        button_start =  (Button) findViewById(R.id.button_start);
        button_cancel = (Button) findViewById(R.id.button_cancel);
        button_cancel.setEnabled(false);
        progressBar = (ProgressBar)findViewById(R.id.progrss_bar);
        final String[] strArray = new String[4];


        button_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(start){
                    button_start.setEnabled(false);
                    button_cancel.setEnabled(true);
                    start= !start;
                    cancel= !cancel;
                }
                Log.d(TAG,"press start");


                try {
                     FileRead.readProperty("/data/payload_properties.txt", strArray);
                } catch (Exception e) {
                    Log.d(TAG,"readProperty fail");
                    e.printStackTrace();
                }



               /* try {
                    engine.applyPayload("file:///data/payload.bin", 0, 0, strArray);
                } catch (Exception e) {
                    Log.d(TAG,"parameter error in applypayload,check logcat");
                    Toast.makeText(MainActivity.this, "log:" + e.fillInStackTrace(),Toast.LENGTH_SHORT).show();

                }*/
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


        CallbackImplement = new UpdateEngineCallback() {
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


        if(engine != null){
            Log.d(TAG,"engine initialize ok");
        }

        if(engine.bind(CallbackImplement)==true){
            //Toast.makeText(MainActivity.this,"bind success", Toast.LENGTH_SHORT).show();

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
