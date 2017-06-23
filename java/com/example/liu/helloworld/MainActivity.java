package com.example.liu.helloworld;

import android.os.Bundle;
import android.os.Handler;
import android.os.UpdateEngine;
import android.os.UpdateEngineCallback;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "Hello";
    private static final String PAYLOAD_FILE = "payload.bin";
    private static final String PROPERTY_FILE = "payload_properties.txt";
    private Handler mainHandler = new Handler();
    private String[] strArray = new String[4];
    private  Button button_start = null;
    private  Button button_cancel = null;
    private  Button button_reset = null;
    private  static final Object lock = new Object();
    private  int engineState = -1;

    public MyProgressBar progressBar = null;
    public UpdateEngine engine = new UpdateEngine();;
    public TextView tvShow = null;
    ImplentOps ops = new ImplentOps();


    Map<Integer,String> updateStatus = new HashMap<>();
    Map<Integer,String> errorCode    = new HashMap<>();

    private int getEngineState(){
        synchronized (lock){
            return engineState;
        }
    }

    private void setEngineState(int state){
        synchronized (lock){
            engineState = state;
        }
    }

    public UpdateEngineCallback CallbackImplement = new UpdateEngineCallback() {
        @Override
        public void onStatusUpdate(int i, float v) {  //i==status  v==percent
            Log.d(TAG, "status:" + i);
            Log.d(TAG, "percent:" + v);
            tvShow.append(updateStatus.get(i) + "\n");

            setEngineState(i);
            Log.d(TAG, "set status " + getEngineState());
            progressBar.setProgress((int) (v * 100));
        }

        @Override
        public void onPayloadApplicationComplete(int errorNum) {
            //run here means success
            tvShow.append(errorCode.get(errorNum)+"\n");
        }
    };


    //denote some log on UI
    private void sendInfoToUI(final String str, final Handler handler){
        if(handler != null){
            handler.post(new Runnable() {
                @Override
                public void run() {
                   tvShow.append(str + "\n");
                }
            });
        }
    }

    class ImplentOps extends Thread{
        @Override
        public void run() {
            try {
                String path = new String();
                path = FileRead.readUSBFile();
                if(path == null)
                    throw new FileNotFoundException("ota zip not found");

                //ota.zip resides on /data manually
                Zip zip = new Zip(MainActivity.this);
                sendInfoToUI("unzipping file...",mainHandler);
                zip.unZipToFolder(path);
                sendInfoToUI("unzip done...", mainHandler);

                //read file
                FileRead.readProperty(PROPERTY_FILE , MainActivity.this , strArray);
                sendInfoToUI("reading property...", mainHandler);

                //do perform
                sendInfoToUI("applying payload...", mainHandler);
                engine.applyPayload("file://" + MainActivity.this.getFilesDir() + File.separator + PAYLOAD_FILE, 0, 0, strArray);

            } catch (Exception e) {
                StringWriter sw = new StringWriter();
                e.printStackTrace(new PrintWriter(sw,true));
                String str = sw.toString();
                sendInfoToUI(str,mainHandler);
            }finally {
                Log.d(TAG,"finally");
            }
        }
    }


    @Override
    public void onClick(View v) {
        try {
            switch (v.getId()){
                case R.id.button_start:

                    ops.start();
                    Log.d(TAG, "press start");
                    break;

                case R.id.button_cancel:
                    Log.d(TAG, "get status in cancel" + getEngineState());
                    if(ops.isAlive())
                        ops.interrupt();
                    if( getEngineState() > UpdateEngine.UpdateStatusConstants.UPDATE_AVAILABLE) {
                        Log.d(TAG, "engine ready to stop");
                        engine.cancel();
                    }
                    Log.d(TAG, "press cancel");
                    break;

                case R.id.button_reset:
                    Log.d(TAG, "press reset");
                    engine.resetStatus();
                    break;
            }
        } catch (Exception e) {
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw,true));
            String str = sw.toString();
            sendInfoToUI(str, mainHandler);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvShow        = (TextView)findViewById(R.id.show_text);
        button_start  =  (Button) findViewById(R.id.button_start);
        button_cancel = (Button) findViewById(R.id.button_cancel);
        button_reset  = (Button)findViewById(R.id.button_reset);
        button_start.setOnClickListener(this);
        button_cancel.setOnClickListener(this);
        button_reset.setOnClickListener(this);

        progressBar   = (MyProgressBar)findViewById(R.id.progrssbar_new);
        tvShow.setMovementMethod(new ScrollingMovementMethod());

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
        errorCode.put(UpdateEngine.ErrorCodeConstants.PAYLOAD_SIZE_MISMATCH_ERROR, "PAYLOAD_SIZE_MISMATCH_ERROR");
        errorCode.put(UpdateEngine.ErrorCodeConstants.DOWNLOAD_PAYLOAD_VERIFICATION_ERROR, "DOWNLOAD_PAYLOAD_VERIFICATION_ERROR");


        if(engine.bind(CallbackImplement, mainHandler)){
            Log.d(TAG,"bind to callback success");
            tvShow.setText("bind to callback success \n");
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
