package com.example.liu.helloworld;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import android.content.Context;
/**
 * Created by liu on 2017/6/20.
 */
public class Zip {

    private static final String TAG="Zip";
    private Context mContext;
    public Zip(Context con){this.mContext = con;}

    //decompress the file to /data/data/com.example.liu.helloworld/files
    public  void unZipToFolder( String zipFileString) throws Exception{
        ZipInputStream inZip = null;
        ZipEntry zipEntry    ;
        String szName        ;
        byte[] buffer = new byte[1024];
        File zipFile = new File(Environment.getDataDirectory(),zipFileString);

        Log.d(TAG,"" + Environment.getDataDirectory());
        Log.d(TAG, "" + mContext.getFilesDir());

        if( !zipFile.isFile() ){
            throw new FileNotFoundException("file: " + zipFileString + "not found");
        }

        try {
            inZip = new ZipInputStream(new FileInputStream(zipFile));
        } catch (FileNotFoundException e) {
            Log.d(TAG,"file problem ?");
            e.printStackTrace();
        }

        while( ( zipEntry=inZip.getNextEntry()) != null ){

            //files we need rely on the toppest level
            if( zipEntry.getName().contains("/") ){
                Log.d(TAG,"found directory:" + zipEntry.getName() + "but ignore");

            }else{
                szName = zipEntry.getName();
                Log.d(TAG,szName);

             //   File file = new File(Environment.getDataDirectory() + File.separator + szName);
              /*  File file = new File(Environment.getDataDirectory(), szName);

                if( file.exists() && file.delete()){
                    Log.d(TAG,"file " + file.getName() + " exists and be deleted");
                }

                if( !file.createNewFile()){
                    Log.d(TAG,"create" + file.getName() + "fail");
                    throw  new IOException();
                }*/

                //FileOutputStream out = new FileOutputStream(file);
                FileOutputStream out = mContext.openFileOutput(szName, mContext.MODE_PRIVATE);
                int len;
                try {
                    while( (len = inZip.read(buffer)) != -1){
                        out.write(buffer,0,len);
                        out.flush();
                    }
                } catch (IOException e) {
                    out.close();
                    buffer = null;
                    e.printStackTrace();
                }
                out.close();
            }
        }
        buffer = null;
    }
   /* public static void main(String [] args){

        try {
            Zip.unZipToFolder("0614ota.zip",System.getProperty("user.dir"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/
}
