package com.example.liu.helloworld;

import android.content.Context;
import android.content.Intent;
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
import java.util.HashMap;
import java.util.Map;


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

    Map<Integer,String> updateStatus = new HashMap<>();
    Map<Integer,String> errorCode = new HashMap<>();



    public UpdateEngineCallback CallbackImplement = new UpdateEngineCallback() {
        @Override
        public void onStatusUpdate(int i, float v) {
            Log.d(TAG,"status:"+i);
            Log.d(TAG,"percent:"+v);
            tvShow.append(updateStatus.get(i));

            if( (int)(v*100) < 100) {
                //we leave 100% to show by onPayloadApplicationComplete
                progressBar.setProgress((int) (v * 100));
            }
        }

        @Override
        public void onPayloadApplicationComplete(int errorNum) {
            //run here means success
            if( errorNum == UpdateEngine.ErrorCodeConstants.SUCCESS ) {
                tvShow.append(errorCode.get(errorNum));
                progressBar.setProgress(progressBar.getMax());
            }
            tvShow.append(errorCode.get(errorNum));
        }
    };


    //denote some log on UI
    private void sendInfoToUI(final String str, final Handler handler){
        if(handler != null){
            handler.post(new Runnable() {
                @Override
                public void run() {
                   tvShow.append(str);
                }
            });
        }
    }

    class ImplentOps extends Thread{
        @Override
        public void run() {
            Log.d(TAG,"inside run");
            try {
                //ota.zip resides on /data manually
                Zip zip = new Zip(MainActivity.this);
                sendInfoToUI("unzipping file...",mainHandler);
                zip.unZipToFolder(UPDATE_FILE);
                sendInfoToUI("unzip done...", mainHandler);

                //read file
                FileRead.readProperty(PROPERTY_FILE , MainActivity.this , strArray);
                sendInfoToUI("reading property...", mainHandler);

                //do perform
                sendInfoToUI("applying payload...",mainHandler);
                engine.applyPayload("file://"+MainActivity.this.getFilesDir()+File.separator+PAYLOAD_FILE , 0, 0, strArray);
                sendInfoToUI("applying done...",mainHandler);

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

        //for clearly denotion
        updateStatus.put(UpdateEngine.UpdateStatusConstants.IDLE ,"IDLE");
        updateStatus.put(UpdateEngine.UpdateStatusConstants.CHECKING_FOR_UPDATE ,"CHECKING_FOR_UPDATE");
        updateStatus.put(UpdateEngine.UpdateStatusConstants.UPDATE_AVAILABLE ,"UPDATE_AVAILABLE");
        updateStatus.put(UpdateEngine.UpdateStatusConstants.DOWNLOADING ,"DOWNLOADING");
        updateStatus.put(UpdateEngine.UpdateStatusConstants.VERIFYING ,"VERIFYING");
        updateStatus.put(UpdateEngine.UpdateStatusConstants.FINALIZING ,"FINALIZING");
        updateStatus.put(UpdateEngine.UpdateStatusConstants.UPDATED_NEED_REBOOT ,"UPDATED_NEED_REBOOT");
        updateStatus.put(UpdateEngine.UpdateStatusConstants.REPORTING_ERROR_EVENT ,"REPORTING_ERROR_EVENT");
        updateStatus.put(UpdateEngine.UpdateStatusConstants.ATTEMPTING_ROLLBACK ,"ATTEMPTING_ROLLBACK");
        updateStatus.put(UpdateEngine.UpdateStatusConstants.DISABLED ,"DISABLED");

        errorCode.put(UpdateEngine.ErrorCodeConstants.SUCCESS,"SUCCESS");
        errorCode.put(UpdateEngine.ErrorCodeConstants.ERROR,"ERROR");
        errorCode.put(UpdateEngine.ErrorCodeConstants.FILESYSTEM_COPIER_ERROR,"FILESYSTEM_COPIER_ERROR");
        errorCode.put(UpdateEngine.ErrorCodeConstants.POST_INSTALL_RUNNER_ERROR,"POST_INSTALL_RUNNER_ERROR");
        errorCode.put(UpdateEngine.ErrorCodeConstants.PAYLOAD_MISMATCHED_TYPE_ERROR,"PAYLOAD_MISMATCHED_TYPE_ERROR");
        errorCode.put(UpdateEngine.ErrorCodeConstants.INSTALL_DEVICE_OPEN_ERROR,"INSTALL_DEVICE_OPEN_ERROR");
        errorCode.put(UpdateEngine.ErrorCodeConstants.KERNEL_DEVICE_OPEN_ERROR,"KERNEL_DEVICE_OPEN_ERROR");
        errorCode.put(UpdateEngine.ErrorCodeConstants.DOWNLOAD_TRANSFER_ERROR,"DOWNLOAD_TRANSFER_ERROR");
        errorCode.put(UpdateEngine.ErrorCodeConstants.PAYLOAD_HASH_MISMATCH_ERROR,"PAYLOAD_HASH_MISMATCH_ERROR");
        errorCode.put(UpdateEngine.ErrorCodeConstants.PAYLOAD_SIZE_MISMATCH_ERROR,"PAYLOAD_SIZE_MISMATCH_ERROR");
        errorCode.put(UpdateEngine.ErrorCodeConstants.DOWNLOAD_PAYLOAD_VERIFICATION_ERROR,"DOWNLOAD_PAYLOAD_VERIFICATION_ERROR");

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
